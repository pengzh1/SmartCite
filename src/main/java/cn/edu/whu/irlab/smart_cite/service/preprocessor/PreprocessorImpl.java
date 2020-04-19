package cn.edu.whu.irlab.smart_cite.service.preprocessor;

import cn.edu.whu.irlab.smart_cite.exception.SplitSentenceException;
import cn.edu.whu.irlab.smart_cite.service.splitter.LingPipeSplitterImpl;
import cn.edu.whu.irlab.smart_cite.util.ElementUtil;
import cn.edu.whu.irlab.smart_cite.util.WriteUtil;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cn.edu.whu.irlab.smart_cite.vo.FileLocation.*;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/6 10:43
 * @desc 预处理器抽象类
 **/
public abstract class PreprocessorImpl {

    private static final Logger logger = LoggerFactory.getLogger(PreprocessorImpl.class);

    ThreadLocal<List<Element>> paragraphs = new ThreadLocal<List<Element>>(){
        @Override
        protected List<Element> initialValue() {
            return new ArrayList<>();
        }
    };

    ThreadLocal<List<Element>> sentences = new ThreadLocal<List<Element>>(){
        @Override
        protected List<Element> initialValue() {
            return new ArrayList<>();
        }
    };

    ThreadLocal<List<Element>> xrefs = new ThreadLocal<List<Element>>(){
        @Override
        protected List<Element> initialValue() {
            return new ArrayList<>();
        }
    };

    ThreadLocal<File> file = new ThreadLocal<File>();
    ThreadLocal<Element> root = new ThreadLocal<Element>();
//    File file;
//    Element root;

    @Autowired
    private LingPipeSplitterImpl lingPipeSplitter;

    public Element parseXML(Element root, File file) {
        paragraphs.get().clear();
        sentences.get().clear();
        xrefs.get().clear();

        //节点过滤
        filterTags(root);
        //段落抽取
        extractParagraphs(root);
        removeElementNotXref();
        //写出到新文件
        writeFile(root, FILTERED, file);


        //给段落编号
        numberElement(paragraphs.get());
        //分句
        splitSentences();
        //抽取句子
        extractSentence(root);
        //给句子编号
        numberElement(sentences.get());
        //抽取引文标志节点
        extractXref(root);
        //引文标志编号
        numberElement(xrefs.get());
        //写出到新文件
        writeFile(root, NUMBERED, file);

        //整理有效信息
        Element newRoot = reformat(root);
        writeFile(newRoot, REFORMATTED, file);
        return newRoot.setAttribute("status", "preprocessed");
    }

