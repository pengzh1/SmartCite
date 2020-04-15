package cn.edu.whu.irlab.smart_cite.exception;

import cn.edu.whu.irlab.smart_cite.enums.SplitSentenceExceptionEnum;

/**
 * @author gcr19
 * @version 1.0
 * @date 2019/12/18 21:27
 * @desc 分句异常
 **/
public class SplitSentenceException extends Exception {

    private Integer code;

    public SplitSentenceException(SplitSentenceExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMsg());
        this.code = exceptionEnum.getCode();
    }

    public SplitSentenceException(SplitSentenceExceptionEnum exceptionEnum, String text) {
        super(exceptionEnum.getMsg() + text);
        this.code = exceptionEnum.getCode();
    }
}
