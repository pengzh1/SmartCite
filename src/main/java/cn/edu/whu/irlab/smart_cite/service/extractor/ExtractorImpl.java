package cn.edu.whu.irlab.smart_cite.service.extractor;

import cn.edu.whu.irlab.smart_cite.enums.XMLTypeEnum;
import cn.edu.whu.irlab.smart_cite.exception.FileTypeException;
import cn.edu.whu.irlab.smart_cite.service.featureExtractor.FeatureExtractor;
import cn.edu.whu.irlab.smart_cite.service.grobid.GrobidService;
import cn.edu.whu.irlab.smart_cite.service.paperXmlReader.PaperXmlReader;
import cn.edu.whu.irlab.smart_cite.service.preprocessor.GrobidPreprocessorImpl;
import cn.edu.whu.irlab.smart_cite.service.preprocessor.JsonXmlPreprocessorImpl;
import cn.edu.whu.irlab.smart_cite.service.preprocessor.LeiPreprocessorImpl;
import cn.edu.whu.irlab.smart_cite.service.preprocessor.PlosPreprocessorImpl;
import cn.edu.whu.irlab.smart_cite.service.classifier.SVMClassifier;
import cn.edu.whu.irlab.smart_cite.util.ReadUtil;
import cn.edu.whu.irlab.smart_cite.util.TypeConverter;
import cn.edu.whu.irlab.smart_cite.util.WriteUtil;
import cn.edu.whu.irlab.smart_cite.vo.Article;
import cn.edu.whu.irlab.smart_cite.vo.FileLocation;
import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Result;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.helpers.DefaultHandler;
import weka.core.Instances;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static cn.edu.whu.irlab.smart_cite.vo.FileLocation.*;

/**
 * @author gcr19
 * @date 2019-10-19 11:27
 * @desc 任务执行器 实现类
 **/
@Slf4j
@Service("extractor")
public class ExtractorImpl {

    @Autowired
    private GrobidService grobidService;

    @Autowired
    private PlosPreprocessorImpl plosPreprocessor;

    @Autowired
    private LeiPreprocessorImpl leiPreprocessor;

    @Autowired
    private GrobidPreprocessorImpl grobidPreprocessor;

    @Autowired
    private PaperXmlReader paperXmlReader;

    @Autowired
    private FeatureExtractor featureExtractor;

    @Autowired
    private SVMClassifier SVMClassifier;

    @Autowired
    private JsonXmlPreprocessorImpl jsonXmlPreprocessor;


    public JSONObject extract(MultipartFile file) throws IOException {
        File upload = saveUploadedFile(file);
        return extract(upload);
    }


    public JSONObject extract(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在");
        }

        XMLTypeEnum xmlTypeEnum = null;
        //XML根节点
        Element root = null;
        //识别文件类型
        String mimeType = identifyMimeType(file);

        //识别XML类型（PDF转换为XML）
        switch (mimeType) {
            case "text/html":
            case "application/xml":
                root = ReadUtil.read2xml(file);
                xmlTypeEnum = identifyXMLType(root, file);
                break;
            case "application/pdf":
                root = grobidService.processFulltextDocument(file);
                xmlTypeEnum = XMLTypeEnum.Grobid;
                break;
            case "application/json":
                String jsonString = ReadUtil.read2Str(file);
                try {
                    root = TypeConverter.jsonStr2Xml(jsonString);
                } catch (JDOMException | IOException e) {
                    log.error("文件[" + file.getName() + "]解析错误", e);
                    throw new IllegalArgumentException("Json解析错误");
                }
                xmlTypeEnum = XMLTypeEnum.Json;
                break;
            default:
                throw new FileTypeException("文件[" + file.getName() + "]的类型为：" + mimeType + ",不是合法的文件类型");
        }

        //根据不同XML文件类型进行预处理 处理后重要的文件numbered,addedAttr
        switch (xmlTypeEnum) {
            case Plos:
                root = plosPreprocessor.parseXML(root, file);
                break;
            case Grobid:
                root = grobidPreprocessor.parseXML(root, file);
                break;
            case Lei:
                root = leiPreprocessor.parseXML(root, file);
                break;
            case Json:
                root = jsonXmlPreprocessor.parseXML(root, file);
            default:
                break;
        }

        //解析XML文件
        Article article = paperXmlReader.processFile(file, root);

        //抽取特征
        List<Result> results = featureExtractor.extract(article,false);

        article = null;//释放内存

