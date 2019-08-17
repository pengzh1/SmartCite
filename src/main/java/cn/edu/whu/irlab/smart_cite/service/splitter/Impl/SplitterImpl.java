package cn.edu.whu.irlab.smart_cite.service.splitter.Impl;

import cn.edu.whu.irlab.smart_cite.service.splitter.Splitter;
import org.jdom2.Element;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gcr19
 * @date 2019-08-16 19:56
 * @desc 句子分割实现类
 **/
@Service("splitter")
public class SplitterImpl implements Splitter {

    private List<Element> paragraphs = new ArrayList<>();


    @Override
    public void splitSentence(Element paragraph) {
        System.out.println(paragraph.getValue());
    }


    @Override
    public void selectParagraph(Element element) {
        if (element.getName().equals("p")){
            if (element.getChild("xref")!=null)
                paragraphs.add(element);
        }else {
            List<Element> temp=element.getChildren();
            for (Element e:
                    temp) {
                selectParagraph(e);
            }
        }
    }

    @Override
    public List<Element> getParagraphs() {
        return paragraphs;
    }
}
