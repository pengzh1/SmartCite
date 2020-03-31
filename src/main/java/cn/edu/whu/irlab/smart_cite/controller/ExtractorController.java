package cn.edu.whu.irlab.smart_cite.controller;

import cn.edu.whu.irlab.smart_cite.service.extractor.Impl.ExtractorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

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

    public void extractController(){

    }

}
