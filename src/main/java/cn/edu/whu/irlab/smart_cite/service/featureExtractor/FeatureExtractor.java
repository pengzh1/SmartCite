package cn.edu.whu.irlab.smart_cite.service.featureExtractor;

import cn.edu.whu.irlab.smart_cite.feature.*;
import cn.edu.whu.irlab.smart_cite.service.artFileReader.ArtFileReader;
import cn.edu.whu.irlab.smart_cite.service.statisticVisitor.StatisticVisitor;
import cn.edu.whu.irlab.smart_cite.util.WriteUtil;
import cn.edu.whu.irlab.smart_cite.vo.Article;
import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;
import cn.edu.whu.irlab.smart_cite.vo.WordItem;
import com.leishengwei.jutils.Files;
import com.leishengwei.jutils.Maps;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.leishengwei.jutils.Collections.toStr;
import static com.leishengwei.jutils.Strings.contain;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/19 16:24
 * @desc 特征抽取器
 **/
@Service
public class FeatureExtractor {

    private static final Logger logger = LoggerFactory.getLogger(FeatureExtractor.class);

    //输入输出
    public static String TRAIN_DIR;
    public static final String FEATURE_FILE = "temp/feature_files/";


    private static List<Article> trainArticles; //训练数据

    private static List<String> location = new ArrayList<>();

    @SuppressWarnings("rawtypes")
    private List<IFeature> features = new ArrayList<>();    //特征列表

    public Files featureWriter;  //写特征

    @Autowired
    private StatisticVisitor statisticVisitor;   //统计分析

    public void extract(Article article, File file) {
        featureWriter = Files.open(FEATURE_FILE + FilenameUtils.getBaseName(file.getName()) + "_features.libsvm");
        features.clear();
        loadFeatures();
        extractArticle(article);

        statisticVisitor.printRCCount();
        statisticVisitor.save();
        //关闭特征文件流
        featureWriter.close();
        //保存位置信息
        WriteUtil.writeList("temp/location/"+FilenameUtils.getBaseName(file.getName()) + "_location.txt",location);
    }

    /**
     * 加载特征
     */
    public void loadFeatures() {
        logger.info("--------load features---------");

        features.add(new AuthorFeature());//1:
        features.add(new RefListFeature(RefListFeature.NUMBER));//2
        features.add(new ConjFeature(ConjFeature.START));//3
        features.add(new DistanceFeature());//4
        features.add(new InParaFeature());//5
        features.add(new LexicalHooksFeature());//6
        features.add(new PronFeature(10));//7
        features.add(new SimilarityFeature(SimilarityFeature.GRAM_1));//8
        features.add(new SimilarityFeature(SimilarityFeature.GRAM_2));//9
        features.add(new SimilarityFeature(SimilarityFeature.GRAM_3));//10
        features.add(new RefPositionFeature(RefPositionFeature.PRE));//11
        features.add(new RefPositionFeature(RefPositionFeature.NEXT));//12
        features.add(new RefRefFeature());//13
        features.add(new SectionFeature(SectionFeature.START));//14
        features.add(new SectionFeature(SectionFeature.PRE_START));//15
        features.add(new SectionFeature(SectionFeature.NEXT_START));//16
        features.add(new SectionPositionFeature());//17
        features.add(new WorkNounsFeature(5));//18
        features.add(new RefFeature(RefFeature.OTHER_REF));//19
//        features.add(new RefPronFeature()); //20

        logger.info(">>>>特征总数为" + features.size());
    }

    public static List<Article> loadTrainArticles() {
        logger.info("--------------加载所有训练集---------------");
        ArtFileReader afr = new ArtFileReader();
        if (trainArticles == null) {
            trainArticles = new ArrayList<>();
            File[] files = new File(TRAIN_DIR).listFiles(v -> v.getName().endsWith(".art"));
            Arrays.asList(files).stream().forEach(f -> {
                logger.info("读取文件 " + f.getName());
                trainArticles.add(afr.load(f));
            });
        }
        return trainArticles;
    }

    public static List<Article> loadTrainArticles(List<Article> articles) {
        trainArticles = articles;
        return articles;
    }

    /**
     * 开始抽取
     */
    public void extractAll() {
        logger.info("start extract...");

        trainArticles.stream().forEach(ar -> {  //每篇文章
            logger.info("开始处理Article：" + ar.getNum() + "-" + ar.getName() + ":" + ar.getTitle());
            extractArticle(ar);
        });

        statisticVisitor.printRCCount();
        statisticVisitor.save();
        //关闭特征文件流
        featureWriter.close();
    }

    /**
     * 每篇文章
     *
     * @param ar
     */
    public void extractArticle(Article ar) {

        ar.getSentenceTreeMap().forEach((k, sent) -> {  //遍历每个句子
            //	System.out.println("abc");
            logger.debug("article:[" + sent.getArticle().getNum() + "].sentence<" + sent.getId() + ">");
            if (sent.getCType().equals("r")) { //只在r中找

                statisticVisitor.visitRSentence(sent);
                statisticVisitor.visitRefListSentence(sent);

                sent.getWordList().stream().filter((i) -> i.getType() != WordItem.WordType.Word).forEach((i) -> {  //过滤出引文标记类WordItem

                    logger.debug("ref:" + i.getIndex() + ":" + i.getWord());

                    i.getRefs().forEach((r) -> {    //每个引文标记
                        featuresRef(r);
                    });
                });
            }
            statisticVisitor.contextSCount(sent);
        });

    }

