package cn.edu.whu.irlab.smart_cite.service.classifier;

import cn.edu.whu.irlab.smart_cite.service.Classifier;
import cn.edu.whu.irlab.smart_cite.vo.Result;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;

@Slf4j
@Service
public class PretrainModelClassifier implements Classifier {

    private static final Logger logger = LoggerFactory.getLogger(PretrainModelClassifier.class);


    private static final String URL_PREFIX = "http://localhost:5000/";

    @Autowired
    private RestTemplate restTemplate;

    public List<Result> classify(List<Result> results, File file) {

        int outputType = 3;
        JSONArray sentence_pairs = new JSONArray();
        results.forEach(result -> {
            JSONArray pair = new JSONArray();
            pair.add(Sentence.standardText(result.getSentence().getWordList(), outputType));
            pair.add(Sentence.standardText(result.getRefTag().getSentence().getWordList(), outputType));
            sentence_pairs.add(pair);
        });

        System.out.println(sentence_pairs.size());

        String url = URL_PREFIX + "classify";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        valueMap.add("to_predict", sentence_pairs.toString());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(valueMap, httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
        String response = responseEntity.getBody();

        //todo 待测试
        JSONArray jsonArray = JSONObject.parseObject(response).getJSONArray("predictions");

        int index = 0;
        results.forEach(result -> {
            result.setContext(jsonArray.get(index) == "1");
        });

        return results;
    }

}
