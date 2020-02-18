package cn.edu.whu.irlab.smart_cite.vo;

import lombok.Data;

import javax.xml.bind.Element;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/13 16:22
 * @desc 文章实体
 **/
@Data
public class Article {

//    private int num;//该参数暂无
//    private String pubInfo; //该参数暂无
    private String absText;

    //文件名
    private String name;
    //引文句子编号是顺序的，所以这里用TreeMap保证句子的排序
    private TreeMap<Integer, Sentence> sentenceTreeMap = new TreeMap<>();
    private List<Author> authors;
    private Title title;
    private Map<Integer, Reference> references = new HashMap<>();  //参考文献列表


    public Article() {
    }

    public Article(String name) {
        this.name = name;
    }

    /**
     * 将句子添加到当前文章，按顺序添加
     *
     * @param s
     * @return
     */
    private int index = 0;  //句子索引，temp data
    private int sectionIndex = 0; //章节索引，temp data
    private int paraIndex = 0;    //段落索引，temp data
    private Sentence last = null; //上一个句子


    public void append(Sentence s) {
        if (s == null) {
            throw new IllegalArgumentException("不能添加空句子.");
        }
        s.setIndex(++index);
        if (last != null && last.getPNum() != s.getPNum()) {
            paraIndex = 0;
        }
        if (last != null && !last.getSect().equals(s.getSect())) {
            sectionIndex = 0;
        }
        s.setPIndex(++paraIndex);
        s.setSectionIndex(++sectionIndex);
        sentenceTreeMap.put(s.getId(), s);
        last = s;
    }

    public void putRef(Integer number, Reference ref) {
        references.putIfAbsent(number, ref);
    }
}
