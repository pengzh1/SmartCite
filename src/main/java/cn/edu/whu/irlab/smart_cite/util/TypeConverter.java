package cn.edu.whu.irlab.smart_cite.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * @author gcr19
 * @date 2019-08-18 09:57
 * @desc 类型转换工具
 **/
public class TypeConverter {

    /**
     *@auther gcr19
     *@desc str转element
     *@param string 待转换的xml字符串
     *@return jdom 节点
     **/
    public static Element str2xml(String string) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Element root = null;
        Document doc = saxBuilder.build(new StringReader(string));
        root = doc.getRootElement();
        return root;
    }


    public static String element2string(Element element){
        XMLOutputter outputter=new XMLOutputter();
        return outputter.outputElementContentString(element);
    }

    public static <T> JSONArray list2JsonArray(List<T> list){
        JSONArray array = new JSONArray();
        for (T t :
                list) {
            array.add(JSON.parse(t.toString()));
        }
        return array;
    }
}
