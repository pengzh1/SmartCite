package cn.edu.whu.irlab.smart_cite.vo;

import com.leishengwei.jutils.Collections;
import com.leishengwei.jutils.Files;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

import static com.leishengwei.jutils.Prints.print;
import static com.leishengwei.jutils.Strings.startCapital;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/13 16:22
 * @desc 文章实体
 **/
@Data
public class Article {

    private int num = 0;//0:代表参与未批量处理的文件，参与批量处理的文件从1开始编号 todo 该参数如何初始化？
    //    private String pubInfo; //该参数暂无
    private String absText;

    //文件名
    private String name;

    //作者列表
    private List<Author> authors;

    //标题
    private Title title;
    //引文句子编号是顺序的，所以这里用TreeMap保证句子的排序
    private TreeMap<Integer, Sentence> sentenceTreeMap = new TreeMap<>();
    private Map<String, Reference> references = new HashMap<>();  //参考文献列表

    private Map<String, Integer> lexicalHookMap = null;
    private List<Map.Entry<String, Integer>> lexicalHooks = null;


    List<String> sections = new ArrayList<>();

    private static List<String> lexicalHookStopword = Files.lines("data/corpus/lexicalhook_stopword.data");


    public Article() {
    }

    public Article(String name) {
        this.name = name;
    }

    private int index = 0;  //句子索引，temp data
    private int sectionIndex = 0; //章节索引，temp data
    private int paraIndex = 0;    //段落索引，temp data
    private Sentence last = null; //上一个句子

    /**
     * 将句子添加到当前文章，按顺序添加
     *
     * @param s
     * @return
     */
    public void append(Sentence s) {
        if (s == null) {
            throw new IllegalArgumentException("不能添加空句子.");
        }
        s.setIndex(++index);
        if (last != null && last.getPNum() != s.getPNum()) {
            paraIndex = 0;
        }

        if (last != null && last.getSect() == null) {
            System.out.println("last [article]" + last.getArticle().getName() + "[sentence]" + last.getId());
            System.out.println("this [article]" + s.getArticle().getName() + "[sentence]" + s.getId());
        }//todo 这里可能有错误

        if (last != null && !last.getSect().equals(s.getSect())) {
            sectionIndex = 0;
        }
        s.setPIndex(++paraIndex);
        s.setSectionIndex(++sectionIndex);

        sentenceTreeMap.put(s.getId(), s);
        last = s;
    }

    public void putRef(String rid, Reference ref) {
        references.putIfAbsent(rid, ref);
    }

    public void put(Sentence sentence) {
        sentenceTreeMap.put(sentence.getId(), sentence);
    }


    /**
     * 因为要统计当前section在整个article的位置，因此要统计section列表
     */
    public void loadSects() {
        sentenceTreeMap.forEach((k, v) -> {
            if (!sections.contains(v.getSect())) {
                sections.add(v.getSect());
            }
        });
        System.out.println();
    }

    /**
     * 这里返回的索引
     *
     * @param s
     * @return
     */
    public int sectIndex(Sentence s) {
        return sections.indexOf(s.getSect()) + 1;
    }

    /**
     * section
     *
     * @return
     */
    public int sectionCount() {
        return sections.size();
    }

    /**
     * 计算句子num1到句子num2的距离
     *
     * @param num1
     * @param num2
     * @return num1在num2之前则<0 ， 相同则 = 0 ， 否则>0
     */
    public Optional<Integer> distance(int num1, int num2) {
        Sentence s1 = sentenceTreeMap.get(num1);
        Sentence s2 = sentenceTreeMap.get(num2);
        if (s1 != null && s2 != null) {
            return Optional.of(s1.getIndex() - s2.getIndex());
        } else {
            return Optional.empty();
        }
    }


    public List<Map.Entry<String, Integer>> lexicalHooks() {
        if (lexicalHooks == null) {
            collectLexicalHooks();
        }
        return lexicalHooks;
    }

