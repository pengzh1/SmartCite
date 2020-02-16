package cn.edu.whu.irlab.smart_cite.vo;

import lombok.Data;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/14 10:44
 * @desc 标题实体
 **/
@Data
public class Title {

    private String title;
    private String altTitle;

    public Title(String title, String altTitle) {
        this.title = title;
        this.altTitle = altTitle;
    }
}
