package cn.edu.whu.irlab.smart_cite.service.splitter;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.sentences.MedlineSentenceModel;
import com.aliasi.sentences.SentenceChunker;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author gcr19
 * @version 1.0
 * @date 2019/12/19 15:21
 * @desc LingPipe 分割句子
 **/
@Service("lingPipeSplitter")
public class LingPipeSplitterImpl extends SplitterImpl {

    static final TokenizerFactory TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
    static final SentenceModel SENTENCE_MODEL = new MedlineSentenceModel();
    static final SentenceChunker SENTENCE_CHUNKER = new SentenceChunker(TOKENIZER_FACTORY, SENTENCE_MODEL);


    @Override
    public List<String> splitSentences(String text){

        List<String> sentenceList = new ArrayList<>();

        Chunking chunking
                = SENTENCE_CHUNKER.chunk(text.toCharArray(), 0, text.length());
        Set<Chunk> sentences = chunking.chunkSet();
        if (sentences.size() < 1) {
            throw new IllegalArgumentException("未在文本[" + text + "]中发现句子边界");
        }
        String slice = chunking.charSequence().toString();

        for (Chunk sentence : sentences) {
            int start = sentence.start();
            int end = sentence.end();
            sentenceList.add(slice.substring(start, end));
        }

        return sentenceList;
    }


}
