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


/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/16 10:13
 * @desc 属性生成器
 **/
@Service
public class AttrGenerator {

    private static final Logger logger = LoggerFactory.getLogger(AttrGenerator.class);

    //存放完成编号的XML文档
    private final static String ADDED = "temp/addedAttr/";


    private List<Element> sentences = new ArrayList<>();

    public void generateAttr(Element root, File file) {
        Element body = root.getChild("body");
        ElementUtil.extractElements(body, "s", sentences);
        reNumberSec(body);
        addSecAttr();
        addLevelAndPAttr();
        writeFile(root, ADDED, file);
    }

    /**
     * @param body body节点
     * @return
     * @auther gcr19
     * @desc 为sec节点重新排序
     **/
    private void reNumberSec(Element body) {
        List<Element> children = body.getChildren("sec");
        if (children != null) {
            int i = 1;
            for (Element sec :
                    children) {
                String parentId = sec.getParentElement().getAttributeValue("id");
                if (parentId != null) {
                    sec.setAttribute("id", parentId + "." + i++);
                } else {
                    sec.setAttribute("id", String.valueOf(i++));
                }
                reNumberSec(sec);
            }
        }
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

    private void addSecAttr(){
        for (Element s :
                sentences) {
            s.setAttribute("sec", s.getParentElement().getParentElement().getAttributeValue("id"));
        }
    }

}
