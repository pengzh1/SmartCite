package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.service.statisticVisitor.StatisticVisitor;
import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;
import com.leishengwei.jutils.Collections;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 计算出目标引文所在句子和 N-Gram的IDF？值还是相似度？
 * Created by Lei Shengwei (Leo) on 2015/3/30.
 */
public class NGramFeature extends SArrayCorpusFeature<Integer> {
    private int n;

    public NGramFeature(int n) {
        if (n < 1) {
            n = 1;
        }
        if (n > 3) {
            n = 3;
        }
        this.n = n;
    }

    @Override
    public Map<String, Integer> collect(RefTag s) {
        return StatisticVisitor.getNGram(n);
    }

    @Override
    public Integer[] match(Map map, Sentence data, RefTag r) {
        List<Integer> rList = new ArrayList<>();
        List<String> wordList = r.getSentence().getWordList().stream().map(v -> v.getWord().trim()).collect(Collectors.toList());
        List<String> dataWL = data.getWordList().stream().map(v -> v.getWord().trim().replace("-", "")).collect(Collectors.toList());
        wordList = com.leishengwei.jutils.Collections.nGram(wordList, n);
        dataWL = Collections.nGram(dataWL, n);
        Set<String> set = new HashSet<>(wordList);
        set.retainAll(dataWL);
        set.forEach(v -> {
            if (map.containsKey(v)) {
                rList.add((Integer) map.get(v));
            }
        });
        return rList.toArray(new Integer[]{});
    }


}
