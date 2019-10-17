package cn.edu.whu.irlab.smart_cite.service.dataPreprocesser;

import org.jdom2.Element;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gcr19
 * @date 2019-10-16 15:38
 * @desc 数据预处理类
 **/
@Service("dataPreprocessor")
public class DataPreprocessor {

    public boolean filitSubject(Element article, String subject){
        Element article_categories=article.getChild("article-meta").getChild("article-categories");
        List<Element> subject_group=article_categories.getChildren("subject-group");
        for (Element element:
                subject_group) {
            String value= element.getValue();
            boolean a= value.toLowerCase().contains(subject.toLowerCase());
            return a;
        }
        return false;
    }
}
