package cn.edu.whu.irlab.smart_cite.util;

import org.apache.commons.io.FileUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;

/**
 * @author gcr
 * @version 1.0
 * @date 2018-09-08 0:27
 * @desc 读文件的工具类
 **/
public class ReadUtil {

    /**
     * 读文件
     * @param docPath 文件路径
     * @return 文件内容
     */
    public static String read2Str(String docPath) {
        File file = FileUtils.getFile(docPath);
        String docContent= null;
        try {
            docContent = FileUtils.readFileToString(file,"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return docContent;
    }

    /**
     * 读xml文件
     * @param docPath 文件路径
     * @return jdom element 对象
     */
    public static Element read2xml(String docPath) throws IOException,JDOMException{
        SAXBuilder saxBuilder = new SAXBuilder();
        saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        Document doc;
        Element root = null;
            doc = saxBuilder.build(FileUtils.getFile(docPath));
            root = doc.getRootElement();
        return root;
    }


}
