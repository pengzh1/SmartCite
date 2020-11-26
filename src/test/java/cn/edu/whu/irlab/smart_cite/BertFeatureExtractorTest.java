package cn.edu.whu.irlab.smart_cite;

import cn.edu.whu.irlab.smart_cite.service.featureExtractor.BertFeatureExtractor;
import cn.edu.whu.irlab.smart_cite.service.paperXmlReader.PaperXmlReader;
import cn.edu.whu.irlab.smart_cite.service.preprocessor.LeiPreprocessorImpl;
import cn.edu.whu.irlab.smart_cite.service.preprocessor.PlosPreprocessorImpl;
import cn.edu.whu.irlab.smart_cite.util.ReadUtil;
import cn.edu.whu.irlab.smart_cite.util.WriteUtil;
import cn.edu.whu.irlab.smart_cite.vo.Article;
import cn.edu.whu.irlab.smart_cite.vo.BertPair;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/11/26 10:27
 * @desc bert模型的特征收集器测试类
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class BertFeatureExtractorTest {

    private static final String LeiTestFilePath = "test/source/31-P07-1001-paper.xml";
    private static final String PlosTestFilePath = "test/source/asset_id=10.1371%2Fjournal.pone.0000039.XML";

    @Autowired
    private LeiPreprocessorImpl leiPreprocessor;

    @Autowired
    private PlosPreprocessorImpl plosPreprocessor;

    @Autowired
    private PaperXmlReader paperXmlReader;

    @Autowired
    private BertFeatureExtractor bertFeatureExtractor;

    @Test
    public void LeiTest() {
        File file = new File(LeiTestFilePath);
        Element element = ReadUtil.read2xml(file);
        Element newRoot = leiPreprocessor.parseXML(element, file);
        Article article = paperXmlReader.processLabeledFile(file, newRoot);
        List<BertPair> bertPairs = bertFeatureExtractor.extract(article);
        WriteUtil.writeBertPair2csv("test/testOutput/31-P07-1001-paper.csv",bertPairs);
        System.out.println(bertPairs);
    }


}
