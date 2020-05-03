package cn.edu.whu.irlab.smart_cite.util;

import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/14 10:56
 * @desc 节点相关操作工具类
 **/
public class ElementUtil {


    public static void extractElements(Element element, String elementName, List<Element> elements) {
        if (element.getName().equals(elementName)) {
            elements.add(element);
        } else {
            for (Element e :
                    element.getChildren()) {
                extractElements(e, elementName, elements);
            }
        }
    }

    public static List<Element> extractElements(Element element, String elementName){
        List<Element> elements=new ArrayList<>();
        extractElements(element,elementName,elements);
        return elements;
    }

}
