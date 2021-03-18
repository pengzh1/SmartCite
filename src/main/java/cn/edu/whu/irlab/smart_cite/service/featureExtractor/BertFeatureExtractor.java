package cn.edu.whu.irlab.smart_cite.service.featureExtractor;

import cn.edu.whu.irlab.smart_cite.util.WriteUtil;
import cn.edu.whu.irlab.smart_cite.vo.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.leishengwei.jutils.Files;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.leishengwei.jutils.Collections.toStr;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/11/25 17:14
 * @desc 用于训练bert模型的特征收集器
 **/
@Service
public class BertFeatureExtractor extends FeatureExtractor {
    private static final Random random = new Random(100);

    @Override
    void init() {
        information.get().clear();
    }

    @Override
    void close() {

    }

    /**
     * 每个引文标记进行特征抽取
     *
     * @param r 引文标记
     */
    void featuresRef(RefTag r, Files featureWriter) {
        candidate(r).forEach(sentence -> label(r, sentence));
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
