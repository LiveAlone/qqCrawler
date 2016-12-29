package org.qingqing.crawler.demo.crawler;

import com.google.common.collect.Lists;
import org.qingqing.crawler.demo.crawler.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by yaoqijun on 2016/12/29.
 */
@Component
public class PagedContentAnalyse {

    private static final Logger logger = LoggerFactory.getLogger(PagedContentAnalyse.class);

    private final MyConfiguration myConfiguration;

    private final Pattern lastPageCount = Pattern.compile("前\\d{0,3}页</a>");

    private final Pattern pagedUrlPattern = Pattern.compile("<a href=\".{0,100}\" target=\"_blank\" class=\"d\">点击下载</a></span>");

    @Autowired
    public PagedContentAnalyse(MyConfiguration myConfiguration){
        this.myConfiguration = myConfiguration;
    }

    public Integer analyseTotalPage(String content){
        String find = StringUtil.findFirst(content, lastPageCount);
        if (find == null){
            logger.error("page url analyse total page count error");
            return null;
        }
        return Integer.valueOf(find.substring(1, find.indexOf("页")));
    }

    public List<String> analysePagedFileUrl(String content){
        List<String> originUrl = StringUtil.findAll(content, pagedUrlPattern);
        List<String> result = Lists.newArrayList();
        for (String s : originUrl) {
            result.add(s.substring(s.indexOf("href")+6, s.indexOf("\" target")));
        }
        return result;
    }
}
