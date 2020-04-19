package cn.edu.whu.irlab.smart_cite.service.extractor.Impl;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExtractorImplTest {

    @Autowired
    private ExtractorImpl extractor;


    @Test
    public void batchTest() throws ExecutionException, InterruptedException {
        File folder = new File("C:\\Users\\Zhang_Li\\Desktop\\plos-samples1\\cs");
        File[] files = folder.listFiles();
        int size = 4;
        List<ListenableFuture<JSONObject>> listenableFutureList = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(size);
        if (files != null) {
            for (int i = 0; i < size; i++) {
                ListenableFuture<JSONObject> jsonObjectListenableFuture = extractor.asyncExtract(files[i], countDownLatch);
                listenableFutureList.add(jsonObjectListenableFuture);
            }
        }

        countDownLatch.await();

        for (ListenableFuture<JSONObject> jsonObjectListenableFuture : listenableFutureList) {
            JSONObject jsonObject = jsonObjectListenableFuture.get();
            System.out.println(jsonObject.toJSONString());
        }

        System.out.println("finished");
    }
}
