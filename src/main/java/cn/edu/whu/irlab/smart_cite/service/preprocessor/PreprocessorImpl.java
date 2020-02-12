package cn.edu.whu.irlab.smart_cite.service.preprocessor;

import cn.edu.whu.irlab.smart_cite.exception.SplitSentenceException;
import cn.edu.whu.irlab.smart_cite.service.splitter.LingPipeSplitterImpl;
import cn.edu.whu.irlab.smart_cite.util.WriteUtil;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/6 10:43
 * @desc TODO
 **/
public abstract class PreprocessorImpl {

    private static final Logger logger = LoggerFactory.getLogger(PreprocessorImpl.class);

    List<Element> paragraphs = new ArrayList<>();

    List<Element> sentences = new ArrayList<>();

    List<Element> xrefs = new ArrayList<>();

    //存放完成编号的XML文档
    private final static String NUMBERED = "temp/numbered/";

    //存放完成过滤操作的XML文档
    final static String FILTERED = "temp/filtered/";

    //存放重新整理后的XML文档
    private final static String REFORMATTED = "temp/reformatted/";

    @Autowired
    private LingPipeSplitterImpl lingPipeSplitter;

    public void parseXML(Element root, File file) {
        parseStep1(root);
        parseStep2(root);
        parseStep3(root, file);
    }

    /**
     * @param root xml根节点
     * @return
     * @auther gcr19
     * @desc 预处理第一步：完成分割句子，段落，句子，引文标志编号。
     **/
    public void parseStep1(Element root) {
        //段落抽取
        extractParagraphs(root);
        //给段落编号
        numberElement(paragraphs);
        //分句
        splitSentences();
        //抽取句子
        extractSentence(root);
        //给句子编号
        numberElement(sentences);
    }

    void parseStep2(Element root) {
        //抽取引文标志节点
        extractXref(root);
        //引文标志编号
        numberElement(xrefs);
    }

    void parseStep3(Element root, File file) {
        //写出到新文件
        try {
            WriteUtil.writeXml(root, NUMBERED + FilenameUtils.getBaseName(file.getName()) + ".xml");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        //节点过滤
        filterTags(root);
        removeElementNotXref();
        //写出到新文件
        try {
            WriteUtil.writeXml(root, FILTERED + FilenameUtils.getBaseName(file.getName()) + ".xml");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        //整理有效信息
        Element newRoot = reformat(root);
        try {
            WriteUtil.writeXml(newRoot, REFORMATTED + FilenameUtils.getBaseName(file.getName()) + ".xml");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * @param root
     * @return
     * @auther gcr19
     * @desc 过滤删除无关节点
     **/
    public abstract void filterTags(Element root);

    public abstract void removeElementNotXref();

    void filterTags(Element root, List<String> filterTagList) {
        ArrayList<Element> nodes = new ArrayList<>();
        selectNode(nodes, root);
        filter(nodes, filterTagList);
    }

    private Element reformat(Element root) {

        Element newRoot = new Element("article");
        newRoot.addContent(new Element("header"));
        newRoot.addContent(new Element("body"));
        newRoot.addContent(new Element("back"));

        fillHeader(root, newRoot.getChild("header"));
        fillBody(root, newRoot.getChild("body"));
        fillBack(root, newRoot.getChild("back"));
        return newRoot;
    }

    abstract void fillHeader(Element root, Element header);

    abstract void fillBody(Element root, Element body);

    abstract void fillBack(Element root, Element back);

    private void selectNode(ArrayList<Element> nodes, Element node) {
        List<Element> list = node.getChildren();
        if (list != null && list.size() > 0) {
            nodes.addAll(list);
            for (Element element : list) {
                selectNode(nodes, element);
            }
        }
    }

    private void filter(List<Element> nodes, List<String> filterTagList) {
        for (Element node : nodes) {
            if (filterTagList.contains(node.getName())) {    //删掉filter标签
                node.getParentElement().removeContent(node);
            }
        }
    }

    void extractElements(Element element, String elementName, List<Element> elements) {
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

    public void extractXref(Element element, String xrefLabelName, String attributeName, String attributeValue) {
        if (element.getName().equals(xrefLabelName) && element.getAttributeValue(attributeName).equals(attributeValue)) {
            xrefs.add(element);
        } else {
            for (Element e :
                    element.getChildren()) {
                extractXref(e);
            }
        }
    }

    void extractParagraphs(Element root) {
        extractElements(root.getChild("body"), "p", paragraphs);
    }

    void extractSentence(Element root) {
        extractElements(root.getChild("body"), "s", sentences);
    }

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

    private <T extends Element> void numberElement(List<T> elements) {
        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).setAttribute("id", String.valueOf(i));
        }
    }

    void removeElementNotXref(String xrefLabelName, String attributeName, String attributeValue) {
        for (Element sentence :
                sentences) {
            if (sentence.getChildren() != null) {
                List<Element> needDeleted = new ArrayList<>();
                for (Element child :
                        sentence.getChildren()) {
                    if (!child.getName().equals(xrefLabelName)) {
                        putValue2before(sentence, child, needDeleted);
                    } else {
                        if (!child.getAttributeValue(attributeName).equals(attributeValue)) {
                            putValue2before(sentence, child, needDeleted);
                        }
                    }
                }
                for (Element element :
                        needDeleted) {
                    sentence.removeContent(element);
                }
            }
        }
    }

    /**
     * @param sentence 句子节点（child的父节点）
     * @param child    下个节点
     * @return
     * @auther gcr19
     * @desc 取得下个节点的值合并到上个节点，并删除下个节点
     **/
    private void putValue2before(Element sentence, Element child, List<Element> needDeleted) {
        Text text = new Text(sentence.getContent(sentence.indexOf(child) - 1).getValue()
                + child.getValue());
        sentence.setContent(sentence.indexOf(child) - 1, text);
        needDeleted.add(child);
    }

    /**
     * @param element 节点
     * @param oldName 原节点名
     * @param newName 现节点名
     * @return
     * @auther gcr19
     * @desc 为节点重新命名
     **/
    void reNameElement(Element element, String oldName, String newName) {
        if (element.getName().equals(oldName)) {
            element.setName(newName);
        } else {
            for (Element e :
                    element.getChildren()) {
                reNameElement(e, oldName, newName);
            }
        }
    }

    /**
     *@auther gcr19
     *@desc 为属性重命名
     *@param element 节点
     *@param targetElementName 目标节点名称
     *@param oldName 原属性名
     *@param newName 现属性名
     *@return
     **/
    void reNameAttribute(Element element, String targetElementName, String oldName, String newName) {
        if (element.getName().equals(targetElementName)) {
            Attribute attribute = element.getAttribute(oldName);
            attribute.setName(newName);
        } else {
            for (Element e :
                    element.getChildren()) {
                reNameAttribute(e, targetElementName, oldName, newName);
            }
        }
    }

}
