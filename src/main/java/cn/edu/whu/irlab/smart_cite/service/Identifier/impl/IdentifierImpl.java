package cn.edu.whu.irlab.smart_cite.service.Identifier.impl;

import cn.edu.whu.irlab.smart_cite.enums.CiteMarkEnum;
import cn.edu.whu.irlab.smart_cite.enums.ExceptionEnum;
import cn.edu.whu.irlab.smart_cite.enums.XMLTypeEnum;
import cn.edu.whu.irlab.smart_cite.exception.FileTypeException;
import cn.edu.whu.irlab.smart_cite.service.Identifier.Identifier;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.jdom2.Element;
import org.springframework.stereotype.Service;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * @author gcr19
 * @date 2019-10-14 11:38
 * @desc 文件类型识别类
 **/
@Service("identifier")
public class IdentifierImpl implements Identifier {

    /**
     * 获取类型
     *
     * @param file 文件
     * @return mimeType
     */
    @Override
    public String identifyMimeType(File file) {
        if (file.isDirectory()) {
            throw new IllegalArgumentException("该路径为文件夹，不是文件。路径：" + file.getPath());
        }
        AutoDetectParser parser = new AutoDetectParser();
        parser.setParsers(new HashMap<MediaType, Parser>());
        Metadata metadata = new Metadata();
        metadata.add(TikaMetadataKeys.RESOURCE_NAME_KEY, file.getName());
        try (InputStream stream = new FileInputStream(file)) {
            parser.parse(stream, new DefaultHandler(), metadata, new ParseContext());
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return metadata.get(HttpHeaders.CONTENT_TYPE);
    }

    @Override
    public XMLTypeEnum identifyXMLType(Element article, File file) {
        String nameOfFirstNode = article.getName();
        if (nameOfFirstNode.equals("article")) {
            return XMLTypeEnum.Plos;
        } else if (nameOfFirstNode.equals("TEI")) {
            return XMLTypeEnum.Grobid;
        }
        throw new IllegalArgumentException("非法的XML类型，文件名："+file.getName());
    }
}
