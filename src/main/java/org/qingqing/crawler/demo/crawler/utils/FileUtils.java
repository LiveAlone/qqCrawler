package org.qingqing.crawler.demo.crawler.utils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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

    public static void tryDeleteFile(String path){
        File f = new File(path);
        f.deleteOnExit();
    }

    public static void appendFileContent(String path, String content){
        try {
            Files.append(content, new File(path), Charsets.UTF_8);
        }catch (IOException e){
            logger.error("append file error e:{}", e);
        }
    }
}
