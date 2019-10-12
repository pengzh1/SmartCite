package cn.edu.whu.irlab.smart_cite.service.splitter.Impl;

import cn.edu.whu.irlab.smart_cite.service.splitter.Splitter;
import cn.edu.whu.irlab.smart_cite.util.TypeConverter;
import cn.edu.whu.irlab.smart_cite.vo.ReferenceVo;
import com.aliasi.sentences.MedlineSentenceModel;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gcr19
 * @date 2019-08-16 19:56
 * @desc 句子分割实现类
 **/
@Service("splitter")
public class SplitterImpl implements Splitter {

    private List<Element> paragraphs = new ArrayList<>();

    static final TokenizerFactory TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
    static final SentenceModel SENTENCE_MODEL  = new MedlineSentenceModel();

    @Override
    public Element splitSentence(Element paragraph) {
        XMLOutputter xmlOutputter=new XMLOutputter();
        String text=xmlOutputter.outputElementContentString(paragraph);

        List<String> tokenList = new ArrayList<String>();
        List<String> whiteList = new ArrayList<String>();
        Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(text.toCharArray(),0,text.length());
        tokenizer.tokenize(tokenList,whiteList);

        String[] tokens = new String[tokenList.size()];
        String[] whites = new String[whiteList.size()];
        tokenList.toArray(tokens);
        whiteList.toArray(whites);
        int[] sentenceBoundaries = SENTENCE_MODEL.boundaryIndices(tokens,whites);

        if (sentenceBoundaries.length < 1) {
            System.out.println("No sentence boundaries found.");
            return null;
        }

        List<String> sentenceList=new ArrayList<>();
        int sentStartTok = 0;
        int sentEndTok = 0;

        for (int i = 0; i < sentenceBoundaries.length; ++i) {
            String sentence="";
            sentEndTok = sentenceBoundaries[i];
            for (int j=sentStartTok; j<=sentEndTok; j++) {
                sentence+=tokens[j]+whites[j+1];
            }
            sentStartTok = sentEndTok+1;
//            System.out.println("SENTENCE "+(i+1)+": ");
//            System.out.println(sentence);
            sentenceList.add(sentence);
        }
        return sentences2paragraph(sentenceList);
    }

    @Override
    public List<ReferenceVo> selectReference(Element article){
        List<ReferenceVo> referenceList=new ArrayList<>();
        Element ref_list=article.getChild("back").getChild("ref-list");
        for (Element ref:
             ref_list.getChildren()) {
            if ("ref".equals(ref.getName())){
                ReferenceVo referenceVo=xml2pojo4Ref(ref);
                referenceList.add(referenceVo);
            }
        }
        return referenceList;
    }

    private ReferenceVo xml2pojo4Ref(Element ref){
        ReferenceVo referenceVo=new ReferenceVo();
        Element nlm_citation=ref.getChild("nlm-citation");
        referenceVo.setID(ref.getAttributeValue("id"));
        referenceVo.setTitle(nlm_citation.getChild("article-title").getText());
        return referenceVo;
    }

    /**
     * 将分句结果组装成一个段落节点
     * @param sentenceList 句子列表
     * @return <p>节点
     */
    private Element sentences2paragraph(List<String> sentenceList){
        String prefix="<s>";
        String suffix="</s>";
        Element paragraph=new Element("p");
        for (int i = 0; i <sentenceList.size() ; i++) {
            String s=prefix+sentenceList.get(i)+suffix;
            Element sentence= TypeConverter.str2xml(s);
            paragraph.addContent(sentence.clone());
        }
        return paragraph;
    }
}
