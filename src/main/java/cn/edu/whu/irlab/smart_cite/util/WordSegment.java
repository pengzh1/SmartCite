package cn.edu.whu.irlab.smart_cite.util;

import cn.edu.whu.irlab.smart_cite.vo.Sentence;
import cn.edu.whu.irlab.smart_cite.vo.WordItem;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.RuntimeInterruptedException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 英文分词和词性标注
 * Created by Lei Shengwei (Leo) on 2015/3/30.
 */
@Slf4j
public class WordSegment {
    public static LexicalizedParser lexicalizedParser;

    static {
        lexicalizedParser = LexicalizedParser
                .loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
//        tagger = new MaxentTagger("lib/stanford-postagger/modelsj-0-18-bidirectional-distsim.tagger".replace("/", File.separator));

    }

    public static void wordSegment(Sentence sentence) {
        Tree tree = getTree(sentence);
        getWordItems(sentence, tree);
    }

//    public static List<WordItem> wordSegment(Sentence sentence) {
//        Tree tree = getTree(sentence);
//        return getWordItems(sentence, tree);
//    }

    /**
     * 分词
     *
     * @param wordItems
     */
    public static void wordSegment(List<WordItem> wordItems) {
        Tree tree = getTree(toWordArr(wordItems));
        List<TaggedWord> list = getTaggedWords(tree);
        for (int i = 0; i < wordItems.size(); i++) {
            wordItems.get(i).setTag(list.get(i).tag());
        }
    }

    private static String[] toWordArr(List<WordItem> wordItems) {
        return wordItems.stream().map((v) -> v.getWord()).collect(Collectors.toList()).toArray(new String[]{});
    }


    private static List<WordItem> getWordItems(Sentence sentence, Tree tree) {
        List<TaggedWord> list = getTaggedWords(tree);
        for (int i = 0; i < sentence.wordSize(); i++) {
            sentence.word(i).setTag(list.get(i).tag());
        }
        return sentence.getWordList();
    }

    /**
     * 得到句子对应的Tree
     *
     * @param sentence
     * @return
     */
    private static Tree getTree(Sentence sentence) {
        String[] words = sentence.toTextArr();
        try {
            return getTree(words);
        } catch (RuntimeInterruptedException e) {
            log.error("error in sentence [" + sentence.getId() + "] at article [" + sentence.getArticle().getName(), e);
            throw e;
        }
    }

    private static Tree getTree(String[] words) {
        List<HasWord> rawWords = SentenceUtils.toWordList(words);
        Tree tree = lexicalizedParser.apply(rawWords);//todo 速度慢
        rawWords = null;//释放内存
        return tree;
    }

    /**
     * 得到依存句法分析树
     *
     * @param parse
     * @return
     */
    private static List<TypedDependency> getTypedDependencies(Tree parse) {
        //生成依存树
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
        return gs.typedDependenciesCCprocessed();
    }

    /**
     * 得到分词结果
     *
     * @param parse
     * @return
     */
    private static ArrayList<TaggedWord> getTaggedWords(Tree parse) {
        //在执行句法分析之后重新生成postag并覆盖原有的（因为进行过一次citation标记替换，所以句子结构有变）
        //parse = lexicalizedParser.parse(dp.getSentence());
        return parse.taggedYield();
    }

}
