package cn.edu.whu.irlab.smart_cite.util;

import cn.edu.whu.irlab.smart_cite.vo.ToJsonAble;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author gcr19
 * @date 2019-08-18 09:57
 * @desc 类型转换工具
 **/
public class TypeConverter {

    /**
     * @param string 待转换的xml字符串
     * @return jdom 节点
     * @auther gcr19
     * @desc str转element
     **/
    public static Element str2xml(String string) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Element root = null;
        Document doc = saxBuilder.build(new StringReader(string));
        root = doc.getRootElement();
        return root;
    }


    public static String element2string(Element element) {
        XMLOutputter outputter = new XMLOutputter();
        return outputter.outputElementContentString(element);
    }

    public static <T extends ToJsonAble> JSONArray list2JsonArray(List<T> list) {
        JSONArray array = new JSONArray();
        for (T t :
                list) {
            try {
                array.add(t.toJson());
            } catch (Exception e) {
                System.out.println("exception");
            }
        }
        return array;
    }

    /**
     * @param json json string
     * @return jdom Element
     * @auther gcr19
     * @desc json string to jdom element
     **/
    public static Element jsonStr2Xml(String json) throws JDOMException, IOException {
        String s = json2XmlStr(json);
        return str2xml(s);
    }

    /**
     * Json to xml string.
     *
     * @param json the json
     * @return the string
     */
    public static String json2XmlStr(String json) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        buffer.append("<article>");
        JSONObject jObj = JSON.parseObject(json);
        json2XmlStr(jObj, buffer);
        buffer.append("</article>");
        return buffer.toString();
    }


    /**
     * Json to xmlstr string.
     *
     * @param jObj   the j obj
     * @param buffer the buffer
     * @return the string
     */
    public static String json2XmlStr(JSONObject jObj, StringBuffer buffer) {
        Set<Map.Entry<String, Object>> se = jObj.entrySet();
        for (Map.Entry<String, Object> en : se) {
            if (en.getValue() != null) {
                switch (en.getValue().getClass().getName()) {
                    case "com.alibaba.fastjson.JSONObject":
                        buffer.append("<" + en.getKey() + ">");
                        JSONObject jo = jObj.getJSONObject(en.getKey());
                        json2XmlStr(jo, buffer);
                        buffer.append("</" + en.getKey() + ">");
                        break;
                    case "com.alibaba.fastjson.JSONArray":
                        JSONArray jarray = jObj.getJSONArray(en.getKey());
                        for (int i = 0; i < jarray.size(); i++) {
                            buffer.append("<" + en.getKey() + ">");
                            Object object = jarray.get(i);
                            if (object.getClass().getName().equals("com.alibaba.fastjson.JSONObject")) {
                                JSONObject jsonobject = (JSONObject) object;
                                json2XmlStr(jsonobject, buffer);
                            } else {
                                buffer.append(escapeCharacter(object.toString()));
                            }
                            buffer.append("</" + en.getKey() + ">");
                        }
                        break;
                    case "java.lang.String":
                        buffer.append("<" + en.getKey() + ">" + escapeCharacter(en.getValue().toString()));
                        buffer.append("</" + en.getKey() + ">");
                        break;
                    default:
                        break;
                }
            } else {
                buffer.append("<" + en.getKey() + "></" + en.getKey() + ">");
            }
        }
        return buffer.toString();
    }

    private static String escapeCharacter(String s) {
        if (s.contains("&")) {
            s = s.replace("&", "&amp;");
        }
        if (s.contains("<")) {
            s = s.replace("<", "&lt;");
        }
        if (s.contains(">")) {
            s = s.replace(">", "&gt;");
        }
        if (s.contains("'")) {
            s = s.replace("'", "&apos;");
        }
        if (s.contains("\"")) {
            s = s.replace("\"", "&quot;");
        }
        return s;
    }
}