    /**
     * @param root 父节点
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

    Element reformat(Element root) {

        Element newRoot = new Element("article");
        newRoot.addContent(new Element("header"));
        newRoot.addContent(new Element("body"));
        newRoot.addContent(new Element("back"));

        fillHeader(root, newRoot.getChild("header"));
        fillBody(root, newRoot.getChild("body"));
        fillBack(root, newRoot.getChild("back"));

        //删除所有节点的命名空间
        removeNameSpace(newRoot);

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

    public abstract void extractXref(Element element);

    void extractXref(Element element, String xrefLabelName, String attributeName, String attributeValue) {
        if (element.getName().equals(xrefLabelName) && element.getAttributeValue(attributeName).equals(attributeValue)) {
            xrefs.get().add(element);
        } else {
            for (Element e :
                    element.getChildren()) {
                extractXref(e, xrefLabelName, attributeName, attributeValue);
            }
        }
    }

    void extractParagraphs(Element root) {
        ElementUtil.extractElements(root.getChild("body"), "p", paragraphs.get());
    }

    void extractSentence(Element root) {
        ElementUtil.extractElements(root.getChild("body"), "s", sentences.get());
    }

    private void splitSentences() {
        for (Element p :
                paragraphs.get()) {
            try {
                List<Content> contents = lingPipeSplitter.splitSentences(p);
                p.removeContent();
                p.addContent(contents);
            } catch (SplitSentenceException e) {
                logger.error(e.getMessage() + " At paragraph: " + p.getAttributeValue("id"));
            }
        }
    }

    <T extends Element> void numberElement(List<T> elements) {
        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).setAttribute("id", String.valueOf(i + 1));
        }
    }

    void removeElementNotXref(String xrefLabelName, String attributeName, String attributeValue) {
        for (Element paragraph :
                paragraphs.get()) {
            if (paragraph.getChildren() != null) {
                List<Element> needDeleted = new ArrayList<>();
                List<Element> children = paragraph.getChildren();

                for (int i = children.size() - 1; i >= 0; i--) {
                    Element child = children.get(i);

                    if (!child.getName().equals(xrefLabelName) || !child.getAttributeValue(attributeName).equals(attributeValue)) {

                        if (paragraph.indexOf(child) == 0) {
                            paragraph.setContent(0, new Text(child.getValue()));
                        } else {
                            putValue2before(paragraph, child);
                        }
                        needDeleted.add(child);
                    }
                }

                for (Element element :
                        needDeleted) {
                    paragraph.removeContent(element);
                }
            }
        }
    }

    /**
     * @param sentence 句子节点（child的父节点）
     * @param child    下个节点
     * @return
     * @auther gcr19
     * @desc 取得下个节点的值合并到上个节点
     **/
    private void putValue2before(Element sentence, Element child) {
        if (sentence.getContent(sentence.indexOf(child) - 1).getCType().equals(Content.CType.Text)) {
            Text text = new Text(sentence.getContent(sentence.indexOf(child) - 1).getValue()
                    + child.getValue());
            sentence.setContent(sentence.indexOf(child) - 1, text);
        } else {
            Element before = (Element) sentence.getContent(sentence.indexOf(child) - 1);
            Text text = new Text(before.getValue() + child.getValue());
            before.setContent(text);
        }
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
     * @param element           父节点
     * @param targetElementName 目标节点名称
     * @param oldName           原属性名
     * @param newName           现属性名
     * @return
     * @auther gcr19
     * @desc 为父节点下所有目标节点的属性重命名
     **/
    void reNameAttribute(Element element, String targetElementName, String oldName, String newName) {
        if (element.getName().equals(targetElementName)) {
            Attribute attribute = element.getAttribute(oldName);
            if (attribute != null) {
                attribute.setName(newName);
            }
            List<Element> children = element.getChildren();
            if (children != null) {
                for (Element e :
                        children) {
                    reNameAttribute(e, targetElementName, oldName, newName);
                }
            }
        } else {
            for (Element e :
                    element.getChildren()) {
                reNameAttribute(e, targetElementName, oldName, newName);
            }
        }
    }

    /**
     * @param root          父节点
     * @param attributeName 属性名
     * @return
     * @auther gcr19
     * @desc 删除该节点下所有节点的某属性
     **/
    void removeAttribute(Element root, String attributeName) {
        root.removeAttribute(attributeName);
        if (root.getChildren() != null) {
            for (Element e :
                    root.getChildren()) {
                removeAttribute(e, attributeName);
            }
        }
    }

    /**
     * @param root 父节点
     * @return
     * @auther gcr19
     * @desc 删除该节点所有节点的命名空间
     **/
    void removeNameSpace(Element root) {
        root.setNamespace(Namespace.NO_NAMESPACE);
        if (root.getChildren() != null) {
            for (Element e :
                    root.getChildren()) {
                removeNameSpace(e);
            }
        }
    }

    void writeFile(Element root, String folderPath, File file) {
        try {
            WriteUtil.writeXml(root, folderPath + FilenameUtils.getBaseName(file.getName()) + ".xml");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

    /**
     * @param body body节点
     * @return
     * @auther gcr19
     * @desc 为sec节点重新排序
     **/
    void reNumberSec(Element body) {
        List<Element> children = body.getChildren("sec");
        if (children != null) {
            int i = 1;
            for (Element sec :
                    children) {
                String parentId = sec.getParentElement().getAttributeValue("id");
                if (parentId != null) {
                    sec.setAttribute("id", parentId + "." + i++);
                } else {
                    sec.setAttribute("id", String.valueOf(i++));
                }
                reNumberSec(sec);
            }
        }
    }
}
