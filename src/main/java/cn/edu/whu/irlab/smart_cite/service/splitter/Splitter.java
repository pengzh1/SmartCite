package cn.edu.whu.irlab.smart_cite.service.splitter;

import org.jdom2.Element;

import java.util.List;

/**
 * @author gcr19
 * @date 2019-08-16 19:55
 * @desc 句子分割器 接口
 **/
public interface Splitter {

    void splitSentence(Element paragraph);

    void selectParagraph(Element element);

    List<Element> getParagraphs();
}
