package cn.edu.whu.irlab.smart_cite.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 英文正则分词
 * Created by Lei Shengwei (Leo) on 2015/4/6.
 */
public class WordTokenizer {
    /**
     * 英文分词,这种分词方法分的特别开，比如).之类的表达  todo 效果不好的情况下，需要改用成熟的分词工具
     * @param text
     * @return
     */
    public static String[] split(String text) {
        //查看一下有哪些乱起八糟的字符干扰分词
//        String regex2 = "[^\\.\\w-\\$%\\[\\].,;\"'?():-\u00AD_`\\s]+";
//        String s = toStr(",", findAll(text, regex2));
//        if (isNotEmpty(s)) {
//            System.out.println(s);
//        }
        text = text.replaceAll("\u00AD|—", "-")
                .replaceAll("é|è", "e").replace("à", "a").replace("ü", "u").replace("ô", "o").replace("ï", "i");
        List<String> r = new ArrayList<>();
        //还有一些乱起八糟的字符：­
        //regex ï    é à è ü ô ™
        String regex = "([A-Z]\\.)+|[\\w]+(-\\w+|\\+\\+)*|\\$?\\d+(\\.\\d+)?%?|\\.\\.\\.|[\\]\\[.,;\"'?():-_`*#&!]";
        Matcher mt = Pattern.compile(regex).matcher(text);
        while (mt.find()) {
            r.add(mt.group());
        }
        return r.toArray(new String[]{});
    }
}
