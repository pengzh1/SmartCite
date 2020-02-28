package cn.edu.whu.irlab.smart_cite.util;

import cn.edu.whu.irlab.smart_cite.vo.RecordVo;
import com.csvreader.CsvWriter;
import org.apache.commons.io.FileUtils;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

/**
 * @author gcr19
 * @date 2019-10-12 15:19
 * @desc
 **/
public class WriteUtil {

    private final static Logger logger = LoggerFactory.getLogger(WriteUtil.class);

    public static void writeXml(Element element,String outputPath) throws IOException {
        XMLOutputter outputter=new XMLOutputter();
        outputter.setFormat(outputter.getFormat().setEncoding("UTF-8"));
        outputter.output(element,new FileOutputStream(outputPath));
    }

    public static void writeStr(String docPath, String content) {
        File file = new File(docPath);
        try {
            FileUtils.writeStringToFile(file, content, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeList(String docPath,List list){
        File file = new File(docPath);
        try {
            FileUtils.writeLines(file,list,"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeRecord2csv(String path, List<RecordVo> recordVos) {
        CsvWriter csvWriter = new CsvWriter(path, ',', Charset.forName("UTF-8"));
        String[] header = {"article_id", "ref_rid", "ref_title", "sentence", "position", "is_similar"};

        try {
            csvWriter.writeRecord(header, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            for (RecordVo r :
                    recordVos) {
                String[] record = new String[6];
                record[0] = r.getArticle_id();
                record[1] = r.getRef_rid().toString();
                record[2] = r.getRef_title().toString();
                record[3] = r.getSentence();
                record[4] = String.valueOf(r.getPosition());
                csvWriter.writeRecord(record, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        csvWriter.close();
    }


}
