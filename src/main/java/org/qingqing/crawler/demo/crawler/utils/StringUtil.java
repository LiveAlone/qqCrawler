package org.qingqing.crawler.demo.crawler.utils;

import com.google.common.collect.Lists;
import org.apache.poi.hssf.util.HSSFColor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yaoqijun.
 * Date:2016-12-24
 * Email:yaoqijunmail@gmail.io
 * Descirbe:
 */
public class StringUtil {

    public static String findFirst(String source, List<Pattern> patterns){
        for (Pattern pattern : patterns) {
            String s = findFirst(source, pattern);
            if (s != null){
                return s;
            }
        }
        return null;
    }

    public static String findFirst(String source, Pattern pattern){
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()){
            return matcher.group();
        }
        return null;
    }

    public static List<String> findAll(String source, Pattern pattern){
        Matcher matcher = pattern.matcher(source);
        List<String> result = Lists.newArrayList();
        while (matcher.find()){
            result.add(matcher.group());
        }
        return result;
    }

    public static String getScriptValContent(String input){
        return input.substring(input.indexOf("val('")+5, input.indexOf("');"));
    }

    private static String FILENAME = "filename=";

    private static Integer FILENAME_LENGTH = 9;

    public static String gainHeaderFileName(String header){
        return header.substring(header.indexOf(FILENAME) + FILENAME_LENGTH);
    }

    private static String VALUE = "value=\"";

    private static Integer VALUE_LENGTH = 7;

    private static String COLON = "\"";

    public static String gainInputValue(String input){
        Integer startIndex = input.indexOf(VALUE) + VALUE_LENGTH;
        Integer endIndex = input.indexOf(COLON, startIndex + 1);
        return input.substring(startIndex, endIndex);
    }
}
