package cn.edu.whu.irlab.smart_cite.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
    private int refNum; //指向的参考文献编号
    private String left;    //左侧字符
    private String right;   //右侧字符

    private String contexts;    //上下文句子编号
    private List<Sentence> contextList = new ArrayList<>(6); //上下文句子列表

    private WordItem wordItem;    //所在WordItem
    private Sentence sentence;    //所在句子

    //第一次需要该数据的时候搜集（没有并发）
    //引文标记中包含的作者
    private List<String> authors = null;
    //引文标记有可能指代的那些词
    private List<String> refPhrases = null;
}
