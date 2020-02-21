package cn.edu.whu.irlab.smart_cite.service.artFileSrcReader;

import cn.edu.whu.irlab.smart_cite.service.artFileReader.ArtFileReader;
import cn.edu.whu.irlab.smart_cite.vo.Article;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 还原art文件的内容 还原成句子 todo 未测试 是否有测试的意义？
 * Created by leoray on 16/8/13.
 */
public class ArtFileSrcReader {
    public static void main(String[] args) throws IOException {
        String dir = "D:/data/xml1";
        String out = "D:/data/xml1/";
        File outDir = new File(out);
        if (!outDir.exists()) {
            outDir.mkdir();
        }
        File[] files = new File(dir).listFiles((dir1, name) -> {
            return name.endsWith(".art");
        });
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            Article r = new ArtFileReader().load(f);
            FileWriter fileWriter = new FileWriter(new File(out + f.getName().replace(".xml.art", ".txt")));
            fileWriter.write("#name#" + r.getName() + "\n");
            fileWriter.write("#title#" + r.getTitle() + "\n");
            fileWriter.write("#author#" + r.getAuthors() + "\n");
            r.getSentenceTreeMap().forEach((k, v) -> {
                try {
                    fileWriter.write("s\t");
                    fileWriter.write(v.getIndex() + "\t");
                    fileWriter.write(v.plain());
                    fileWriter.write("\n");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            r.getReferences().forEach((k, v) -> {
                try {
                    fileWriter.write("r\t");
                    fileWriter.write(v + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            fileWriter.close();
        }

    }
}
