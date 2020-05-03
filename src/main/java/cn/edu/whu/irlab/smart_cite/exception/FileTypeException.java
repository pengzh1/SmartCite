package cn.edu.whu.irlab.smart_cite.exception;

import cn.edu.whu.irlab.smart_cite.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author gcr19
 * @date 2019-10-19 16:09
 * @desc 文件类型异常
 **/
public class FileTypeException extends IllegalArgumentException {

    public FileTypeException(String s) {
        super(s);
    }
}
