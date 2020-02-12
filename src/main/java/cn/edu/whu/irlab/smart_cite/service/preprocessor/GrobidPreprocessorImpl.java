package cn.edu.whu.irlab.smart_cite.service.preprocessor;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/11 22:00
 * @desc Grobid数据预处理
 **/
@Service
public class GrobidPreprocessorImpl extends PreprocessorImpl {

    private static final String XREF_LABEL_NAME = "ref";
    private static final String ATTRIBUTE_NAME = "type";
    private static final String ATTRIBUTE_VALUE = "bibr";

    //需要删除的节点名列表
    private static final String[] filterTag = {"publicationStmt", "sourceDesc", "encodingDesc", "formula", "figure", "note"};
    private static final List<String> filterTagList = Arrays.asList(filterTag);


    @Override
    public void filterTags(Element root) {
        super.filterTags(root, filterTagList);
    }

    @Override
    public void removeElementNotXref() {
        super.removeElementNotXref(XREF_LABEL_NAME, ATTRIBUTE_NAME, ATTRIBUTE_VALUE);
    }

    @Override
    void fillHeader(Element root, Element header) {
        //设置标题
        Element title_group = root.getChild("teiHeader").getChild("fileDesc").getChild("titleStmt");
        if (title_group.getChildren().size() == 1) {
            header.addContent(new Element("title-group").addContent(new Element("title").
                    addContent(title_group.getChild("title").getValue())));
        } else {
            //todo 需要测试grobid识别副标题
        }

        //设置摘要
        Element abstract_ = root.getChild("teiHeader").getChild("profileDesc").getChild("abstract");
        Element newAbstract = new Element("abstract");
        for (Element e :
                abstract_.getChildren()) {
            //todo 需要测试有些特殊文章摘要中由title的情况
            newAbstract.addContent(e.setName("sec").detach());
        }

    }

    @Override
    void fillBody(Element root, Element newBody) {
        Element body = root.getChild("text").getChild("body").detach();
        reNameElement(body, "div", "sec");
        reNameElement(body, "head", "title");
        reNameElement(body, "ref", "xref");

        reNameAttribute(body, "xref", "type", "ref-type");
        reNameAttribute(body, "xref", "target", "rid");

        newBody.addContent(body);
    }

    @Override
    void fillBack(Element root, Element back) {
        Element ref_list = new Element("ref-list");
        Element listBibl = root.getChild("back").getChild("div").getChild("listBibl");
        for (Element biblStruct :
                listBibl.getChildren()) {
            Element ref = new Element("ref");
            ref_list.addContent(ref);

            ref.setAttribute("id", biblStruct.getAttributeValue("xml:id"));
            Element element_citation = new Element("element-citation");
            ref.addContent(element_citation);

            Element person_group = new Element("person-group").setAttribute("person-group-type", "author");
            element_citation.addContent(person_group);

            //设置标题
            element_citation.addContent(new Element("article-title").addContent(biblStruct.getChild("analytic").
                    getChild("title").getValue()));

            //设置作者
            for (Element author :
                    biblStruct.getChild("analytic").getChildren("author")) {
                Element name = new Element("name");
                name.addContent(new Element("surname").addContent(author.getChild("persName").getChild("surname").getValue()));
                name.addContent(new Element("given-names").addContent(author.getChild("persName").getChild("forename").getValue()));
                person_group.addContent(name);
            }

            //设置年份
            element_citation.addContent(new Element("year").addContent(biblStruct.getChild("monogr").
                    getChild("imprint").getChild("date").getAttributeValue("when")));


            //设置来源
            element_citation.addContent(new Element("source").addContent(biblStruct.getChild("monogr").getChild("title").getValue()));

        }
    }

    @Override
    public void extractXref(Element element) {
        super.extractXref(element, XREF_LABEL_NAME, ATTRIBUTE_NAME, ATTRIBUTE_VALUE);
    }

    @Override
    void extractParagraphs(Element root) {
        extractElements(root.getChild("text").getChild("body"), "p", paragraphs);
    }

    @Override
    void extractSentence(Element root) {
        extractElements(root.getChild("text").getChild("body"), "s", sentences);
    }
}
