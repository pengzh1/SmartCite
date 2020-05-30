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

            List<Element> sentences = lingPipeSplitter.splitSentences(p.getChild("text"));

            p.removeChild("text");

            Queue<Element> cite_spans = new LinkedList<>(p.getChildren("cite_spans"));

            if (!cite_spans.isEmpty()) {
                int origin = 0;
                for (Element sentence :
                        sentences) {
                    if (cite_spans.isEmpty()) {
                        break;
                    }

                    //获取句子内容
                    String sentence_value = sentence.getValue();
                    sentence.removeContent();
                    int preElementEnd = 0;
                    while (!cite_spans.isEmpty()) {
                        Element top = cite_spans.peek();
                        int start = Integer.parseInt(top.getChildText("start"));

                        //判定cite_span是否在句子内部
                        if (start < origin || start > origin + sentence_value.length()) {
                            break;
                        }

                        top = cite_spans.poll();
                        //获取cite_span值
                        int end = Integer.parseInt(top.getChildText("end"));
                        Element mention = top.getChild("mention");
                        Element text = top.getChild("text");
                        String cite_span_value = null;
                        if (mention != null) {
                            cite_span_value = mention.getValue();
                        } else if (text != null) {
                            cite_span_value = text.getValue();
                        }

                        //置入cite_span
                        String theFront;
                        String current = sentence_value.substring(start - origin, end - origin);
                        String theAfter = sentence_value.substring(end - origin);
                        if (preElementEnd == 0) {
                            theFront = sentence_value.substring(0, start - origin);
                        } else {
                            if (sentence.getContentSize() == 0) {
                                System.out.println("error");
                            }
                            sentence.removeContent(sentence.getContentSize() - 1);
                            theFront = sentence_value.substring(preElementEnd - origin, start - origin);
                        }

                        //验证是否获取正确并置入
                        if (current.equals(cite_span_value)) {
                            sentence.addContent(theFront);
                            Element xref = new Element("xref");
                            xref.setAttribute("rid", top.getChild("ref_id").getValue());
                            xref.setAttribute("ref-type", "bibr");
                            xref.setText(current);
                            sentence.addContent(xref);
                            sentence.addContent(theAfter);
                        } else {
                            System.out.println("error");//todo 分句结果可能做过trim 或者空格位置并不精准 有偏差 throw new IllegalArgumentException();
                        }
                        preElementEnd = end;
                    }
                    origin += sentence_value.length() + 1;
                    if (sentence.getContentSize()==0){
                        sentence.addContent(sentence_value);
                    }
                }
            }
            p.addContent(sentences);
            p.removeChildren("cite_spans");
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
            p.removeChild("section");
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
            sec.addContent(new Element("title").addContent(section.getKey()));
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
        List<Element> oldRefList = root.getChild("bib_entries").getChildren();
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
