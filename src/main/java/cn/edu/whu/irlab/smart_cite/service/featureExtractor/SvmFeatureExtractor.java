package cn.edu.whu.irlab.smart_cite.service.featureExtractor;

import cn.edu.whu.irlab.smart_cite.feature.*;
import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Result;
import com.leishengwei.jutils.Files;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.leishengwei.jutils.Collections.toStr;

@Slf4j
@Service
public class SvmFeatureExtractor extends FeatureExtractor {

    private static final Logger logger = LoggerFactory.getLogger(SvmFeatureExtractor.class);

    private static ThreadLocal<List<String>> svmFeatures = new ThreadLocal<List<String>>() {
        @Override
        protected List<String> initialValue() {
            return new ArrayList<>();
        }
    };


    @Override
    void init() {
        features.get().clear();
        information.get().clear();
        svmFeatures.get().clear();
        loadFeatures();
    }

    @Override
    void close() {
        List<Result> results = information.get();
        List<String> svm = svmFeatures.get();
        System.out.println("results size:" + results.size() + " svm size:" + svm.size());
        for (int i = 0; i < results.size(); i++) {
            results.get(i).setLibsvmFeature(svm.get(i));
        }
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


    /**
     * 每个引文标记进行特征抽取
     *
     * @param r 引文标记
     */
    void featuresRef(RefTag r, Files featureWriter) {
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

}
