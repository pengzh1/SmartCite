package cn.edu.whu.irlab.smart_cite.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/13 19:13
 * @desc 作者实体
 **/
@Data
public class Author implements ToJsonAble {

    private String surName;
    private String givenName;

    public Author(String surName, String givenName) {
        this.surName = surName;
        this.givenName = givenName;
    }

    @Override
    public String toString() {
        return "{" + "\"surName\":\"" + surName + '\"' +
                ",\"givenName\":\"" + givenName + '\"' +
                '}';
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("surName",surName);
        jsonObject.put("givenName",givenName);
        return jsonObject;
    }
}
