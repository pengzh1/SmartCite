package cn.edu.whu.irlab.smart_cite.controller;

import cn.edu.whu.irlab.smart_cite.enums.ResponseEnum;
import cn.edu.whu.irlab.smart_cite.exception.FileTypeException;
import cn.edu.whu.irlab.smart_cite.service.extractor.PretrainModelExtractor;
import cn.edu.whu.irlab.smart_cite.service.extractor.SvmExtractor;
import cn.edu.whu.irlab.smart_cite.util.ResponseUtil;
import cn.edu.whu.irlab.smart_cite.util.UnPackeUtil;
import cn.edu.whu.irlab.smart_cite.vo.ResponseVo;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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
    private SvmExtractor svmExtractor;

    @Autowired
    private PretrainModelExtractor pretrainModelExtractor;


    @PostMapping("/extract")
    @ResponseBody
    public ResponseVo extractController(MultipartFile file, @RequestParam(name = "method", defaultValue = "pretrain") String method) {

        try {
            JSONObject object = null;
            if (method.equals("pretrain")) {
                object = pretrainModelExtractor.extract(file);
            } else if (method.equals("svm")) {
                object = svmExtractor.extract(file);
            }
            return ResponseUtil.success(object);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (e instanceof FileTypeException) {
                return ResponseUtil.error(ResponseEnum.FILE_ERROR);
            } else {
                return ResponseUtil.error(ResponseEnum.SERVER_ERROR);
            }
        }
    }

    @PostMapping("/batchExtract")
    @ResponseBody
    public ResponseVo batchController(MultipartFile file) {
        List<JSONObject> array = new ArrayList<>();
        try {
            File uploaded = svmExtractor.saveUploadedFile(file);
            String mimeType = svmExtractor.identifyMimeType(uploaded);
            File folder;
            switch (mimeType) {
                case "application/zip":
                    folder = UnPackeUtil.unPackZip(uploaded);
                    break;
                case "application/x-rar-compressed":
                    throw new FileTypeException("文件[" + file.getName() + "]的类型为：" + mimeType + ",暂时无法处理rar类型的压缩文件");
                default:
                    throw new FileTypeException("文件[" + file.getName() + "]的类型为：" + mimeType + ",不是合法的压缩文件类型");
            }
            File[] files = folder.listFiles();
            if (files == null || files.length == 0) {
                logger.error("待抽取的文件个数非法");
                return ResponseUtil.error(ResponseEnum.FILE_ERROR);
            }
            CountDownLatch countDownLatch = new CountDownLatch(files.length);
            List<ListenableFuture<JSONObject>> listenableFutureList = new ArrayList<>();
            for (File f : files) {
                ListenableFuture<JSONObject> jsonObjectListenableFuture = svmExtractor.asyncExtract(f, countDownLatch);
                listenableFutureList.add(jsonObjectListenableFuture);
            }
            countDownLatch.await();

            for (ListenableFuture<JSONObject> jsonObjectListenableFuture : listenableFutureList) {
                JSONObject jsonObject = jsonObjectListenableFuture.get();
                array.add(jsonObject);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (e instanceof FileTypeException) {
                return ResponseUtil.error(ResponseEnum.FILE_ERROR);
            } else {
                return ResponseUtil.error(ResponseEnum.SERVER_ERROR);
            }
        }
        return ResponseUtil.success(array);
    }

    @GetMapping("/localExtract")
    @ResponseBody
    public ResponseVo localController(@RequestParam("path") String path) {
        long start = System.currentTimeMillis();

        try {
            File folder = new File(path);
            File[] files = folder.listFiles();
            if (files == null || files.length == 0) {
                logger.error("待抽取的文件个数非法");
                return ResponseUtil.error(ResponseEnum.FILE_ERROR);
            }
            CountDownLatch countDownLatch = new CountDownLatch(files.length);
            for (File f : files) {
                svmExtractor.asyncExtract(f, countDownLatch);
            }
            countDownLatch.await();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (e instanceof FileTypeException) {

            } else {
                return ResponseUtil.error(ResponseEnum.SERVER_ERROR);
            }
        }
        long end = System.currentTimeMillis();
        return ResponseUtil.success("finished! 用时：" + (end - start) + " ms");
    }


}
