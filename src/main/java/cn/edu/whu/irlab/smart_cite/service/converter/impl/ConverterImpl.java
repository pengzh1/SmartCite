package cn.edu.whu.irlab.smart_cite.service.converter.impl;

import cn.edu.whu.irlab.smart_cite.service.converter.Converter;
import org.jdom2.Element;
import org.springframework.stereotype.Service;


/**
 * @author gcr19
 * @date 2019-10-14 19:54
 * @desc
 **/
@Service("converter")
public class ConverterImpl implements Converter {


    public void getKeyLabel(Element firstNode){
        String nameOfFirstNode=firstNode.getName();
        if (nameOfFirstNode.equals("article")){

        }else if (nameOfFirstNode.equals("TEI")){

        }
    }
}