        //分类
        Instances instances = SVMClassifier.classify(FEATURE_FILE + File.separator + file.getName() + "_features.libsvm");

        //匹配分类结果
        for (int i = 0; i < results.size(); i++) {
            if (instances.get(i).classValue() == 0) {
                results.get(i).setContext(false);
            } else {
                results.get(i).setContext(true);
            }
        }

        JSONArray refTags = CombinedResult(results);

        JSONObject result = new JSONObject();
        result.put("fileName", file.getName());
        result.put("refTags", refTags);

//        WriteUtil.writeList(OUTPUT + FilenameUtils.getBaseName(file.getName()) + ".txt", refTags);//todo 配置多样的输出
        WriteUtil.writeStr(OUTPUT + File.separator + file.getName() + ".json", result.toJSONString());
        log.info("extract context of article [" + file.getName() + "] successfully");
        removeTempFile(file);

        return result;
    }

    private void removeTempFile(File file) {
        File added = new File(ADDED + File.separator + file.getName() + ".xml");
        File filtered = new File(FILTERED + File.separator + file.getName() + ".xml");
        File numbered = new File(NUMBERED + File.separator + file.getName() + ".xml");
        File reformatted = new File(REFORMATTED + File.separator + file.getName() + ".xml");
        File feature = new File(FEATURE_FILE + File.separator + file.getName() + "_features.libsvm");

        added.delete();
        filtered.delete();
        numbered.delete();
        reformatted.delete();
        feature.delete();
        log.info("remove temp file about article [" + file.getName() + "] successfully!");
    }

    @Async
    public ListenableFuture<JSONObject> asyncExtract(File file, CountDownLatch countDownLatch) {
        AsyncResult<JSONObject> jsonObjectAsyncResult = null;
        try {
            JSONObject object = extract(file);
            jsonObjectAsyncResult = new AsyncResult<>(object);
        } finally {
            countDownLatch.countDown();
        }
        return jsonObjectAsyncResult;
    }

    /**
     * @param results
     * @return
     * @auther gcr19
     * @desc 聚合分析结果
     **/
    private JSONArray CombinedResult(List<Result> results) {
        List<RefTag> refTags = new ArrayList<>();
        for (Result r :
                results) {
            RefTag refTag = r.getRefTag();
            if (r.isContext()) {
                refTag.getContextList().add(r.getSentence());
            }
            if (!refTags.contains(refTag)) {
                refTags.add(refTag);
            }
        }

        JSONArray array = new JSONArray();
        for (RefTag r :
                refTags) {
            JSONObject json = r.toJson();
            array.add(json);
        }
        return array;
    }

    /**
     * 获取文件类型
     *
     * @param file 文件
     * @return mimeType
     */
    public String identifyMimeType(File file) {
        if (file.isDirectory()) {
            throw new IllegalArgumentException("该路径为文件夹，不是文件。路径：" + file.getPath());
        }
        AutoDetectParser parser = new AutoDetectParser();
        parser.setParsers(new HashMap<MediaType, Parser>());
        Metadata metadata = new Metadata();
        metadata.add(TikaMetadataKeys.RESOURCE_NAME_KEY, file.getName());
        try (InputStream stream = new FileInputStream(file)) {
            parser.parse(stream, new DefaultHandler(), metadata, new ParseContext());
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return metadata.get(HttpHeaders.CONTENT_TYPE);
    }

    /**
     * @param article 根节点
     * @param file    文件
     * @return
     * @auther gcr19
     * @desc 识别XML文件的类型
     **/
    private XMLTypeEnum identifyXMLType(Element article, File file) {
        String nameOfFirstNode = article.getName();
        if (nameOfFirstNode.equals("article")) {
            Element header=article.getChild("header");
            if (header!=null){
                return XMLTypeEnum.Lei;
            }else {
                return XMLTypeEnum.Plos;
            }
        } else if (nameOfFirstNode.equals("TEI")) {
            return XMLTypeEnum.Grobid;
        }
        throw new IllegalArgumentException("非法的XML类型，文件名：" + file.getName());
    }

    /**
     * @param file 上传的文件
     * @return 保存的文件
     * @auther gcr19
     * @desc 保存上传的文件
     **/
    public File saveUploadedFile(MultipartFile file) throws IOException {
        File upload = new File(FileLocation.UPLOAD_FILE + File.separator + file.getOriginalFilename());
        file.transferTo(upload);
        return upload;
    }
}

