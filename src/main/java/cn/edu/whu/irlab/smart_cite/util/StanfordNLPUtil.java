package cn.edu.whu.irlab.smart_cite.util;

import cn.edu.whu.irlab.smart_cite.vo.Sentence;
import cn.edu.whu.irlab.smart_cite.vo.WordItem;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/11/30 15:41
 * @desc standfordNLP 工具
 **/
@Service
public class StanfordNLPUtil {

    @Autowired
    private StanfordCoreNLP pipeline;

    /**
     *@auther gcr19
     *@desc 分词与词性标注
     *@param text
     *@return
     **/
    public List<WordItem> tokenizeAndPosTags(String text) {
        CoreDocument coreDocument = new CoreDocument(text);
        pipeline.annotate(coreDocument);
        List<WordItem> words = new ArrayList<>();
        for (CoreLabel coreLabel :
                coreDocument.tokens()) {
            words.add(new WordItem(coreLabel.word(),coreLabel.tag()));
        }
        return words;
    }


}
