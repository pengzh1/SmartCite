package cn.edu.whu.irlab.smart_cite.controller;

import cn.edu.whu.irlab.smart_cite.enums.ResponseEnum;
import cn.edu.whu.irlab.smart_cite.service.extractor.Impl.ExtractorImpl;
import cn.edu.whu.irlab.smart_cite.util.ResponseUtil;
import cn.edu.whu.irlab.smart_cite.vo.FileLocation;
import cn.edu.whu.irlab.smart_cite.vo.ResponseVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/3/9 11:27
 * @desc 抽取功能控制类
 **/
@RestController
public class ExtractorController {

    private static final Logger logger = LoggerFactory.getLogger(ExtractorController.class);

    @Autowired
    private ExtractorImpl extractor;

    @PostMapping("/extract")
    public ResponseVo extractController(MultipartFile file) {

        File upload = new File(FileLocation.UPLOAD_FILE + file.getOriginalFilename());
        try {
            file.transferTo(upload);
        } catch (IOException e) {
            logger.error(e.getMessage() + " [缓存本地文档失败，文档名：" + file.getOriginalFilename() + "]");
            return ResponseUtil.error(ResponseEnum.SERVER_ERROR1);
        }
        JSONObject object=extractor.ExtractCitationContext(upload);

        return ResponseUtil.success(object);
    }

    public JSONObject batchController() {
        return null;
    }

}
