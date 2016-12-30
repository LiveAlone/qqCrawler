package org.qingqing.crawler.demo;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.qingqing.crawler.demo.crawler.FileCrawlerManager;
import org.qingqing.crawler.demo.crawler.MyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by yaoqijun.
 * Date:2016-04-27
 * Email:yaoqj@terminus.io
 * Descirbe:
 */
@Component
@Slf4j
public class RunCommanLine implements CommandLineRunner{

    private static final Logger logger = LoggerFactory.getLogger(RunCommanLine.class);

    @Autowired
    private FileCrawlerManager fileCrawlerManager;

    @Autowired
    private MyConfiguration myConfiguration;

    public void run(String... args) throws Exception {
        logger.info("current version is : {} " + myConfiguration.getVersion());
        if (args.length != 1){
            logger.error("input args not found, please input args");
            return;
        }
        if (args[0].equals(MyConfiguration.ERROR_RELOAD)){
            logger.info("start error reload content");
            fileCrawlerManager.loadErrorFile();
            logger.info("end error reload content");
        }

        if (args[0].equals(MyConfiguration.TOTAL_COMMAND)){
            logger.info("start total file load");
            fileCrawlerManager.loadTotalPagedFile();
            logger.info("end total file load");
        }
        System.out.println(args[0]);

    }
}
