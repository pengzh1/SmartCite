package cn.edu.whu.irlab.smart_cite.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author gcr
 * @version 1.0
 * @date 2018-09-08 0:27
 * @desc 读文件的工具类
 **/
public class ReadDoc {

    /**
     * 读文件
     * @param docPath
     * @return
     */
    public static String readDoc(String docPath) {
        File file = FileUtils.getFile(docPath);
        String docContent= null;
        try {
            docContent = FileUtils.readFileToString(file,"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return docContent;
    }


}
