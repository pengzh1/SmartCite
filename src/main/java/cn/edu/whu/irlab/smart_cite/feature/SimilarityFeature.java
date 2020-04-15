package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;
import cn.edu.whu.irlab.smart_cite.vo.WordItem;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.leishengwei.jutils.Collections.nGram;

/**
 * NGram得到的词在引文句中间出现的次数(1,2,3,4)
 * Created by Lei Shengwei (Leo) on 2015/4/8.
 */
public class SimilarityFeature extends SVFeature<Double> {
    int n;
    public static final int GRAM_1 = 1;
    public static final int GRAM_2 = 2;
    public static final int GRAM_3 = 3;

    public SimilarityFeature(int n) {
        if (n < 1) {
            n = 1;
        } else if (n > 3) {
            n = 3;
        }
        this.n = n;
    }

    @Override
    public Double f(Sentence data, RefTag target) {
        return nGramSim(target.getSentence().getWordList(), data.getWordList(), n);
    }

    /**
     * N-gram Jaccard 相似度
     * TODO 函数有问题，计算出的结果很多大于1的，好奇怪啊
     *
     * @param s1
     * @param s2
     * @return
     */
    private Double nGramSim(List<WordItem> s1, List<WordItem> s2, int n) {
        List<String> list1 = s1.stream().map(v -> v.getWord()).collect(Collectors.toList());
        List<String> list2 = s2.stream().map(v -> v.getWord()).collect(Collectors.toList());
        list1 = nGram(list1, n);
        list2 = nGram(list2, n);
        Set<String> temp = new HashSet<>();
        temp.addAll(list1);
        temp.addAll(list2);//并集
        list1.retainAll(list2);   //交集
        Set<String> list1Set = new HashSet<>();
        list1Set.addAll(list1);
        list1Set.retainAll(list2);
        if (temp.size() == 0) { //防止除数为0（不大可能出现）
            return 0.0;
        }
        double v = (double) list1Set.size() / temp.size();
        v = round(v, 4);
        return v; //这里肯定是小于等于1的啊
    }

    public static double round(double fv, int n) {
        return (new BigDecimal(fv)).setScale(n, 4).doubleValue();
    }

}
