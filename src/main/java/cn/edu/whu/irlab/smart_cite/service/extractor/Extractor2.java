package cn.edu.whu.irlab.smart_cite.service.extractor;

import cn.edu.whu.irlab.smart_cite.exception.SplitSentenceException;
import cn.edu.whu.irlab.smart_cite.service.splitter.LingPipeSplitterImpl;
import org.jdom2.Content;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gcr19
 * @version 1.0
 * @date 2019/12/23 16:48
 * @desc 抽取器2
 **/
@Service
public class Extractor2 {

    private static final Logger logger = LoggerFactory.getLogger(Extractor2.class);

    @Autowired
    private LingPipeSplitterImpl lingPipeSplitter;

    private Element article;

    private List<Element> paragraphs = new ArrayList<>();

    private List<Element> sentences = new ArrayList<>();

    private List<Element> xrefs=new ArrayList<>();


    /**
     * @param article 文档
     * @return
     * @auther gcr19
     * @desc 初始化方法
     **/
    public void init(Element article) {
        paragraphs.clear();
        sentences.clear();
        this.article = article;
    }


    public void extract() {
        //抽取段落
        extractElements(article.getChild("body"),"p",paragraphs);
        //为段落编号
        numberElement(paragraphs);
        //为每一段分句
        splitSentences();
        //抽取句子
        extractElements(article.getChild("body"),"s",sentences);
        //为句子编号
        numberElement(sentences);
        //抽取引文节点
        extractXref(article.getChild("body"));
        //为引文节点编号
        numberElement(xrefs);

    }

    public void extractXref(Element element){
        if (element.getName().equals("xref")&&element.getAttributeValue("ref-type").equals("bibr")){
            xrefs.add(element);
        }else {
            for (Element e :
                    element.getChildren()) {
                extractXref(e);
            }
        }
    }

    public void splitSentences() {
        for (Element p :
                paragraphs) {
            try {
                List<Content> contents = lingPipeSplitter.splitSentences(p);
                p.removeContent();
                p.addContent(contents);
            } catch (SplitSentenceException e) {
                logger.error(e.getMessage() + " At paragraph: " + p.getAttributeValue("id"));
            }
        }
    }

    private void extractElements(Element element,String elementName,List<Element> elements){
        if (element.getName().equals(elementName)) {
            elements.add(element);
        } else {
            for (Element e :
                    element.getChildren()) {
                extractElements(e,elementName,elements);
            }
        }
    }


    public <T extends Element> void numberElement(List<T> elements) {
        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).setAttribute("id", String.valueOf(i));
        }
    }

    public Element getArticle() {
        return article;
    }
}
