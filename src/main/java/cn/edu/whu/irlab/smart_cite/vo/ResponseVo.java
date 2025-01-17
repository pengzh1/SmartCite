package cn.edu.whu.irlab.smart_cite.vo;

import lombok.Data;

/**
 * @author fangrf
 * @version 1.0
 * @date 2019-07-23 16:14
 * @desc 前端请求返回的对象
 **/
@Data
public class ResponseVo<T> {

    //状态码
    private Integer code;

    //提示信息
    private String msg;

    //返回的数据
    private T data;
}
