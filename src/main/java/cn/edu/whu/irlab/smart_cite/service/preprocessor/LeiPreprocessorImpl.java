package cn.edu.whu.irlab.smart_cite.service.preprocessor;

import org.jdom2.Element;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/6 11:29
 * @desc lei数据预处理实现
 **/
@Service
public class LeiPreprocessorImpl extends PreprocessorImpl {

    @Override
    public void parseXML(Element root, File file) {
        parseStep1(root);
        //todo 识别引文标志

    }

    @Override
    public void parseStep1(Element root) {
        //将reference节点下的p节点重命名为ref节点
        Element references = root.getChild("body").getChild("references");
        for (Element ref :
                references.getChildren()) {
            ref.setName("ref");
        }
        super.parseStep1(root);
    }

    @Override
    public void extractXref(Element element) {

    }


}
