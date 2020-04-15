package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;

/**
 * 特征：和目标引文所在句子的距离
 * Created by Lei Shengwei (Leo) on 2015/3/29.
 */
public class DistanceFeature extends SVFeature<Integer> {

    @Override
    public Integer f(Sentence data, RefTag target) {
        return data.distance(target.getSentence());
    }
}
