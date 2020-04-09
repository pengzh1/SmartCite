package cn.edu.whu.irlab.smart_cite.vo;

import cn.edu.whu.irlab.smart_cite.util.WordTokenizer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.leishengwei.jutils.Strings.isNotEmpty;
import static com.leishengwei.jutils.Strings.startCapital;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/14 11:33
 * @desc 引文标记实体 （lei：Reference）
 **/
@Data
public class RefTag {

    public RefTag(Sentence sentence, String text, int id) {
        this.sentence = sentence;
        this.text = text;
        this.id = id;
    }

    private int id;    //引文编号
    private String text;    //引文标记内容
    private String rid; //指向的参考文献的Id
    private String left;    //左侧字符 <xref/>节点中不含“(”时回被初始化
    private String right;   //右侧字符 <xref/>节点中不含“)”时回被初始化

    private Reference reference;

    private String contexts;    //上下文句子编号
    private List<Sentence> contextList = new ArrayList<>(6); //上下文句子列表

    private WordItem wordItem;    //所在WordItem
    private Sentence sentence;    //所在句子

    //第一次需要该数据的时候搜集（没有并发）
    //引文标记中包含的作者
    private List<String> authors = null;
    //引文标记有可能指代的那些词
    private List<String> refPhrases = null;
    public Integer getContextNum() {
        return com.leishengwei.jutils.Arrays.list(contexts.split(",")).size();
    }

    public Article getArticle() {
        return this.getSentence().getArticle();
    }

    /**
     * 获取引文标记中包含的作者 todo 如果是数字的引文标记呢？该方法有待改进
     *
     * @return
     */
    public List<String> getAuthors() {
        if (authors == null) {
            collectAuthors();
        }
        return authors;
    }

    private void collectAuthors() {
        String[] tr = WordTokenizer.split(this.text);
        //搜集作者信息
        this.authors = Arrays.asList(tr).stream().filter((t) -> t.length() > 0 && t.charAt(0) >= 65 && t.charAt(0) <= 90).collect(Collectors.toList());//大写字母
    }

    /**
     * 获取引文指代短语序列
     *
     * @return
     */
    public List<String> getRefPhrases() {
        if (refPhrases == null) {
            collectRefPhrases();
        }
        return refPhrases;
    }
    /**
     * 收集引文指代数据
     * 在引文所在单词前面（包括引文当前词）的六个窗口返回为寻找缩略词（包括全大写和首字母大写缩略词）
     * 规则：
     * 1. 首字母大写的词性为NN的词语
     * 2. 连续首字母大写的词语则构造首字母缩略词
     * TODO  该方法有问题()
     */
    private void collectRefPhrases() {
        //搜集指代词语，先进行分词，然后寻找引文标记前面的所有名词作为指代对象，如果是连续大写，加上首字母缩略词
        this.refPhrases = new ArrayList<>();
        List<WordItem> list = wordItem.nearWords(5, 0, (v) -> v.getType() == WordItem.WordType.Word || v.getType() == WordItem.WordType.WordRef || v.getType() == WordItem.WordType.Word_G_Ref);
        WordItem item;
        for (int i = list.size() - 1; i >= 0; i--) {    //从最后一个开始找
            item = list.get(i);
            String tag = item.getTag();
            if (isNotEmpty(tag) && tag.startsWith("NN") && startCapital(tag)) {  //名词还是首字母大写的词（专业名词）呢
                this.refPhrases.add(item.getWord());
            }
        }
        //从list开始
        List<String> ss = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {   //从最后一个开始找
            item = list.get(i);
            if (startCapital(item.getWord())) {
                ss.add(item.getWord());
            } else {
                if (ss.size() < 2) {
                    ss.clear();
                } else {    //一旦找到一个则终止
                    break;
                }
            }
        }
        if (ss.size() >= 2) {   //已经找到首字母连续大写序列
            refPhrases.add(ss.stream().reduce("", (r, v) -> ("" + r + v.charAt(0)).toUpperCase()));
        }

    }

    private JSONArray contextList2Json(){
        JSONArray array=new JSONArray();
        for (Sentence s :
                contextList) {
            array.add(JSON.parse(s.toString()));
        }
        return array;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":")
                .append(id);
        sb.append(",\"text\":\"")
                .append(text).append('\"');
        sb.append(",\"reference\":")
                .append(reference);
        sb.append(",\"contextList\":")
                .append(contextList2Json().toJSONString());
        sb.append(",\"sentence\":")
                .append(sentence);
        sb.append('}');
        return sb.toString();
    }
}
