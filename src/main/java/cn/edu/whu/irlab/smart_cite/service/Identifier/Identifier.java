package cn.edu.whu.irlab.smart_cite.service.Identifier;


import cn.edu.whu.irlab.smart_cite.enums.XMLTypeEnum;
import cn.edu.whu.irlab.smart_cite.exception.FileTypeException;
import org.jdom2.Element;

import java.io.File;
import java.io.IOException;

/**
 * @author gcr19
 * @date 2019-08-14 20:35
 * @desc 文件类型判定器接口
 **/
public interface Identifier {

    String identifyMimeType(File file) ;

    XMLTypeEnum identifyXMLType(Element firstNode, File file);
}
