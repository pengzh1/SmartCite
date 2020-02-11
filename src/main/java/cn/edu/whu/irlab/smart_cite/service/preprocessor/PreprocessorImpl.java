package cn.edu.whu.irlab.smart_cite.service.preprocessor;

import cn.edu.whu.irlab.smart_cite.exception.SplitSentenceException;
import cn.edu.whu.irlab.smart_cite.service.splitter.LingPipeSplitterImpl;
import cn.edu.whu.irlab.smart_cite.util.WriteUtil;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.Content;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/6 10:43
 * @desc TODO
 **/
public abstract class PreprocessorImpl {

    private static final Logger logger = LoggerFactory.getLogger(PreprocessorImpl.class);

    private List<Element> paragraphs = new ArrayList<>();

    private List<Element> sentences = new ArrayList<>();

    private List<Element> xrefs = new ArrayList<>();

    //存放完成编号的XML文档
    private final static String NUMBERED = "temp/numbered/";

    //存放完成过滤操作的XML文档
    final static String FILTERED ="temp/filtered/";

    @Autowired
    private LingPipeSplitterImpl lingPipeSplitter;

    public abstract void parseXML(Element root, File file);

    /**
     * @param root xml根节点
     * @return
     * @auther gcr19
     * @desc 预处理第一步：完成分割句子，段落，句子，引文标志编号。
     **/
    public void parseStep1(Element root) {
        //段落抽取
        extractElements(root, "p", paragraphs);
        //给段落编号
        numberElement(paragraphs);
        //分句
        splitSentences();
        //抽取句子
        extractElements(root.getChild("body"), "s", sentences);
        //给句子编号
        numberElement(sentences);
    }

    public void parseStep2(Element root) {
        //抽取引文标志节点
        extractXref(root);
        //引文标志编号
        numberElement(xrefs);
    }

    public void parseStep3(Element root, File file) {
        //写出到新文件
        try {
            WriteUtil.writeXml(root, NUMBERED + FilenameUtils.getBaseName(file.getName()) + ".xml");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        //节点过滤
        filterTags(root);
        //抽取有效信息
        //写出到新文件
        try {
            WriteUtil.writeXml(root, FILTERED + FilenameUtils.getBaseName(file.getName()) + ".xml");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public abstract void filterTags(Element root);

    public void filterTags(Element root, List<String> filterTagList) {
        ArrayList<Element> nodes = new ArrayList<>();
        selectNode(nodes, root);
        filter(nodes, filterTagList);
    }

    private void selectNode(ArrayList<Element> nodes, Element node) {
        List<Element> list = node.getChildren();
        if (list != null && list.size() > 0) {
            nodes.addAll(list);
            for (int i = 0; i < list.size(); i++) {
                selectNode(nodes, list.get(i));
            }
        }
    }

    private void filter(List<Element> nodes, List<String> filterTagList) {
        for (int i = 0; i < nodes.size(); i++) {
            if (filterTagList.contains(nodes.get(i).getName())) {    //删掉filter标签
                nodes.get(i).getParentElement().removeContent(nodes.get(i));
            }
        }
    }


    private void extractElements(Element element, String elementName, List<Element> elements) {
        if (element.getName().equals(elementName)) {
            elements.add(element);
        } else {
            for (Element e :
                    element.getChildren()) {
                extractElements(e, elementName, elements);
            }
        }
    }

    public abstract void extractXref(Element element);


    private void splitSentences() {
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

    public <T extends Element> void numberElement(List<T> elements) {
        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).setAttribute("id", String.valueOf(i));
        }
    }

}
