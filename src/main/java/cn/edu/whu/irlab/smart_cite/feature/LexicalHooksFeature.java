package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Lexical Hooks 特征：句子中是否存在当前文章中高频的大写字母开头的词语
 * 需要首先在构造Article的时候就统计好
 * Created by Lei Shengwei (Leo) on 2015/3/30.
 */
public class LexicalHooksFeature extends SVWordCorpusFeature<Map.Entry<String, Integer>, Integer> {

    @Override
    public Collection<Map.Entry<String, Integer>> collect(RefTag target) {
        return target.getSentence().getArticle().lexicalHooks();
    }

    @Override
    public Integer match(Collection<Map.Entry<String, Integer>> items, RefTag target, Sentence data) {
        List<String> sl = Arrays.asList(data.toTextArr());
        for (Map.Entry item : items) {
            if (sl.contains(item.getKey())) {
                return 1;
            }
        }
        return 0;
    }
}
