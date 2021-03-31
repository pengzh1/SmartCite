package cn.edu.whu.irlab.smart_cite.controller;


import cn.edu.whu.irlab.smart_cite.enums.ResponseEnum;
import cn.edu.whu.irlab.smart_cite.service.extractor.SvmExtractor;
import cn.edu.whu.irlab.smart_cite.service.unpack.AjaxList;
import cn.edu.whu.irlab.smart_cite.service.unpack.FileUploadService;
import cn.edu.whu.irlab.smart_cite.service.extractor.ExtractorImpl;
import cn.edu.whu.irlab.smart_cite.util.ReadUtil;
import cn.edu.whu.irlab.smart_cite.util.ResponseUtil;
import cn.edu.whu.irlab.smart_cite.vo.PackParam;
import cn.edu.whu.irlab.smart_cite.vo.ResponseVo;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.exception.ZipException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


@Controller
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private SvmExtractor svmExtractor;


    @GetMapping("/connectTest")
    public String connectTest() {
        return "work";
    }

    @PostMapping("/upload/zip")
    @ResponseBody
    public String uploadZip(MultipartFile zipFile, @RequestBody PackParam packParam) throws ZipException {
        AjaxList<String> ajaxList = fileUploadService.handlerUpload(zipFile, packParam);
        return ajaxList.getData();
    }

    @PostMapping("/upload/file")
    @ResponseBody
    public ResponseVo uploadFile(@RequestParam("file") MultipartFile multipartFile)  {
        if (multipartFile==null){
            return ResponseUtil.error(ResponseEnum.FILE_NOT_FOUND);
        }
        try {
            File file=svmExtractor.saveUploadedFile(multipartFile);
            String str= ReadUtil.read2Str(file);
            JSONObject jsonObject=JSONObject.parseObject(str);
            return ResponseUtil.success(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseUtil.error(ResponseEnum.SERVER_ERROR);
        }
    }

}

