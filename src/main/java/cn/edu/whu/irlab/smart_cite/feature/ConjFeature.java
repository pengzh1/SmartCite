package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;
import com.leishengwei.jutils.Arrays;
import com.leishengwei.jutils.Files;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 句子开头是否有特定的一些连词
 * Created by Lei Shengwei (Leo) on 2015/3/29.
 */
public class ConjFeature extends SVWordCorpusFeature<String, Integer> {
    private static List<String> conjList = new ArrayList<>();
    public static List<String> conjExamples = new ArrayList<>();

    static {
        conjList = Files.lines(new File("data/corpus/conj.data"));
    }

    int type = 0;
    public static final int START = 0;    //起始
    public static final int CONTAIN = -1;  //包含

    public ConjFeature(int type) {
        if (type < -1)
            type = -1;
        this.type = type;
    }

    @Override
    public Collection<String> collect(RefTag target) {
        return conjList;
    }

    //这里是包含还是起始
    @Override
    public Integer match(Collection<String> conjList, RefTag target, Sentence data) {
//        List<String> sa = Arrays.asList(data.toTextArr());
        for (String conj : conjList) {
            if (type == START) {    //起始
                if (data.getText().replace("-", "").trim().toLowerCase().startsWith(conj.trim().toLowerCase())) {
                    //TEST
                    conjExamples.add(label(target, data));
                    return 1;
                }
            } else if (type == CONTAIN) {    //包含
                if (data.getText().replace("-", "").toLowerCase().contains(conj.toLowerCase())) {
                    //TEST
                    conjExamples.add(label(target, data));
                    return 1;
                }
            }
        }

        return 0;
    }

    public static String label(RefTag target, Sentence current) {
        return (Arrays.list(target.getContexts().trim().split(",")).contains(current.getId() + "") ? 1 : 0) + ":" + current.getArticle().getNum() + ":" + target.getSentence().getId() + ":" + target.getId() + ":" + current.getId();
    }
}
