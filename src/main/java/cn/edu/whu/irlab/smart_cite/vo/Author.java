package cn.edu.whu.irlab.smart_cite.vo;

import lombok.Data;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/13 19:13
 * @desc 作者实体
 **/
@Data
public class Author {

    private String surName;
    private String givenName;

    public Author(String surName, String givenName) {
        this.surName = surName;
        this.givenName = givenName;
    }
}
