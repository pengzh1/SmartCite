package cn.edu.whu.irlab.smart_cite.service.statisticVisitor;

import cn.edu.whu.irlab.smart_cite.feature.ConjFeature;
import cn.edu.whu.irlab.smart_cite.feature.SectionPositionFeature;
import cn.edu.whu.irlab.smart_cite.feature.WorkNounsFeature;
import cn.edu.whu.irlab.smart_cite.service.featureExtractor.FeatureExtractor;
import cn.edu.whu.irlab.smart_cite.vo.Article;
import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;
import cn.edu.whu.irlab.smart_cite.vo.WordItem;
import cn.edu.whu.irlab.smart_cite.util.Count;
import com.leishengwei.jutils.Collections;
import com.leishengwei.jutils.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.*;
import java.util.stream.Collectors;

import static com.leishengwei.jutils.Collections.nGram;

/**
 * 统计类
 * Created by lsw on 2015/4/26.
 */
@Service
public class StatisticVisitor {
    //统计数据
    private int rCount = 0;   //引文标记统计
    private int rSCount = 0;  //引文句统计
    private int cCount = 0;   //上下文句统计
    private Map<Integer, Integer> refCount = new HashMap<>();   //引文句引文个数统计
    private int refGroupTotal = 0;    //总共的引文组个数
    private Map<Integer, Integer> refGroupCount = new HashMap<>();  //引文句引文分组内引文个数统计
    private int moreRef = 0;  //包含多个引文标记的引文句数
    private int singleRef = 0;    //单一引文标记的引文句数
    private Map<Integer, Integer> distanceCount = new HashMap<>(); //引文距离统计
    private int difSectionCount = 0;  //跨section上下文个数
    //疑问搜集
    private List<String> warnList = new ArrayList<>();

    public static int nGramIdIndex = 100; //从100开始
    public static Map<Integer, Map<String, Integer>> gramMap = new HashMap<>();   //n-gram得到的所有词条，{n:{item:id}}
    private Map<Integer, Map<Integer[], Integer>> contextCount = new HashMap<>();//引文上下文个数统计

    /**
     * 存储数据
     */
    public void save() {
        //将统计数据写入到词表中
        Files.open("result/统计数据.txt")
                .append("引文标记个数：").appendLn(rCount + "")
                .append("引文句个数：").appendLn(rSCount + "")
                .append("上下文句个数：").appendLn(cCount + "")
                .appendLn("引文句引文个数统计分布：").appendLn(refCount.toString())
                .appendLn("-------------------------")
                .append("总共的引文组个数：").appendLn(refGroupTotal + "")
                .appendLn("-------------------------")
                .appendLn("引文分组引文个数分布：").appendLn(refGroupCount.toString())
                .appendLn("------------------------------")
                .append("包含多个引文的引文句数：").appendLn(moreRef + "")
                .append("单一引文的引文句数：").appendLn(singleRef + "")
                .appendLn("------------------------------")
                .appendLn("引文距离分布：")
                .appendLn(distanceCount.toString())
                .append("跨section上下文数：")
                .appendLn(difSectionCount + "")
                .appendLn("---------------------------------")
                .appendLn("引文上下文个数分布.")
                .appendLn(contextCount)
                .appendLn("----------------------------------")
                .appendLn("引文上下文关系在不同文章区域的个数分布")
                .appendLn(SectionPositionFeature.sectionCount)
                .appendLn("------------------------------")
                .appendLn("引文句在文章不同区域的个数分布")
                .appendLn(rSectionMap)
                .appendLn("------------------------------")
                .appendLn("跨引实例：")
                .appendLn(crossSent)
                .appendLn(crossSent.size() + "")
                .appendLn("--------------")
                .appendLn(crossContext)
                .appendLn(crossContext.size() + "")
                .appendLn("------------------------------")
                .appendLn("preCross")
                .appendLn(preCross)
                .appendLn("------------------------------")
                .appendLn("nextCross")
                .appendLn(nextCross)
                .appendLn("------------------------------")
                .appendLn("没有引文上下文的引文标记个数分布")
                .appendLn(nonContexts)
                .appendLn("------------------------------")
                .appendLn(Arrays.toString(refListContextsCount))
                .appendLn(Arrays.toString(refListContextsCount2))
                .appendLn("------------------------------")
                .append(sameContextsCount1 + "").append("\t").appendLn(difContextsCount1 + "")
                .append(sameContextsCount2 + "").append("\t").appendLn(difContextsCount2 + "")
                .appendLn("------------------------------")
                .appendLn(sectionCCountMap)
                .appendLn("------------------------------")
                .appendLn(Collections.toStr(warnList, "\n"))
                .close();
        //将gram词表写入到文件中
        gramMap.forEach((k, v) -> {
            switch (k) {
                case 1:
                    Files.open("result/1-gram.txt").append(Maps.toStr(gramMap.get(1), "\n", " : ")).close();
                    break;
                case 2:
                    Files.open("result/2-gram.txt").append(Maps.toStr(gramMap.get(2), "\n", " : ")).close();
                    break;
                case 3:
                    Files.open("result/3-gram.txt").append(Maps.toStr(gramMap.get(3), "\n", " : ")).close();
                    break;
            }

        });
        //TODO temp
        Files.open("result/conj_examples.txt").append(ConjFeature.conjExamples, "\n").close();
        Files.open("result/work_nouns_example.txt").append(WorkNounsFeature.workNounsExamples, "\n").close();
    }

