package org.qingqing.crawler.demo.single;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.poi.util.IOUtils;
import org.apache.tools.ant.taskdefs.EchoXML;
import org.qingqing.crawler.demo.paper.CookiesManager;
import org.qingqing.crawler.demo.paper.HttpHeaderContants;
import org.qingqing.crawler.demo.paper.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * Created by yaoqijun on 2016/12/26.
 */
public class SingleMainTest {

    private static String xpPassword = "";

    private static String basicUrl = "http://search.zxxk.com/Search1.aspx?keyword=&typeid=1&provinceid=4&year=2016&isfree=1&orderby=score&pagesize=10&isprecise=0&onlytitle=1&dontsave=0&SelectTypeID=3&page=";

    private static Integer totalPage = 30;

    private static String logFile = "loadLog.txt";

    private static String basicDir = "free/";

    private static Integer page = 0;

    private static List<DefaultProxyRoutePlanner> planners = Lists.newArrayList();

    static {
        planners.add(new DefaultProxyRoutePlanner(new HttpHost("119.29.60.138", 1081)));
        planners.add(new DefaultProxyRoutePlanner(new HttpHost("119.29.4.212", 1081)));
        planners.add(new DefaultProxyRoutePlanner(new HttpHost("115.159.185.221", 1081)));
        planners.add(new DefaultProxyRoutePlanner(new HttpHost("123.206.52.180", 1081)));
        planners.add(new DefaultProxyRoutePlanner(new HttpHost("123.206.52.123", 1081)));
        planners.add(new DefaultProxyRoutePlanner(new HttpHost("115.159.188.177", 1081)));
    }

