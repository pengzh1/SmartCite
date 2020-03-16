package cn.edu.whu.irlab.smart_cite.service.attrGenerator;

import cn.edu.whu.irlab.smart_cite.util.ElementUtil;
import cn.edu.whu.irlab.smart_cite.util.WriteUtil;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cn.edu.whu.irlab.smart_cite.vo.FileLocation.ADDED;


/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/16 10:13
 * @desc 属性生成器
 **/
@Service
public class AttrGenerator {

    private static final Logger logger = LoggerFactory.getLogger(AttrGenerator.class);



    private List<Element> sentences = new ArrayList<>();

    public Element generateAttr(Element root, File file) {
        Element body = root.getChild("body");
        ElementUtil.extractElements(body, "s", sentences);
        addSecAttr();
        addLevelAndPAttr();
        addCTypeAttr();
        writeFile(root, ADDED, file);
        return root.setAttribute("status","attrAdded");
    }



    private void addLevelAndPAttr() {
        for (Element sElement :
                sentences) {

            org.jdom2.Element parent = sElement.getParentElement();
            //addPAttr 添加p属性
            sElement.setAttribute("p", String.valueOf(parent.getAttributeValue("id")));
            //addLevelAttr 添加level属性
            parent = parent.getParentElement();
            int level = 0;
            while (parent.getName().equals("sec")) {
                level++;
                parent = parent.getParentElement();
            }
            sElement.setAttribute("level", String.valueOf(level));
        }
    }


    private void writeFile(Element root, String folderPath, File file) {
        try {
            WriteUtil.writeXml(root, folderPath + FilenameUtils.getBaseName(file.getName()) + ".xml");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void addSecAttr() {
        for (Element s :
                sentences) {
            s.setAttribute("sec", s.getParentElement().getParentElement().getAttributeValue("id"));
        }
    }

    /**
     *@auther gcr19
     *@desc 仅为含有引文标记的sentence添加属性c_type="r"
     *@return
     **/
    private void addCTypeAttr() {
        for (Element s :
                sentences) {
            if (s.getChildren("xref") != null)
                s.setAttribute("c_type","r");
        }
    }
}
