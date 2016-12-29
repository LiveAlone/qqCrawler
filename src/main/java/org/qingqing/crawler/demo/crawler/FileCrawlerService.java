package org.qingqing.crawler.demo.crawler;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.poi.util.IOUtils;
import org.qingqing.crawler.demo.crawler.domain.PagedUrl;
import org.qingqing.crawler.demo.crawler.utils.HttpHeaderContants;
import org.qingqing.crawler.demo.crawler.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * Created by yaoqijun on 2016/12/29.
 */
@Service
public class FileCrawlerService {

    private static final Logger logger = LoggerFactory.getLogger(FileCrawlerService.class);

    private final MyConfiguration myConfiguration;

    private final List<DefaultProxyRoutePlanner> planners = Lists.newArrayList();

    private final PagedContentAnalyse pagedContentAnalyse;

    private final List<PatternGetUrl> patternGetUrls = Lists.newArrayList();

    private final List<String> xkPasswords = Lists.newArrayList();

    @Autowired
    public FileCrawlerService(MyConfiguration myConfiguration, PagedContentAnalyse pagedContentAnalyse){
        this.myConfiguration = myConfiguration;
        this.pagedContentAnalyse = pagedContentAnalyse;
        initProxy();
        initPatternList();
        xkPasswords.addAll(myConfiguration.getXp());
    }

    public String loadRealFile(PagedUrl pagedUrl, String loadFilePath){

        try {
            String convertLoadUrl = convertUrl(loadFilePath);
            String content = getPageContent(convertLoadUrl);
            String downloadUrl = analyseDownloadPageContent(content);
            if (downloadUrl == null){
                logger.info("page :{}, loadFilePath:{}, downloadUrl not found", pagedUrl, loadFilePath);
                return null;
            }

            for (String xkPassword : xkPasswords) {
                String filename = downloadFile(pagedUrl, downloadUrl, xkPassword);
                if (filename != null){
                    return filename;
                }
            }
            logger.error("load xl passwords load fail, pageUrl:{}", pagedUrl);
            return null;
        }catch (Exception e){
            logger.error("page:{}, file :{} download fail, cause:{}", pagedUrl, loadFilePath, e);
            return null;
        }
    }

    public String downloadFile(PagedUrl pagedUrl, String url, String xkPassword){
        HttpGet httpGet = new HttpGet(url);
        CookiesManager cookiesManager = new CookiesManager();
        cookiesManager.setAttribute("xk.passport",xkPassword);
        httpGet.setHeader(HttpHeaderContants.Cookie,cookiesManager.formatCookies());

        try {
            HttpResponse response = gainHttpClient().execute(httpGet);
            String filenameSource = StringUtil.gainHeaderFileName(
                    response.getHeaders(HttpHeaderContants.Content_Disposition)[0].getValue());
            String filename = URLDecoder.decode(filenameSource);
            filename = filename.replace(":", "-");
            File file = new File(pagedUrl.formatPathUrl(filename));
            Files.createParentDirs(file);
            Files.write(IOUtils.toByteArray(response.getEntity().getContent()), file);
            return filename;
        }catch (Exception e){
            logger.error("load file error , pagedUrl:{}, url:{}, xpPassword:{}", pagedUrl, url, xkPassword);
            return null;
        }
    }

    // 获取页面的所有Url
    public List<String> analyseTotalUrl(String targetPage){
        List<String> files = Lists.newArrayList();
        try {
            String content = getPageContent(targetPage);
            Integer totalPageSize = pagedContentAnalyse.analyseTotalPage(content);
            if (totalPageSize == null){
                logger.error("totalPageSize not found, targetPage:{}", targetPage);
                return new ArrayList<>(0);
            }
            logger.info("page :{}, total size is :{}", targetPage,  totalPageSize);

            for (int i=1; i<=totalPageSize; i++){
                String pagedUrl = PagedUrl.appendPaged(targetPage, i);
                logger.info("start analyse file download url, pageNumber:{} targetPage:{}", i, pagedUrl);
                files.addAll(pagedContentAnalyse.analysePagedFileUrl(getPageContent(pagedUrl)));
            }
        }catch (Exception e){
            logger.error("analyse total page error, targetPage:{}, e:{}", targetPage, e);
            return new ArrayList<>(0);
        }
        return files;
    }

    private String analyseDownloadPageContent(String content) throws Exception{
        for (PatternGetUrl patternGetUrl : patternGetUrls) {
            String s = StringUtil.findFirst(content, patternGetUrl.getPattern());
            if (s != null){
                return s.substring(s.indexOf("href")+6, s.indexOf(patternGetUrl.getEndTag()));
            }
        }
        return null;
    }

    private String getPageContent(String url) throws Exception{
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = gainHttpClient().execute(httpGet);
        return new String(IOUtils.toByteArray(response.getEntity().getContent()));
    }

    private HttpClient gainHttpClient(){
        if (planners.isEmpty()){
            return HttpClients.createDefault();
        }
        Integer result = ThreadLocalRandom.current().nextInt(planners.size());
        return HttpClients.custom().setRoutePlanner(planners.get(result)).build();
    }

    private void initProxy(){
        List<String> proxies = myConfiguration.getProxy();
        if (proxies == null || proxies.isEmpty()){
            logger.warn("proxies is empty");
            return;
        }
        for (String proxy : proxies) {
            String[] addressPort = proxy.split(":");
            if (addressPort.length != 2){
                logger.error("proxy setting format error, auto ignore, proxies:{}, prosy:{}", proxies, proxy);
                continue;
            }
            planners.add(new DefaultProxyRoutePlanner(new HttpHost(addressPort[0], Integer.valueOf(addressPort[1]))));
        }
    }

    private void initPatternList(){
        for (String s : myConfiguration.getRegular()) {
            String[] patternUrls = s.split(":");
            patternGetUrls.add(new PatternGetUrl(Pattern.compile(patternUrls[0]), patternUrls[1]));
        }
    }

    // url convert
    private static String convertUrl(String originUrl){
        if (originUrl.contains("http://sx.zxxk.com/soft/")
                || originUrl.contains("http://sw.zxxk.com/soft/")
                || originUrl.contains("http://yy.zxxk.com/soft/")
                || originUrl.contains("http://wl.zxxk.com/soft/")
                || originUrl.contains("http://dl.zxxk.com/soft/")
                || originUrl.contains("http://zz.zxxk.com/soft/")
                || originUrl.contains("http://hx.zxxk.com/soft/")
                || originUrl.contains("http://ls.zxxk.com/soft/")
                || originUrl.contains("http://xx.zxxk.com/soft/")){
            return originUrl.replaceFirst("soft/", "s");
        }
        return originUrl;
    }

    private static class PatternGetUrl{
        private Pattern pattern;

        private String endTag;

        public PatternGetUrl(Pattern pattern, String endTag) {
            this.pattern = pattern;
            this.endTag = endTag;
        }

        public PatternGetUrl() {
        }

        public Pattern getPattern() {
            return pattern;
        }

        public void setPattern(Pattern pattern) {
            this.pattern = pattern;
        }

        public String getEndTag() {
            return endTag;
        }

        public void setEndTag(String endTag) {
            this.endTag = endTag;
        }
    }
}
