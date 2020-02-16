package cn.edu.whu.irlab.smart_cite.service.actuator.Impl;

import cn.edu.whu.irlab.smart_cite.enums.CiteMarkEnum;
import cn.edu.whu.irlab.smart_cite.enums.FileTypeEnum;
import cn.edu.whu.irlab.smart_cite.enums.XMLTypeEnum;
import cn.edu.whu.irlab.smart_cite.service.Identifier.Identifier;
import cn.edu.whu.irlab.smart_cite.service.actuator.Actuator;
import cn.edu.whu.irlab.smart_cite.service.extractor.Extractor;
import cn.edu.whu.irlab.smart_cite.service.extractor.ExtractorOfGrobid;
import cn.edu.whu.irlab.smart_cite.service.extractor.ExtractorOfPlos;
import cn.edu.whu.irlab.smart_cite.service.grobid.GrobidService;
import cn.edu.whu.irlab.smart_cite.util.ReadUtil;
import cn.edu.whu.irlab.smart_cite.vo.RecordVo;
import org.jdom2.Element;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private GrobidService grobidService;


    public void AnalyzeCitationContext(File file) throws Exception {
        String filePath = file.getPath();
        XMLTypeEnum xmlTypeEnum = null;
        //XML根节点
        Element article = null;

        //识别文件类型
        String mimeType = identifier.identifyMimeType(file);

        //识别XML类型（PDF转换为XML）
        switch (mimeType) {
            case "application/xml":
                article = ReadUtil.read2xml(file);
                xmlTypeEnum = identifier.identifyXMLType(article, filePath);
                break;
            case "application/pdf":
                article = grobidService.processFulltextDocument(file);
                xmlTypeEnum=XMLTypeEnum.Grobid;
                break;
            default:
                throw new IllegalArgumentException("此文件类型为：" + mimeType+",不是合法的文件类型");
        }

        switch (xmlTypeEnum){
            case Plos:

            case Grobid:
            case Lei:
            default:
        }
    }
}

