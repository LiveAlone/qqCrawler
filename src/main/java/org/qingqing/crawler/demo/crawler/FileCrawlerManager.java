package org.qingqing.crawler.demo.crawler;

import org.apache.poi.common.usermodel.LineStyle;
import org.qingqing.crawler.demo.crawler.domain.PagedUrl;
import org.qingqing.crawler.demo.crawler.domain.PagedUrl.FileLoadResult;
import org.qingqing.crawler.demo.crawler.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yaoqijun on 2016/12/29.
 */
@Component
public class FileCrawlerManager {

    private static final Logger logger = LoggerFactory.getLogger(FileCrawlerManager.class);

    private final MyConfiguration myConfiguration;

    private final FileCrawlerService fileCrawlerService;

    @Autowired
    public FileCrawlerManager(MyConfiguration myConfiguration, FileCrawlerService fileCrawlerService){
        this.myConfiguration = myConfiguration;
        this.fileCrawlerService = fileCrawlerService;
    }

    public void loadTotalPagedFile(){

        List<String> targetPages = myConfiguration.getTargetPage();
        if (targetPages==null || targetPages.isEmpty()){
            logger.warn("target paged url is empty");
        }

        for (String targetPage : targetPages) {

            PagedUrl targetPageUrl = PagedUrl.convertPagedUrl(targetPage);

            if (targetPageUrl == null){
                logger.info("target paged url analyse fail");
                continue;
            }

            List<String> fileUrls = fileCrawlerService.analyseTotalUrl(targetPageUrl.getPagedUrl());

            if (fileUrls == null || fileUrls.isEmpty()){
                logger.info("target page url not found load file url, pageUrl:{}", targetPageUrl);
            }

            // save pageSize condition
            FileUtils.overwriteFile(targetPageUrl.formatPathUrl(MyConfiguration.FILE_LOAD_URL, myConfiguration), fileUrls);
            FileUtils.tryDeleteFile(targetPageUrl.formatPathUrl(MyConfiguration.FILE_LOAD_LOG, myConfiguration));

            for (String fileUrl : fileUrls) {
                String filename = fileCrawlerService.loadRealFile(targetPageUrl, fileUrl);
                if (filename == null){
                    logger.info("error load file, file url :{}", fileUrl);
                }else {
                    logger.info("success load file, file :{}", filename);
                }
                FileUtils.appendFileContent(
                        targetPageUrl.formatPathUrl(MyConfiguration.FILE_LOAD_LOG, myConfiguration),
                        new PagedUrl.FileLoadResult(filename != null, fileUrl, filename == null?"NONE":filename).toString() + "\n");
            }
        }
    }

    public void loadErrorFile(){

        // get config
        List<String> targetPages = myConfiguration.getTargetPage();
        if (targetPages==null || targetPages.isEmpty()){
            logger.warn("target paged url is empty");
        }

        for (String targetPage : targetPages) {
            PagedUrl targetPageUrl = PagedUrl.convertPagedUrl(targetPage);

            if (targetPageUrl == null){
                logger.info("target paged url analyse fail");
                continue;
            }

            // load error content
            File loadLogFile = new File(targetPageUrl.formatPathUrl(MyConfiguration.FILE_LOAD_LOG, myConfiguration));
            List<String> loadLogFileStrings = FileUtils.readAllLines(loadLogFile);
            if (loadLogFileStrings==null || loadLogFileStrings.isEmpty()){
                logger.warn("no error record found, path is :{}", targetPageUrl.formatPathUrl(MyConfiguration.FILE_LOAD_LOG, myConfiguration));
                return;
            }
            List<FileLoadResult> loadResults = new ArrayList<>(loadLogFileStrings.size());
            for (String loadLogFileString : loadLogFileStrings) {
                loadResults.add(FileLoadResult.from(loadLogFileString));
            }

            // load data
            for (FileLoadResult loadResult : loadResults) {
                if (!loadResult.getSuccess()){
                    String filename = fileCrawlerService.loadRealFile(targetPageUrl, loadResult.getPath());
                    if (filename == null){
                        logger.info("error load file, file url :{}", loadResult.getPath());
                    }else {
                        logger.info("success load file, file :{}", filename);
                        loadResult.setSuccess(true);
                        loadResult.setFilename(filename);
                    }
                }
            }

            // refresh content
            FileUtils.tryDeleteFile(targetPageUrl.formatPathUrl(MyConfiguration.FILE_LOAD_LOG, myConfiguration));
            for (FileLoadResult loadResult : loadResults) {
                FileUtils.appendFileContent(
                        targetPageUrl.formatPathUrl(MyConfiguration.FILE_LOAD_LOG, myConfiguration), loadResult.toString() + "\n");
            }
        }

    }
}
