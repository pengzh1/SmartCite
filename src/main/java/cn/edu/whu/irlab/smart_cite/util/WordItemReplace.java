package cn.edu.whu.irlab.smart_cite.util;

import cn.edu.whu.irlab.smart_cite.enums.Words;
import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.WordItem;

import java.util.List;
import java.util.stream.Collectors;

import static com.leishengwei.jutils.Collections.in;

/**
 * 词项过滤：包括空白词项删除，括号删除，引文分组替换，非句法成分替换（两种方法）
 * Created by Lei Shengwei (Leo) on 2015/3/30.
 */
public class WordItemReplace {


    /**
     * 空白WordItem删除和括号删除
     *
     * @param wordList
     */
    public static List<WordItem> replaceBracket(List<WordItem> wordList) {
        wordList = wordList.stream().filter((v) -> !v.getWord().trim().equals("")).collect(Collectors.toList());
        for (int i = 1; i < wordList.size() - 1; i++) {
            if (wordList.get(i).getType() == WordItem.WordType.Ref) {
                if (wordList.get(i - 1).getWord().endsWith("(")) {
                    wordList.get(i).getRef().setLeft("(");
                    wordList.remove(i - 1);
                    i--;
                }
                if (wordList.get(i + 1).getWord().startsWith(")")) {
                    wordList.get(i).getRef().setRight(")");
                    wordList.remove(i + 1);
                }
            }
        }
        return wordList;
    }

    /**
     * 引文分组替换
     * 找出符合R(;R)+模式替换为WordItem(WordRef)类型 //todo R(;R)+ 是什么模式？
     *
     * @param wordList
     */

    public static void replaceGroup(List<WordItem> wordList) {
        for (int i = 0; i < wordList.size() - 2; i++) {
            WordItem item = wordList.get(i);
            if (item.getType() == WordItem.WordType.Ref) {  //R
                int index = 0;
                while (i < wordList.size() - 2 && wordList.get(i + 1).getWord().equals(";") && wordList.get(i + 2).getType() == WordItem.WordType.Ref) { //todo 判定条件可能不通用，“-”分割符号如何处理？
                    if (index  ==0) {
                        RefTag ref = item.getRef();
                        item = new WordItem(WordItem.WordType.G_REF, WordItem.G_REF);
                        item.addRef(ref);
                        wordList.set(i, item);
                    }
                    item.addRef(wordList.get(i + 2).getRef());
                    wordList.remove(i + 2);
                    wordList.remove(i + 1);
                    index++;
                }
//                if (index > 0) {
//                    System.out.println(item);
//                }
            }
        }
//        print(wordList);
    }

    /**
     * 非句法成分替换
     *
     * @param wordList
     */
    public static void replaceNonSyntactic(List<WordItem> wordList) {

        for (int i = 0; i < wordList.size(); i++) {
            WordItem item = wordList.get(i);
            if (item.getType() == WordItem.WordType.Ref || item.getType() == WordItem.WordType.G_REF) { //引文类型
                if (item.getRef().getText().matches(".+?\\(\\d{4}\\).*?")) {  //作者,(年份)格式 todo？？Ittycheriah and Roukos, 2005没有匹配成功？？
                    continue;
                }
                if (i == 0) {  //第一个位置
                    continue;
                }
                if (i > 0 && in(Words.SP, wordList.get(i - 1).getWord().toLowerCase())) {   //第一个位置
                    continue;
                }
                if (i > 0 && in(Words.PREP, wordList.get(i - 1).getWord().toLowerCase())) {   //前一个字符是in,of,by etc
                    continue;
                }
                /*
                 * 将citation拼到前一个词上（citation_word），并删除该citation标记 todo 为啥要做这步??
                 */
                wordList.get(i - 1).setType(item.getType() == WordItem.WordType.G_REF ? WordItem.WordType.Word_G_Ref : WordItem.WordType.WordRef);
                wordList.get(i - 1).setRefs(item.getRefs());
                wordList.remove(i);
                i--;
            }
        }
    }

//    /**
//     * 使用分词信息替换非句法成分
//     *
//     * @param list
//     */
//    public void replaceNonSyntactic2(List<WordItem> list) {
//        if (list.stream().filter((v) -> v.getType() == WordItem.WordType.Ref || v.getType() == WordItem.WordType.G_REF).count() == 0) {
//            return;
//        }
//        //词性标记
//        WordSegment.wordSegment(list);
//        //根据<citation>前面一个词的postag来判断这个引文标记在句子中是否有作用
//        int i = 0;
//        while (i < list.size()) {
//            WordItem item = list.get(i);
//            if (item.getRef().getText().matches(".+?\\(\\d{4}\\).*?")) {  //作者,(年份)格式
//                continue;
//            }
//            if (list.indexOf(item) == 0) {  //第一个位置
//                continue;
//            }
//            if (i > 0 && in(Words.SP, list.get(i - 1).getWord().toLowerCase())) {   //第一个位置
//                continue;
//            }
//            //前面一个词是介词，词性为IN或者TO
//            if (list.get(i - 1).getTag().equals("IN") || list.get(i - 1).getTag().equals("TO")) {
//                i++;
//                continue;
//            }
//            //前一个词是动词且是动词主动时态（是VB但是非VBN）
//            if (list.get(i - 1).getTag().startsWith("VB") && !list.get(i - 1).getTag().equals("VBN")) {
//                i++;
//                continue;
//            }
//            //前一个词是连词如While、However（CC连词）
//            if (list.get(i - 1).getTag().equals("CC")) {
//                i++;
//                continue;
//            }
//
//            //前面是标点符号
//            if (list.get(i - 1).getTag().length() == 1 && !Character.isLetter(list.get(i - 1).getTag().charAt(0))) {
//                i++;
//                continue;
//            }
//            //前面是e.g., cf.
//            if (list.get(i - 1).getWord().trim().equals("e.g.") || list.get(i - 1).getWord().trim().equals("cf.")) {
//                i++;
//                continue;
//            }
//            /*
//             * 否则将citation拼到前一个词上（citation_word），并删除该citation标记
//             */
//            list.get(i - 1).setType(item.getType() == WordItem.WordType.G_REF ? WordItem.WordType.Word_G_Ref : WordItem.WordType.WordRef);
//            list.get(i - 1).setRefs(item.getRefs());
//            list.remove(i);
//            i--;
//        }
//    }

}

//}
