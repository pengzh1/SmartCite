package cn.edu.whu.irlab.smart_cite.service.actuator.Impl;

import cn.edu.whu.irlab.smart_cite.enums.CiteMarkEnum;
import cn.edu.whu.irlab.smart_cite.enums.FileTypeEnum;
import cn.edu.whu.irlab.smart_cite.enums.XMLTypeEnum;
import cn.edu.whu.irlab.smart_cite.service.Identifier.Identifier;
import cn.edu.whu.irlab.smart_cite.service.actuator.Actuator;
import cn.edu.whu.irlab.smart_cite.service.attrGenerator.AttrGenerator;
import cn.edu.whu.irlab.smart_cite.service.featureExtractor.FeatureExtractor;
import cn.edu.whu.irlab.smart_cite.service.grobid.GrobidService;
import cn.edu.whu.irlab.smart_cite.service.paperXmlReader.PaperXmlReader;
import cn.edu.whu.irlab.smart_cite.service.preprocessor.GrobidPreprocessorImpl;
import cn.edu.whu.irlab.smart_cite.service.preprocessor.LeiPreprocessorImpl;
import cn.edu.whu.irlab.smart_cite.service.preprocessor.PlosPreprocessorImpl;
import cn.edu.whu.irlab.smart_cite.service.weka.WekaService;
import cn.edu.whu.irlab.smart_cite.util.ReadUtil;
import cn.edu.whu.irlab.smart_cite.vo.Article;
import cn.edu.whu.irlab.smart_cite.vo.RecordVo;
import cn.edu.whu.irlab.smart_cite.vo.Result;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static cn.edu.whu.irlab.smart_cite.vo.FileLocation.FEATURE_FILE;

/**
 * @author gcr19
 * @date 2019-10-19 11:27
 * @desc 任务执行器 实现类
 **/
@Service("actuator")
public class ActuatorImpl implements Actuator {

    private static final Logger logger = LoggerFactory.getLogger(ActuatorImpl.class);


    @Resource(name = "identifier")
    public Identifier identifier;

    @Autowired
    private GrobidService grobidService;

    @Autowired
    private PlosPreprocessorImpl plosPreprocessor;

    @Autowired
    private LeiPreprocessorImpl leiPreprocessor;

    @Autowired
    private GrobidPreprocessorImpl grobidPreprocessor;

    @Autowired
    private AttrGenerator attrGenerator;

    @Autowired
    private PaperXmlReader paperXmlReader;

    @Autowired
    private FeatureExtractor featureExtractor;

    @Autowired
    private WekaService wekaService;


    public void AnalyzeCitationContext(File file) {

        XMLTypeEnum xmlTypeEnum = null;
        //XML根节点
        Element root = null;

        //识别文件类型
        String mimeType = identifier.identifyMimeType(file);

        //识别XML类型（PDF转换为XML）
        switch (mimeType) {
            case "application/xml":
                root = ReadUtil.read2xml(file);
                xmlTypeEnum = identifier.identifyXMLType(root, file);
                break;
            case "application/pdf":
                root = grobidService.processFulltextDocument(file);
                xmlTypeEnum = XMLTypeEnum.Grobid;
                break;
            default:
                throw new IllegalArgumentException("此文件类型为：" + mimeType + ",不是合法的文件类型");
        }

        //根据不同XML文件类型进行预处理
        switch (xmlTypeEnum) {
            case Plos:
                root = plosPreprocessor.parseXML(root, file);
                root = attrGenerator.generateAttr(root, file);
            case Grobid:
                root = grobidPreprocessor.parseXML(root, file);
                root = attrGenerator.generateAttr(root, file);
            case Lei:
                root = leiPreprocessor.parseXML(root, file);
            default:
        }

        //解析XML文件
        Article article = paperXmlReader.processFile(file, root);

        //抽取特征
        List<Result> results = featureExtractor.extract(article, file);

        Instances instances= wekaService.classify(FEATURE_FILE+ FilenameUtils.getBaseName(file.getName()) + "_features.libsvm");

        for (int i = 0; i < results.size(); i++) {
            if (instances.get(i).classValue()==0){
                results.get(i).setContext(false);
            }else {
                results.get(i).setContext(true);
            }
        }

        System.out.println(results);
    }
}

