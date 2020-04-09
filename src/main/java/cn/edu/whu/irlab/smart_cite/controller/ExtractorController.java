package cn.edu.whu.irlab.smart_cite.controller;

import cn.edu.whu.irlab.smart_cite.service.extractor.Impl.ExtractorImpl;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/3/9 11:27
 * @desc TODO
 **/
@RestController
public class ExtractorController {


    @Autowired
    private ExtractorImpl extractor;

    @PostMapping("/extract")
    public JSONObject extractController(MultipartFile file) {
        return null;
    }

}
