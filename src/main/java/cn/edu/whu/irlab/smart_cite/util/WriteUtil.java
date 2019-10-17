package cn.edu.whu.irlab.smart_cite.util;

import cn.edu.whu.irlab.smart_cite.vo.RecordVo;
import com.csvreader.CsvWriter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;

/**
 * @author gcr19
 * @date 2019-10-12 15:19
 * @desc
 **/
public class WriteUtil {

    public static void writeStr(String docPath, String content){
        File file =new File(docPath);
        try {
            FileUtils.writeStringToFile(file,content,"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeRecord2csv(String path, Set<RecordVo> recordVos){
        CsvWriter csvWriter=new CsvWriter(path,',', Charset.forName("UTF-8"));
        String[] header={"article_id","ref_rid","ref_title","sentence","position","is_similar"};

        try {
            csvWriter.writeRecord(header,false);
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
                csvWriter.writeRecord(record,false);
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        csvWriter.close();
    }

}
