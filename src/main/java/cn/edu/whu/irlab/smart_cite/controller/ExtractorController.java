package cn.edu.whu.irlab.smart_cite.controller;

import cn.edu.whu.irlab.smart_cite.enums.ResponseEnum;
import cn.edu.whu.irlab.smart_cite.exception.FileTypeException;
import cn.edu.whu.irlab.smart_cite.service.extractor.Impl.ExtractorImpl;
import cn.edu.whu.irlab.smart_cite.util.ResponseUtil;
import cn.edu.whu.irlab.smart_cite.vo.ResponseVo;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

        JSONObject object;
        try {
            object = extractor.Extract(file);
        } catch (Exception e) {
            logger.error(e.getMessage());
            if (e instanceof FileTypeException) {
                return ResponseUtil.error(ResponseEnum.FILE_ERROR);
            } else {
                return ResponseUtil.error(ResponseEnum.SERVER_ERROR);
            }
        }

        return ResponseUtil.success(object);
    }

    public JSONObject batchController() {
        return null;
    }

}
