package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.leishengwei.jutils.Files.lines;

/**
 * 代词特征
 * Created by Lei Shengwei (Leo) on 2015/3/30.
 */
public class PronFeature extends SVWordCorpusFeature<String, Integer> {
    private static List<String> pronList;
    private int start = CONTAIN;  //>=0则表示在钱多少个范围内
    public static final int CONTAIN = -1;   //包含
    public static final int START = 0;   //起始

    public PronFeature() {
        start = CONTAIN;
    }

    public PronFeature(int start) {
        this.start = start;
    }

    static {
        pronList = lines(new File("data/corpus/hers.data"));
    }

    @Override
    public Collection<String> collect(RefTag target) {
        return pronList;
    }

    @Override
    public Integer match(Collection<String> strings, RefTag target, Sentence data) {
        List<String> wl = Arrays.asList(data.getText().toLowerCase().split(" "));
        for (String pron : strings) {
            if (start == CONTAIN) {
                if (wl.contains(pron)) {
                    return 1;
                }
            } else if (start == START) {
                if (wl.size() > 0 && wl.get(0).trim().equals(pron)) {
                    return 1;
                }
            } else {
                if (start >= 1 && wl.subList(0, wl.size() > start ? start : wl.size()).contains(pron)) {
                    return 1;
                }
            }
        }
        return 0;
    }
}
