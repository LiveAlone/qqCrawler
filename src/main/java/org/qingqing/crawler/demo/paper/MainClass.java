package org.qingqing.crawler.demo.paper;

import com.google.common.collect.Lists;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.poi.util.IOUtils;
import org.qingqing.crawler.demo.crawler.CookiesManager;
import org.qingqing.crawler.demo.crawler.utils.HttpHeaderContants;
import org.qingqing.crawler.demo.crawler.utils.StringUtil;

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

    // http client proxy content
    private static HttpHost proxy = new HttpHost("119.29.60.138", 1081, "http");

    private static DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);

    private static String loginUrl = "http://sso.zxxk.com/login";

    private static String loginUrls = "https://sso.zxxk.com/login";

    private static String resourceUrl =  "http://download.zxxk.com//?UrlID=29&InfoID=5920553";

    private static CookiesManager cookiesManager;

    private static LoginRequestForm loginRequestForm;

    private static String afterLoginUrl;

//    public static void main(String[] args) throws Exception {
//        System.out.println("test out condition");
//        preLogin();
//        login();
//        afterLoginContent1();
////        afterLoginContent2();
////        getResource();
//        System.out.println(cookiesManager.toString());
//    }

    private static void preLogin() throws Exception{
        HttpGet httpGet = new HttpGet(loginUrl);
        HttpClient httpClient = HttpClients.custom().setRoutePlanner(routePlanner).build();
        HttpResponse response = httpClient.execute(httpGet);

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
        HttpPost httpPost = new HttpPost(loginUrls);
        cookiesManager.setAttribute(HttpHeaderContants.SESSION, "7b404393-23be-4a34-ae10-15d619870877");
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
//        afterLoginUrl = "http://gw.open.zxxk.com/router?$method=xk.user.callback&curl=http://user.zxxk.com/default.aspx&ticket=ST-4929-LzjLfVwgxJMHhzE7RAFh-sso3.zxxk.com";
        HttpGet httpGet = new HttpGet(afterLoginUrl);
        httpGet.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding","gzip, deflate");
        httpGet.setHeader("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        httpGet.setHeader("Connection","keep-alive");
        httpGet.setHeader("Host","gw.open.zxxk.com");
        httpGet.setHeader("Upgrade-Insecure-Requests","1");
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:50.0) Gecko/20100101 Firefox/50.0");
//        httpGet.setHeader(HttpHeaderContants.Cookie, cookiesManager.formatCookies());
        HttpResponse response = HttpClients.custom().setRedirectStrategy(new DefaultRedirectStrategy(){
            @Override
            public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
                boolean isRedirect=false;
                try {
                    isRedirect = super.isRedirected(request, response, context);
                } catch (ProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (!isRedirect) {
                    int responseCode = response.getStatusLine().getStatusCode();
                    if (responseCode == 301 || responseCode == 302) {
                        return true;
                    }
                }
                return isRedirect;
            }
        }).build().execute(httpGet);
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

        private static String randomPatternString = "name='myRandom']\"\\)\\.val\\('.{0,100}'\\);}";
//
        private static Pattern randomPattern = Pattern.compile(randomPatternString);

        private static String myRandom;

        private String username;

        private String password;

        public HttpEntity formEntity(){
            List<NameValuePair> formParams = Lists.newArrayList();
            formParams.add(new BasicNameValuePair("_eventId", eventId));
            formParams.add(new BasicNameValuePair("execution", execution));
            formParams.add(new BasicNameValuePair("lt", lt));
            formParams.add(new BasicNameValuePair("username", username));
            formParams.add(new BasicNameValuePair("password", password));
            formParams.add(new BasicNameValuePair("rememberMe", "true"));
            formParams.add(new BasicNameValuePair("myRandom", myRandom));
            return new UrlEncodedFormEntity(formParams, Consts.UTF_8);
        }

        public void analyseSource(String source){
            this.eventId = StringUtil.gainInputValue(StringUtil.findFirst(source, eventIdPattern));
            this.execution = StringUtil.gainInputValue(StringUtil.findFirst(source, executionPattern));
            this.lt = StringUtil.gainInputValue(StringUtil.findFirst(source, ltPattern));
            this.myRandom = StringUtil.getScriptValContent(StringUtil.findFirst(source, randomPattern));

        }

        public static String getMyRandom() {
            return myRandom;
        }

        public static void setMyRandom(String myRandom) {
            LoginRequestForm.myRandom = myRandom;
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