    /**
     * 每个引文标记进行特征抽取
     *
     * @param r 引文标记
     */
    private void featuresRef(RefTag r) {
        //统计距离
        statisticVisitor.visitRefCount(r);
        statisticVisitor.visitRefDistanceCount(r);
        statisticVisitor.visitRefNonContext(r);
//        if (r.getContexts().trim().equals("")) {    //TEST，没有正例则不进行抽取
//            return;
//        }
        //计算特征
        @SuppressWarnings("unchecked")
        List<String> result = candidate(r).stream() //遍历每个引文的候选上下文句
                .map(sent -> features.stream()  //遍历所有特征
                        .map(f -> f.f(sent, r)) //求特征值
                        .reduce(label(r, sent), (re, fv) -> re + " " + text(fv), (re1, re2) -> re1))   //拼接特征值
                //搜集特征值
                .collect(Collectors.toList());

        //转换格式
        result = toSVMFormat(result);
        //       result = toCRFFormat(result);
        featureWriter.appendLn(toStr(result, "\n"));
        System.out.println(toStr(result, "\n"));
    }

    /**
     * 得到候选上下文
     * 规则：同一Section内上N句下M句（N=4，M=4），不包括引文句 todo 这里规定了lei的候选句子的抽取标准
     *
     * @param ref 目标引文所在引文句
     * @return
     */
    private List<Sentence> candidate(RefTag ref) {
        NavigableMap<Integer, Sentence> map = Article.sectionSentences(ref);
        int index = ref.getSentence().getIndex(); //当前句的索引
        //去掉引文句
        List<Sentence> collect = map.values().stream().filter(v -> v.getIndex() >= index - 4 && v.getIndex() <= index + 4 && v.getIndex() != ref.getSentence().getIndex()).collect(Collectors.toList());
        return collect;
    }

    /**
     * 类别标记
     *
     * @param r 引文标记
     * @param s 句子
     * @return
     */
    public static String label(RefTag r, Sentence s) {
        location.add(s.getArticle().getNum() + ":" + r.getSentence().getId() + ":" + r.getId() + ":" + s.getId());
        //类别值:文章编号:引文句编号:引文编号:候选上下文编号 todo important
//        return (contain(r.getContexts().split(","), s.getId() + "") ? "1" : "0") + ":" + s.getArticle().getNum() +
//                ":" + r.getSentence().getId() + ":" + r.getId() + ":" + s.getId();
        return (contain(r.getContexts().split(","), s.getId() + "") ? "1" : "0");
    }


    /**
     * 特征值文本化
     *
     * @param fv
     * @return
     */
    public static String text(Object fv) {
        if (fv instanceof Object[]) {
            return "[" + com.leishengwei.jutils.Arrays.toStr((Object[]) fv, ",") + "]";
        } else if (fv instanceof Map) {
            return "{" + Maps.toStr((Map) fv, ",", ":");
        } else if (fv instanceof Boolean) {
            return ((Boolean) fv) == true ? "1" : "0";
        } else if (fv instanceof Double || fv instanceof Float) { //如果是0.0这样的就直接算0了
            boolean dv = ((Double) fv) == 0;
            if (dv)
                return "0";
            else return fv.toString();
        }
        return fv.toString();
    }

    /**
     * 修改为稀疏矩阵的形式
     *
     * @param result
     * @return
     */
    private List<String> toSVMFormat(List<String> result) {
        result = result.stream().map(v -> {
            String[] fvs = v.split(" ");    //特征值
            StringBuilder builder = new StringBuilder();
            builder.append(fvs[0]); //label
            int idIndex = 0;
            for (int j = 1; j < fvs.length; j++) {
                if (fvs[j].startsWith("[")) {   //列表
                    List<String> items = com.leishengwei.jutils.Arrays.list(fvs[j].substring(0, fvs[j].length() - 1).split(","));
                    for (int i = 0; i < items.size(); i++) {
                        idIndex++;  //ID index始终+1
                        if (!items.get(i).trim().equals("0")) {
                            builder.append(" ").append(idIndex).append(":").append(items.get(i));
                        }
                    }
                } else if (fvs[j].startsWith("{")) {    //集合
                    List<String> list = com.leishengwei.jutils.Arrays.list(fvs[j].substring(0, fvs[j].length() - 1).split(","));
                    for (int i = 0; i < list.size(); i++) {
                        builder.append(" ").append(list.get(i));
                    }
                } else {    //普通
                    idIndex++;  //ID index始终+1
                    if (!fvs[j].trim().equals("0")) { //特征不为0时才添加
                        builder.append(" ").append(idIndex).append(":").append(fvs[j]);
                    }
                }
            }
            return builder.toString().trim();
        }).collect(Collectors.toList());
        return result;
    }

}
