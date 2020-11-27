package cn.edu.whu.irlab.smart_cite.service.paperXmlReader;

import cn.edu.whu.irlab.smart_cite.util.ElementUtil;
import cn.edu.whu.irlab.smart_cite.util.WordItemReplace;
import cn.edu.whu.irlab.smart_cite.util.WordSegment;
import cn.edu.whu.irlab.smart_cite.util.WordTokenizer;
import cn.edu.whu.irlab.smart_cite.vo.*;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.io.File;
import java.util.*;

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

    //    private Article article;
    ThreadLocal<Article> article = new ThreadLocal<>();

    /**
     * @param file 文件
     * @param root 根节点
     * @return Article对象
     * @auther gcr19
     * @desc 将预处理后的xml文档转换为Article对象(处理标记文件)
     **/
    public Article processLabeledFile(File file, Element root) {
        Article article = processFile(file, root);

        TreeMap<Integer, Sentence> sentenceTreeMap = article.getSentenceTreeMap();
        for (Map.Entry<Integer, Sentence> sentenceEntry :
                sentenceTreeMap.entrySet()) {
            Sentence sentence = sentenceEntry.getValue();
            List<RefTag> refTags = sentence.getRefList();
            if (!refTags.isEmpty()) {
                for (RefTag refTag :
                        refTags) {
                    if (!refTag.getContexts().equals("")) {
                        String[] contexts = refTag.getContexts().split(",");
                        for (String s :
                                contexts) {
                            refTag.addContext(sentenceTreeMap.get(Integer.parseInt(s)));
                        }
                    }
                }
            }
        }
        return article;
    }

    /**
     * @param file 文件
     * @param root 根节点
     * @return Article对象
     * @auther gcr19
     * @desc 将预处理后的xml文档转换为Article对象
     **/
    public Article processFile(File file, Element root) {
        Element header = root.getChild("header");

        Article article = new Article(file.getName());
        this.article.set(article);

        //初始化article 设置摘要
        article.setAbsText(header.getChild("abstract").getValue());//todo plos数据中有的摘要有多个段落

        //设置作者列表
        List<Author> authors = new ArrayList<>();
        for (Element e :
                header.getChild("author-group").getChildren()) {
            authors.add(new Author(e.getChildText("surname"), e.getChildText("given-names")));
        }
        article.setAuthors(authors);
        authors = null;//释放内存

        //设置标题
        article.setTitle(new Title(header.getChild("title-group").getChildText("article-title"), header.
                getChild("title-group").getChildText("alt-title")));

        //references
        Element ref_list = root.getChild("back").getChild("ref-list");
        if (ref_list != null) {
            List<Element> refs = ref_list.getChildren("ref");
            for (Element ref : refs) {
                article.putRef(ref.getAttributeValue("id"), parseReference(ref));// lei保存的是string
            }
        }

        //设置并解析句子
        List<Element> sentenceElements = ElementUtil.extractElements(root, "s");
        Sentence sentence;
        for (Element e :
                sentenceElements) {
            sentence = new Sentence(Integer.parseInt(e.getAttributeValue("id").split(",")[0]),
                    e.getValue().replaceAll("\\s", " ").replaceAll(" {2,}", " "), article);//todo 1.lei的数据个别句子存在一个句子含2各以上id的情况,按照lei的做法只取了第一个.

            logger.info("analyze [article] " + sentence.getArticle().getName() + " [sentence] " + sentence.getId());
            setSecInfo(e, sentence);
            article.append(sentence);   //append中设置一些索引位置信息

            sentence = null;//释放内存
//            logs.info(s.toText());
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
                    reference.addAuthor(new Author(name.getChildText("surname"), name.getChildText("given-names")));
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

    /**
     * @param e        句子节点
     * @param sentence 句子对象
     * @return
     * @auther gcr19
     * @desc 设置句子相关信息
     **/
    private void setSecInfo(Element e, Sentence sentence) {

        //设置cType
        sentence.setCType(e.getAttributeValue("c_type"));

        //设置pNum
        sentence.setPNum(Integer.parseInt(e.getAttributeValue("p")));

        //设置level
        int level = Integer.parseInt(e.getAttributeValue("level"));
        sentence.setLevel(level);

        sentence.setSect(e.getAttributeValue("sec"));
        wordItem(e, sentence);
    }

    /**
     * @param e        句子节点
     * @param sentence 句子对象
     * @return
     * @auther gcr19
     * @desc 设置word相关信息
     **/
    private void wordItem(Element e, Sentence sentence) {
        List<WordItem> wordItemList = new ArrayList<>();
        for (Content content :
                e.getContent()) {
            addWordItem(sentence, wordItemList, content);
        }

//        Iterator iterator = e.getDescendants();
//        while (iterator.hasNext()) {
//            Content next = (Content) iterator.next();
//            addWordItem(sentence, wordItemList, next);
//        } 重复遍历。。。

        //括号替换
        wordItemList = WordItemReplace.replaceBracket(wordItemList);
        //引文分组替换 todo 可能不适用于我的模板
        WordItemReplace.replaceGroup(wordItemList);
        //非句法成分移除
        WordItemReplace.replaceNonSyntactic(wordItemList);
        //分词
        sentence.setWordList(wordItemList);
        WordSegment.wordSegment(sentence);
        wordItemList.forEach(wordItem -> {
            if (wordItem.getType() != WordItem.WordType.Word) {
                wordItem.getRef().setWordItem(wordItem);
            }
        });
        logger.debug(toStr(sentence.getWordList(), " "));
        wordItemList = null;//释放内存
    }

    private void addWordItem(Sentence sentence, List<WordItem> wordList, Content content) {
        if (content.getCType().equals(Content.CType.Text)) {
            String text = content.getValue();
            String[] arr = WordTokenizer.split(text);
            wordList.addAll(WordItem.words(arr));
        } else if (content.getCType().equals(Content.CType.Element)) {
            Attribute c_type = content.getParentElement().getAttribute("c_type");
            if (c_type != null && !c_type.getValue().equals("r")) {
                return;//todo 除了Lei的数据，预处理中没有生成c_type属性 为什么要检查c_type(gcr)?
            }
            Element element = (Element) content;
            if (element.getName().equals("xref")) {   //应该都是xref

//                try {
//                    Integer.parseInt((element.getAttributeValue("id")));
//                } catch (Exception e) {
//                    logger.error(e.getMessage(), e);
//                    System.out.println(element.getAttributeValue("id"));
//                    System.out.println();
//                }//未发现作用，注释

                RefTag xref = new RefTag(sentence, element.getText().trim().replaceAll("\\s", " ").replaceAll(" {2,}", " "),
                        Integer.parseInt((element.getAttributeValue("id"))));
                String contexts = element.getAttributeValue("context");
                xref.setContexts(contexts != null ? contexts : "");//todo 预处理无法生成这个属性
                String refNum = element.getAttributeValue("rid");
                if (refNum != null && !refNum.trim().equals("")) {  //指向的参考文献
                    if (refNum.trim().substring(0, 1).equals("#")) {
                        refNum = refNum.trim().substring(1);
                    }
                    xref.setRid(refNum.trim());
                    xref.setReference(article.get().getReferences().get(xref.getRid()));
                }
                sentence.addRef(xref);  //给句子加引文引用
                //引文替换工作
                wordList.add(WordItem.ref(WordItem.REF, xref));
                xref = null;//释放内存
            }
        }
    }
}
