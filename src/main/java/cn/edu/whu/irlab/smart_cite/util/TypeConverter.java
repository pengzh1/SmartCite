package cn.edu.whu.irlab.smart_cite.util;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.StringReader;

/**
 * @author gcr19
 * @date 2019-08-18 09:57
 * @desc 类型转换工具
 **/
public class TypeConverter {

    public static Element str2xml(String string) {
        SAXBuilder saxBuilder = new SAXBuilder();
        Element root = null;

        try {
            Document doc = saxBuilder.build(new StringReader(string));
            root = doc.getRootElement();
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }
}
