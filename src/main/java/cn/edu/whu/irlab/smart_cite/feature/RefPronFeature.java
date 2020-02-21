package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.leishengwei.jutils.Files.lines;

/**
 * 引文中包含指代词语
 * Created by lsw on 2015/4/29.
 */
public class RefPronFeature extends SVWordCorpusFeature<String, Integer> {
    private static List<String> pronList;
    private int start = CONTAIN;  //>=0则表示在钱多少个范围内
    public static final int CONTAIN = -1;   //包含
    public static final int START = 0;   //起始

    static {
        pronList = lines(new File("data/corpus/rpron.data"));
    }


    @Override
    public Collection<String> collect(RefTag target) {
        return pronList;
    }


    @Override
    public Integer match(Collection<String> corpus, RefTag target, Sentence data) {
        List<String> wl = Arrays.asList(target.getSentence().getText().toLowerCase().split(" "));
        for (String pron : corpus) {
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