    /**
     * 得到N-gram
     *
     * @param n
     * @return
     */
    public static Map<String, Integer> getNGram(int n) {
        List<Article> trainArticles = FeatureExtractor.loadTrainArticles();
        if (n > 3)
            n = 3;
        if (n < 1)
            n = 1;
        if (gramMap.containsKey(n)) {
            return gramMap.get(n);
        } else {
            gramMap.put(n, new HashMap<>());
            StatisticVisitor.collectNGram(trainArticles, gramMap.get(n), n);
            return gramMap.get(n);
        }
    }

    /**
     * gramMap 不能为空
     *
     * @param nGramMap
     * @param n
     */
    public static void collectNGram(List<Article> trainArticles, Map<String, Integer> nGramMap, int n) {
        Logs.info(FeatureExtractor.class, "抽取数据集的%sGram词表.", n);
        trainArticles.forEach(ar -> {   //遍历每个文章
            ar.getSentenceTreeMap().forEach((num, sentence) -> {    //遍历每个句子
                nGram(sentence.getWordList().stream().filter(v -> WordItem.isWord(v)).map(word -> word.getWord()).collect(Collectors.toList()), n).forEach(gram -> {
                    String trim = gram.replace("-", "").trim();
                    nGramMap.putIfAbsent(trim, 1);
                    nGramMap.computeIfPresent(trim, (k, v) -> v + 1);
                });
            });
        }); //gram>3:6023,16911,>5=8361
        Maps.filter(nGramMap, v -> v.getValue() > 5);
        nGramMap.keySet().forEach(v -> {
            nGramMap.put(v, nGramIdIndex++);
        });
        Logs.info(FeatureExtractor.class, "抽取结果，%s-gram得到词表大小为：%s", n, nGramMap.size());
    }

    /**
     * 引文句相关信息统计
     *
     * @param sent
     */
    public void visitRSentence(Sentence sent) {

        Article ar = sent.getArticle();
        //引文句个数统计
        rSCount++;
        int refSize = sent.getRefList().size();
        if (refSize > 1) {
            moreRef++;
        } else {
            singleRef++;
        }
        //引文句引文标记个数分布
        if (refSize > 9) {
            warnList.add(Strings.s("refSize>9:%s.%s", ar.getNum(), sent.getId()));
        }
        if (refSize == 0) {
            warnList.add(Strings.s("refSize=0:%s.%s", ar.getNum(), sent.getId()));
        }
        refCount.putIfAbsent(refSize, 1);

        refCount.computeIfPresent(refSize, (key, v) -> v + 1);
        //引文句内引文组个数分布
        sent.getWordList().stream().filter(w -> w.getType() == WordItem.WordType.G_REF || w.getType() == WordItem.WordType.Word_G_Ref).forEach(w -> {
            //统计引文分组个数
            refGroupTotal++;
            //统计引文分组内引文标记个数概况
            if (w.getRefs().size() > 8) {
                warnList.add(Strings.s("refGroupSize>8:%s.%s", ar.getNum(), sent.getId()));
            }
            refGroupCount.putIfAbsent(w.getRefs().size(), 1);
            refGroupCount.computeIfPresent(w.getRefs().size(), (key, v) -> v + 1);
        });
    }

    public Map<Integer, Integer> rSectionMap = new HashMap<>();
    public Map<Integer, Count> sectionCCountMap = new HashMap<>();

