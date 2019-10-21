package cn.edu.whu.irlab.smart_cite.service.extractor.impl;

import cn.edu.whu.irlab.smart_cite.enums.CiteMarkEnum;
import cn.edu.whu.irlab.smart_cite.vo.RecordVo;
import cn.edu.whu.irlab.smart_cite.vo.ReferenceVo;
import org.jdom2.Content;
import org.jdom2.Element;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author gcr19
 * @date 2019-10-20 15:21
 * @desc
 **/
@Service("ExtractorOfPlos")
public class ExtractorOfPlos extends ExtractorImpl {

    private final String RefLabelName = "xref";
    private final String RefTypeLabelName = "ref-type";
    private final String RefLabelRidAttrName = "rid";

    public Map<String, ReferenceVo> extractReferences() {
        Element ref_list = super.article.getChild("back").getChild("ref-list");
        Map<String, ReferenceVo> referencesMap = new HashMap<>();
        for (Element ref :
                ref_list.getChildren()) {
            if ("ref".equals(ref.getName())) {
                ReferenceVo referenceVo = xml2pojo4Ref(ref);
                referencesMap.put(referenceVo.getID(), referenceVo);
            }
        }
        return referencesMap;
    }

    public List<Element> extractParagraphs(Element article) {
        return super.extractParagraphs(article, RefLabelName, RefTypeLabelName);
    }

    public Element cleanLabel(Element paragraph) {
        return super.cleanLabel(paragraph, RefLabelName, RefTypeLabelName);
    }

    @Override
    public Set<RecordVo> extractRefContext(Element paragraphAfterClean,CiteMarkEnum citeMarkEnum) {
        return super.extractRefContext(paragraphAfterClean, citeMarkEnum,RefLabelName);
    }

    @Override
    public CiteMarkEnum identifyCiteMark(Element paragraph) {
        return super.identifyCiteMark(paragraph, RefLabelName);
    }

    /**
     * 处理含有引文标识的句子
     *
     * @param s 含有引文标识的句子节点
     * @return 含有引文标识的记录集合
     */
    protected Set<RecordVo> handleSentenceHasNotNumLabel(Element s) {
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
                while (contents.get(endIndex + 1).getValue().length() < 3) {
                    if (endIndex + 2 >= contents.size()) {
                        break;
                    } else {
                        endIndex += 2;
                    }
                }
                List<Content> before = contents.subList(0, startIndex);
                List<Content> after = contents.subList(endIndex + 1, contents.size());
                List<Content> cite = contents.subList(startIndex, endIndex + 1);
                List<String> ref_rid = new ArrayList<>();
                for (Content ci :
                        cite) {
                    if (ci.getCType().equals(Content.CType.Element)) {
                        Element element = (Element) ci;
                        ref_rid.add(element.getAttributeValue(RefLabelRidAttrName));
                    }
                }
                String sentence = super.content2string(before) + "[#]" + super.content2string(after);
                RecordVo recordVo = new RecordVo(ref_rid, sentence, 0);
                recordVosHasLabel.add(recordVo);
                i = endIndex;
            }
        }
        return recordVosHasLabel;
    }

    @Override
    protected Set<RecordVo> handleSentenceHasNumLabel(Element s) {
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
                while (contents.get(endIndex + 1).getValue().length() < 3) {
                    if (endIndex + 2 >= contents.size()) {
                        break;
                    } else {
                        endIndex += 2;
                    }
                }
                List<Content> before = contents.subList(0, startIndex);
                List<Content> after = contents.subList(endIndex + 1, contents.size());
                List<Content> cite = contents.subList(startIndex, endIndex + 1);
                List<String> ref_rid = new ArrayList<>();

                String operator = "";

                for (int j = 0; j < cite.size(); j++) {
                    Content ci = cite.get(j);
                    if (ci.getCType().equals(Content.CType.Element)) {
                        Element element = (Element) ci;
                        if (operator.equals("-")) {
                            Element pre = (Element) cite.get(j - 2);
                            ref_rid.addAll(extractRidBetween2Elements(pre, element, RefLabelRidAttrName));
                        }else {
                            ref_rid.add(element.getAttributeValue(RefLabelRidAttrName));
                        }
                    } else {
                        operator = ci.getValue();
                    }
                }
                String sentence = super.content2string(before) + "[#]" + super.content2string(after);
                RecordVo recordVo = new RecordVo(ref_rid, sentence, 0);
                recordVosHasLabel.add(recordVo);
                i = endIndex;
            }
        }
        return recordVosHasLabel;
    }

    protected ReferenceVo xml2pojo4Ref(Element ref) {
        ReferenceVo referenceVo = new ReferenceVo();
        Element nlm_citation = ref.getChild("nlm-citation");
        if (nlm_citation == null) {
            nlm_citation = ref.getChild("element-citation");
        }
        referenceVo.setID(ref.getAttributeValue("id"));
        referenceVo.setLabel(ref.getChild("label").getValue());
        referenceVo.setTitle(nlm_citation.getChild("article-title").getText());
        return referenceVo;
    }


}
