package org.qingqing.crawler.demo.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by yaoqijun on 2016/12/29.
 */
@Component
@ConfigurationProperties(prefix="qingqing")
public class MyConfiguration {

    public static final String TOTAL_COMMAND = "total";

    public static final String ERROR_RELOAD = "error_reload";

    public static final String FILE_LOAD_URL = "LoadFileUrl.txt";

    public static final String FILE_LOAD_LOG = "LoadLog.txt";

    private String version;

    private List<String> proxy;

    private List<String> xp;

    private List<String> regular;

    private List<String> targetPage;

    private Map<String, String> cityMap;

    private Map<String, String> cateMap;

    public List<String> getProxy() {
        return proxy;
    }

    public void setProxy(List<String> proxy) {
        this.proxy = proxy;
    }

    public List<String> getXp() {
        return xp;
    }

    public void setXp(List<String> xp) {
        this.xp = xp;
    }

    public List<String> getRegular() {
        return regular;
    }

    public void setRegular(List<String> regular) {
        this.regular = regular;
    }

    public List<String> getTargetPage() {
        return targetPage;
    }

    public void setTargetPage(List<String> targetPage) {
        this.targetPage = targetPage;
    }

    public Map<String, String> getCityMap() {
        return cityMap;
    }

    public void setCityMap(Map<String, String> cityMap) {
        this.cityMap = cityMap;
    }

    public Map<String, String> getCateMap() {
        return cateMap;
    }

    public void setCateMap(Map<String, String> cateMap) {
        this.cateMap = cateMap;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
