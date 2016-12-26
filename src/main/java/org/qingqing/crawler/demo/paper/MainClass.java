package org.qingqing.crawler.demo.paper;

import com.google.common.collect.Lists;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.poi.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by yaoqijun.
 * Date:2016-12-24
 * Email:yaoqijunmail@gmail.io
 * Descirbe:
 */
public class MainClass {

    private static String loginUrl = "https://sso.zxxk.com/login";

    private static String resourceUrl =  "http://download.zxxk.com/?UrlID=29&InfoID=5913085";

    private static CookiesManager cookiesManager;

    private static LoginRequestForm loginRequestForm;

    private static String afterLoginUrl;

    public static void main(String[] args) throws Exception {
        System.out.println("test out condition");
//        preLogin();
//        login();
        afterLoginUrl = "http://gw.open.zxxk.com/router?$method=xk.user.callback&curl=http://user.zxxk.com/default.aspx&ticket=ST-617713-vAoVWAaQ6gERSzMZTlqh-sso4.zxxk.com";
        afterLoginContent1();
//        afterLoginContent2();
//        getResource();
        System.out.println(cookiesManager.toString());

//        cookiesManager = new CookiesManager();
        // first must content
//        cookiesManager.setAttribute("xk.passport", "D15428460DE8C686B14839AADCC62280F5EEB88960FFB887FBABECFF489EBF0F3C4F9D232EA7220C383F9E453C009148FD939A423D22E38B4DAC0183E3947BCF722503B325D91D651BE8B20B523C2396B5D601421C1B1A102BCB9BA59AE6ABBCFD40FB67B44A4518266FFA1EBE84FAE1FC638B284CD93ADA7086EB8D5FA3811099D4A7734F51677EB8EB5D0BAE8CC4CC96F86376DC1084D844BCC532F1059F323847892B5AC010401CCD7AE2FBFA2DA267AD260161791B4392033A1F5FC9BFE52583DC7C63DD7D3943A56A7A2F981933123EAB36D678AA9BE5C3B83DE10DF43038298B821990A5B610B3BAE4798EC965750C92465BB457F1D1A6DBC3BECC803919091461F23CDC4F3EF047F1A2DC6F521663C4B32CEB3152F4F97A13894A89EEDE5DC899186C97B83AA6F893F5420341FB064B40904AB6DDA985E3A80D866DC122D5598307994D49CA9A64D1A3F71FAB38421EDA7FED07AC3E0982871D68BD2493CF745B833B2277099F67306407746CF8216859D081E53EA56312D5609BE5F69FA8BF38");
//        cookiesManager.setAttribute("Hm_lvt_0e522924b4bbb2ce3f663e505b2f1f9c","1482582411");
//        cookiesManager.setAttribute("Hm_lpvt_0e522924b4bbb2ce3f663e505b2f1f9c","1482585339");
//        cookiesManager.setAttribute("xk.passport.uid","25283132");
//        cookiesManager.setAttribute("ASP.NET_SessionId", "fc0lsr4ybsmh4smv4mgj2x33");

//        getResource();

    }

    private static void preLogin() throws Exception{
        HttpGet httpGet = new HttpGet(loginUrl);
        HttpResponse response = HttpClients.createDefault().execute(httpGet);

        //add cookies
        cookiesManager = new CookiesManager();
        for (Header header : response.getHeaders(HttpHeaderContants.Set_Cookie)) {
            cookiesManager.mergeSessionContent(header.getValue());
        }

        // analyser content
        loginRequestForm = new LoginRequestForm();
        String httpSourceContent = new String(IOUtils.toByteArray(response.getEntity().getContent()));
        loginRequestForm.analyseSource(httpSourceContent);
    }

    // user login save session cookies
    private static void login() throws Exception {
        HttpPost httpPost = new HttpPost(loginUrl);

        httpPost.setHeader(new BasicHeader(HttpHeaderContants.Cookie, cookiesManager.gainSessionString()));

        // add entity
        loginRequestForm.setUsername("18930185695");
        loginRequestForm.setPassword("qingqing12");
        httpPost.setEntity(loginRequestForm.formEntity());

        HttpResponse response = HttpClients.createDefault().execute(httpPost);
        for (Header header : response.getHeaders(HttpHeaderContants.Set_Cookie)) {
            cookiesManager.mergeSessionContent(header.getValue());
        }

        afterLoginUrl = response.getHeaders(HttpHeaderContants.Location)[0].getValue();
        System.out.println(afterLoginUrl);
    }

