package cn.edu.whu.irlab.smart_cite.service.preprocessor;

import cn.edu.whu.irlab.smart_cite.util.ElementUtil;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.Content;
import org.jdom2.Element;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

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

                List<Element> cite_spans = p.getChildren("cite_spans");

                for (Element cite_span :
                        cite_spans) {
                    String mention = cite_span.getChild("mention").getValue();
                    for (Content content :
                            contents) {
                        if (content.getCType().equals(Content.CType.Element)) {
                            Element element = (Element) content;
                            if (element.getValue().contains(mention)){
                                Element xref=new Element("xref");
                                xref.setAttribute("rid",element.getChild("ref_id").getValue());
                                xref.setAttribute("id",element.getAttributeValue("id"));
                                xref.setAttribute("ref-type","bibr");
                                xref.setText(mention);
                                element.addContent(xref);
                            }
                        }
                    }
                }
                p.removeChild("text");
                p.addContent(contents);
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

    }

    @Override
    void fillBody(Element root, Element body) {

    }

    @Override
    void fillBack(Element root, Element back) {

    }

    @Override
    public void extractXref(Element element) {
        ElementUtil.extractElements(element, "cite_spans", xrefs.get());
    }
}
