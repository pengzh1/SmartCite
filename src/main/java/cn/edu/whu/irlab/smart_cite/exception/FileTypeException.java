package cn.edu.whu.irlab.smart_cite.exception;

import cn.edu.whu.irlab.smart_cite.enums.ExceptionEnum;

/**
 * @author gcr19
 * @date 2019-10-19 16:09
 * @desc 文件类型异常
 **/
public class FileTypeException extends Exception {

    private Integer code;

    public FileTypeException() {
    }

    public FileTypeException(ExceptionEnum exceptionEnum){
        super(exceptionEnum.getMsg());
        this.code= exceptionEnum.getCode();
    }

    public FileTypeException(ExceptionEnum exceptionEnum,String otherMsg){
        super(exceptionEnum.getMsg()+otherMsg);
        this.code= exceptionEnum.getCode();
    }


}
