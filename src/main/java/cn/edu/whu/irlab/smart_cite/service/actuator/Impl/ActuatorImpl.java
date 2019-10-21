package cn.edu.whu.irlab.smart_cite.service.actuator.Impl;

import cn.edu.whu.irlab.smart_cite.enums.CiteMarkEnum;
import cn.edu.whu.irlab.smart_cite.enums.FileTypeEnum;
import cn.edu.whu.irlab.smart_cite.enums.XMLTypeEnum;
import cn.edu.whu.irlab.smart_cite.service.Identifier.Identifier;
import cn.edu.whu.irlab.smart_cite.service.actuator.Actuator;
import cn.edu.whu.irlab.smart_cite.service.extractor.Extractor;
import cn.edu.whu.irlab.smart_cite.util.ReadUtil;
import org.jdom2.Element;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * @author gcr19
 * @date 2019-10-19 11:27
 * @desc 任务执行器 实现类
 **/
@Service("actuator")
public class ActuatorImpl implements Actuator {

    @Resource(name = "identifier")
    public Identifier identifier;

    @Resource(name = "extractor")
    public Extractor extractor;


    public void AnalyzeCitionContext(File file) throws Exception{
        String filePath=file.getPath();
        String mimeType = identifier.identifyMimeType(file);
        XMLTypeEnum xmlTypeEnum;
        CiteMarkEnum citeMarkEnum;
        Element article=null;
        switch (mimeType) {
            case "application/xml":
                article = ReadUtil.read2xml(file);
                xmlTypeEnum=identifier.identifyXMLType(article,filePath);
                break;
            case "application/pdf":
                break;
            default:
                throw new IOException("文件类型不合法：" + mimeType);
        }
        extractor.extract(article);
    }
}

