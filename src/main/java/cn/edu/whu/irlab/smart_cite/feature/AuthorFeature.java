package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.vo.Sentence;
import cn.edu.whu.irlab.smart_cite.vo.RefTag;

import java.util.Arrays;
import java.util.List;

/**
 * 是否包含目标引文的作者
 * Created by Lei Shengwei (Leo) on 2015/3/29.
 */
public class AuthorFeature extends SVFeature<Integer> {

    @Override
    public Integer f(Sentence data, RefTag target) {
        List<String> sa = Arrays.asList(data.toTextArr());
        for (String author : target.getAuthors()) {
            boolean result = sa.contains(author);
            if (result) {
                return 1;
            }
        }
        return 0;
    }
}
