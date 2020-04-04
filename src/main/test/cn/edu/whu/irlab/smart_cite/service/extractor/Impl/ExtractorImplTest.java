package cn.edu.whu.irlab.smart_cite.service.extractor.Impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExtractorImplTest {
    @Autowired
    private ExtractorImpl extractor;

    @Test
    public void extractController() {
        String path = "C:\\Users\\Zhang_Li\\Desktop\\plos-samples";
        File[] dirs = new File(path).listFiles();
        for (File dir : dirs) {
            for (File file : dir.listFiles()) {
                extractor.AnalyzeCitationContext(file);
            }
        }

    }
}
