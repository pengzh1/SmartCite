package cn.edu.whu.irlab.smart_cite.service.extractor;

import cn.edu.whu.irlab.smart_cite.service.featureExtractor.BertFeatureExtractor;
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
 * @desc 预训练语言模型识别
 **/
@Slf4j
@Service
public class PretrainModelExtractor extends ExtractorImpl {

    @Override
    List<Result> classify(List<Result> results, File file) {
        return null;
    }

    @Autowired
    private BertFeatureExtractor bertFeatureExtractor;

    @Override
    public List<Result> extractFeature(Article article, boolean isPutInTogether) {
        return bertFeatureExtractor.extract(article, false);
    }
}
