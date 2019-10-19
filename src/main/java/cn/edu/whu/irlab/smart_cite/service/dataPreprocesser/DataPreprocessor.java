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


    public boolean containsTheSubject(Element article, String string){
        Element article_categories=article.getChild("front").getChild("article-meta").getChild("article-categories");
        List<Element> subject_groups=article_categories.getChildren("subj-group");
        for (Element subj_group:
                subject_groups) {
            List<Element> subjects=subj_group.getChildren("subject");
            for (Element subject :
                    subjects) {
                if (subject.getValue().toLowerCase().contains(string.toLowerCase())){
                    return true;
                }
           }
        }
        return false;
    }
}
