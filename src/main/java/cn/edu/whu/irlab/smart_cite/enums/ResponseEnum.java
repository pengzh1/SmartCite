package cn.edu.whu.irlab.smart_cite.enums;

/**
 * @author gcr
 * @version 1.0
 * @date 2020-04-09 20:35
 * @desc 枚举管理返回给前端的code
 **/
public enum ResponseEnum {

    SUCCESS(0,"成功"),
    UNKNOW_ERROR(-1,"未知错误"),
    FILE_ERROR(100,"文件错误，请上传正确的文件"),
    SERVER_ERROR(500,"服务器错误"),
    ;

    private Integer code;

    private String msg;

    ResponseEnum(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
