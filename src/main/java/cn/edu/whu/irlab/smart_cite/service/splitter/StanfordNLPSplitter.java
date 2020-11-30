package cn.edu.whu.irlab.smart_cite.service.splitter;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/11/30 14:20
 * @desc stanfordNLP 分割句子
 **/
@Service("stanfordNLPSplitter")
public class StanfordNLPSplitter extends SplitterImpl {


    @Autowired
    private StanfordCoreNLP pipeline;

    @Override
    public List<String> splitSentences(String text) {
        List<String> sentenceList = new ArrayList<>();
        if (text == null || text.equals("") || text.length() == 1) {
            return sentenceList;
        }
        CoreDocument document = new CoreDocument(text);
        pipeline.annotate(document);
        for (CoreSentence sentence :
                document.sentences()) {
            sentenceList.add(sentence.text());
        }
        return sentenceList;
    }
}
