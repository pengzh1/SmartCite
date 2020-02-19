package cn.edu.whu.irlab.smart_cite.service.artFileReader;

import cn.edu.whu.irlab.smart_cite.vo.Article;
import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import cn.edu.whu.irlab.smart_cite.vo.Sentence;
import cn.edu.whu.irlab.smart_cite.vo.WordItem;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.leishengwei.jutils.Strings.isEmpty;
import static com.leishengwei.jutils.Strings.toInt;

/**
 * 读取art文件 todo 测试不成功 是否有测试的意义？
 * Created by Lei Shengwei (Leo) on 2015/4/5.
 */
public class ArtFileReader {

    public static void main(String[] args) {
        ArtFileReader afr = new ArtFileReader();
        String fileName = "temp/art/31-P07-1001-paper.art";
        File file = new File(fileName);
        Article article = afr.load(file);
        System.out.println("");
    }

    public Article load(File file) {
        Article ar = article(file);
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.replace(":null:", "::").replace("-", "");
                if (line.startsWith("s")) {
                    ar.put(sentence(line, ar));
                } else if (line.startsWith("i")) {

                } else if (line.startsWith("r")) {

                }
            }
            ar.loadSects(); //加载所有section名称到列表中
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ar;
    }

    /**
     * 100-P08-1020-paper.xml.art
     *
     * @param file
     * @return
     */
    public Article article(File file) {
        return new Article(FilenameUtils.getBaseName(file.getName()));
    }

    /**
     * 解析一行数据得到Sentence对象
     *
     * @param line
     * @return
     */
    public Sentence sentence(String line, Article ar) {
        String[] infos = line.split("\t", 10);
        if (!infos[0].trim().equals("s")) {
            return null;
        }
        Sentence s = new Sentence(toInt(infos[1]), "", ar);
        s.setCType(infos[2]);
        s.setPNum(toInt(infos[3]));
        s.setLevel(toInt(infos[4]));
        sect(s, infos[5]);
        s.setIndex(toInt(infos[6]));
        s.setPIndex(toInt(infos[7]));
        s.setSectionIndex(toInt(infos[8]));
        wordList(s, infos[9]);
//        System.out.println(s.toText());
        return s;
    }

    /**
     * 设置单词信息
     *
     * @param s
     * @param words
     */
    private void wordList(Sentence s, String words) {
        if (isEmpty(words)) { //空句子
            return;
        }
        String[] wordArr = words.split("\t");
        for (String word : wordArr) {
            s.addWord(wordItem(s, word));
        }
        s.setText(s.plain());
    }

    private String text(List<WordItem> wordList) {
        return wordList.stream().reduce("", (r, w) -> r + " " + w.getWord(), (r1, r2) -> r2).trim();
    }

    /**
     * 设置单词信息
     *
     * @param s
     * @param word
     * @return
     */
    private WordItem wordItem(Sentence s, String word) {
        WordItem item = null;
        String wordInfo[];
        String w;   //单词
        switch (word.charAt(0)) {
            case 'W':
                wordInfo = word.split(":", 3);
                item = new WordItem(WordItem.WordType.Word, wordInfo[1].replace("$", ":"));
                item.setTag(wordInfo[2]);
                break;
            case 'R':
                //[RW|R]:文本内容:引文标记内容:词性标记:引文编号:上下文:对应参考文献编号:左侧:右侧
                wordInfo = word.split(":", 9);
                w = wordInfo[1];
                item = new WordItem(wordInfo[0].trim().equals("R") ? WordItem.WordType.Ref : WordItem.WordType.WordRef, w.replace("$", ":"), reference(s, wordInfo));
                item.getRef().setWordItem(item);
                item.setTag(wordInfo[3]);
                break;
            case 'G':
                //[GW|G]:文本内容:词性标记[|引文标记内容:编号:上下文:对应参考文献编号:左侧:右侧]
                wordInfo = word.split("\\|", 100);  //尽量多分
                String[] split = wordInfo[0].split(":");
                w = split[1];
                String tag = split[2];
                item = new WordItem(split[0].trim().equals("G") ? WordItem.WordType.G_REF : WordItem.WordType.Word_G_Ref, w.trim());
                item.setTag(tag.trim());
                List<String> refList = Arrays.asList(wordInfo);
                List<RefTag> refs = refList.subList(1, refList.size()).stream().map((v) -> {
                    String infos[] = v.split(":", 6);
                    RefTag ref = new RefTag(s, infos[0].trim().replaceAll("$", ":"), Integer.parseInt(infos[1]));
                    ref.setContexts(infos[2].trim());
                    if (infos[3] != null && !infos[3].trim().equals("")) {
                        ref.setRefNum(Integer.parseInt(infos[3]));
                    }
                    ref.setLeft(infos[4]);
                    ref.setRight(infos[5]);
                    s.addRef(ref);
                    return ref;
                }).collect(Collectors.toList());
                //设置所在WordItem
                for (int i = 0; i < refs.size(); i++) {
                    refs.get(i).setWordItem(item);
                }
                item.setRefs(refs);
        }
        return item;
    }

    /**
     * 创建引文对象
     *
     * @param s
     * @param wordInfo
     */
    private RefTag reference(Sentence s, String[] wordInfo) {
        //[RW|R]:文本内容:引文标记内容:词性标记:引文编号:上下文:对应参考文献编号:左侧:右侧
        RefTag r = new RefTag(s, wordInfo[2].replace("$", ":"), toInt(wordInfo[4]));
        if (!wordInfo[6].trim().equals("")) {
            r.setRefNum(toInt(wordInfo[6]));
        }
        r.setLeft(wordInfo[7]);
        r.setRight(wordInfo[8]);
        r.setContexts(wordInfo[5]);
        s.addRef(r);
        return r;
    }

    /**
     * 设置章节信息
     *
     * @param s
     * @param sect
     */
    private void sect(Sentence s, String sect) {
        String[] sects = sect.split(":", 4);
        s.setSect(sect);
        int level = toInt(sects[0]);
        s.setLevel(level);
        s.setSection(sects[1]);
        switch (level) {
            case 2:
                s.setSubSection(sects[2]);
                break;
            case 3:
                s.setSubSection(sects[2]);
                s.setSubSubSection(sects[3]);
                break;
        }
    }

}
