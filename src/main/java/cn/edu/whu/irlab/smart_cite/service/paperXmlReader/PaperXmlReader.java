package cn.edu.whu.irlab.smart_cite.service.paperXmlReader;

import cn.edu.whu.irlab.smart_cite.util.ElementUtil;
import cn.edu.whu.irlab.smart_cite.util.WordItemReplace;
import cn.edu.whu.irlab.smart_cite.util.WordSegment;
import cn.edu.whu.irlab.smart_cite.util.WordTokenizer;
import cn.edu.whu.irlab.smart_cite.vo.*;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.Content;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.leishengwei.jutils.Collections.toStr;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/13 16:15
 * @desc 读取Xml文件 转换为art文件（仿lei的代码）
 **/
@Service
public class PaperXmlReader {
    private static final Logger logger = LoggerFactory.getLogger(PaperXmlReader.class);



    public Article processFile(File file, Element root) {
        Element header = root.getChild("header");

        Article article = new Article(FilenameUtils.getBaseName(file.getName()));

        //初始化article
        article.setAbsText(header.getChild("abstract").getValue());//todo plos数据中有的摘要有多个段落
        List<Author> authors = new ArrayList<>();
        for (Element e :
                header.getChild("author-group").getChildren()) {
            authors.add(new Author(e.getChildText("surname"), e.getChildText("given-names")));
        }
        article.setAuthors(authors);
        article.setTitle(new Title(header.getChild("title-group").getChildText("article-title"), header.
                getChild("title-group").getChildText("alt-title")));

        //解析所有句子
        List<Element> sentenceElements = ElementUtil.extractElements(root, "s");
        Sentence sentence;
        for (Element e :
                sentenceElements) {
            sentence = new Sentence(Integer.parseInt(e.getAttributeValue("id")), e.getText(), article);
            logger.info("sentence:an%s:sn%s", sentence.getArticle().getName(), sentence.getId());
//            s.setcType(cType(e)); lei备注没多大用

            setSecInfo(e, sentence);
            sentence.setPIndex(Integer.parseInt(e.getAttributeValue("p")));
            wordItem(e, sentence);
            article.append(sentence);   //append中设置一些索引位置信息
//            logs.info(s.toText());
        }
        //references
        Element ref_list = root.getChild("back").getChild("ref-list");
        if (ref_list != null) {
            List<Element> refs = ref_list.getChildren("ref");
            for (Element ref : refs) {
                article.putRef(Integer.parseInt(ref.getAttributeValue("id")), parseReference(ref));// lei保存的是string
            }
        }

        return article;
    }

    private Reference parseReference(Element ref) {
        Reference reference = new Reference();
        reference.setId(ref.getAttributeValue("id"));
        reference.setLabel(ref.getAttributeValue("label"));
        Element element_citation = ref.getChild("element-citation");
        if (element_citation != null) {
            Element person_group = element_citation.getChild("person-group");
            if (person_group != null && person_group.getAttributeValue("person-group-type").equals("author")) {
                for (Element name :
                        person_group.getChildren("name")) {
                    reference.addAuthor(new Author(name.getChildText("surname"), name.getChildText("given-name")));
                }
            }
            reference.setYear(element_citation.getChildText("year"));
            reference.setArticle_title(element_citation.getChildText("article-title"));
            reference.setSource(element_citation.getChildText("source"));
            reference.setVolume(element_citation.getChildText("volume"));
            reference.setFpage(element_citation.getChildText("fpage"));
            reference.setLpage(element_citation.getChildText("lpage"));
            reference.setIssue(element_citation.getChildText("issue"));
        }
        return reference;
    }

    private void setSecInfo(Element e, Sentence sentence) {
        int level = Integer.parseInt(e.getAttributeValue("level"));
        sentence.setLevel(level);
        String sec = e.getAttributeValue("sec");
//        sentence.setSect(e.getAttributeValue("sec")); todo gcr的想法
        switch (level) {
            case 1:
                sentence.setSect(level + ":" + sec);
                break;
            case 2:
                sentence.setSect(level + ":" + sec.substring(0, 1) + ":" + sec);
                break;
            case 3:
                sentence.setSect(level + ":" + sec.substring(0, 1) + ":" + sec.substring(0, 3) + ":" + sec);
                break;
        }
    }

    private void wordItem(Element e, Sentence sentence) {
        Iterator iterator = e.getDescendants();
        List<WordItem> wordItemList = new ArrayList<>();
        while (iterator.hasNext()) {
            Content next = (Content) iterator.next();
            addWordItem(sentence, wordItemList, next);
        }
        //括号替换
        wordItemList = WordItemReplace.replaceBracket(wordItemList);
        //引文分组替换 todo 可能不适用于我的模板
        WordItemReplace.replaceGroup(wordItemList);
        //非句法成分移除
        WordItemReplace.replaceNonSyntactic(wordItemList);
        //分词
        sentence.setWordList(wordItemList);
        WordSegment.wordSegment(sentence);
        logger.debug(toStr(sentence.getWordList(), " "));
    }

    private static void addWordItem(Sentence sentence, List<WordItem> wordList, Content content) {
        if (content.getCType().equals(Content.CType.Text)) {
            String text = content.getValue();
            String[] arr = WordTokenizer.split(text);
            wordList.addAll(WordItem.words(arr));
        } else if (content.getCType().equals(Content.CType.Element)) {
            if (!content.getParentElement().getAttributeValue("c_type").equals("r")) {
                return;//todo 预处理中没有生成c_type属性
            }
            Element element = (Element) content;
            if (element.getName().equals("xref")) {   //应该都是ref
                RefTag xref = new RefTag(sentence, element.getText().trim(), Integer.parseInt((element.getAttributeValue("id"))));
                xref.setContexts(element.getAttributeValue("context"));//todo 预处理无法生成这个属性
                String refNum = element.getAttributeValue("rid");
                if (refNum != null && !refNum.trim().equals("")) {  //指向的参考文献
                    xref.setRefNum(Integer.parseInt(refNum.trim()));
                }
                sentence.addRef(xref);  //给句子加引文引用
                //引文替换工作
                wordList.add(WordItem.ref(WordItem.REF, xref));
            }
        }
    }

    private void writeFile(Article ar, File out) {

        FileWriter fw = null;
        try {
            fw = new FileWriter(out);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append("i\t");
            bw.append(ar.getTitle() + "\t"  + ar.getAuthors() + "\n"); // 这里没有pubInfo。lei有
            bw.append("i\t");
            bw.append(ar.getAbsText().replaceAll("\\s+", " ")).append("\n");
            ar.getSentenceTreeMap().forEach((k, v) -> {
                try {
                    bw.append(v.toText() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            ar.getReferences().forEach((k, v) -> {
                try {
                    bw.append("r\t" + k + "\t" + v + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
