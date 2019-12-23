package cn.edu.whu.irlab.smart_cite.service.extractor.impl;

import cn.edu.whu.irlab.smart_cite.enums.CiteMarkEnum;
import cn.edu.whu.irlab.smart_cite.exception.ExtractException;
import cn.edu.whu.irlab.smart_cite.service.extractor.Extractor;
import cn.edu.whu.irlab.smart_cite.service.splitter.Splitter;
import cn.edu.whu.irlab.smart_cite.util.TypeConverter;
import cn.edu.whu.irlab.smart_cite.vo.RecordVo;
import cn.edu.whu.irlab.smart_cite.vo.ReferenceVo;
import org.apache.commons.collections4.CollectionUtils;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Text;
import org.jdom2.output.XMLOutputter;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @author gcr19
 * @date 2019-08-16 19:08
 * @desc 抽取器实现类
 **/
public abstract class ExtractorImpl implements Extractor {

    protected Element article;
    protected List<Element> paragraphs;
    protected Map<String, ReferenceVo> referencesMap;

    @Resource(name = "splitter")
    private Splitter splitter;

    @Override
    public void init(Element article) {
        paragraphs = new ArrayList<>();
        referencesMap = new HashMap<>();
        this.article = article;
        extractReferences();
        extractParagraphs(article);
    }

    @Override
    public List<RecordVo> extract() {
        List<RecordVo> result = new ArrayList<>();
        CiteMarkEnum citeMarkEnum = identifyCiteMark(paragraphs.get(0));
        String articleId = extractArticleId();

        for (Element paragraph :
                paragraphs) {
            Element pAfterSplit = splitter.splitSentence(paragraph);
            Element pAfterClean = cleanLabel(pAfterSplit);
            result.addAll(extractRefContext(pAfterClean, citeMarkEnum));
        }

        result = matchRefTitle(result, referencesMap);
        result = matchArticleId(result, articleId);
        return result;
    }



    protected abstract void extractReferences();

    protected abstract void extractParagraphs(Element article);

    void extractParagraphs(Element article, String RefLabelName, String RefTypeLabelName) {
        if (article.getName().equals("p")) {
            boolean hasXref = false;
            for (Element e :
                    article.getChildren()) {
                if (RefLabelName.equals(e.getName()) && "bibr".equals(e.getAttributeValue(RefTypeLabelName))) {
                    hasXref = true;
                    break;
                }
            }
            if (hasXref)
                paragraphs.add(article);
        } else {
            for (Element e :
                    article.getChildren()) {
                extractParagraphs(e);
            }
        }
    }

    protected abstract Element cleanLabel(Element paragraph);

