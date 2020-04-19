package cn.edu.whu.irlab.smart_cite.extractor;

import cn.edu.whu.irlab.smart_cite.service.extractor.Impl.ExtractorImpl;
import com.alibaba.fastjson.JSONArray;
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
        long start = System.currentTimeMillis();
        File folder = new File("sources/plos/computer_science");
        File[] files = folder.listFiles();
        int size = 8;
        List<ListenableFuture<JSONObject>> listenableFutureList = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(size);
        if (files != null) {
            for (int i = 0; i < 10; i++) {
                ListenableFuture<JSONObject> jsonObjectListenableFuture = extractor.asyncExtract(files[i], countDownLatch);
                listenableFutureList.add(jsonObjectListenableFuture);
            }
        }

        countDownLatch.await();

        JSONArray array = new JSONArray();
        for (ListenableFuture<JSONObject> jsonObjectListenableFuture : listenableFutureList) {
            JSONObject jsonObject = jsonObjectListenableFuture.get();
            array.add(jsonObject);
        }
        System.out.println(array);
        long end = System.currentTimeMillis();
        System.out.println("finished! 用时：" + (end - start) + " ms");
    }
}