    public static void main(String[] args) throws Exception{
        FileWriter fileWriter = new FileWriter(new File(logFile), true);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("target.txt")));

        while (true){
            String current = bufferedReader.readLine();
            if (current == null){
                break;
            }

            Boolean status = false;
            String basicUrl = "";
            if (current.contains("current")){
                page = Integer.valueOf(current.substring(current.length()-2, current.length()));
                if ( page != 16)
                    break;
                fileWriter.append("current page is " + page + "\n");
                continue;
            }else {
                if (current.contains("ERROR")){
                    status=false;
                    basicUrl = current.split(" ")[1];
                }else {
                    status = true;
                    basicUrl = current.split(" ")[1];
                }
            }

            if (status){
                fileWriter.append("JUMP " + basicUrl + "\n");
                continue;
            }

            String basicUrlConvert = convertUrl(basicUrl);
            try {
                getResource(analyseDownloadPageContent(getPageContent(basicUrlConvert)));
                fileWriter.append("SUCCESS " + basicUrlConvert + "\n");
                Thread.sleep(10000);
            }catch (Exception e){
                fileWriter.append("ERROR "+ basicUrlConvert + "\n");
            }

        }

        fileWriter.close();
        bufferedReader.close();
    }

    public static void originalLoadContent() throws Exception{
//        FileWriter fileWriter = new FileWriter(new File(logFile), true);
//        for (int i = 1; i<=30; i++){
//
//            String realUrl = basicUrl + i;
//            page = i;
//            List<String> result = analysePagedAnalyseUrl(getPageContent(realUrl));
//            fileWriter.append("current page is " + page + "\n");
//            for (String s : result) {
//                try {
//                    getResource(analyseDownloadPageContent(getPageContent(s)));
//                    fileWriter.append("SUCCESS " + s + "\n");
//                    Thread.sleep(5000);
//                }catch (Exception e){
//                    fileWriter.append("ERROR "+ s + "\n");
//                }
//            }
//        }
//        fileWriter.close();
//        System.out.println("测试环境的配置方式");
    }

    private static String convertUrl(String originUrl){
        if (originUrl.contains("http://sx.zxxk.com/soft/")
                || originUrl.contains("http://sw.zxxk.com/soft/")
                || originUrl.contains("http://yy.zxxk.com/soft/")
                || originUrl.contains("http://wl.zxxk.com/soft/")
                || originUrl.contains("http://dl.zxxk.com/soft/")
                || originUrl.contains("http://zz.zxxk.com/soft/")
                || originUrl.contains("http://hx.zxxk.com/soft/")
                || originUrl.contains("http://ls.zxxk.com/soft/")){
            return originUrl.replaceFirst("soft/", "s");
        }

        return null;
    }

    private static void getResource(String resourceUrl) throws Exception{
        HttpGet httpGet = new HttpGet(resourceUrl);
        CookiesManager cookiesManager = new CookiesManager();
        // first must content
        cookiesManager.setAttribute("xk.passport","894AB9750164045D7CC481DD7AE294D403083A93CDFDA5ECA9C68DFCF17810B3AC30155AB2E8BF286B650E48766587316C249640F019AA19CF51A7AC7875CA53D66C2D496A66F2E1C1C30F558B5A94E5D0A6520BF2BE6C0E7744BE0863132C486119844793275C881B1D32CDDFB3F975E1D1DD3EE76BABC79E7262ADE467175A72812C4D003A83C6A523660B17B19AA910A3967EEA62A0C81BBEE238D26818B45288D1B214CD70B14203D0E75148B2F2957B882592750120BE26F640475009EF21C601C557D2AE413195E5E56C104470D8215C0AA4E135D5EEDFB843CAEABA884E058300060D6868B22DA5A445470B7A0ECD18AA2B8A797348443DE15592D710906D6D6A052BBF1386AB36ADDED1C73CC6DD95C55E4754644DF8E52611FF4E0EE60E69FBC7BDB7D46AC1BC26AB804B7E31A3D2B623149606A5B44CB25E7251A516CFAA1C7381EB4103F37CC5579F7915FE46CD24AD07F0E4B607A68C72DFA060EAB9A663DD05B32FCADD4031F12709122343C62397176A4B58A75DE11EEF35C5972B2BA7");
//        cookiesManager.setAttribute("xk.passport", "0088908718045B5321723848923CE0254AC2B9B5A4B9EC7D309F08F23524C04266304FC202BC66811D3626B539FC39B954897AE8555DAC4C699F3FE0EA5174EFF45B51A3188F62DB014E29B491DA187CE19072C9DBF73ABDD040E156AFB931487211565C6D1B1F859ACAF663A94291FD785C566A08D787852A076DE3310D8BDD1E05D963768ADB6FBF6FB32ADE9D3D3CDE279EC7E7252B4D248755321A0927B7DA0835EADE76C721E81819C67D1DE2108494C906E1217E49A6E2BF9B378C38B5ED42BF07CB16584F39892228E273806911EEC1FEE6732CB28B53AE6A08CD8F98A5DB5EA65CC4397EEBBC2D9D087A6D30C09E6E9376441623266B91393FCC5A13AC2D4FC57158E397F1A1F856FD1223B3DC927F9E98C1DF79B95F5D51737B91D5E60CB2F026E053A1E21BF9AE5536699A0245B17319EC163829EA017DA350AAA91C3F08A2E6530BF797004D940CFB208AD6B8C4291D305D8EF9F778360DF7D8402288AFEE042FF1E86D91639764AAB6B5809F1C62345FC0C789554160EEDD06DEE63CCDFB");
//        cookiesManager.setAttribute("Hm_lvt_0e522924b4bbb2ce3f663e505b2f1f9c","1482582411");
//        cookiesManager.setAttribute("Hm_lpvt_0e522924b4bbb2ce3f663e505b2f1f9c","1482585339");
//        cookiesManager.setAttribute("xk.passport.uid","25283132");
//        cookiesManager.setAttribute("ASP.NET_SessionId", "fc0lsr4ybsmh4smv4mgj2x33");

        httpGet.setHeader(HttpHeaderContants.Cookie,cookiesManager.formatCookies());
        HttpResponse response = createRandomClient().execute(httpGet);
        System.out.println(response.getStatusLine());

        String filenameSource = StringUtil.gainHeaderFileName(response.getHeaders(HttpHeaderContants.Content_Disposition)[0].getValue());
        String filename = URLDecoder.decode(filenameSource);

        System.out.println(filename);
        File file = new File(basicDir + page + "/" + filename);
        Files.createParentDirs(file);

        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(IOUtils.toByteArray(response.getEntity().getContent()));

        outputStream.close();
    }

    static List<Pattern> relPattern = Lists.newArrayList(
            Pattern.compile("<a data-xop-login id=\"goDownload\" href=\".{0,100}\" rel=\"nofollow\""),
            Pattern.compile("<a data-xop-login class=\"down-btn\" target=\"_blank\" href=\".{0,100}\" rel=\"nofollow\""),
            Pattern.compile("<a data-xop-login href=\".{0,100}\" rel=\"nofollow\" id=\"goDownload\" data-download"));

    static List<Pattern> dataDownload = Lists.newArrayList(
            Pattern.compile("<a data-xop-login id=\"goDownload\" rel=\"nofollow\" href=\".{0,100}\" data-download"),
            Pattern.compile("<a data-xop-login id=\"goDownload\" href=\".{0,100}\" data-download"));

    static List<Pattern> idStart = Lists.newArrayList(
            Pattern.compile("<a data-xop-login href=\".{0,100}\" id=\""));

    private static String analyseDownloadPageContent(String content) throws Exception{

        String s = StringUtil.findFirst(content, relPattern);
        if (s!=null){
            return s.substring(s.indexOf("href")+6, s.indexOf("\" rel=\""));
        }

        s = StringUtil.findFirst(content, dataDownload);
        if (s!=null){
            return s.substring(s.indexOf("href")+6, s.indexOf("\" data-download"));
        }

        s = StringUtil.findFirst(content, idStart);
        if (s!=null){
            return s.substring(s.indexOf("href")+6, s.indexOf("\" id=\""));
        }

        System.out.println("not find page content");
        return "";
    }

    private static List<String> analysePagedAnalyseUrl(String content){
        String regular = "<a href=\".{0,100}\" target=\"_blank\" class=\"d\">点击下载</a></span>";
        List<String> originUrl = StringUtil.findAll(content, Pattern.compile(regular));
        List<String> result = Lists.newArrayList();
        for (String s : originUrl) {
            result.add(s.substring(s.indexOf("href")+6, s.indexOf("\" target")));
        }
        return result;
    }

    private static String getPageContent(String url) throws Exception{
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = createRandomClient().execute(httpGet);
        return new String(IOUtils.toByteArray(response.getEntity().getContent()));
    }

    private static HttpClient createRandomClient(){
        Integer result = ThreadLocalRandom.current().nextInt(6);
        return HttpClients.custom().setRoutePlanner(planners.get(result)).build();
    }
}
