package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;

import java.util.Optional;

/**
 * 选用特征：是不是目标引文所在句子的邻居（相邻），包含两个特征：
 * type=1:是不是目标引文所在句子的下一句
 * type=2:是不是目标引文所在句子的上一句
 * Created by Lei Shengwei (Leo) on 2015/3/30.
 */
public class RefNeighborFeature extends SVFeature<Integer> {
    int type;

    public RefNeighborFeature(int type) {
        this.type = type;
    }

    @Override
    public Integer f(Sentence data, RefTag target) {
        boolean r = false;
        switch (type) {
            case 1:
                r = data.previous().flatMap((s) -> Optional.of(s.getId() == target.getSentence().getId())).get();
                break;
            case 2:
                r = data.next().flatMap((s) -> Optional.of(s.getId() == target.getSentence().getId())).get();
                break;
        }
        if (r) {
            return 1;
        } else {
            return 0;
        }
    }
}
