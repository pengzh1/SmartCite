package cn.edu.whu.irlab.smart_cite.service.preprocessor;

import cn.edu.whu.irlab.smart_cite.util.WriteUtil;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/6 11:29
 * @desc lei数据预处理实现
 **/
@Service
public class LeiPreprocessorImpl extends PreprocessorImpl {

    private static final Logger logger = LoggerFactory.getLogger(LeiPreprocessorImpl.class);

    //需要删除的节点名列表
    private static final String[] filterTag = {"footnote", "page", "doubt", "table", "tr", "td", "appendix", "figure",
            "frontmatter", "meta", "pdfmetadata", "ocrmetadata"};
    private static final List<String> filterTagList = Arrays.asList(filterTag);


    @Override
    public Element parseXML(Element root, File file) {
        //过滤无内容的无关节点
        filterTags(root, filterTagList);
        parseStep1(root);
        //todo 过滤含内容的无关节点
        //todo 识别引文标志
        //写出到新文件
        try {
            WriteUtil.writeXml(root, super.FILTERED + FilenameUtils.getBaseName(file.getName()) + ".xml");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public void parseStep1(Element root) {
        //将reference节点下的p节点重命名为ref节点
        Element references = root.getChild("body").getChild("references");
        for (Element ref :
                references.getChildren()) {
            ref.setName("ref");
        }
    }

    @Override
    public void filterTags(Element root) {
        super.filterTags(root, filterTagList);
    }

    @Override
    public void extractXref(Element element) {
    }

    @Override
    public void removeElementNotXref() {

    }

    @Override
    void fillHeader(Element root, Element header) {

    }

    @Override
    void fillBody(Element root, Element body) {

    }

    @Override
    void fillBack(Element root, Element back) {

    }
}
