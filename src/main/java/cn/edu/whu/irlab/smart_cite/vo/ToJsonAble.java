package cn.edu.whu.irlab.smart_cite.vo;


import com.alibaba.fastjson.JSONObject;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/5/5 12:16
 * @desc 可输出Json的特性
 **/
public interface ToJsonAble {

    JSONObject toJson();
}
