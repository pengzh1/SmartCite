package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;

import java.util.Collection;

/**
 * 词表类特征
 * 需要实现两个方法，1一个方法负责搜集词表，另外一个方法负责和当前句进行匹配
 * Created by Lei Shengwei (Leo) on 2015/3/29.
 */
public abstract class SVWordCorpusFeature<ITEM, V> extends SVFeature<V> {
    /**
     * 搜集词表
     *
     * @return
     */
    public abstract Collection<ITEM> collect(RefTag target);

    /**
     * 是否匹配
     *
     * @param items
     * @return
     */
    public abstract V match(Collection<ITEM> items, RefTag target, Sentence data);

    @Override
    public V f(Sentence data, RefTag target) {
        Collection<ITEM> items = collect(target);
        return match(items, target, data);
    }

}
