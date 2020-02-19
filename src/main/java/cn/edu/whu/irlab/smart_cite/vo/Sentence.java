package cn.edu.whu.irlab.smart_cite.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.leishengwei.jutils.Collections.isEmpty;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/14 11:32
 * @desc 句子实体
 **/
@Data
public class Sentence {
    private int id;   //句子编号，从1开始(lei:number)
    private String text;    //句子内容
    private Article article; //对应文章ID
    private List<WordItem> wordList = new ArrayList<>();    //词项列表，不对外开放结构，否则可能会出错

    /**
     * 结构信息
     */
    private int pNum;   //段落编号
    private int level;    //1,2,3，对应section的类型
    private String section;    //一级章节编号
    private String subSection;  //二级章节编号
    private String subSubSection;   //三级章节编号
    private String sect; //章节
    private int index = 0;    //句子索引，从1开始连续编号
    private int pIndex; //当前段落的句子索引，从1开始，值由Article代理
    private int sectionIndex;   //当前章节的索引，从1开始，值由Article代理
    /**
     * 引文信息
     */
    private String cType;  //句子类型,c,n,r
    private List<RefTag> refList = new ArrayList<>(8);    //引文标记列表

    public Sentence(int id, String text, Article article) {
        this.id = id;
        this.text = text;
        this.article = article;
    }

    public void addRef(RefTag ref) {
        refList.add(ref);
    }

    public void setWordList(List<WordItem> wordList) {
        if (wordList == null) {
            return;
        }
        for (int i = 0; i < wordList.size(); i++) {
            WordItem e = wordList.get(i);
            addWord(e);
        }
    }
    /**
     * 添加单词
     *
     * @param item
     */
    public void addWord(WordItem item) {
        item.setSentence(this);
        item.setIndex(wordList.size());
        this.wordList.add(item);
    }

    public String[] toTextArr() {
        String[] wordArr = new String[wordList.size()];
        for (int i = 0; i < wordList.size(); i++) {
            wordArr[i] = wordList.get(i).getWord();
        }
        return wordArr;
    }

    public int wordSize() {
        return this.wordList.size();
    }

    public WordItem word(int i) {
        if (i < 0 || i >= wordList.size()) {
            return null;
        }
        return wordList.get(i);
    }

    /**
     * 将句子的所有信息格式化成文本，前9个Tab分割主要信息，第10块内容是句子分词信息，也是通过Tab分割，每一项对应WordItem对象，第一块是固定的字符"s"代表是句子
     * todo art文件关键方法 重点阅读
     * @return
     */
    public String toText() {
        StringBuilder buf = new StringBuilder();
        buf.append("s" + "\t" + id + "\t" + cType + "\t" + pNum + "\t" + level + "\t" + sect + "\t" + index + "\t" + pIndex + "\t" + sectionIndex + "\t");
        for (WordItem item : wordList) {
            if (item.getType() == WordItem.WordType.Word) { //W:单词:词性标记
                buf.append("W:" + item.getWord().replace(":", "\\$") + ":" + item.getTag());
            } else if (item.getType() == WordItem.WordType.Ref || item.getType() == WordItem.WordType.WordRef) {   //Ref|WordRef
                //[RW|R]:文本内容:引文标记内容:词性标记:引文编号:上下文:对应参考文献编号:左侧:右侧
                //TODO 这里有改动，设置了R和RW，并增加了item.getWOrd()
                buf.append(item.getType() == WordItem.WordType.Ref ? "R:" : "RW:");
                buf.append(item.getWord()).append(":");
                buf.append(item.getRef().getText().replace(":", "\\$") + ":" + item.getTag());
                buf.append(":" + item.getRef().getId());
                buf.append(":" + item.getRef().getContexts());
                buf.append(":" + item.getRef().getRefNum());
                buf.append(":" + item.getRef().getLeft());
                buf.append(":" + item.getRef().getRight());
            } else {//G_REF|Word_G_Ref
                //[GW|G]:文本内容:词性标记[|引文标记内容:编号:上下文:对应参考文献编号:左侧:右侧]
                //TODO 这里有改动，分别设置了GW和G
                buf.append(item.getRefs().stream().reduce((item.getType() == WordItem.WordType.G_REF ? "G:" : "GW:") + item.getWord() + ":" + item.getTag(),
                        (r, i) -> r + "|" + i.getText().replace(":", "\\$") + ":" + i.getId() + ":" + i.getContexts() + ":" + i.getRefNum() + ":" + i.getLeft() + ":" + i.getRight(),
                        (r1, r2) -> r1));
            }
            buf.append("\t");
        }
        return buf.toString();
    }

    @Override
    public String toString() {
        return "Sentence{}";
    }

    public String plain() {
        return text(wordList);
    }

    public static String text(List<WordItem> list) {
        if (isEmpty(list)) {
            return "";
        }
        return list.stream().reduce("", (r, v) -> r + " " + v.getWord(), (r1, r2) -> r1).trim();
    }

}
