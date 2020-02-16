package cn.edu.whu.irlab.smart_cite.vo;

import lombok.Data;

import javax.xml.bind.Element;
import java.io.File;
import java.util.List;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/13 16:22
 * @desc 文章实体
 **/
@Data
public class Article {

//    private int num;//该参数暂无
//    private String pubInfo; //该参数暂无
private String absText;
    private String name;

    private List<Author> authors;
    private Title title;

    public Article() {
    }

}
