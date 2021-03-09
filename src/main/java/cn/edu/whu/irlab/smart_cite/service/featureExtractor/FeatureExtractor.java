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
@Service
public class FeatureExtractor {

    private static final Logger logger = LoggerFactory.getLogger(FeatureExtractor.class);

    private static final Random random = new Random(100);

    //输入输出
    public static String TRAIN_DIR;

    private static List<Article> trainArticles; //训练数据

    private static ThreadLocal<List<Result>> information = new ThreadLocal<List<Result>>() {
        @Override
        protected List<Result> initialValue() {
            return new ArrayList<>();
        }
    };

    private static ThreadLocal<List<String>> svmFeatures = new ThreadLocal<List<String>>() {
        @Override
        protected List<String> initialValue() {
            return new ArrayList<>();
        }
    };

    @SuppressWarnings("rawtypes")
    private ThreadLocal<List<IFeature>> features = new ThreadLocal<List<IFeature>>() {
        @Override
        protected List<IFeature> initialValue() {
            return new ArrayList<>();
        }
    };
//    private List<IFeature> features = new ArrayList<>();    //特征列表

//    @Autowired
//    private StatisticVisitor statisticVisitor;   //统计分析

    public List<Result> extract(Article article, boolean isPutInTogether) {
        String name = article.getName();
        String fileName = isPutInTogether ? FEATURE_FILE + File.separator + "together_features.libsvm" : FEATURE_FILE + File.separator + name + "_features.libsvm";
        Files featureWriter = Files.open(fileName, isPutInTogether, 0);

        features.get().clear();
        information.get().clear();
        svmFeatures.get().clear();

        loadFeatures();
        extractArticle(article, featureWriter);

//        statisticVisitor.printRCCount();
//        statisticVisitor.save();
        //关闭特征文件流
        featureWriter.close();
        //保存位置信息
        List<Result> results = information.get();
        List<String> svm = svmFeatures.get();
        System.out.println("results size:" + results.size() + " svm size:" + svm.size());
        for (int i = 0; i < results.size(); i++) {
            results.get(i).setLibsvmFeature(svm.get(i));
        }

        WriteUtil.writeList(isPutInTogether ? "temp/location/together_location.txt" : "temp/location/" + article.getName() + "_location.txt", information.get(), isPutInTogether);

        return information.get();
    }

    /**
     * 加载特征
     */
    public void loadFeatures() {
        logger.info("--------load features---------");

        features.get().add(new WorkNounsFeature(5));//1 句子中是否包含work nous词组
        features.get().add(new RefRefFeature());//2 是否包含引文句中目标引文标记前面相邻的名词短语
        features.get().add(new AuthorFeature());//3 是否包含作者名字
        features.get().add(new PronFeature(10));//4 是否包含第三人称代词
        features.get().add(new LexicalHooksFeature());//5* Lexical hooks
        features.get().add(new DistanceFeature());//6* 与目标引文句的距离
        features.get().add(new InParaFeature());//7* 是否和目标引文在同一个段落内
        features.get().add(new SectionFeature(SectionFeature.START));//8*
        features.get().add(new SectionFeature(SectionFeature.PRE_START));//9*
        features.get().add(new SectionFeature(SectionFeature.NEXT_START));//10*
        features.get().add(new RefPositionFeature(RefPositionFeature.PRE));//11* 当前候选上下文句前面一句是否是非目标引文句
        features.get().add(new RefPositionFeature(RefPositionFeature.NEXT));//12* 当前候选上下文句后面一句是否是非目标引文句
        features.get().add(new RefFeature(RefFeature.OTHER_REF));//13 当前句中是否只包含其他引文标记
        features.get().add(new SectionPositionFeature());//14* 句子在文章中的区域
        features.get().add(new ConjFeature(ConjFeature.START));//15 是否起始于指定的连接副词
        features.get().add(new SimilarityFeature(SimilarityFeature.GRAM_1));//16
        features.get().add(new SimilarityFeature(SimilarityFeature.GRAM_2));//17
        features.get().add(new SimilarityFeature(SimilarityFeature.GRAM_3));//18
        features.get().add(new RefListFeature(RefListFeature.NUMBER));//19 引文句中的引文标记个数

//        features.add(new RefPronFeature()); //20

        logger.info(">>>>特征总数为" + features.get().size());
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
    private void featuresRef(RefTag r, Files featureWriter) {
        //统计距离
//        statisticVisitor.visitRefCount(r);
//        statisticVisitor.visitRefDistanceCount(r);
//        statisticVisitor.visitRefNonContext(r);
//        if (r.getContexts().trim().equals("")) {    //TEST，没有正例则不进行抽取
//            return;
//        }
        //计算特征
        @SuppressWarnings("unchecked")
        List<String> result = candidate(r).stream() //遍历每个引文的候选上下文句
                .map(sent -> features.get().stream()  //遍历所有特征
                        .map(f -> f.f(sent, r)) //求特征值
                        .reduce(label(r, sent), (re, fv) -> re + " " + text(fv), (re1, re2) -> re1))   //拼接特征值
                //搜集特征值
                .collect(Collectors.toList());

        //转换格式
        result = toSVMFormat(result);
        svmFeatures.get().addAll(result);

        //       result = toCRFFormat(result);
        featureWriter.appendLn(toStr(result, "\n"));
//        System.out.println(toStr(result, "\n"));
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
                    for (String item : items) {
                        idIndex++;  //ID index始终+1
                        if (!item.trim().equals("0")) {
                            builder.append(" ").append(idIndex).append(":").append(item);
                        }
                    }
                } else if (fvs[j].startsWith("{")) {    //集合
                    List<String> list = com.leishengwei.jutils.Arrays.list(fvs[j].substring(0, fvs[j].length() - 1).split(","));
                    for (String s : list) {
                        builder.append(" ").append(s);
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

    /**
     * 数据采样 采用完整数据集
     *
     * @param results
     * @return
     */
    public Map<String, List<Result>> sampleData(List<Result> results) {

        //随机打散数据
        Collections.shuffle(results, random);
        //数据检查
        WriteUtil.writeResult1(results);
        return splitTrainAndEval(results);
    }

    public Map<String, List<Result>> sampleData1(List<Result> results) {

        List<Result> positiveResults = results.stream().filter(Result::isContext).collect(Collectors.toList());
        List<Result> negativeResults = results.stream().filter(result -> !result.isContext()).collect(Collectors.toList());

        //随机打散负例
        Collections.shuffle(negativeResults, random);
        //取和正例数量相同的负样本
        negativeResults = negativeResults.subList(0, positiveResults.size());

        results.clear();
        results.addAll(positiveResults);
        results.addAll(negativeResults);

        //随机打散样本
        Collections.shuffle(results, random);

        return splitTrainAndEval(results);
    }

    /**
     * 分割训练集 评估集 测试集 8 1 1
     *
     * @param results
     * @return
     */
    public Map<String, List<Result>> splitTrainAndEval(List<Result> results) {
        Map<String, List<Result>> resultMap = new HashMap<>();

        resultMap.put("train_set", results.subList(0, results.size() / 10 * 8));
        resultMap.put("eval_set", results.subList(results.size() / 10 * 8, results.size() / 10 * 9));
        resultMap.put("train_and_eval_set", results.subList(0, results.size() / 10 * 9));
        resultMap.put("test_set", results.subList(results.size() / 10 * 9, results.size()));
        resultMap.put("all_set", results);
        return resultMap;
    }

}
