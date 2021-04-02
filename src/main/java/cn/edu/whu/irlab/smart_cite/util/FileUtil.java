package cn.edu.whu.irlab.smart_cite.util;

import cn.edu.whu.irlab.smart_cite.vo.FileLocation;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class FileUtil {

    /**
     * @param file 上传的文件
     * @return 保存的文件
     * @auther gcr19
     * @desc 保存上传的文件
     **/
    public static File saveUploadedFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("没有获取到上传的文件");
        }
        File upload = new File(FileLocation.UPLOAD_FILE + File.separator + file.getOriginalFilename());
        file.transferTo(upload);
        return upload;
    }

    /**
     * 获取文件类型
     *
     * @param file 文件
     * @return mimeType
     */
    public static String identifyMimeType(File file) {
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


}
