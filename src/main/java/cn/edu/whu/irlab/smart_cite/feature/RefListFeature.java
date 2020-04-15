package cn.edu.whu.irlab.smart_cite.feature;


import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;

/**
 * 特征：是否包含多个引用
 * 0-1特征：
 * 该特征是否可以转化为数值特征：就是所在句子引文的个数
 * Created by Lei Shengwei (Leo) on 2015/3/30.
 */
public class RefListFeature extends SVFeature<Integer> {
    int type;
    public static final int TWO = 0;    //0-1特征
    public static final int NUMBER = 1; //数字特征

    public RefListFeature(int type) {
        this.type = type;
    }

    @Override
    public Integer f(Sentence data, RefTag target) {
        int refSize = target.getSentence().getRefList().size();
        switch (type) {
            case NUMBER:
                return refSize;
            case TWO:
                if (refSize > 1) {
                    return 1;
                } else {
                    return 0;
                }

        }
        return refSize;
    }
}
