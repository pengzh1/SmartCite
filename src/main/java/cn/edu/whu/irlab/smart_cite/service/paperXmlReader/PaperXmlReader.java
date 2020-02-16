package cn.edu.whu.irlab.smart_cite.service.paperXmlReader;

import cn.edu.whu.irlab.smart_cite.util.ElementUtil;
import cn.edu.whu.irlab.smart_cite.vo.Article;
import cn.edu.whu.irlab.smart_cite.vo.Author;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;
import cn.edu.whu.irlab.smart_cite.vo.Title;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/13 16:15
 * @desc 读取Xml文件 转换为art文件（仿lei的代码）
 **/
@Service
public class PaperXmlReader {
    private static final Logger logger = LoggerFactory.getLogger(PaperXmlReader.class);

    public void processFile(File file, Element root) {
        Element header = root.getChild("header");

        Article article = new Article();
        //初始话article
        article.setName(FilenameUtils.getBaseName(file.getName()));
        article.setAbsText(header.getChild("abstract").getValue());//todo plos数据中有的摘要有多个段落
        List<Author> authors = new ArrayList<>();
        for (Element e :
                header.getChild("author-group").getChildren()) {
            authors.add(new Author(e.getChildText("surname"), e.getChildText("given-names")));
        }
        article.setAuthors(authors);
        article.setTitle(new Title(header.getChild("title-group").getChildText("article-title"), header.
                getChild("title-group").getChildText("alt-title")));

        //解析所有句子
        List<Element> sentenceElements= ElementUtil.extractElements(root,"s");
        Sentence sentence;
        for (Element e :
                sentenceElements) {
            sentence = new Sentence(Integer.parseInt(e.getAttributeValue("id")), e.getText(), article);
//            logger.info("sentence:an%s:sn%s", sentence.getArticle().getNum(), sentence.getId());
//            s.setcType(cType(e));

        }
    }

    private void setSecInfo(Element e,Sentence sentence){

    }
}
