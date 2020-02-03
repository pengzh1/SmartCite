package cn.edu.whu.irlab.smart_cite.enums;

/**
 * @author gcr19
 * @version 1.0
 * @date 2019/12/19 18:17
 * @desc 分句异常 枚举类
 **/
public enum  SplitSentenceExceptionEnum {

    NoSentenceFound(1,"未在文本中发现句子边界: "),
    IllegalElement(2,"非法的节点类型 ");

    private Integer code;

    private String msg;

    SplitSentenceExceptionEnum(Integer code, String msg) {
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
