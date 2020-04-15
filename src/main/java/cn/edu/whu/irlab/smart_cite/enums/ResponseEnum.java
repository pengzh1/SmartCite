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
    ADMIN_LOGIN_SUCCESS(100,"管理员登录"),
    USER_LOGIN_SUCCESS(101,"用户登录"),
    PLANT_USER_LOGIN_SUCCESS(201,"平台用户登录"),
    USERNAME_ERROR(102,"用户名输入错误"),
    USERNAME_SIGN_ERROR(103,"用户名已经注册过了"),
    PASSWORD_ERROR(104,"密码输入错误"),
    PASSWORD_SIGN_ERROR(105,"密码已经注册过了"),
    PHONE_SIGN_ERROR(106,"手机号已经注册过了"),
    EMAIL_SIGN_ERROR(107,"邮箱已经注册过了"),
    PHONE_EMAIL_ERROR(108,"手机号或者邮箱输入错误"),
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
