package cn.edu.whu.irlab.smart_cite.service.grobid;

import cn.edu.whu.irlab.smart_cite.util.TypeConverter;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/1/5 17:48
 * @desc 利用Grobid处理的业务
 **/
@Service
public class GrobidService {

    private static final String urlPrefix = "http://localhost:8070/api/";

    @Autowired
    private RestTemplate restTemplate;

    public String parseCitation(String citation) {
        String url = urlPrefix + "processCitation";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        valueMap.add("consolidateHeader", "1");
        valueMap.add("citations", citation);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(valueMap, httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
        return responseEntity.getBody();
    }

    /**
     *@auther gcr19
     *@desc 将pdf文档转换为xml文档
     *@param pdf 待转换的pdf文档
     *@return 转换后的jdom节点
     **/
    public Element processFulltextDocument(File pdf) throws JDOMException, IOException {
        String url = urlPrefix + "processFulltextDocument";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> valueMap = new LinkedMultiValueMap<>();
        FileSystemResource fileSystemResource = new FileSystemResource(pdf);

        valueMap.add("consolidateHeader", "1");
        valueMap.add("consolidateCitations", "1");
        valueMap.add("input", fileSystemResource);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(valueMap, httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
        return TypeConverter.str2xml(responseEntity.getBody());
    }


}
