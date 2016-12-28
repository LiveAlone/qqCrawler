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

//    private static String basicUrl = "http://search.zxxk.com/Search1.aspx?keyword=&typeid=1&provinceid=4&year=2016&isfree=1&orderby=score&pagesize=10&isprecise=0&onlytitle=1&dontsave=0&SelectTypeID=3&page=";

    private static String basicUrl =   "http://search.zxxk.com/Search1.aspx?keyword=&typeid=1&provinceid=4&year=2017&ismoney=1&orderby=score&pagesize=10&isprecise=0&onlytitle=1&dontsave=0&SelectTypeID=3&page=";

    private static Integer totalPage = 30;

    private static String logFile = "loadLog.txt";

    private static String basicDir = "money/";

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
//        originalLoadContent();
        targetFileLoad();
    }

    public static void originalLoadContent() throws Exception{
        FileWriter fileWriter = new FileWriter(new File(logFile), true);
        for (int i = 1; i<=6; i++){

            String realUrl = basicUrl + i;
            page = i;
            List<String> result = analysePagedAnalyseUrl(getPageContent(realUrl));
            fileWriter.append("current page is " + page + "\n");
            for (String s : result) {
                try {
                    String basicUrlConvert = convertUrl(s);
                    getResource(analyseDownloadPageContent(getPageContent(basicUrlConvert)));
                    fileWriter.append("SUCCESS " + s + "\n");
                    fileWriter.flush();
                    Thread.sleep(5000);
                }catch (Exception e){
                    fileWriter.append("ERROR "+ s + "\n");
                    fileWriter.flush();
                }
            }
        }
        fileWriter.close();
    }

    private static void targetFileLoad() throws Exception{
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
                page = Integer.valueOf(current.substring(current.length()-1, current.length()));
                if (page !=2 && page!=3 && page!=4 && page!=5 && page!=6)
                    break;
                fileWriter.append("current page is " + page + "\n");
                fileWriter.flush();
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
                fileWriter.flush();
                continue;
            }

            String basicUrlConvert = convertUrl(basicUrl);
            try {
                getResource(analyseDownloadPageContent(getPageContent(basicUrlConvert)));
                fileWriter.append("SUCCESS " + basicUrlConvert + "\n");
                Thread.sleep(10000);
            }catch (Exception e){
                fileWriter.append("ERROR "+ basicUrlConvert + "\n");
                throw e;
            }
            fileWriter.flush();
        }

        fileWriter.close();
        bufferedReader.close();
    }

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

    private static void getResource(String resourceUrl) throws Exception{
        HttpGet httpGet = new HttpGet(resourceUrl);
        CookiesManager cookiesManager = new CookiesManager();
        // first must content
        cookiesManager.setAttribute("xk.passport","EB821A6BA4518CC0549155B88998CBD3DEB96BD0A464D8ACADE3F70F30544D9D70961E019962FA17E4B32B474C6E9A8F83B978ADA2E65C1C1326FAE5AC7B8390A3C3A0C1B70CBDE0AD98BF6FE4C60467FF67DF3AB30FEFDF60D992C9252E41C63983A1EDE32AD6EA3C264B92AFCAD4D16BD79E64CA3CBEAE2041785083C727D97D5C10D6E5417C404B8508AA91B98835D5EE948832FD956D2FEC0F8208C35513F04E26034187C0F2507B80D0B94D2632AAC07F094768A068E9C0B9FBD31E5A73BD29290EE266530E727FBE8C86FA2F42FB607072C123E5F0C8DD921F7E40D623BE185F10DAA4EA4FC1EE29F9C0FC598F482037BA83E2AD0D09F6D23C6850D93C584DBE7DD724E3C9EC5500CB52FB35CC870D67652A942526750882B59474D85092D988537C6CAEC7DB89AB10FCEABDDB7A202E984B5A397EEE165F5E7D86844AE5EC8EBB201208C9FA33D1D09B745848C7F57A125EE5D9CF594CE3AC3E6DC38FCB7297616D4B5AE4EDAEE9C8B8DD6928DF8EC8FE27B844CA2033C0539E727F0E7B649A08");
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
        filename = filename.replace(":", "-");
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
