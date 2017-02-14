package org.qingqing.crawler.demo.crawler.domain;

import org.qingqing.crawler.demo.crawler.MyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by yaoqijun on 2016/12/29.
 */
public class PagedUrl {

    /**
     * right url fuck ...............
     * http://search.zxxk.com/Search1.aspx?keyword=&typeid=1&provinceid=4&year=2017&isfree=1&orderby=score&pagesize=10&isprecise=0&onlytitle=1&dontsave=0&SelectTypeID=3
     */

    private static final Logger logger = LoggerFactory.getLogger(PagedUrl.class);

    private static final String isFreeTag = "&isfree=1";

    private static final String isMoneyTag = "&ismoney=1";

    private static final String ispoint = "&ispoint=1";

    private static final String issupply = "&issupply=1";

    private static final String provinceTag = "&provinceid=";

    private static final String yearTag = "&year=";

    private static final String cateTag = "&cateid=";

    private static final String unknow = "-1";

    private static final String basicUrl = "http://search.zxxk.com/Search1.aspx?keyword=&typeid=1&orderby=score&pagesize=10&isprecise=0&onlytitle=1&dontsave=0&SelectTypeID=3";

    private static final String pagedTag = "&page=";

    private String province;

    private String year;

    private String charge;

    private String cate;

    private String pagedUrl;

    public PagedUrl() {
    }

    public PagedUrl(String province, String year, String charge, String cate, String pagedUrl) {
        this.province = province;
        this.year = year;
        this.charge = charge;
        this.cate = cate;
        this.pagedUrl = pagedUrl;
    }

    public static String appendPaged(String basicUrl, Integer pageNum){
        return basicUrl + pagedTag + pageNum;
    }

    public static PagedUrl convertPagedUrl(String targetPage){
        String[] config = targetPage.split(",");
        if (config.length !=4){
            logger.error("target page config error, auto ignore, targetPage:{}", targetPage);
            return null;
        }

        StringBuilder sb = new StringBuilder(basicUrl);
        String configProvince = config[0];
        if (!configProvince.equals(unknow)){
            sb.append(provinceTag).append(configProvince);
        }

        String configYear = config[1];
        if (!config.equals(unknow)){
            sb.append(yearTag).append(configYear);
        }

        String isFreeConfig = config[2];
        if (isFreeConfig.equals("0")){
            sb.append(isFreeTag);
        }else if (isFreeConfig.equals("1")){
            sb.append(isMoneyTag);
        }else if (isFreeConfig.equals("2")){
            sb.append(ispoint);
        }else if (isFreeConfig.equals("3")){
            sb.append(issupply);
        }else {
            logger.error("target page config error, charge filed error, auto ignore, targetPage:{}", targetPage);
            return null;
        }

        String cateConfig = config[3];
        if (!cateConfig.equals(unknow)){
            sb.append(cateTag).append(cateConfig);
        }
        String result = sb.toString();
        logger.info("convert paged url finish,  paged url is :{}", result);
        return new PagedUrl(configProvince, configYear, isFreeConfig, cateConfig, result);
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPagedUrl() {
        return pagedUrl;
    }

    public void setPagedUrl(String pagedUrl) {
        this.pagedUrl = pagedUrl;
    }

    public String formatPathUrl(String fileName, MyConfiguration myConfiguration){
        String provinceString = myConfiguration.getCityMap().get(getProvince());
        String cateString = myConfiguration.getCateMap().get(getCate());
        return new StringBuilder(provinceString == null ? "unknow" : provinceString)
                .append("/").append(year)
                .append("/").append(charge.equals("0") ? "免费" : "收费")
                .append("/").append(cateString == null ? "unknow" : cateString )
                .append("/").append(fileName)
                .toString();
    }

    private String chargeToString(String charge){
        if (charge.equals("0")){
            return "免费";
        }
        if (charge.equals("1")){
            return "储值";
        }
        if(charge.equals("2")){
            return "点数";
        }
        if (charge.equals("3")){
            return "特供";
        }
        return "unknown";
    }

    public static class FileLoadResult{

        private Boolean isSuccess;

        private String path;

        private String filename;

        public FileLoadResult(Boolean isSuccess, String path, String filename) {
            this.isSuccess = isSuccess;
            this.path = path;
            this.filename = filename;
        }

        public static FileLoadResult from(String content){
            String[] contents = content.split(" ");
            if (contents.length != 3){
                logger.error("load log string convert error, content:{}", content);
                throw new IllegalArgumentException("load data convert error");
            }
            return new FileLoadResult(contents[0].equals("SUCCESS"), contents[1], contents[2]);
        }

        public FileLoadResult() {
        }

        @Override
        public String toString() {
            return (isSuccess?"SUCCESS ":"ERROR ") + path + " " + filename;
        }

        public Boolean getSuccess() {
            return isSuccess;
        }

        public void setSuccess(Boolean success) {
            isSuccess = success;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }
    }
}
