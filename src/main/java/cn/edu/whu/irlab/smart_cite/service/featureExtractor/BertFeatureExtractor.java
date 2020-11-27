package cn.edu.whu.irlab.smart_cite.service.featureExtractor;

import cn.edu.whu.irlab.smart_cite.vo.Article;
import cn.edu.whu.irlab.smart_cite.vo.BertPair;
import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/11/25 17:14
 * @desc 用于训练bert模型的特征收集器
 **/
@Service
public class BertFeatureExtractor {


    public List<BertPair> extract(Article article) {
        List<BertPair> bertPairs = new ArrayList<>();
        TreeMap<Integer, Sentence> sentenceTreeMap = article.getSentenceTreeMap();
        for (Map.Entry<Integer, Sentence> sentenceEntry :
                sentenceTreeMap.entrySet()) {
            Sentence s = sentenceEntry.getValue();
            for (RefTag reftag :
                    s.getRefList()) {
                //插入正例
                for (Sentence contextSentence :
                        reftag.getContextList()) {
                    bertPairs.add(new BertPair(contextSentence.getText(), reftag.getSentence().getText(), true));
                }
                //插入负例（前后各4句中非上下文的）
                int sentenceId = s.getId();
                List<String> context = new ArrayList<>(Arrays.asList(reftag.getContexts().split(",")));
                context.add(String.valueOf(reftag.getSentence().getId()));
                for (int i = sentenceId - 4; i < sentenceId + 4; i++) {
                    if (i > 0 && !context.contains(String.valueOf(i))) {
                        Sentence unContextSentence = sentenceTreeMap.get(i);
                        if (unContextSentence != null){
                            bertPairs.add(new BertPair(unContextSentence.getText(), reftag.getSentence().getText(), false));
                        }
                    }
                }

            }
        }

        return bertPairs;
    }
}
