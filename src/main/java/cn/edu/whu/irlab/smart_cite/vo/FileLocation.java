package cn.edu.whu.irlab.smart_cite.vo;

import java.io.File;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/29 20:35
 * @desc 文件位置配置
 **/
public class FileLocation {

    //存放上传的文件
    public final static String UPLOAD_FILE = System.getProperty("user.dir") + File.separator + "temp" + File.separator + "upload";

    //存放Libsvm特征文件
    public final static String FEATURE_FILE = System.getProperty("user.dir") + File.separator + "temp" + File.separator + "feature_files";

    //存放完成编号的XML文档
    public final static String NUMBERED = "temp/numbered/";

    //存放完成过滤操作的XML文档
    public final static String FILTERED = "temp/filtered/";

    //存放重新整理后的XML文档
    public final static String REFORMATTED = "temp/reformatted";

    //存放完成编号的XML文档
    public final static String ADDED = "temp/addedAttr";

    //存放art文件
    public final static String ART = "temp/art";

    //存放输出文件
    public final static String OUTPUT = "output";

}
