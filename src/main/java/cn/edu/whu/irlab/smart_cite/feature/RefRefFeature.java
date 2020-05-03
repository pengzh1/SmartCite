package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 是否有指向引文标记指代名词短语(Ref)的指代(Ref)
 * Created by Lei Shengwei (Leo) on 2015/3/29.
 */
public class RefRefFeature extends SVWordCorpusFeature<String, Integer> {

    @Override
    public Collection<String> collect(RefTag target) {
        return target.getRefPhrases();
    }

    @Override
    public Integer match(Collection<String> refPhrases, RefTag target, Sentence data) {
        List<String> sl = Arrays.asList(data.toTextArr());
        for (String refRef : refPhrases) {
            if (sl.contains(refRef)) {
                return 1;
            }
        }
        return 0;
    }
}
