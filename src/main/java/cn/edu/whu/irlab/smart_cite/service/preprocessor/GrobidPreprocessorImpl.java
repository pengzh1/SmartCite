package cn.edu.whu.irlab.smart_cite.service.preprocessor;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
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
        Element title_group = root.getChild("teiHeader", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"))
                .getChild("fileDesc", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).
                        getChild("titleStmt", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
        if (title_group.getChildren().size() == 1) {
            header.addContent(new Element("title-group").addContent(new Element("title").
                    addContent(title_group.getChild("title", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"))
                            .getValue())));
        } else {
            //todo 需要测试grobid识别副标题
        }

        //设置摘要
        Element abstract_ = root.getChild("teiHeader", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).
                getChild("profileDesc", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).
                getChild("abstract", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
        Element newAbstract = new Element("abstract");
        for (Element e :
                abstract_.getChildren()) {
            //todo 需要测试有些特殊文章摘要中由title的情况
            newAbstract.addContent(e.setName("sec").detach());
        }

    }

    @Override
    void fillBody(Element root, Element newBody) {
        Element body = root.getChild("text", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).
                getChild("body", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).detach();
        //为节点重命名
        reNameElement(body, "div", "sec");
        reNameElement(body, "head", "title");
        reNameElement(body, "ref", "xref");

        //为属性重命名
        reNameAttribute(body, "xref", "type", "ref-type");
        reNameAttribute(body, "xref", "target", "rid");

        newBody.addContent(body.removeContent());
        //删除所有节点的命名空间
        removeNameSpace(newBody);

        //为sec节点编号
        List<Element> secs=new ArrayList<>();
        extractElements(newBody,"sec",secs);
        numberElement(secs);

        //删除n属性
        removeAttribute(newBody,"n");
    }

    @Override
    void fillBack(Element root, Element back) {
        Element ref_list = new Element("ref-list");
        Element listBibl = root.getChild("text", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).
                getChild("back", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).
                getChild("div", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).
                getChild("listBibl", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
        for (Element biblStruct :
                listBibl.getChildren()) {
            Element ref = new Element("ref");
            Element element_citation = new Element("element-citation");

            ref.setAttribute("id", biblStruct.getAttributeValue("id",
                    Namespace.getNamespace("xml", "http://www.w3.org/XML/1998/namespace")));


            //设置标题
            Element analytic = biblStruct.getChild("analytic", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
            if (analytic != null) {
                Element title = analytic.getChild("title", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
                if (title != null) {
                    element_citation.addContent(new Element("article-title").addContent(title.getValue()));
                }
            }

            ref.addContent(element_citation);
            ref_list.addContent(ref);
        }
        back.addContent(ref_list);
    }

    @Override
    public void extractXref(Element element) {
        super.extractXref(element.getChild("text", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).
                        getChild("body", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")),
                XREF_LABEL_NAME, ATTRIBUTE_NAME, ATTRIBUTE_VALUE);
    }

    @Override
    void extractParagraphs(Element root) {
        extractElements(root.getChild("text", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).
                getChild("body", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")), "p", paragraphs);
    }

    @Override
    void extractSentence(Element root) {
        extractElements(root.getChild("text", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).
                getChild("body", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")), "s", sentences);
    }
}
