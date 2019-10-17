package cn.edu.whu.irlab.smart_cite.service.Identifier;


import java.io.File;

/**
 * @author gcr19
 * @date 2019-08-14 20:35
 * @desc 文件类型判定器接口
 **/
public interface Identifier {

    String getMimeType(File file);
}
