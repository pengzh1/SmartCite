package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;

/**
 * 特征：当前句是不是目标引文所在的句子
 * Created by Lei Shengwei (Leo) on 2015/3/29.
 */
public class InSentenceFeature extends SVFeature<Integer> {


    @Override
    public Integer f(Sentence data, RefTag target) {
        boolean result = data.getId() == target.getSentence().getId();
        if (result) {
            return 1;
        } else {
            return 0;
        }
    }
}