    /**
     * 遍历每个引文标记，进行统计
     */
    public void visitRefCount(RefTag r) {
        //统计每个section的引文标记个数
        int sectionNum = new Long((long) Math.ceil(r.getSentence().getArticle().sectIndex(r.getSentence()) * (6 / (double) r.getSentence().getArticle().sectionCount()))).intValue();
        rSectionMap.putIfAbsent(sectionNum, 1);
        rSectionMap.computeIfPresent(sectionNum, (k, v) -> v + 1);
        sectionCCountMap.putIfAbsent(sectionNum, Count.count(1));
        sectionCCountMap.computeIfPresent(sectionNum, (k, v) -> v.add(r.getContextNum()));
        //统计引文标记个数
        rCount++;
    }

    /**
     * 上下文句个数++
     *
     * @param sent
     */
    public void contextSCount(Sentence sent) {
        if (sent.getCType() != null && sent.getCType().equals("c")) {
            cCount++;
        }
    }

    Map<String, Integer> crossSent = new HashMap<>();  //跨引的句子
    Map<String, Integer> crossContext = new HashMap<>();  //跨引的关系
    Map<Integer, Integer> preCross = new HashMap<>();
    Map<Integer, Integer> nextCross = new HashMap<>();

    /**
     * 统计引文r和他的上下文的距离分布
     *
     * @param r
     */
    public void visitRefDistanceCount(RefTag r) {
        //统计距离
        String[] contexts = r.getContexts().split(",");
        //统计上下文个数分布
        Integer[] integers = new Integer[2];
        integers[0] = 0;    //前面的个数
        integers[1] = 0;    //后面的个数
        Arrays.asList(contexts).stream().filter(c -> Strings.isNotEmpty(c)).forEach(c -> {
            Optional<Sentence> s = r.getSentence().getArticle().sentence(Integer.parseInt(c));
            s.ifPresent(ss -> {
                int distance = ss.getIndex() - r.getSentence().getIndex();
                if (ss.getId() > r.getSentence().getId()) {//后面的
                    //判断有没有跨引
                    integers[1]++;
                    if (ss.getIndex() - r.getSentence().getIndex() > integers[1]) {    //跨引了
                        String as = ss.getArticle().getNum() + "." + ss.getId();
                        String arf = as + ">" + r.getSentence().getId() + "." + r.getId();
                        preCross.putIfAbsent(ss.getIndex() - r.getSentence().getIndex(), 1);
                        preCross.computeIfPresent(ss.getIndex() - r.getSentence().getIndex(), (k, v) -> v + 1);
                        crossSent.putIfAbsent(as, 1);
                        crossSent.computeIfPresent(as, (k, v) -> v + 1);
                        crossContext.putIfAbsent(arf, 1);
                        crossContext.computeIfPresent(arf, (k, v) -> v + 1);

                    }
                } else {//前面的
                    integers[0]++;
                    if (r.getSentence().getIndex() - ss.getIndex() > integers[0]) {    //跨引了
                        String as = ss.getArticle().getNum() + "." + ss.getId();
                        crossSent.putIfAbsent(as, 1);
                        crossSent.computeIfPresent(as, (k, v) -> v + 1);
                        nextCross.putIfAbsent(r.getSentence().getIndex() - ss.getIndex(), 1);
                        nextCross.computeIfPresent(r.getSentence().getIndex() - ss.getIndex(), (k, v) -> v + 1);
                        String arf = as + ">" + r.getSentence().getId() + "." + r.getId();
                        crossContext.putIfAbsent(arf, 1);
                        crossContext.computeIfPresent(arf, (k, v) -> v + 1);
                    }
                }

                distanceCount.putIfAbsent(distance, 1);
                distanceCount.computeIfPresent(distance, (key, v) -> v + 1);

                if (distance > 4 || distance < -4) {
                    warnList.add(Strings.s("distanceTooLong:%s:%s.%s.%s->%s.%s", distance, r.getSentence().
                            getArticle().getNum(), r.getSentence().getId(), r.getId(), ss.getArticle().getNum(), ss.getId()));
                }
                if (!ss.getSect().equals(r.getSentence().getSect())) {
                    warnList.add(Strings.s("differentSection:%s:%s.%s.%s->%s.%s", distance, r.getSentence().
                            getArticle().getNum(), r.getSentence().getId(), r.getId(), ss.getArticle().getNum(), ss.getId()));
                    difSectionCount++;
                }
            });
        });

        Map<Integer[], Integer> map = new HashMap<>();
        map.put(integers, 1);
        contextCount.putIfAbsent(integers[0] + integers[1], map);
        contextCount.computeIfPresent(integers[0] + integers[1], (k, v) -> {
            Set set = v.keySet();
            Iterator<Integer[]> iterator = set.iterator();
            Integer[] next;
            boolean find = false;
            while (iterator.hasNext()) {
                next = iterator.next();
                if (next[0] == integers[0] && next[1] == integers[1]) { //找到这样的key
                    find = true;
                    v.compute(next, (kk, vv) -> vv + 1);  //value++
                }
            }
            if (!find) {    //找不到
                v.put(integers, 1);   //value=1
            }
            return v;
        });
    }

