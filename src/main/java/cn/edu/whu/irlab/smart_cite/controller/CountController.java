package cn.edu.whu.irlab.smart_cite.controller;

import cn.edu.whu.irlab.smart_cite.enums.ResponseEnum;
import cn.edu.whu.irlab.smart_cite.service.statisticVisitor.StatisticVisitor;
import cn.edu.whu.irlab.smart_cite.util.ResponseUtil;
import cn.edu.whu.irlab.smart_cite.vo.FileLocation;
import cn.edu.whu.irlab.smart_cite.vo.ResponseVo;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/7/4 14:17
 * @desc 统计工具
 **/
@Slf4j
@RestController
@RequestMapping("/count")
public class CountController {

    @Autowired
    private StatisticVisitor statisticVisitor;


    @GetMapping("/contextNum")
    public ResponseVo CountCiteConController(@RequestParam(value = "outputFolder", defaultValue = FileLocation.OUTPUT) String outputFolder) {
        File file = new File(outputFolder);

        if (!file.isDirectory()) {
            return ResponseUtil.error(ResponseEnum.FILE_ERROR);
        }

        JSONObject object = new JSONObject();

        Long icc = 0L;
        Long ecc = 0L;
        int fileNum = 0;
        for (File f :
                file.listFiles()) {
            if (f.isDirectory()) continue;
            Long[] result = null;
            try {
                result = statisticVisitor.calculateOutput(f).get();
            } catch (Exception e) {
                log.error("error in [file]" + f.getName(), e);
                return ResponseUtil.error(ResponseEnum.SERVER_ERROR);
            }
            icc += result[0];
            ecc += result[1];
            fileNum++;
        }

        object.put("ICC", icc);
        object.put("ECC", ecc);
        object.put("FileNum", fileNum);
        return ResponseUtil.success(object);
    }
}