    public static void afterLoginContent1() throws Exception {
        HttpGet httpGet = new HttpGet(afterLoginUrl);
        httpGet.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding","gzip, deflate");
        httpGet.setHeader("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        httpGet.setHeader("Connection","keep-alive");
        httpGet.setHeader("Host","gw.open.zxxk.com");
        httpGet.setHeader("Upgrade-Insecure-Requests","1");
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:50.0) Gecko/20100101 Firefox/50.0");
//        httpGet.setHeader(HttpHeaderContants.Cookie, cookiesManager.formatCookies());
        HttpResponse response = HttpClients.createDefault().execute(httpGet);
        for (Header header : response.getHeaders(HttpHeaderContants.Set_Cookie)) {
            cookiesManager.mergeSessionContent(header.getValue());
        }
        afterLoginUrl = response.getHeaders(HttpHeaderContants.Location)[0].getValue();
        System.out.println(afterLoginUrl);
    }

    public static void afterLoginContent2() throws Exception {
        HttpGet httpGet = new HttpGet(afterLoginUrl);
        HttpResponse response = HttpClients.createDefault().execute(httpGet);
        for (Header header : response.getHeaders(HttpHeaderContants.Set_Cookie)) {
            cookiesManager.mergeSessionContent(header.getValue());
        }
        System.out.println(response.getStatusLine());
        System.out.println(new String(IOUtils.toByteArray(response.getEntity().getContent())));
    }

    private static void getResource() throws Exception{
        HttpGet httpGet = new HttpGet(resourceUrl);
        httpGet.addHeader(HttpHeaderContants.Cookie, cookiesManager.formatCookies());
        HttpResponse response = HttpClients.createDefault().execute(httpGet);
        System.out.println(response.getStatusLine());

        String filenameSource = StringUtil.gainHeaderFileName(response.getHeaders(HttpHeaderContants.Content_Disposition)[0].getValue());

        String filename = URLDecoder.decode(filenameSource);

        System.out.println(filename);
        File file = new File(filename);

        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(IOUtils.toByteArray(response.getEntity().getContent()));

        outputStream.close();

        System.out.println("finish content");
    }

    private static class LoginRequestForm{

        private String eventId;

        private static String eventIdPatternString = "<input type=\"hidden\" name=\"_eventId\" value=\".{0,100}\" />";

        private static Pattern eventIdPattern = Pattern.compile(eventIdPatternString);

        private String execution;

        private static String executionPatternString = "<input type=\"hidden\" name=\"execution\" value=\".*\" />";

        private static Pattern executionPattern = Pattern.compile(executionPatternString);

        private String lt;

        private static String ltPatternString = "<input type=\"hidden\" name=\"lt\" value=\".{0,100}\" />";

        private static Pattern ltPattern = Pattern.compile(ltPatternString);

        private String username;

        private String password;

        public HttpEntity formEntity(){
            List<NameValuePair> formParams = Lists.newArrayList();
            formParams.add(new BasicNameValuePair("_eventId", eventId));
            formParams.add(new BasicNameValuePair("execution", execution));
            formParams.add(new BasicNameValuePair("lt", lt));
            formParams.add(new BasicNameValuePair("username", username));
            formParams.add(new BasicNameValuePair("password", password));
            return new UrlEncodedFormEntity(formParams, Consts.UTF_8);
        }

        public void analyseSource(String source){
            this.eventId = StringUtil.gainInputValue(StringUtil.findFirst(source, eventIdPattern));
            this.execution = StringUtil.gainInputValue(StringUtil.findFirst(source, executionPattern));
            this.lt = StringUtil.gainInputValue(StringUtil.findFirst(source, ltPattern));
        }

        public String getEventId() {
            return eventId;
        }

        public void setEventId(String eventId) {
            this.eventId = eventId;
        }

        public String getExecution() {
            return execution;
        }

        public void setExecution(String execution) {
            this.execution = execution;
        }

        public String getLt() {
            return lt;
        }

        public void setLt(String lt) {
            this.lt = lt;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
