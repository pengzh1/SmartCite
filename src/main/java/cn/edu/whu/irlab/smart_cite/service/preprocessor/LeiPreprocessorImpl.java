package cn.edu.whu.irlab.smart_cite.service.preprocessor;

import cn.edu.whu.irlab.smart_cite.service.grobid.GrobidService;
import cn.edu.whu.irlab.smart_cite.util.ElementUtil;
import cn.edu.whu.irlab.smart_cite.util.TypeConverter;
import cn.edu.whu.irlab.smart_cite.util.WriteUtil;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/6 11:29
 * @desc lei数据预处理实现
 **/
@Service
public class LeiPreprocessorImpl extends PreprocessorImpl {

    private static final Logger logger = LoggerFactory.getLogger(LeiPreprocessorImpl.class);


    //需要删除的节点名列表
    private static final String[] filterTag = {"footnote", "page", "doubt", "table", "tr", "td", "appendix", "figure",
            "frontmatter", "meta", "pdfmetadata", "ocrmetadata"};
    private static final List<String> filterTagList = Arrays.asList(filterTag);
    @Autowired
    private GrobidService grobidService;


    @Override
    public Element parseXML(Element root, File file) {
        Element newRoot = reformat(root);
        newRoot.setAttribute("status", root.getAttributeValue("status"));
        writeFile(newRoot, REFORMATTED, file);
        return newRoot;
    }

    @Override
    public void filterTags(Element root) {
        super.filterTags(root, filterTagList);
    }

    @Override
    public void extractXref(Element element) {
    }

    @Override
    public void removeElementNotXref() {
    }

    @Override
    void fillHeader(Element root, Element header) {
        Element firstpageheader = root.getChild("header").getChild("firstpageheader");
        //设置 title
        header.addContent(new Element("title-group").addContent(new Element("article-title").
                addContent(firstpageheader.getChildText("title"))));
        //设置 authors
        List<String> authors = Arrays.asList(firstpageheader.getChildText("authors").split(";"));
        Element author_group = new Element("author-group");
        for (String s :
                authors) {
            String[] nameString = s.split(" ");
            Element name = new Element("name");
            name.addContent(new Element("surname").addContent(nameString[0]));
            name.addContent(new Element("given-name").addContent(nameString[1]));
            author_group.addContent(name);
        }
        header.addContent(author_group);
        //设置abstract
        header.addContent(new Element("abstract").addContent(header.getChildText("abstract")));
    }

    @Override
    void fillBody(Element root, Element newBody) {

        for (Element section :
                root.getChild("body").getChildren("section")) {
            newBody.addContent(section.clone());
        }
        reNameElement(newBody, "section", "sec");
        reNameElement(newBody, "subsection", "sec");
        reNameElement(newBody, "subsubsection", "sec");
        reNameElement(newBody, "ref", "xref");
        reNameAttribute(newBody, "sec", "number", "id");
        reNameAttribute(newBody, "p", "number", "id");
        reNameAttribute(newBody, "xref", "number", "id");
        reNameAttribute(newBody, "s", "number", "id");
        reNameAttribute(newBody, "xref", "ref_num", "rid");
        List<Element> sentences = ElementUtil.extractElements(newBody, "s");
        for (Element s :
                sentences) {
            s.removeAttribute("section");
            s.removeAttribute("subsection");
            s.removeAttribute("subsubsection");

            s.setAttribute("sec", s.getParentElement().getParentElement().getAttributeValue("id"));
        }

        List<Element> secs = ElementUtil.extractElements(newBody, "sec");
        for (Element sec :
                secs) {
            sec.addContent(new Element("title").addContent(sec.getAttributeValue("title")));
            sec.removeAttribute("title");
        }

    }

    @Override
    void fillBack(Element root, Element back) {
        Element references = root.getChild("body").getChild("references");
        Element ref_list = new Element("ref-list");
        for (Element ref :
                references.getChildren()) {
            ref_list.addContent(parseCitation(ref));
        }
        back.addContent(ref_list);
    }

    private Element parseCitation(Element ref) {
        Element newRef = new Element("ref");
        newRef.setAttribute("id", ref.getAttributeValue("number"));

        String stringRef = grobidService.parseCitation(ref.getValue());
        Element biblStruct = null;
        try {
            biblStruct = TypeConverter.str2xml(stringRef);
        } catch (JDOMException | IOException e) {
            logger.error(e.getMessage());
        }
        Element element_citation = new Element("element-citation");

        //设置标题
        Element analytic = biblStruct.getChild("analytic");
        if (analytic != null) {
            Element title = analytic.getChild("title");
            if (title != null) {
                element_citation.addContent(new Element("article-title").addContent(title.getValue()));
            }
        }

        newRef.addContent(element_citation);

        return newRef;
    }
}
