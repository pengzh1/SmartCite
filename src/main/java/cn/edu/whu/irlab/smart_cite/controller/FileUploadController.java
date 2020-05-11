package cn.edu.whu.irlab.smart_cite.controller;


import cn.edu.whu.irlab.smart_cite.enums.ResponseEnum;
import cn.edu.whu.irlab.smart_cite.service.Unpack.AjaxList;
import cn.edu.whu.irlab.smart_cite.service.Unpack.FileUploadService;
import cn.edu.whu.irlab.smart_cite.service.extractor.Impl.ExtractorImpl;
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
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private ExtractorImpl extractor;

    @GetMapping("/redirect")
    public String redirectHtml() {
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
            File file=extractor.saveUploadedFile(multipartFile);
            String str= ReadUtil.read2Str(file);
            JSONObject jsonObject=JSONObject.parseObject(str);
            return ResponseUtil.success(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseUtil.error(ResponseEnum.SERVER_ERROR);
        }
    }


}

