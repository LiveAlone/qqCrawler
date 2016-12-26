package org.qingqing.crawler.demo.paper;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Created by yaoqijun.
 * Date:2016-12-24
 * Email:yaoqijunmail@gmail.io
 * Descirbe: 管理 Cookies 配置方式
 */
public class CookiesManager {

    private static final Logger logger = LoggerFactory.getLogger(CookiesManager.class);

    private Map<String, String> cookies = new HashMap<String, String>();

    private Set<String> tags = Sets.newHashSet();

    public void mergeSessionContent(String cookiesString){
        for (String s : cookiesString.split(";")) {
            if (s.contains("=")){
                String[] pair = s.split("=");
                if (pair.length != 2){
                    logger.warn("cookies not equals 2, s:{}", s);
                }
                cookies.put(pair[0], pair[1]);
            }else {
                tags.add(s);
            }
        }
    }

    public String getAttribute(String key){
        return cookies.get(key);
    }

    public void setAttribute(String key, String value){
        cookies.put(key, value);
    }

    public String gainSessionString(){
        return HttpHeaderContants.SESSION + "=" + cookies.get(HttpHeaderContants.SESSION);
    }

    public String formatCookies(){
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> cookie : cookies.entrySet()) {
            sb.append(cookie.getKey()).append("=").append(cookie.getValue()).append("; ");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(" cookies:{ ");
        for (Entry<String, String> bi : cookies.entrySet()) {
            sb.append("  key : ").append(bi.getKey()).append(" value: ").append(bi.getValue()).append(" \n");
        }
        sb.append(" } ");

        sb.append(" tags:{ ");
        for (String s: tags) {
            sb.append("  ").append(s).append("  ");
        }
        sb.append(" }");
        return sb.toString();
    }
}
