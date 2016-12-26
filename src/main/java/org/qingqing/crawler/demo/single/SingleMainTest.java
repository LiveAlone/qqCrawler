package org.qingqing.crawler.demo.single;

import com.google.common.collect.Lists;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.poi.util.IOUtils;
import org.qingqing.crawler.demo.paper.CookiesManager;
import org.qingqing.crawler.demo.paper.HttpHeaderContants;
import org.qingqing.crawler.demo.paper.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by yaoqijun on 2016/12/26.
 */
public class SingleMainTest {

    private static String xpPassword = "";

    private static String basicUrl = "http://search.zxxk.com/Search1.aspx?keyword=&typeid=1&year=2017&isfree=1&orderby=score&pagesize=10&isprecise=0&onlytitle=1&dontsave=0&SelectTypeID=3";

    private static Integer totalPage = 30;

    public static void main(String[] args) throws Exception{
        List<String> result = analysePagedAnalyseUrl(getPageContent(basicUrl));
        for (String s : result) {
            getResource(analyseDownloadPageContent(getPageContent(s)));
        }
        System.out.println("测试环境的配置方式");
    }

    private static void getResource(String resourceUrl) throws Exception{
        HttpGet httpGet = new HttpGet(resourceUrl);
        CookiesManager cookiesManager = new CookiesManager();
        // first must content
        cookiesManager.setAttribute("xk.passport", "0088908718045B5321723848923CE0254AC2B9B5A4B9EC7D309F08F23524C04266304FC202BC66811D3626B539FC39B954897AE8555DAC4C699F3FE0EA5174EFF45B51A3188F62DB014E29B491DA187CE19072C9DBF73ABDD040E156AFB931487211565C6D1B1F859ACAF663A94291FD785C566A08D787852A076DE3310D8BDD1E05D963768ADB6FBF6FB32ADE9D3D3CDE279EC7E7252B4D248755321A0927B7DA0835EADE76C721E81819C67D1DE2108494C906E1217E49A6E2BF9B378C38B5ED42BF07CB16584F39892228E273806911EEC1FEE6732CB28B53AE6A08CD8F98A5DB5EA65CC4397EEBBC2D9D087A6D30C09E6E9376441623266B91393FCC5A13AC2D4FC57158E397F1A1F856FD1223B3DC927F9E98C1DF79B95F5D51737B91D5E60CB2F026E053A1E21BF9AE5536699A0245B17319EC163829EA017DA350AAA91C3F08A2E6530BF797004D940CFB208AD6B8C4291D305D8EF9F778360DF7D8402288AFEE042FF1E86D91639764AAB6B5809F1C62345FC0C789554160EEDD06DEE63CCDFB");
//        cookiesManager.setAttribute("Hm_lvt_0e522924b4bbb2ce3f663e505b2f1f9c","1482582411");
//        cookiesManager.setAttribute("Hm_lpvt_0e522924b4bbb2ce3f663e505b2f1f9c","1482585339");
//        cookiesManager.setAttribute("xk.passport.uid","25283132");
//        cookiesManager.setAttribute("ASP.NET_SessionId", "fc0lsr4ybsmh4smv4mgj2x33");

        httpGet.setHeader(HttpHeaderContants.Cookie,cookiesManager.formatCookies());
        HttpResponse response = HttpClients.createDefault().execute(httpGet);
        System.out.println(response.getStatusLine());

        String filenameSource = StringUtil.gainHeaderFileName(response.getHeaders(HttpHeaderContants.Content_Disposition)[0].getValue());
        String filename = URLDecoder.decode(filenameSource);

        System.out.println(filename);
        File file = new File(filename);

        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(IOUtils.toByteArray(response.getEntity().getContent()));

        outputStream.close();
    }

    private static String analyseDownloadPageContent(String content) throws Exception{
        List<Pattern> patterns = Lists.newArrayList(
                Pattern.compile("<a data-xop-login id=\"goDownload\" href=\".{0,100}\" rel=\"nofollow\""),
                Pattern.compile("<a data-xop-login class=\"down-btn\" target=\"_blank\" href=\".{0,100}\" rel=\"nofollow\""),
                Pattern.compile("<a data-xop-login href=\".{0,100}\" rel=\"nofollow\" id=\"goDownload\" data-download"));
        String s = StringUtil.findFirst(content, patterns);
        if (s==null){
            System.out.println("not find page content");
            return "";
        }
        return s.substring(s.indexOf("href")+6, s.indexOf("\" rel"));
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
        HttpResponse response = HttpClients.createDefault().execute(httpGet);
        return new String(IOUtils.toByteArray(response.getEntity().getContent()));
    }
}
