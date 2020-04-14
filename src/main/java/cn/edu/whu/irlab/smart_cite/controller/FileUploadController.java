package cn.edu.whu.irlab.smart_cite.controller;


import cn.edu.whu.irlab.smart_cite.service.Unpack.AjaxList;
import cn.edu.whu.irlab.smart_cite.service.Unpack.FileUploadService;
import cn.edu.whu.irlab.smart_cite.vo.PackParam;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.exception.ZipException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@Controller
@RequestMapping("/user")
@Slf4j
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

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
}

