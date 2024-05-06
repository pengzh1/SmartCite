package cn.edu.whu.irlab.smart_cite.controller;

import cn.edu.whu.irlab.smart_cite.enums.ResponseEnum;
import cn.edu.whu.irlab.smart_cite.exception.FileTypeException;
import cn.edu.whu.irlab.smart_cite.service.extractor.PretrainModelExtractor;
import cn.edu.whu.irlab.smart_cite.service.extractor.SvmExtractor;
import cn.edu.whu.irlab.smart_cite.util.ResponseUtil;
import cn.edu.whu.irlab.smart_cite.util.UnPackeUtil;
import cn.edu.whu.irlab.smart_cite.util.WriteUtil;
import cn.edu.whu.irlab.smart_cite.vo.ResponseVo;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static cn.edu.whu.irlab.smart_cite.util.FileUtil.identifyMimeType;
import static cn.edu.whu.irlab.smart_cite.util.FileUtil.saveUploadedFile;
import static cn.edu.whu.irlab.smart_cite.vo.FileLocation.OUTPUT;

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

    @PostMapping("/downloadFile")
    @ResponseBody
    public ResponseVo downloadFileController(HttpServletResponse response, @RequestParam(name = "file_name") String file_name) throws UnsupportedEncodingException {
        if (file_name != null) {
            File file = new File(OUTPUT + File.separator + file_name + ".json");
            boolean state = writeFileToResponse(file, response);
            if (state) {
                return ResponseUtil.success();
            }
        }
        return ResponseUtil.error(ResponseEnum.SERVER_ERROR);
    }


    @PostMapping("/batchExtract")
    @ResponseBody
    public ResponseVo batchController(MultipartFile file) {
        List<JSONObject> array = new ArrayList<>();
        try {
            File uploaded = saveUploadedFile(file);
            String mimeType = identifyMimeType(uploaded);
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
        WriteUtil.writeStr(OUTPUT + File.separator + file.getName() + ".json", JSONObject.toJSONString(array));
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

    @GetMapping("/localSplit")
    @ResponseBody
    public ResponseVo splitController(@RequestParam("path") String path) {
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
                svmExtractor.asyncSplit(f, countDownLatch);
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

    @GetMapping("/downLoadTestCase")
    @ResponseBody
    public ResponseVo downLoadTestCaseController(HttpServletResponse response) throws UnsupportedEncodingException {
        File file = new File("data/test_data.zip");
        boolean state = writeFileToResponse(file, response);
        if (state) {
            return ResponseUtil.success();

        }
        return ResponseUtil.error(ResponseEnum.SERVER_ERROR);
    }

    /**
     * 向响应输出流中写入文件
     *
     * @param file
     * @param response
     */
    private boolean writeFileToResponse(File file, HttpServletResponse response) throws UnsupportedEncodingException {
        if (file.exists()) {
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "utf-8"));
            byte[] buffer = new byte[1024];
            FileInputStream fileInputStream = null;
            BufferedInputStream bufferedInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                OutputStream outputStream = response.getOutputStream();
                int i = bufferedInputStream.read(buffer);
                while (i != -1) {
                    outputStream.write(buffer, 0, i);
                    i = bufferedInputStream.read(buffer);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;

    }

}
