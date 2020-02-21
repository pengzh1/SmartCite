package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;

/**
 * 是否在同一个段落内
 * Created by Lei Shengwei (Leo) on 2015/3/30.
 */
public class InParaFeature extends SVFeature<Integer> {

    @Override
    public Integer f(Sentence data, RefTag target) {
        boolean r = data.getPNum() == target.getSentence().getPNum();
        if (r) {
            return 1;
        } else {
            return 0;
        }
    }
}
