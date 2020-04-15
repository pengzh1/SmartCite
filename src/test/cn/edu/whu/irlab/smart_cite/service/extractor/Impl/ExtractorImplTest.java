package cn.edu.whu.irlab.smart_cite.service.extractor.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExtractorImplTest {

    @Autowired
    private ExtractorImpl extractor;

    @Test
    public void test01() throws Exception {
        File[] files = new File("plos-samples/cs").listFiles();
        JSONArray array=new JSONArray();

        for (File file : files) {
            CompletableFuture<JSONObject> jsonObjectCompletableFuture = extractor.asyncExtract(file);
            array.add(jsonObjectCompletableFuture);
        }

        System.out.println(array.size());
//        Assert.assertEquals();
    }

}
