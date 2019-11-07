package cn.edu.whu.irlab.smart_cite.service.actuator.Impl;

import cn.edu.whu.irlab.smart_cite.enums.CiteMarkEnum;
import cn.edu.whu.irlab.smart_cite.enums.FileTypeEnum;
import cn.edu.whu.irlab.smart_cite.enums.XMLTypeEnum;
import cn.edu.whu.irlab.smart_cite.service.Identifier.Identifier;
import cn.edu.whu.irlab.smart_cite.service.actuator.Actuator;
import cn.edu.whu.irlab.smart_cite.service.extractor.Extractor;
import cn.edu.whu.irlab.smart_cite.service.extractor.ExtractorOfGrobid;
import cn.edu.whu.irlab.smart_cite.service.extractor.ExtractorOfPlos;
import cn.edu.whu.irlab.smart_cite.util.ReadUtil;
import cn.edu.whu.irlab.smart_cite.vo.RecordVo;
import org.jdom2.Element;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author gcr19
 * @date 2019-10-19 11:27
 * @desc 任务执行器 实现类
 **/
@Service("actuator")
public class ActuatorImpl implements Actuator {

    @Resource(name = "identifier")
    public Identifier identifier;

    @Resource(name = "extractorOfGrobid")
    public ExtractorOfGrobid extractorOfGrobid;

    @Resource(name = "extractorOfPlos")
    public ExtractorOfPlos extractorOfPlos;


    public void AnalyzeCitionContext(File file) throws Exception {
        String filePath = file.getPath();
        String mimeType = identifier.identifyMimeType(file);
        XMLTypeEnum xmlTypeEnum = null;
        Element article = null;
        switch (mimeType) {
            case "application/xml":
                article = ReadUtil.read2xml(file);
                xmlTypeEnum = identifier.identifyXMLType(article, filePath);
                break;
            case "application/pdf":
                break;
            default:
                throw new IOException("文件类型不合法：" + mimeType);
        }
        List<RecordVo> recordVos = null;
        if (xmlTypeEnum.equals(XMLTypeEnum.Grobid)) {
            extractorOfGrobid.init(article);
            recordVos = extractorOfGrobid.extract();
        } else if (xmlTypeEnum.equals(XMLTypeEnum.Plos)) {
            extractorOfPlos.init(article);
            recordVos = extractorOfPlos.extract();
        }
    }
}

