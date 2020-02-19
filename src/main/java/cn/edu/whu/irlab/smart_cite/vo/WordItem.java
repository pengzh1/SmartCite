package cn.edu.whu.irlab.smart_cite.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/14 11:34
 * @desc TODO
 **/
@Data
public class WordItem {

    public static final String REF = "REF"; //默认引文替换字符
    public static final String G_REF = "GREF";    //引文组替换字符
    private WordType type = WordType.Word;  //词项类型
    private int index; //start from 0
    private String word;    //词语内容
    private List<RefTag> refs = new LinkedList<>();  //如果type=WordType.Ref，则该项大小为1且不为空；如果type=WordType.WordRef，则有可能有多项
    private Sentence sentence;
    private String tag; //词性

    public WordItem() {
    }

    public WordItem(WordType type, String word) {
        this.type = type;
        this.word = word;
    }

    public WordItem(WordType type, String word, RefTag ref) {
        this.type = type;
        this.word = word;
        this.refs.add(ref);
    }

    public RefTag getRef() {
        return refs.get(0);
    }


    public static WordItem ref(String text) {
        return new WordItem(WordType.Ref, text);
    }

    public static WordItem ref(String text, RefTag ref) {
        return new WordItem(WordType.Ref, text, ref);
    }

    public static List<WordItem> words(String... words) {
        if (words == null || words.length == 0) {
            return new ArrayList<>();
        }
        List<WordItem> wordItems = new ArrayList<>();
        for (String word : words) {
            wordItems.add(word(word));
        }
        return wordItems;
    }

    public static WordItem word(String word) {
        return new WordItem(WordType.Word, word);
    }

    public void addRef(RefTag ref) {
        this.refs.add(ref);
    }


    public enum WordType {
        Word //普通单词
        , Ref   //引文标记
        , WordRef    //单词代引文标记
        , G_REF, Word_G_Ref

    }

    @Override
    public String toString() {
        return "WordItem{}";
    }
}
