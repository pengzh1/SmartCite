package cn.edu.whu.irlab.smart_cite.service.featureExtractor;

import cn.edu.whu.irlab.smart_cite.util.WriteUtil;
import cn.edu.whu.irlab.smart_cite.vo.Article;
import cn.edu.whu.irlab.smart_cite.vo.BertPair;
import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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


    /**
     * @param article
     * @param dataType
     * @return
     * @auther gcr19
     * @desc 提起用于bert模型的数据
     **/
    public List<BertPair> extract(Article article, int dataType) {
        List<BertPair> bertPairs = new ArrayList<>();
        TreeMap<Integer, Sentence> sentenceTreeMap = article.getSentenceTreeMap();
        JSONArray errorList = new JSONArray();

        for (Map.Entry<Integer, Sentence> sentenceEntry :
                sentenceTreeMap.entrySet()) {
            Sentence s = sentenceEntry.getValue();
            for (RefTag reftag :
                    s.getRefList()) {
                //插入正例
                for (Sentence contextSentence :
                        reftag.getContextList()) {
                    switch (dataType) {
                        case 1:
                            bertPairs.add(new BertPair(contextSentence.getText(), reftag.getSentence().getText(), true));
                            break;
                        case 2:
                            if (reftag.getReference() != null && reftag.getReference().getArticle_title() != null) {
                                bertPairs.add(new BertPair(contextSentence.getText(), reftag.getSentence().getText() + reftag.getReference().getArticle_title(), true));
                            } else {
                                JSONObject error = new JSONObject();
                                error.put("article", s.getArticle().getName());
                                error.put("refTagId", reftag.getId());
                                error.put("refTag", reftag.getText());
                                errorList.add(error);
                                bertPairs.add(new BertPair(contextSentence.getText(), reftag.getSentence().getText(), true));
                            }
                            break;
                        default:
                            break;
                    }
                }
                //插入负例（前后各4句中非上下文的）
                int sentenceId = s.getId();
                List<String> context = new ArrayList<>(Arrays.asList(reftag.getContexts().split(",")));
                context.add(String.valueOf(reftag.getSentence().getId()));
                for (int i = sentenceId - 4; i < sentenceId + 4; i++) {
                    if (i > 0 && !context.contains(String.valueOf(i))) {
                        Sentence unContextSentence = sentenceTreeMap.get(i);
                        if (unContextSentence != null) {
                            switch (dataType) {
                                case 1:
                                    bertPairs.add(new BertPair(unContextSentence.getText(), reftag.getSentence().getText(), false));
                                    break;
                                case 2:
                                    if (reftag.getReference() != null) {
                                        bertPairs.add(new BertPair(unContextSentence.getText(), reftag.getSentence().getText() + reftag.getReference().getArticle_title(), false));
                                    } else {
                                        bertPairs.add(new BertPair(unContextSentence.getText(), reftag.getSentence().getText(), false));
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }

            }
        }
        WriteUtil.writeList("test/testOutput/referenceEmpty.json", errorList, true);
        System.out.println(errorList);
        return bertPairs;
    }
}
