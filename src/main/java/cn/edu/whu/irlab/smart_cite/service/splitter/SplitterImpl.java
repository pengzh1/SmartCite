package cn.edu.whu.irlab.smart_cite.service.splitter;

import org.jdom2.Content;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gcr19
 * @version 1.0
 * @date 2019/12/19 15:22
 * @desc 分词器
 **/
public abstract class SplitterImpl {

    public abstract List<String> splitSentences(String text);

    public List<Element> splitSentences(Element p) {
        String text = p.getValue();
        List<String> sentences = splitSentences(text);
        List<Element> elements = calculateCoordinate(p.getContent());

        return sentences2paragraph(sentences, elements);
    }

    private List<Element> sentences2paragraph(List<String> sentences, List<Element> elements) {
        List<Element> sentenceElements = new ArrayList<>();
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

                try {
                    while (residualSentence.contains(elements.get(0).getAttributeValue("localizer"))) {
                        //     System.out.println(elements.get(0).getAttributeValue("localizer"));
                        Element sub = elements.remove(0).detach();
                        int end = Integer.parseInt(sub.getAttributeValue("coordinate")) - positionCoordinate;
                        sentenceElement.addContent(residualSentence.substring(0, end));
                        //  System.out.println(residualSentence+"  "+(end+sub.getValue().length())+"  "+residualSentence.length());
                        sub.removeAttribute("coordinate");
                        sub.removeAttribute("localizer");
                        sentenceElement.addContent(sub);
                        if (end + sub.getValue().length() >= residualSentence.length()) {
                            positionCoordinate += residualSentence.length();
                            residualSentence = "";
                        } else {
                            residualSentence = residualSentence.substring(end + sub.getValue().length());
                            positionCoordinate += end + sub.getValue().length();
                        }

                        //    System.out.println(residualSentence);
                        if (elements.isEmpty()) break;
                    }
                    sentenceElement.addContent(residualSentence);
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
        int pdnum = 0;
        String prefix = "";
        for (Content c :
                contents) {
            String contentValue = c.getValue();

            if (c.getCType().equals(Content.CType.Element)) {

                Element e = (Element) c;
                if (e.getAttribute("target") == null && e.getAttribute("rid") == null) break;
                if (pdnum == 0)
                    e.setAttribute("localizer", prefix + contentValue);
                else
                    e.setAttribute("localizer", contentValue);
                e.setAttribute("coordinate", String.valueOf(coordinatePoint));
                coordinatePoint += contentValue.length();
                elements.add(e);
                pdnum = 1;

            } else {
                coordinatePoint += contentValue.length();
                pdnum = 0;
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
