package cn.edu.whu.irlab.smart_cite.service.splitter.Impl;

import cn.edu.whu.irlab.smart_cite.service.splitter.Splitter;
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
    public void splitSentence(Element paragraph) {

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
            return;
        }

        List<String> sentenceList=new ArrayList<>();

        int sentStartTok = 0;
        int sentEndTok = 0;
        for (int i = 0; i < sentenceBoundaries.length; ++i) {
            sentEndTok = sentenceBoundaries[i];
            System.out.println("SENTENCE "+(i+1)+": ");
            for (int j=sentStartTok; j<=sentEndTok; j++) {
                sentenceList.add(tokens[j]+whites[j+1]);
                System.out.print(tokens[j]+whites[j+1]);
            }
            System.out.println();
            sentStartTok = sentEndTok+1;
        }
    }


    @Override
    public void selectParagraph(Element element) {
        if (element.getName().equals("p")){
            if (element.getChild("xref")!=null)
                paragraphs.add(element);
        }else {
            List<Element> temp=element.getChildren();
            for (Element e:
                    temp) {
                selectParagraph(e);
            }
        }
    }

    @Override
    public List<Element> getParagraphs() {
        return paragraphs;
    }
}
