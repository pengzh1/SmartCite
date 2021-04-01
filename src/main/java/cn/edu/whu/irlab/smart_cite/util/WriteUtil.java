package cn.edu.whu.irlab.smart_cite.util;

import cn.edu.whu.irlab.smart_cite.vo.*;
import com.csvreader.CsvWriter;
import org.apache.commons.io.FileUtils;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gcr19
 * @date 2019-10-12 15:19
 * @desc
 **/
public class WriteUtil {

    private final static Logger logger = LoggerFactory.getLogger(WriteUtil.class);

    public static void writeXml(Element element, String outputPath) throws IOException {
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(outputter.getFormat().setEncoding("UTF-8"));
        outputter.output(element, new FileOutputStream(outputPath));
    }

    public static void writeStr(String docPath, String content) {
        File file = new File(docPath);
        try {
            FileUtils.writeStringToFile(file, content, "UTF-8");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void writeList(String docPath, List list) {
        File file = new File(docPath);
        try {
            FileUtils.writeLines(file, list, "\n");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void writeList(String docPath, List list, boolean isAppend) {
        File file = new File(docPath);
        try {
            FileUtils.writeLines(file, list, "\n", isAppend);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    //检查数据用
    public static void writeResult1(List<Result> results) {
        CsvWriter csvWriter = new CsvWriter("test/jiancha.csv", ',', StandardCharsets.UTF_8);
        String[] header = {"aroundSentence", "refInformation", "isContextPair", "articleId", "sentId", "refSentId", "refSentPlain", "refProcessed"};

        try {
            csvWriter.writeRecord(header, false);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        try {
            for (Result result :
                    results) {
                String[] record = new String[8];
                record[0] = result.getSentence().getText();
                record[1] = result.getRefTag().getSentence().getText();
                record[2] = String.valueOf(result.isContext() ? 1 : 0);
                record[3] = result.getSentence().getArticle().getName();
                record[4] = String.valueOf(result.getSentence().getId());
                record[5] = String.valueOf(result.getRefTag().getSentence().getId());
                record[6] = result.getRefTag().getSentence().plain();
                record[7] =result.getRefTag().getSentence().standardText(1);
//                record[7] = result.getRefTag().getSentence().plain().replaceAll("-LRB- -RRB- ", "[#]").replaceAll("-LRB-", "(").replaceAll("-RRB-", ")");
                csvWriter.writeRecord(record, false);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        csvWriter.close();
    }

    public static void writeResult(String path, List<Result> results, int outputType, boolean isInOrder) {
        List<String> libsvm = new ArrayList<>();
        CsvWriter csvWriter = new CsvWriter(path + ".csv", ',', StandardCharsets.UTF_8);
        String[] header;
        if (isInOrder) {
            header = new String[]{"text_a", "text_b", "isContextPair"};
        } else {
            header = new String[]{"aroundSentence", "refInformation", "isContextPair"};
        }

        try {
            csvWriter.writeRecord(header, false);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        try {
            for (Result result :
                    results) {
                String[] record = new String[3];
                if (isInOrder) {
                    Sentence aroundSentence = result.getSentence();
                    Sentence refSentence = result.getRefTag().getSentence();
                    if (aroundSentence.getId() > refSentence.getId()) {
                        record[0] = refSentence.standardText(outputType);
                        record[1] = aroundSentence.standardText(outputType);
                    }else {
                        record[0] =aroundSentence.standardText(outputType);
                        record[1] =refSentence.standardText(outputType);
                    }
                } else {
                    record[0] =result.getSentence().standardText(outputType);
                    record[1] =result.getRefTag().getSentence().standardText(outputType);
                }
                record[2] = String.valueOf(result.isContext() ? 1 : 0);
                csvWriter.writeRecord(record, false);
                libsvm.add(result.getLibsvmFeature());
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        csvWriter.close();
        writeList(path + ".libsvm", libsvm);
    }


    public static void writeBertPair2csv(String path, List<BertPair> bertPairs) {
        CsvWriter csvWriter = new CsvWriter(path, ',', StandardCharsets.UTF_8);
        String[] header = {"aroundSentence", "refInformation", "isContextPair"};

        try {
            csvWriter.writeRecord(header, false);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        try {
            for (BertPair bertPair :
                    bertPairs) {
                String[] record = new String[3];
                record[0] = bertPair.getAroundSentence();
                record[1] = bertPair.getRefInformation();
                record[2] = String.valueOf(bertPair.isContextPair() ? 1 : 0);
                csvWriter.writeRecord(record, false);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        csvWriter.close();
    }

    public static void writeRecord2csv(String path, List<RecordVo> recordVos) {

        CsvWriter csvWriter = new CsvWriter(path, ',', StandardCharsets.UTF_8);
        String[] header = {"article_id", "ref_rid", "ref_title", "sentence", "position", "is_similar"};

        try {
            csvWriter.writeRecord(header, false);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
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
            logger.error(e.getMessage(), e);
        }

        csvWriter.close();
    }

    public static String plain(List<WordItem> wordItems) {
        return wordItems.stream().reduce("", (s, wordItem) -> s + " " + wordItem.getWord(), (r1, r2) -> r1);
    }

}
