package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;

/**
 * 在目标引文之前则为1,否则为0
 * Created by leoray on 15/11/15.
 */
public class PositionTypeFeature extends SVFeature<Integer> {
    DistanceFeature distanceFeature = new DistanceFeature();

    @Override
    public Integer f(Sentence sentence, RefTag target) {
        return distanceFeature.f(sentence, target) > 0 ? 1 : 0;
    }
}
