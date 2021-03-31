package cn.edu.whu.irlab.smart_cite.service.extractor;

import cn.edu.whu.irlab.smart_cite.service.classifier.SvmClassifier;
import cn.edu.whu.irlab.smart_cite.service.featureExtractor.SvmFeatureExtractor;
import cn.edu.whu.irlab.smart_cite.vo.Article;
import cn.edu.whu.irlab.smart_cite.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * @author gcr19
 * @date 2021-03-17 23:28
 * @desc SVM模型识别
 **/
@Slf4j
@Service
public class SvmExtractor extends ExtractorImpl {
    @Autowired
    private SvmClassifier SVMClassifier;

    @Autowired
    private SvmFeatureExtractor svmFeatureExtractor;

    @Override
    List<Result> classify(List<Result> results, File file) {
        return SVMClassifier.classify(results, file);
    }

    @Override
    public List<Result> extractFeature(Article article, boolean isPutInTogether) {
        return svmFeatureExtractor.extract(article,false);
    }
}
