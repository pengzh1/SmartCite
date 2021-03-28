package cn.edu.whu.irlab.smart_cite.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/14 11:34
 * @desc TODO
 **/
@Data
public class WordItem {

    private WordType type = WordType.Word;  //词项类型
    public static final String REF = "REF"; //默认引文替换字符
    public static final String G_REF = "GREF";    //引文组替换字符
    private int index; //start from 0
    private String word;    //词语内容
    private List<RefTag> refs = new LinkedList<>();  //如果type=WordType.Ref，则该项大小为1且不为空；如果type=WordType.WordRef，则有可能有多项
    private Sentence sentence;
    private String tag; //词性

    public WordItem() {
    }

    public WordItem(String word,String tag){
        this.word=word;
        this.tag=tag;
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
        if (refs.size()!=0){
            return refs.get(0);
        }else {
            return null;
        }
    }


    public static WordItem ref(String text) {
        return new WordItem(WordType.Ref, text);
    }

    public static WordItem ref(String text, RefTag ref) {
        return new WordItem(WordType.Ref, text, ref);
    }

    public void addRef(RefTag ref) {
        this.refs.add(ref);
    }

    /**
     * 判断一个WordItem的类型是不是word
     *
     * @param v
     * @return
     */
    public static boolean isWord(WordItem v) {
        return v.getType() == WordItem.WordType.Word || v.getType() == WordItem.WordType.WordRef || v.getType() == WordItem.WordType.Word_G_Ref;
    }


    public enum WordType {
        Word //普通单词
        , Ref   //引文标记
        , WordRef    //单词代引文标记
        , G_REF, Word_G_Ref

    }

    /**
     * pre和next都必须大于等于0，如果小于0则默认到最前或者最后一个
     *
     * @param pre    前面pre个单词和后面next个单词
     * @param next
     * @param filter 不满足filter的不算
     * @return
     */
    public List<WordItem> nearWords(int pre, int next, Predicate<WordItem> filter) {
        List list = new ArrayList<>();
        if (filter == null || filter.test(this)) {
            list.add(this);
        }
        int i = 0;
        Optional<WordItem> item = next();
        while (i < next && item.isPresent()) {
            if (filter == null || filter.test(item.get())) {
                list.add(item.get());
                i++;
            }
            item = item.get().next();
        }
        i = 0;
        item = previous();
        while (i < pre && item.isPresent()) {
            if (filter == null || filter.test(item.get())) {
                list.add(0, item.get());
                i++;
            }
            item = item.get().next();
        }
        return list;
    }

    public Optional<WordItem> next() {
        if (index < sentence.wordSize() - 1) {
            return Optional.of(sentence.word(index + 1));
        }
        return Optional.empty();
    }

    public Optional<WordItem> previous() {
        if (index > 0) {
            return Optional.of(sentence.word(index - 1));
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "WordItem{" +
                "index=" + index +
                ", word='" + word + '\'' +
                '}';
    }
}
