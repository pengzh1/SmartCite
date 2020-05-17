package cn.edu.whu.irlab.smart_cite.service.preprocessor;

import cn.edu.whu.irlab.smart_cite.util.ElementUtil;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/5/4 17:17
 * @desc Json转换得到的XML数据预处理
 **/
@Slf4j
@Service
public class JsonXmlPreprocessorImpl extends PreprocessorImpl {

    //需要删除的节点名列表
    private static final String[] filterTag = {"ref_entries", "suffix", "affiliation", "email", "ref_spans"};
    private static final List<String> filterTagList = Arrays.asList(filterTag);

    @Override
    void extractParagraphs(Element root) {
        ElementUtil.extractElements(root, "body_text", super.paragraphs.get());
    }

    @Override
    void extractSentence(Element root) {
        ElementUtil.extractElements(root, "s", sentences.get());
    }

    @Override
    void splitSentences(File file) {
        for (Element p :
                paragraphs.get()) {
            try {
                List<Element> contents = lingPipeSplitter.splitSentences(p.getChild("text"));
                p.removeChild("text");
                p.addContent(contents);

                List<Element> cite_spans = p.getChildren("cite_spans");

                for (Element cite_span :
                        cite_spans) {
                    String mention = cite_span.getChild("mention").getValue();
                    for (Content content :
                            contents) {
                        if (content.getCType().equals(Content.CType.Element)) {
                            Element element = (Element) content;
                            if (element.getValue().contains(mention)) {
                                Element xref = new Element("xref");
                                xref.setAttribute("rid", cite_span.getChild("ref_id").getValue());
                                xref.setAttribute("ref-type", "bibr");
                                xref.setText(mention);
                                element.addContent(xref);
                            }
                        }
                    }
                }
                p.removeChildren("cite_spans");
            } catch (Exception e) {
                log.error("[error message] " + e.getMessage() + " At [paragraph] " + p.getAttributeValue("id") + " [article] " + file.getName(), e);
            }
        }
    }

    @Override
    public void filterTags(Element root) {
        super.filterTags(root, filterTagList);
    }

    @Override
    public void removeElementNotXref() {
    }

    @Override
    void fillHeader(Element root, Element header) {
        Element metaData = root.getChild("metadata");
        //设置标题
        Element title_group = new Element("title-group").
                addContent(new Element("title").addContent(metaData.getChild("title").getValue()));//todo 暂未考虑covid19数据是否有副标题的情况
        header.addContent(title_group);

        //todo 未发现摘要数据
        //设置摘要
        Element newAbstract = new Element("abstract").addContent("");
        header.addContent(newAbstract);

        //设置作者
        List<Element> authors = new ArrayList<>();
        ElementUtil.extractElements(metaData, "authors", authors);
        Element author_group = new Element("author-group");
        for (Element e :
                authors) {
            Element name = new Element("name");
            name.addContent(new Element("surname").addContent(e.getChild("first").getValue()));
            name.addContent(new Element("given-names").addContent(e.getChild("last").getValue()));
            author_group.addContent(name);
        }
        header.addContent(author_group);
    }

    @Override
    void fillBody(Element root, Element body) {
        Map<String, List<Element>> sectionMap = new HashMap<>();

        for (Element p :
                this.paragraphs.get()) {
            p.setName("p");
            String sectionName = p.getChild("section").getValue();
            if (sectionMap.containsKey(sectionName)) {
                sectionMap.get(sectionName).add(p.clone());
            } else {
                List<Element> elements = new ArrayList<>();
                elements.add(p.clone());
                sectionMap.put(sectionName, elements);
            }
        }

        int secId = 0;

        for (Map.Entry<String, List<Element>> section :
                sectionMap.entrySet()) {
            Element sec = new Element("sec");
            sec.addContent(new Element("title").addContent(section.getValue().get(0).getChild("section").getValue()));
            sec.addContent(section.getValue());
            sec.setAttribute("id", String.valueOf(++secId));
            body.addContent(sec);
        }

        paragraphs.get().clear();
        sentences.get().clear();
        ElementUtil.extractElements(body, "p", paragraphs.get());
        ElementUtil.extractElements(body, "s", sentences.get());
    }

    @Override
    void fillBack(Element root, Element back) {
        Element ref_list = new Element("ref-list");
        List<Element> oldRefList=root.getChild("bib_entries").getChildren();
        for (Element bibref :
                oldRefList) {
            Element ref = new Element("ref");
            ref.setAttribute("id", bibref.getName());

            Element element_citation = new Element("element-citation");

            //设置标题
            element_citation.addContent(new Element("article-title").addContent(bibref.getChild("title").getValue()));

            ref.addContent(element_citation);
            ref_list.addContent(ref);
        }
        back.addContent(ref_list);
    }

    @Override
    public void extractXref(Element element) {
        ElementUtil.extractElements(element, "xref", xrefs.get());
    }
}
