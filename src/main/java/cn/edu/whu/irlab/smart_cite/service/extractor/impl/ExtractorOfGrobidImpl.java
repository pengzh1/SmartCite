package cn.edu.whu.irlab.smart_cite.service.extractor.impl;

import cn.edu.whu.irlab.smart_cite.enums.CiteMarkEnum;
import cn.edu.whu.irlab.smart_cite.service.extractor.ExtractorOfGrobid;
import cn.edu.whu.irlab.smart_cite.service.extractor.ExtractorOfPlos;
import cn.edu.whu.irlab.smart_cite.vo.RecordVo;
import cn.edu.whu.irlab.smart_cite.vo.ReferenceVo;
import org.jdom2.Content;
import org.jdom2.Element;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author gcr19
 * @date 2019-10-20 15:17
 * @desc
 **/
@Service("extractorOfGrobid")
public class ExtractorOfGrobidImpl extends ExtractorImpl implements ExtractorOfGrobid {

    private final String RefLabelName = "ref";
    private final String RefTypeLabelName = "type";
    private final String RefLabelRidAttrName = "target";

    public void extractReferences() {
        Element back = super.article.getChild("text").getChild("back");
        Element ref_list = null;
        for (Element e :
                back.getChildren()) {
            if (e.getAttribute("type").equals("references")) {
                ref_list = e.getChild("listBibl");
            }
        }
        for (Element ref :
                ref_list.getChildren()) {
            if ("biblStruct".equals(ref.getName())) {
                ReferenceVo referenceVo = xml2pojo4Ref(ref);
                super.referencesMap.put(referenceVo.getID(), referenceVo);
            }
        }
//        return referencesMap;
    }

    public void extractParagraphs(Element article) {
        super.extractParagraphs(article, RefLabelName, RefTypeLabelName);
    }

    @Override
    public Element cleanLabel(Element paragraph) {
        return super.cleanLabel(paragraph, RefLabelName, RefTypeLabelName);
    }

    @Override
    public Set<RecordVo> extractRefContext(Element paragraphAfterClean, CiteMarkEnum citeMarkEnum) {
        return super.extractRefContext(paragraphAfterClean, citeMarkEnum, RefLabelName);
    }

    @Override
    protected Set<RecordVo> handleSentenceHasNumLabel(Element s) {
        return null;
    }

    @Override
    protected CiteMarkEnum identifyCiteMark(Element paragraph) {
        return identifyCiteMark(paragraph, RefLabelName);
    }

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
                while (contents.get(endIndex + 1).getCType().equals(Content.CType.Element)) {
                    if (endIndex + 1 >= contents.size()) {
                        break;
                    } else {
                        endIndex += 1;
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

    protected ReferenceVo xml2pojo4Ref(Element ref) {
        ReferenceVo referenceVo = new ReferenceVo();
        Element nlm_citation = ref.getChild("analytic");
        referenceVo.setID(ref.getAttributeValue("xml:id"));
        referenceVo.setTitle(nlm_citation.getChild("title").getText());
        return referenceVo;
    }

}
