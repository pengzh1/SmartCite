package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;


import java.util.Optional;

/**
 * 特征：他周围的句子类型(1,0,-1)
 * 包含如下特征：
 * type=1:他前面不是目标引文所在的句子是不是引文句
 * type=2:他后面不是目标引文所在的句子是不是引文句
 * Created by Lei Shengwei (Leo) on 2015/3/30.
 */
public class RefPositionFeature extends SVFeature<Integer> {
    int type;
    public static final int PRE = 1;
    public static final int NEXT = 2;

    public RefPositionFeature(int type) {
        this.type = type;
    }

    @Override
    public Integer f(Sentence data, RefTag target) {
        boolean b1 = false; //待判断句子不是目标引文所在句子
        boolean b2 = false; //待判断句子是引文句
        Optional<Sentence> ts = Optional.empty();
        switch (type) {
            case PRE:
                ts = data.previous();
                break;
            case NEXT:
                ts = data.next();
                break;
        }
        if (ts.isPresent()) {
            b1 = ts.get().getId() != target.getSentence().getId();
            b2 = ts.get().getCType().trim().equals("r");
        }
        if (!b1) { //前面或者后面的句子 是 目标引文句
            return -1;
        }
        if (b2) {   //前面或者后面的句子 是 引文句但 不是 目标引文句
            return 0;
        }
        return 1;  //待判断句子 不是引文句

    }
}
