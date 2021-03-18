package cn.edu.whu.irlab.smart_cite.service.featureExtractor;

import cn.edu.whu.irlab.smart_cite.feature.*;
//import cn.edu.whu.irlab.smart_cite.service.artFileReader.ArtFileReader;
import cn.edu.whu.irlab.smart_cite.service.statisticVisitor.StatisticVisitor;
import cn.edu.whu.irlab.smart_cite.util.WriteUtil;
import cn.edu.whu.irlab.smart_cite.vo.*;
import com.leishengwei.jutils.Files;
import com.leishengwei.jutils.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static cn.edu.whu.irlab.smart_cite.vo.FileLocation.FEATURE_FILE;
import static com.leishengwei.jutils.Collections.toStr;
import static com.leishengwei.jutils.Strings.contain;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/19 16:24
 * @desc 特征抽取器
 **/
public abstract class FeatureExtractor {

    private static final Logger logger = LoggerFactory.getLogger(FeatureExtractor.class);

    static ThreadLocal<List<Result>> information = new ThreadLocal<List<Result>>() {
        @Override
        protected List<Result> initialValue() {
            return new ArrayList<>();
        }
    };


    //输入输出
    public static String TRAIN_DIR;

    private static List<Article> trainArticles; //训练数据


    @SuppressWarnings("rawtypes")
    ThreadLocal<List<IFeature>> features = new ThreadLocal<List<IFeature>>() {
        @Override
        protected List<IFeature> initialValue() {
            return new ArrayList<>();
        }
    };
//    private List<IFeature> features = new ArrayList<>();    //特征列表

//    @Autowired
//    private StatisticVisitor statisticVisitor;   //统计分析

    /**
     * 参数初始化
     */
    abstract void init();

    abstract void close();

    public List<Result> extract(Article article, boolean isPutInTogether) {
        String name = article.getName();
        String fileName = isPutInTogether ? FEATURE_FILE + File.separator + "together_features.libsvm" : FEATURE_FILE + File.separator + name + "_features.libsvm";
        Files featureWriter = Files.open(fileName, isPutInTogether, 0);

        init();
        extractArticle(article, featureWriter);

//        statisticVisitor.printRCCount();
//        statisticVisitor.save();
        //关闭特征文件流
        featureWriter.close();
        //保存位置信息
        WriteUtil.writeList(isPutInTogether ? "temp/location/together_location.txt" : "temp/location/" + article.getName() + "_location.txt", information.get(), isPutInTogether);
        close();
        return information.get();
    }

    public static List<Article> loadTrainArticles() {
        logger.info("--------------加载所有训练集---------------");
//        ArtFileReader afr = new ArtFileReader();
        if (trainArticles == null) {
            trainArticles = new ArrayList<>();
            File[] files = new File(TRAIN_DIR).listFiles(v -> v.getName().endsWith(".art"));
            Arrays.asList(files).forEach(f -> {
                logger.info("读取文件 " + f.getName());
//                trainArticles.add(afr.load(f));
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
    public void extractAll(Files featureWriter) {
        logger.info("start extract...");

        trainArticles.forEach(ar -> {  //每篇文章
            logger.info("开始处理Article：" + ar.getNum() + "-" + ar.getName() + ":" + ar.getTitle());
            extractArticle(ar, featureWriter);
        });

//        statisticVisitor.printRCCount();
//        statisticVisitor.save();
        //关闭特征文件流
        featureWriter.close();
    }

    /**
     * 每篇文章
     *
     * @param ar
     */
    public void extractArticle(Article ar, Files featureWriter) {

        ar.getSentenceTreeMap().forEach((k, sent) -> {  //遍历每个句子
            //	System.out.println("abc");
            logger.debug("article:[" + sent.getArticle().getNum() + "].sentence<" + sent.getId() + ">");
            if (sent.getCType() != null && sent.getCType().equals("r")) { //只在r中找

//                statisticVisitor.visitRSentence(sent);
//                statisticVisitor.visitRefListSentence(sent);

                sent.getWordList().stream().filter((i) -> i.getType() != WordItem.WordType.Word).forEach((i) -> {  //过滤出引文标记类WordItem

                    logger.debug("ref:" + i.getIndex() + ":" + i.getWord());

                    i.getRefs().forEach((r) -> {    //每个引文标记
                        featuresRef(r, featureWriter);
                    });
                });
            }
//            statisticVisitor.contextSCount(sent);
        });

    }

    /**
     * 每个引文标记进行特征抽取
     *
     * @param r 引文标记
     */
    abstract void featuresRef(RefTag r, Files featureWriter);

    /**
     * 得到候选上下文
     * 规则：同一Section内上N句下M句（N=4，M=4），不包括引文句 todo 这里规定了lei的候选句子的抽取标准
     *
     * @param ref 目标引文所在引文句
     * @return
     */
    List<Sentence> candidate(RefTag ref) {
        NavigableMap<Integer, Sentence> map = Article.sectionSentences(ref);
        int index = ref.getSentence().getIndex(); //当前句的索引
        //去掉引文句
        //return map.values().stream().filter(v -> v.getIndex() >= index - 4 && v.getIndex() <= index + 4 && v.getIndex() != ref.getSentence().getIndex()).collect(Collectors.toList());
        //保留引文句
        return map.values().stream().filter(v -> v.getIndex() >= index - 4 && v.getIndex() <= index + 4).collect(Collectors.toList());
    }

    /**
     * 类别标记
     *
     * @param r 引文标记
     * @param s 句子
     * @return
     */
    public static String label(RefTag r, Sentence s) {
        boolean isContext = contain(r.getContexts().split(","), s.getId() + "") || r.getSentence().equals(s);
        Result result = new Result(s, r);
        result.setContext(isContext);
        information.get().add(result);

        //类别值:文章编号:引文句编号:引文编号:候选上下文编号 todo important
//        return (contain(r.getContexts().split(","), s.getId() + "") ? "1" : "0") + ":" + s.getArticle().getNum() +
//                ":" + r.getSentence().getId() + ":" + r.getId() + ":" + s.getId();
        return (isContext ? "1" : "0");
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
            return ((Boolean) fv) ? "1" : "0";
        } else if (fv instanceof Double || fv instanceof Float) { //如果是0.0这样的就直接算0了
            boolean dv = ((Double) fv) == 0;
            if (dv)
                return "0";
            else return fv.toString();
        }
        return fv.toString();
    }

}
