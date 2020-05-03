package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.vo.Sentence;
import cn.edu.whu.irlab.smart_cite.vo.RefTag;

import java.util.Optional;

/**
 * 章节特征
 * 包含三个特征：
 * type=1:句子以章节标题为开头
 * type=2: 前一个句子以章节标题开头
 * type=3:后一个句子以章节标题开头
 * Created by Lei Shengwei (Leo) on 2015/3/30.
 */
public class SectionFeature extends SVFeature<Integer> {
    int type = 0;
    public static final int START = 1;
    public static final int PRE_START = 2;
    public static final int NEXT_START = 3;

    public SectionFeature(int type) {
        this.type = type;
    }

    @Override
    public Integer f(Sentence data, RefTag target) {
        boolean r = false;
        switch (type) {
            case START:
                r = data.getSectionIndex() == 1;
                break;
            case PRE_START:
                r = data.getSectionIndex() == 2;
                break;
            case NEXT_START:
                Optional<Sentence> next = data.next();
                if (next.isPresent()) {
                    r = next.get().getSectionIndex() == 1;
                }
                break;
            default:
                r = false;
        }
        if (r) {
            return 1;
        } else {
            return 0;
        }
    }
}
