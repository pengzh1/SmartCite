package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.vo.Sentence;
import cn.edu.whu.irlab.smart_cite.vo.RefTag;

/**
 * 引文标记特征，包含两个特征
 * 0：包含目标引文的引文标记，则该句是引文上下文
 * 1：包含其他引文（即不包含目标引文），则该句很有可能不是目标引文的上下文
 * Created by Lei Shengwei (Leo) on 2015/4/13.
 */
public class RefFeature extends SVFeature<Integer> {
    private int type;
    public static final int REF_REF = 0;  //包含当前引文
    public static final int OTHER_REF = 1;    //包含其他引文（但不包含当前引文）

    public RefFeature(int type) {
        this.type = type;
    }

    @Override
    public Integer f(Sentence sentence, RefTag target) {
        switch (type) {
            case OTHER_REF:
                if (sentence.getRefList().size() > 0 && !contain(sentence, target)) {
                    return 0;
                } else if (sentence.getRefList().size() == 0) {
                    return 1;
                } else {
                    return 2;
                }
            case REF_REF:
                if (contain(sentence, target)) {
                    return 1;
                } else {
                    return 0;
                }
        }
        return 0;
    }

    /**
     * sent中是否包含ref
     *
     * @param sent
     * @param ref
     * @return
     */
    private boolean contain(Sentence sent, RefTag ref) {
        if (sent.getRefList().size() == 0) {
            return false;
        }

        for (RefTag r : sent.getRefList()) {
            if (r.getRefNum() > 0 && ref.getRefNum() > 0 && r.getRefNum() == ref.getRefNum()) { //指向引文一致
                return true;
            }
            if (r.getText().equals(ref.getText())) {  //标记内容一致
                return true;
            }
        }
        return false;
    }
}