    private void collectLexicalHooks() {
        lexicalHookMap = new HashMap<>();
        print("%s:搜集Lexical Hooks.", name);
        sentenceTreeMap.forEach((num, sentence) -> {
            sentence.getWordList().forEach((w) -> {
                if (w.getIndex() > 0) { //不是第一个单词
                    if (w.getType() == WordItem.WordType.Word || w.getType() == WordItem.WordType.WordRef || w.getType() == WordItem.WordType.Word_G_Ref) {
                        //去掉句首word，去掉不是NN的，去掉停用词中的
                        if (startCapital(w.getWord()) && w.getTag().startsWith("NN") && !lexicalHookStopword.contains(w.getWord())) { //首字母大写的名词
                            lexicalHookMap.putIfAbsent(w.getWord(), 1);
                            lexicalHookMap.computeIfPresent(w.getWord(), (s, c) -> c + 1);
                        }   //TODO 很多引文标记加进去了，不是应该已经去掉了吗？
                    }
                }
            });
        });
        lexicalHooks = new ArrayList<>(lexicalHookMap.entrySet());
        lexicalHooks.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        //截取出现次数大于5次的
        lexicalHooks = lexicalHooks.stream().filter((item) -> item.getValue() >= 3).collect(Collectors.toList());
        //截取最多20个
        lexicalHooks = lexicalHooks.size() > 0 ? lexicalHooks.subList(0, Math.min(lexicalHooks.size(), 20)) : lexicalHooks;
        Collections.print(lexicalHooks, ",");
    }

    /**
     * 得到当前引文所在的section的所有句子     *
     *
     * @param ref
     * @return
     */
    public static NavigableMap<Integer, Sentence> sectionSentences(RefTag ref) {
        Sentence cs = ref.getSentence();
        int min = cs.getId(), max = cs.getId();
        Optional<Sentence> find;
        while ((find = cs.previous()).isPresent() && find.get().sameSection(cs)) {
            min = find.get().getId();
            cs = find.get();
        }
        while ((find = cs.next()).isPresent() && find.get().sameSection(cs)) {
            max = find.get().getId();
            cs = find.get();
        }
        return ref.getSentence().getArticle().getSentenceTreeMap().subMap(min, true, max, true);
    }

    /**
     * 通过number得到句子
     *
     * @param number
     * @return
     */
    public Optional<Sentence> sentence(int number) {
        return sentenceTreeMap.get(number) == null ? Optional.<Sentence>empty() : Optional.of(sentenceTreeMap.get(number));

    }

    /**
     * 选择num所在句子的后面第i个句子
     *
     * @param num
     * @param i
     * @return
     */
    public Optional<Sentence> below(int num, int i) {
        Sentence s = sentenceTreeMap.get(num);
        if (s == null) {
            return Optional.empty();
        }
        s = sentenceTreeMap.get(num + i);
        return Optional.ofNullable(s);
    }

    /**
     * 查找编号num的句子后count个句子
     *
     * @param num
     * @param count
     * @return
     */
    public Optional<NavigableMap<Integer, Sentence>> next(int num, int count) {
        Sentence current = sentenceTreeMap.get(num);
        if (current == null) {
            return Optional.empty();
        }
        return Optional.of(sentenceTreeMap.subMap(current.getIndex(), false, current.getIndex() + count, true));
    }

    /**
     * 选择num所在句子的前面第i个句子
     *
     * @param num
     * @param i
     * @return
     */
    public Optional<Sentence> top(int num, int i) {
        Sentence s = sentenceTreeMap.get(num);
        if (s == null) {
            return Optional.empty();
        }
        s = sentenceTreeMap.get(num - i);
        return Optional.ofNullable(s);
    }

    /**
     * 返回当前句子编号下一句
     *
     * @param num
     * @return
     */
    public Optional<Sentence> next(int num) {
        Map.Entry<Integer, Sentence> entry = sentenceTreeMap.higherEntry(num);
        if (entry == null) {
            return Optional.empty();
        } else {
            return Optional.of(entry.getValue());
        }
    }

    /**
     * 查找编号num的句子前count个句子
     *
     * @param num
     * @param count
     * @return
     */
    public Optional<NavigableMap<Integer, Sentence>> previous(int num, int count) {
        Sentence current = sentenceTreeMap.get(num);
        if (current == null) {
            return Optional.empty();
        }
        return Optional.of(sentenceTreeMap.subMap(current.getIndex() - count, true, current.getIndex(), false));
    }

    /**
     * 返回当前句子编号的上一句
     *
     * @param num
     * @return
     */
    public Optional<Sentence> previous(int num) {
        Map.Entry<Integer, Sentence> entry = sentenceTreeMap.lowerEntry(num);
        if (entry == null) {
            return Optional.empty();
        } else {
            return Optional.of(entry.getValue());
        }
    }

    @Override
    public String toString() {
        return "{" + '}';
    }
}