    protected Element cleanLabel(Element paragraph, String RefLabelName, String RefTypeLabelName) {
        for (Element sentence :
                paragraph.getChildren()) {
            List<Content> contents = sentence.getContent();
            for (int i = 0; i < contents.size(); i++) {
                Content c = contents.get(i);
                if (c.getCType() == Content.CType.Element) {
                    Element e = (Element) c;
                    if (RefLabelName.equals(e.getName())) {
                        if (!"bibr".equals(e.getAttribute(RefTypeLabelName).getValue())) {
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
        Element paragraphAfterClean = null;
        try {
            paragraphAfterClean = TypeConverter.str2xml(text);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paragraphAfterClean;
    }

    public abstract List<RecordVo> extractRefContext(Element paragraphAfterClean, CiteMarkEnum citeMarkEnum);

    public List<RecordVo> extractRefContext(Element paragraphAfterClean, CiteMarkEnum citeMarkEnum, String RefLabelName) {

        List<RecordVo> records = new ArrayList<>();
        List<Element> sentences = paragraphAfterClean.getChildren();
        Stack<String> stringStack = new Stack<>();

        Map<String, List<RecordVo>> recordMap = new HashMap<>();
        //正向遍历
        for (int i = 0; i < sentences.size(); i++) {
            List<Element> xrefElements = sentences.get(i).getChildren(RefLabelName);
            if (xrefElements.size() != 0) {
                List<RecordVo> recordVosHasLabel = null;
                if (citeMarkEnum.equals(CiteMarkEnum.notNUM)) {
                    recordVosHasLabel = handleSentenceHasNotNumLabel(sentences.get(i));
                } else if (citeMarkEnum.equals(CiteMarkEnum.NUM)) {
                    recordVosHasLabel = handleSentenceHasNumLabel(sentences.get(i));
                }

                List<String> strings = new ArrayList<>(stringStack);
                Collections.reverse(strings);
                for (RecordVo r :
                        recordVosHasLabel) {
                    String key = r.getRef_rid().toString() + r.getSentence().length();
                    List<RecordVo> recordsForward = new ArrayList<>();
                    int num = 0;
                    for (String s :
                            strings) {
                        num--;
                        recordsForward.add(new RecordVo(r.getRef_rid(), s, num));
                    }
                    Collections.reverse(recordsForward);
                    recordsForward.add(r);
                    recordMap.put(key, recordsForward);
                }
            } else {
                stringStack.push(sentences.get(i).getValue());
            }
        }

        //反向遍历
        stringStack.clear();
        for (int i = sentences.size() - 1; i >= 0; i--) {
            List<Element> xrefElements = sentences.get(i).getChildren(RefLabelName);
            if (xrefElements.size() != 0) {
                if (!stringStack.empty()) {
                    List<RecordVo> recordVosHasLabel = null;
                    if (citeMarkEnum.equals(CiteMarkEnum.notNUM)) {
                        recordVosHasLabel = handleSentenceHasNotNumLabel(sentences.get(i));
                    } else if (citeMarkEnum.equals(CiteMarkEnum.NUM)) {
                        recordVosHasLabel = handleSentenceHasNumLabel(sentences.get(i));
                    }
                    List<String> strings = new ArrayList<>(stringStack);
                    Collections.reverse(strings);
                    for (RecordVo r :
                            recordVosHasLabel) {
                        String key = r.getRef_rid().toString() + r.getSentence().length();
                        List<RecordVo> recordsBackward = new ArrayList<>();
//                        recordsBackward.add(r);
                        int num = 0;
                        for (String s :
                                strings) {
                            num++;
                            recordsBackward.add(new RecordVo(r.getRef_rid(), s, num));
                        }
                        if (recordMap.containsKey(key)) {
                            recordMap.get(key).addAll(recordsBackward);
                        } else {
                            recordMap.put(key, recordsBackward);
                        }
                    }
                }
            } else {
                stringStack.push(sentences.get(i).getValue());
            }
        }

        for (Map.Entry<String, List<RecordVo>> entry :
                recordMap.entrySet()) {
            records.addAll(entry.getValue());
        }

        return records;
    }

    public List<RecordVo> matchRefTitle(List<RecordVo> recordVos, Map<String, ReferenceVo> referencesMap) {
        for (RecordVo r :
                recordVos) {
            List<String> ref_rid = r.getRef_rid();
            List<String> titles = new ArrayList<>();
            for (String s :
                    ref_rid) {
                ReferenceVo ref = referencesMap.get(s);
                titles.add(ref.getTitle());
            }
            r.setRef_title(titles);
        }
        return recordVos;
    }

    protected abstract ReferenceVo xml2pojo4Ref(Element ref) throws ExtractException;

    protected abstract List<RecordVo> handleSentenceHasNotNumLabel(Element s);

    protected abstract List<RecordVo> handleSentenceHasNumLabel(Element s);

    protected String content2string(List<Content> contents) {
        String output = "";
        for (Content c :
                contents) {
            output += c.getValue();
        }
        return output;
    }

    protected abstract CiteMarkEnum identifyCiteMark(Element paragraph);

    protected CiteMarkEnum identifyCiteMark(Element paragraph, String RefLabelName) {
        Element xref = paragraph.getChild(RefLabelName);
        String valueOfXref = xref.getValue();
        if (valueOfXref.length() < 6) {
            return CiteMarkEnum.NUM;
        } else {
            return CiteMarkEnum.notNUM;
        }
    }

    protected List<String> extractRidBetween2Elements(Element pre, Element next, String RefLabelRidAttrName) {
        List<String> ref_rid = new ArrayList<>();
        String preRid = pre.getAttributeValue(RefLabelRidAttrName);
        String nextRid = next.getAttributeValue(RefLabelRidAttrName);

        int preLabel = Integer.valueOf(referencesMap.get(preRid).getLabel());
        int nextLabel = Integer.valueOf(referencesMap.get(nextRid).getLabel());

        for (Map.Entry<String, ReferenceVo> entry :
                referencesMap.entrySet()) {
            int label = Integer.valueOf(entry.getValue().getLabel());
            if (label >= preLabel || label <= nextLabel) {
                ref_rid.add(entry.getValue().getID());
            }
        }
        return ref_rid;
    }

    /**
     * 抽取文档Id
     *
     * @return 文档Id
     */
    private String extractArticleId() {
        return article.getChild("front").getChild("article-meta").getChild("article-id").getValue();
    }

    /**
     * 为抽取结果匹配文档Id
     *
     * @param recordVos 未匹配文档Id的抽取结果
     * @param articleId 文档Id
     * @return 匹配文档Id后的抽取结果
     */
    private List<RecordVo> matchArticleId(List<RecordVo> recordVos, String articleId) {
        for (RecordVo r :
                recordVos) {
            r.setArticle_id(articleId);
        }
        return recordVos;
    }
}
