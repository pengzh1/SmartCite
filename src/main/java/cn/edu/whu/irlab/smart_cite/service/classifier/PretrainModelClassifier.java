package cn.edu.whu.irlab.smart_cite.service.classifier;

import cn.edu.whu.irlab.smart_cite.service.grobid.GrobidService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.List;


public class PretrainModelClassifier {

    private static final Logger logger = LoggerFactory.getLogger(PretrainModelClassifier.class);


    private static final String URL_PREFIX = "http://localhost:8070/api/";

    @Autowired
    private RestTemplate restTemplate;

    public void predict(List list){

    }

}
