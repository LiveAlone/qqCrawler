package org.qingqing.crawler.demo.crawler.utils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by yaoqijun on 2016/12/29.
 * 文件解析写入操作
 */
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static void overwriteFile(String path, List<String> content){

        try {
            File file = new File(path);
            Files.createParentDirs(file); // try create
            if (Files.isFile().apply(file)){
                file.delete();
            }
            for (String s : content) {
                Files.append(s + "\n", file, Charsets.UTF_8);
            }
        }catch (IOException e){
            logger.error("over write file error");
        }
    }

    public static List<String> readAllLines(File file){
        try {
            if (!file.exists()){
                return new ArrayList<>(0);
            }
            return Files.readLines(file, Charsets.UTF_8);
        }catch (IOException e){
            logger.error("load all lines error");
            return new ArrayList<>(0);
        }
    }

    public static void tryDeleteFile(String path){
        File f = new File(path);
        if (f.exists()){
            f.delete();
        }
    }

    public static void appendFileContent(String path, String content){
        try {
            File f = new File(path);
            if (!f.exists()){
                f.createNewFile();
            }
            Files.append(content, f, Charsets.UTF_8);
        }catch (IOException e){
            logger.error("append file error e:{}", e);
        }
    }
}
