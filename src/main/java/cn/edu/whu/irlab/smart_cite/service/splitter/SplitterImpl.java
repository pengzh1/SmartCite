package cn.edu.whu.irlab.smart_cite.service.splitter;

import cn.edu.whu.irlab.smart_cite.exception.SplitSentenceException;
import org.jdom2.Content;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gcr19
 * @version 1.0
 * @date 2019/12/19 15:22
 * @desc 分词器
 **/
public abstract class SplitterImpl {
    private static final Logger logger = LoggerFactory.getLogger(SplitterImpl.class);

    abstract List<String> splitSentences(String text);

    public List<Content> splitSentences(Element p) {
        if (!p.getName().equals("p")) {
            throw new IllegalArgumentException("该节点<" + p.getName() + ">，请传入<p>节点");
        }
        String text = p.getValue();
        List<String> sentences = splitSentences(text);
        List<Element> elements = calculateCoordinate(p.getContent());
        return sentences2paragraph(sentences, elements);
    }

    private List<Content> sentences2paragraph(List<String> sentences, List<Element> elements) {
        List<Content> sentenceElements = new ArrayList<>();
        //句子位置指针
        int positionCoordinate = 0;
        for (String sentence :
                sentences) {
            Element sentenceElement = new Element("s");

            if (elements.isEmpty() || elements == null) {
                sentenceElement.addContent(sentence);
            } else {
                //残余句子
                String residualSentence = sentence;
                while (residualSentence.contains(elements.get(0).getAttributeValue("localizer"))) {

                    Element sub = elements.remove(0).detach();

                    int end = Integer.parseInt(sub.getAttributeValue("coordinate")) - positionCoordinate;

                    sentenceElement.addContent(residualSentence.substring(0, end));

                    sub.removeAttribute("coordinate");
                    sub.removeAttribute("localizer");
                    sentenceElement.addContent(sub);
                    positionCoordinate += end + sub.getValue().length();

                    residualSentence = residualSentence.substring(end + sub.getValue().length());

                    if (elements.isEmpty()) break;
                }
                sentenceElement.addContent(residualSentence);
                positionCoordinate += residualSentence.length() + 1;
            }
            sentenceElements.add(sentenceElement);
        }
        return sentenceElements;
    }

    /**
     * @param contents 段落节点内容
     * @return 段落中的子节点们
     * @auther gcr19
     * @desc 计算节点位置坐标并添加用于定位的特征
     **/
    private List<Element> calculateCoordinate(List<Content> contents) {
        List<Element> elements = new ArrayList<>();
        int coordinatePoint = 0;
        String prefix = "";
        for (Content c :
                contents) {
            String contentValue = c.getValue();
            if (c.getCType().equals(Content.CType.Element)) {
                Element e = (Element) c;
                e.setAttribute("localizer", prefix + contentValue);
                e.setAttribute("coordinate", String.valueOf(coordinatePoint));
                coordinatePoint += contentValue.length();
                elements.add(e);
            } else {
                coordinatePoint += contentValue.length();
            }
            if (contentValue.length() > 5) {
                prefix = c.getValue().substring(contentValue.length() - 5);
            } else {
                prefix = c.getValue();
            }
        }
        return elements;
    }

}
