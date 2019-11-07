package cn.edu.whu.irlab.smart_cite.exception;

import cn.edu.whu.irlab.smart_cite.enums.ExceptionEnum;

/**
 * @author gcr19
 * @date 2019-10-22 15:15
 * @desc
 **/
public class ExtractException extends Exception {

    private Integer code;

    public ExtractException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMsg());
        this.code = exceptionEnum.getCode();
    }

    public ExtractException(ExceptionEnum exceptionEnum,String otherMsg){
        super(exceptionEnum.getMsg()+otherMsg);
        this.code = exceptionEnum.getCode();
    }

}
