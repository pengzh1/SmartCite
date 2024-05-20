package cn.edu.whu.irlab.smart_cite.service.preprocessor;

import cn.edu.whu.irlab.smart_cite.util.ElementUtil;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class GrobidPreprocessorImpl extends PreprocessorImpl {

    private static final String XREF_LABEL_NAME = "ref";
    private static final String ATTRIBUTE_NAME = "type";
    private static final String ATTRIBUTE_VALUE = "bibr";

    //需要删除的节点名列表
    private static final String[] filterTag = {"publicationStmt", "encodingDesc", "formula", "figure", "note"};
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
            header.addContent(new Element("title-group").addContent(new Element("article-title").
                    addContent(title_group.getChild("title", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"))
                            .getValue())));
        } else {
            //todo 需要测试grobid识别副标题
            header.addContent(new Element("title-group").addContent(new Element("article-title").
                    addContent(title_group.getChild("title", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"))
                            .getValue())));
        }

        //设置摘要
        Element abstract_ = root.getChild("teiHeader", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).
                getChild("profileDesc", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).
                getChild("abstract", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
        Element newAbstract = new Element("abstract");
        List<Element> ablist = abstract_.getChildren();
        for (int i = 0; i < ablist.size(); i++) {
            Element e = ablist.get(i);
            Element ww = e.setName("sec").detach();
            newAbstract.addContent(ww);
        }
        header.addContent(newAbstract);

        /** if(abstract_.getChildren().size()>0){
         for (Element e :
         abstract_.getChildren()) {
         System.out.println(e.getContent());
         //todo 需要测试有些特殊文章摘要中由title的情况
         newAbstract.addContent(e.setName("sec").detach());
         }}
         header.addContent(newAbstract);**/


        //设置作者
        List<Element> persNames = new ArrayList<>();
        ElementUtil.extractElements(root.getChild("teiHeader", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")),
                "persName", persNames);
        Element author_group = new Element("author-group");
        if (persNames.size() > 0) {

            for (Element e :
                    persNames) {
                List<Element> surn = e.getChildren("surname", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
                ;
                List<Element> fore = e.getChildren("forename", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
                ;

                if (surn.size() == 0 || fore.size() == 0) {
                    continue;
                }

                Element name = new Element("name");
                name.addContent(new Element("surname").addContent(e.getChild("surname",
                        Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).getValue()));
                name.addContent(new Element("given-names").addContent(e.getChild("forename",
                        Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).getValue()));
                author_group.addContent(name);

            }
        }
        header.addContent(author_group);
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

        //为sec节点编号
        List<Element> secs = new ArrayList<>();
        ElementUtil.extractElements(newBody, "sec", secs);
        numberElement(secs);

        //删除n属性
        removeAttribute(newBody, "n");
    }

    @Override
    void fillBack(Element root, Element back) {
        Element ref_list = new Element("ref-list");
        Element old_back = root.getChild("text", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).
                getChild("back", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
        List<Element> backrenList = old_back.getChildren();
        int childrennum = backrenList.size();
        for (int i = 0; i < childrennum; i++) {
            Element div = backrenList.get(i);
            if (!div.getName().equals("div") || !div.getAttributeValue("type").equals("references"))
                old_back.removeContent(div);
            i--;
            childrennum--;
        }

        /**
         for (Element div :
         old_back.getChildren()) {
         if (!div.getName().equals("div") || !div.getAttributeValue("type").equals("references"))
         old_back.removeContent(div);
         }**/
        Element listBibl = old_back.getChild("div", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).
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
                Element person_group = new Element("person-group", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
                ;
                person_group.setAttribute("person-group-type", "author");
                try {
                    //设置作者

                    List<Element> authorScopeList = analytic.getChildren("author", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
                    if (authorScopeList.size() > 0) {
                        for (Element e :
                                authorScopeList) {
                            try {
                                Element persName = e.getChild("persName", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
                                List<Element> surn1 = persName.getChildren("surname", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
                                ;
                                List<Element> fore1 = persName.getChildren("forename", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
                                ;
                                if (surn1.size() == 0 || fore1.size() == 0) {
                                    continue;
                                }

                                Element name = new Element("name");
                                name.addContent(new Element("surname").addContent(persName.getChild("surname",
                                        Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).getValue()));
                                name.addContent(new Element("given-names").addContent(persName.getChild("forename",
                                        Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).getValue()));
                                person_group.addContent(name);
                            } catch (Exception xe) {
                                log.warn("authorERROR", xe);
                            }

                        }
                    }
                } catch (Exception xe) {
                    log.warn("authorERROR2", xe);
                }
                element_citation.addContent(person_group);
            }

            //设置其他
            Element monogr = biblStruct.getChild("monogr", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
            if (monogr != null) {
                Element source = monogr.getChild("title", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
                if (source != null && analytic == null && source.getAttributeValue("type") != null && source.getAttributeValue("type").equals("main")) {
                    element_citation.addContent(new Element("article-title").addContent(source.getValue()));

                    Element editor = monogr.getChild("editor", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
                    if (editor != null) {
                        element_citation.addContent(new Element("source", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).addContent(editor.getValue()));
                    }

                } else {
                    if (source != null) {
                        element_citation.addContent(new Element("source", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).addContent(source.getValue()));
                    }
                }
                Element imprint = monogr.getChild("imprint", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
                if (imprint != null) {
                    Element date = imprint.getChild("date", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
                    if (date != null) {
                        element_citation.addContent(new Element("year", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).addContent(date.getAttributeValue("when")));
                    }

                    List<Element> biblScopelist = imprint.getChildren("biblScope", Namespace.getNamespace("http://www.tei-c.org/ns/1.0"));
                    if (biblScopelist.size() > 0) {
                        for (Element e :
                                biblScopelist) {
                            if (e.getAttributeValue("unit").equals("volume")) {
                                element_citation.addContent(new Element("volume", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).addContent(e.getValue()));
                            }
                            if (e.getAttributeValue("unit").equals("issue")) {
                                element_citation.addContent(new Element("issue", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).addContent(e.getValue()));
                            }
                            if (e.getAttributeValue("unit").equals("page")) {
                                element_citation.addContent(new Element("fpage", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).addContent(e.getAttributeValue("from")));
                                element_citation.addContent(new Element("lpage", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).addContent(e.getAttributeValue("to")));
                            }
                        }
                    }
                }


            }

            ref.addContent(element_citation);
            ref_list.addContent(ref);
            //  System.out.println("这是"+ref.getChildren().get(1).getValue()+"这是"+ref.getAttributeValue("id"));
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
        ElementUtil.extractElements(root.getChild("text", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).
                getChild("body", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")), "p", super.paragraphs.get());
    }

    @Override
    void extractSentence(Element root) {
        ElementUtil.extractElements(root.getChild("text", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")).
                getChild("body", Namespace.getNamespace("http://www.tei-c.org/ns/1.0")), "s", super.sentences.get());
    }
}
