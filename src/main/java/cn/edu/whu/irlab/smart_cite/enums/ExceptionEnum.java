package cn.edu.whu.irlab.smart_cite.enums;

public enum ExceptionEnum {

    Not_File(1,"目标路径不是文件"),
    IllegalXml(2,"不可识别的XML类型");

    private Integer code;

    private String msg;

    ExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
