package cn.edu.whu.irlab.smart_cite.service.preprocessor;

import cn.edu.whu.irlab.smart_cite.util.ElementUtil;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/11 13:06
 * @desc Plos数据预处理实现
 **/
@Service
public class PlosPreprocessorImpl extends PreprocessorImpl {

    private static final Logger logger = LoggerFactory.getLogger(PlosPreprocessorImpl.class);

    //需要删除的节点名列表
    private static final String[] filterTag = {"journal-meta", "article-categories", "aff", "author-notes",
            "author-notes", "pub-date", "issue", "elocation-id", "history", "permissions", "funding-group", "counts", "fig",
            "supplementary-material", "ack"};
    private static final List<String> filterTagList = Arrays.asList(filterTag);

    private static final String XREF_LABEL_NAME = "xref";
    private static final String ATTRIBUTE_NAME = "ref-type";
    private static final String ATTRIBUTE_VALUE = "bibr";

    @Override
    public void filterTags(Element root) {
        super.filterTags(root, filterTagList);
    }

    @Override
    public void extractXref(Element element) {
        super.extractXref(element, XREF_LABEL_NAME, ATTRIBUTE_NAME, ATTRIBUTE_VALUE);
    }

    @Override
    public void removeElementNotXref() {
        super.removeElementNotXref(XREF_LABEL_NAME, ATTRIBUTE_NAME, ATTRIBUTE_VALUE);
    }

    @Override
    void fillHeader(Element root, Element header) {
        header.addContent(root.getChild("front").getChild("article-meta").getChild("title-group").detach());
        header.addContent(root.getChild("front").getChild("article-meta").getChild("abstract").detach());

        //设置作者
        List<Element> authors=new ArrayList<>();
        ElementUtil.extractElements(root.getChild("front").getChild("article-meta"),"name",authors);
        Element author_group=new Element("author-group");
        for (Element e :
                authors) {
            author_group.addContent(e.detach());
        }
        header.addContent(author_group);
    }

    @Override
    void fillBody(Element root, Element body) {
        body.addContent(root.getChild("body").removeContent());
        reNumberSec(body);
    }

    @Override
    void fillBack(Element root, Element back) {
        Element ref_list = root.getChild("back").getChild("ref-list");
        ref_list.removeChild("title");
        back.addContent(ref_list.detach());
    }

}
