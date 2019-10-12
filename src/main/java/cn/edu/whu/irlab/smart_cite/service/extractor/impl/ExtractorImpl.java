package cn.edu.whu.irlab.smart_cite.service.extractor.impl;

import cn.edu.whu.irlab.smart_cite.service.extractor.Extractor;
import cn.edu.whu.irlab.smart_cite.service.splitter.Splitter;
import cn.edu.whu.irlab.smart_cite.util.TypeConverter;
import cn.edu.whu.irlab.smart_cite.vo.RecordVo;
import cn.edu.whu.irlab.smart_cite.vo.ReferenceVo;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.output.XMLOutputter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author gcr19
 * @date 2019-08-16 19:08
 * @desc 抽取器实现类
 **/
@Service("extractor")
public class ExtractorImpl implements Extractor {

    private List<Element> paragraphs = new ArrayList<>();

    @Resource(name = "splitter")
    private Splitter splitter;

    public Set<RecordVo> extract(Element article) {
        Set<RecordVo> result = new HashSet<>();

        List<ReferenceVo> referenceVoList = selectReference(article);

        List<Element> paragraphs = extractParagraphs(article);

        for (Element p :
                paragraphs) {

        }

        return result;
    }


    @Override
    public List<ReferenceVo> selectReference(Element article) {
        List<ReferenceVo> referenceList = new ArrayList<>();
        Element ref_list = article.getChild("back").getChild("ref-list");
        for (Element ref :
                ref_list.getChildren()) {
            if ("ref".equals(ref.getName())) {
                ReferenceVo referenceVo = xml2pojo4Ref(ref);
                referenceList.add(referenceVo);
            }
        }
        return referenceList;
    }


    @Override
    public List<Element> extractParagraphs(Element element) {
        if (element.getName().equals("p")) {
            boolean hasXref = false;
            for (Element e :
                    element.getChildren()) {
                if ("xref".equals(e.getName()) && "bibr".equals(e.getAttributeValue("ref-type"))) {
                    hasXref = true;
                }
            }
            if (hasXref)
                paragraphs.add(element);
        } else {
            for (Element e :
                    element.getChildren()) {
                extractParagraphs(e);
            }
        }
        return paragraphs;
    }

    @Override
    public Element cleanLabel(Element paragraph) {
        for (Element sentence :
                paragraph.getChildren()) {
            List<Content> contents = sentence.getContent();
            for (int i = 0; i < contents.size(); i++) {
                Content c = contents.get(i);
                if (c.getCType().equals(Content.CType.Element)) {
                    Element e = (Element) c;
                    if ("xref".equals(e.getName())) {
                        if (!"bibr".equals(e.getAttribute("ref-type").getValue())) {
                            Text text = new Text(e.getValue());
                            sentence.setContent(i, text);
                        }
                    } else {
                        Text text = new Text(e.getValue());
                        sentence.setContent(i, text);
                    }
                }
            }
        }

        //合并Text节点
        XMLOutputter xmlOutputter = new XMLOutputter();
        String text = "<p>" + xmlOutputter.outputElementContentString(paragraph) + "</p>";
        Element paragraphAfterClean = TypeConverter.str2xml(text);

        return paragraphAfterClean;
    }

    @Override
    public Set<RecordVo> extractRefContext(Element paragraphAfterClean) {

        Set<RecordVo> records = new HashSet<>();
        List<Element> sentences = paragraphAfterClean.getChildren();
        Stack<String> stringStack = new Stack<>();

        for (int i = 0; i < sentences.size(); i++) {
            List<Element> xrefElements = sentences.get(i).getChildren("xref");
            if (xrefElements.size() != 0) {
                Set<RecordVo> recordVosHasLabel = handleSentenceHasLabel(sentences.get(i));
                records.addAll(recordVosHasLabel);//正向遍历时保存，反向不保存
                records.addAll(handleOtherSentences(recordVosHasLabel, stringStack, true));
            } else {
                stringStack.push(sentences.get(i).getValue());
            }
        }

        for (int i = sentences.size() - 1; i >= 0; i--) {
            List<Element> xrefElements = sentences.get(i).getChildren("xref");
            if (xrefElements.size() != 0) {
                if (!stringStack.empty()){
                    Set<RecordVo> recordVosHasLabel = handleSentenceHasLabel(sentences.get(i));
                    records.addAll(handleOtherSentences(recordVosHasLabel, stringStack, false));
                }
            } else {
                stringStack.push(sentences.get(i).getValue());
            }
        }
        return records;
    }

    /**
     * 处理含有引文标识的句子
     * @param s 含有引文标识的句子节点
     * @return 含有引文标识的记录集合
     */
    private Set<RecordVo> handleSentenceHasLabel(Element s) {
        List<Content> contents = s.getContent();

        int startIndex;
        int endIndex;
        Set<RecordVo> recordVosHasLabel = new HashSet<>();

        for (int i = 0; i < contents.size(); i++) {
            Content c = contents.get(i);
            if (c.getCType().equals(Content.CType.Element)) {
                //记录位置
                startIndex = i;
                endIndex = i;
                while (contents.get(endIndex+1).getValue().equals(";")) {
                    if (endIndex + 2 >= contents.size()) {
                        break;
                    } else {
                        endIndex += 2;
                    }
                }

                List<Content> before = contents.subList(0, startIndex);
                List<Content> after = contents.subList(endIndex + 1, contents.size());

                List<Content> cite = contents.subList(startIndex, endIndex+1);
                List<String> ref_rid = new ArrayList<>();
                for (Content ci :
                        cite) {
                    if (ci.getCType().equals(Content.CType.Element)) {
                        Element element = (Element) ci;
                        ref_rid.add(element.getAttributeValue("rid"));
                    }
                }
                String sentence = content2string(before) + "[#]" + content2string(after);
                RecordVo recordVo = new RecordVo(ref_rid, sentence, 0);
                recordVosHasLabel.add(recordVo);
                i = endIndex;
            }
        }
        return recordVosHasLabel;
    }

    private String content2string(List<Content> contents) {
        String output = "";
        for (Content c :
                contents) {
            output += c.getValue();
        }
        return output;
    }


    private Set<RecordVo> handleOtherSentences(Set<RecordVo> recordVosHasLabel, Stack<String> stringStack, boolean forward) {
        Set<RecordVo> records = new HashSet<>();
        int num = 0;
        //封装引文对象
        while (!stringStack.empty()) {
            if (forward) {
                num--;
            } else {
                num++;
            }
            String sentence = stringStack.pop();
            for (RecordVo r :
                    recordVosHasLabel) {
                RecordVo recordVo = new RecordVo(r.getRef_rid(), sentence, num);
                records.add(recordVo);
            }
        }
        return records;
    }

    public Set<RecordVo> matchRefTitle(Set<RecordVo> recordVos, List<ReferenceVo> referenceList) {
        for (ReferenceVo ref :
                referenceList) {
            for (RecordVo r :
                    recordVos) {
                List<String> titles = new ArrayList<>();
                for (String s :
                        r.getRef_title()) {
                    if (ref.getID().equals(s)) {
                        titles.add(ref.getTitle());
                    }
                }
                r.setRef_title(titles);
            }
        }
        return recordVos;
    }

    private ReferenceVo xml2pojo4Ref(Element ref) {
        ReferenceVo referenceVo = new ReferenceVo();
        Element nlm_citation = ref.getChild("nlm-citation");
        referenceVo.setID(ref.getAttributeValue("id"));
        referenceVo.setTitle(nlm_citation.getChild("article-title").getText());
        return referenceVo;
    }
}
