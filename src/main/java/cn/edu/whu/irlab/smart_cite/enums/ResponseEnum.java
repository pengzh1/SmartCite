package cn.edu.whu.irlab.smart_cite.enums;

/**
 * @author gcr
 * @version 1.0
 * @date 2020-04-09 20:35
 * @desc 枚举管理返回给前端的code
 **/
public enum ResponseEnum {

    SUCCESS(200,"success"),
    UNKNOWN_ERROR(-1,"unknown error"),
    FILE_ERROR(100,"File type error, please upload the right file"),
    FILE_NOT_FOUND(101,"upload fail,file can no be found"),
    SERVER_ERROR(500,"server error"),
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
