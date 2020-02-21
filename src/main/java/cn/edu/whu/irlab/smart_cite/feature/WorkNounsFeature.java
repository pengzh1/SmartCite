package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;
import com.leishengwei.jutils.Files;
import com.leishengwei.jutils.Strings;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 是否包含一些Work Noun 组合
 * Created by Lei Shengwei (Leo) on 2015/3/30.
 */
public class WorkNounsFeature extends SVFeature<Integer> {
    private static List<String> D = Arrays.asList(new String[]{"this", "that", "those", "these", "his", "her", "their", "such", "previous"});
    private static List<String> W;
    public static List<String> workNounsExamples = new ArrayList<>();
    public static final int START = 0;
    public static final int CONTAIN = -1;
    private int type;

    public WorkNounsFeature(int type) {
        if (type < -1)  //<-1没有意义
            type = -1;
        if (type == 1)  //至少为2，否则无法构成WorkNouns
            type = 2;
        this.type = type;
    }

    static {
        W = Files.lines(new File("data/corpus/worknouns.data"));
    }

    @Override
    public Integer f(Sentence data, RefTag target) {
        List<String> list = Arrays.asList(data.toTextArr());
        if (type == START) {
            if (list.size() >= 2 && D.contains(list.get(0).replace("-", "").trim()) && W.contains(list.get(1).replace("-", "").trim())) {
                workNounsExamples.add(Strings.s("%s:%s:%s:%s", label(data, target), data.getArticle().getNum(), target.getId(), data.getId()));
                return 1;
            } else {
                return 0;
            }
        } else {
            int size = 0;
            if (type == CONTAIN) {
                size = list.size() - 1;
            } else if (type > 0) {
                size = type > list.size() ? list.size() : type;
            }
            for (int i = 0; i < size-1; i++) {
                if (D.contains(list.get(i).replace("-", "")) && W.contains(list.get(i + 1).replace("-", ""))) {
                    workNounsExamples.add(Strings.s("%s:%s:%s:%s", label(data, target), data.getArticle().getNum(), target.getId(), data.getId()));
                    return 1;
                }
            }

        }
        return 0;
    }

    //temp
    private int label(Sentence data, RefTag target) {
        return com.leishengwei.jutils.Arrays.list(target.getContexts().split(",")).contains(data.getId() + "") ? 1 : 0;
    }
}
