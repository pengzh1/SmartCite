package cn.edu.whu.irlab.smart_cite.service.classifier;

import cn.edu.whu.irlab.smart_cite.service.Classifier;
import cn.edu.whu.irlab.smart_cite.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;


public class PretrainModelClassifier implements Classifier {

    private static final Logger logger = LoggerFactory.getLogger(PretrainModelClassifier.class);


    private static final String URL_PREFIX = "http://localhost:8070/api/";

    @Autowired
    private RestTemplate restTemplate;

    public List<Result> classify(List<Result> results, File file) {

        return null;
    }

}
