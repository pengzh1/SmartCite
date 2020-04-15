package cn.edu.whu.irlab.smart_cite.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 词汇素材
 * Created by Lei Shengwei (Leo) on 2015/4/6.
 */
public class Words {
    public static final List<String> PREP = new ArrayList<>();
    public static final List<String> SP = new ArrayList<>();
    static {
        PREP.add("in");
        PREP.add("by");
        PREP.add("of");
        PREP.add("with");
        PREP.add("on");
        PREP.add("from");
        PREP.add("to");
        PREP.add("as");
        PREP.add("like");
        PREP.add("unlike");
        PREP.add("un-like");
        PREP.add("following");
        PREP.add("both");
        PREP.add("while");
        PREP.add("when");
        PREP.add("than");
        PREP.add("is");
        PREP.add("was");
        PREP.add("were");
        PREP.add("are");
        PREP.add("or");
        PREP.add("the");

    }

    static {
        SP.add(",");
        SP.add(".");
        SP.add(";");
        SP.add("!");
        SP.add("...");
        SP.add("\"");
    }


}
