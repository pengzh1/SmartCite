package cn.edu.whu.irlab.smart_cite.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
    private List<Reference> refList = new ArrayList<>(8);    //引文标记列表

    public Sentence(int id, String text, Article article) {
        this.id = id;
        this.text = text;
        this.article = article;
    }
}
