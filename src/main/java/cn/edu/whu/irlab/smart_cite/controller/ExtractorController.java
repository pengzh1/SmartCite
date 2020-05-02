package cn.edu.whu.irlab.smart_cite.controller;

import cn.edu.whu.irlab.smart_cite.enums.ResponseEnum;
import cn.edu.whu.irlab.smart_cite.exception.FileTypeException;
import cn.edu.whu.irlab.smart_cite.service.extractor.Impl.ExtractorImpl;
import cn.edu.whu.irlab.smart_cite.util.ResponseUtil;
import cn.edu.whu.irlab.smart_cite.util.UnPackeUtil;
import cn.edu.whu.irlab.smart_cite.vo.ResponseVo;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
            object = extractor.extract(file);
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

    @PostMapping("/batchExtract")
    public ResponseVo batchController(MultipartFile file) {
        List<JSONObject> array = new ArrayList<>();
        try {
            File uploaded = extractor.saveUploadedFile(file);
            String mimeType = extractor.identifyMimeType(uploaded);
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
                ListenableFuture<JSONObject> jsonObjectListenableFuture = extractor.asyncExtract(f, countDownLatch);
                listenableFutureList.add(jsonObjectListenableFuture);
            }
            countDownLatch.await();

            for (ListenableFuture<JSONObject> jsonObjectListenableFuture : listenableFutureList) {
                JSONObject jsonObject = jsonObjectListenableFuture.get();
                array.add(jsonObject);
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            if (e instanceof FileTypeException) {
                return ResponseUtil.error(ResponseEnum.FILE_ERROR);
            } else {
                return ResponseUtil.error(ResponseEnum.SERVER_ERROR);
            }
        }
        return ResponseUtil.success(array);
    }
}
