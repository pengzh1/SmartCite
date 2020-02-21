package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;

import java.util.Map;

/**
 * 使用词表集合抽取出一系列特征
 * Created by Lei Shengwei (Leo) on 2015/4/9.
 */
public abstract class SArrayCorpusFeature<V> extends SArrayFeature<V> {
    /**
     * 收集素材
     *
     * @param s
     */
    public abstract Map collect(RefTag s);

    /**
     * 匹配函数
     *
     * @param entry
     * @param r
     * @return
     */
    public abstract V[] match(Map entry, Sentence data, RefTag r);

    @Override
    public V[] f(Sentence sentence, RefTag target) {
        Map corpus = collect(target);
        return match(corpus, sentence, target);
    }
}