    /**
     * 打印句子统计信息
     */
    public void printRCCount() {
        Logs.info(StatisticVisitor.class, "-------------------------统计：----------------------------\n" +
                "引文标记个数：%s\n引文句个数：%s\n上下文句个数：%s", rCount, rSCount, cCount);
    }


    /**
     * @param r
     */
    public void refCount(RefTag r) {

    }

    Map<Integer, Integer> nonContexts = new HashMap<>();

    /**
     * 统计没有上下文的引文标记的区域分布
     *
     * @param r
     */
    public void visitRefNonContext(RefTag r) {
        if (r.getContexts().trim().equals("") || com.leishengwei.jutils.Arrays.list(r.getContexts().split(",")).stream().filter(v -> Strings.isNotEmpty(v)).collect(Collectors.toList()).size() == 0) { //没有上下文
            int sectionNum = new Long((long) Math.ceil(r.getArticle().sectIndex(r.getSentence()) * (6 / (double) r.getArticle().sectionCount()))).intValue();
            nonContexts.putIfAbsent(sectionNum, 1);
            nonContexts.computeIfPresent(sectionNum, (k, v) -> v + 1);
        }
    }

    int refListContextsCount[] = new int[2];    //0是引文个数为0的个数,1是引文个数大于0的个数
    int refListContextsCount2[] = new int[2];
    int difContextsCount1 = 0;
    int sameContextsCount1 = 0;
    int difContextsCount2 = 0;
    int sameContextsCount2 = 0;

    /**
     * 对多引用引文句的上下文相关统计
     *
     * @param sent
     */
    public void visitRefListSentence(Sentence sent) {
        int countRef = 0; // 每篇文章每引用一次+1
        int countGRef = 0; //每出现一次引文标记+1  GREF表示为一个REF，即如果只有一个GREF，则该句仍认定为只包含一个引文
        //从两种方式判断该引文句是否是多引用引文句
        Iterator<WordItem> iterator = sent.getWordList().stream().filter((i) -> i.getType() != WordItem.WordType.Word).iterator();
        Integer[] count = new Integer[2];
        count[0] = 0;
        count[1] = 0;
        Set contexts = new HashSet<>();
        while (iterator.hasNext()) {
            WordItem item = iterator.next();
            if (item.getRefs().size() == 1) {
                countRef++;
                countGRef++;
            } else {
                countGRef++;
                countRef += item.getRefs().size();
            }
            item.getRefs().forEach(vv -> {
                List<String> vvContexts = com.leishengwei.jutils.Arrays.list(vv.getContexts().split(",")).
                        stream().filter(vs -> Strings.isNotEmpty(vs)).collect(Collectors.toList());
                String vvc = Collections.toStr(vvContexts, ",", true);
                int size = vvContexts.size();
                contexts.add(vvc);
                if (size > 0) {
                    count[0]++;
                } else {
                    count[1]++;
                }

            });
        }
        //上下文相关统计
        if (countRef > 1) { //多个Ref
            refListContextsCount[0] = refListContextsCount[0] + count[0];
            refListContextsCount[1] = refListContextsCount[1] + count[1];
            if (contexts.size() == 1) {
                sameContextsCount1++;
            } else {
                difContextsCount1++;
            }
        }
        if (countGRef > 1) {    //多个Ref或GRef
            refListContextsCount2[0] = refListContextsCount2[0] + count[0];
            refListContextsCount2[1] = refListContextsCount2[1] + count[1];
            if (contexts.size() == 1) {
                sameContextsCount2++;
            } else {
                difContextsCount2++;
            }
        }

    }
}
