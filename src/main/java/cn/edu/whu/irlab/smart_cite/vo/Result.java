package cn.edu.whu.irlab.smart_cite.vo;

import lombok.Data;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/29 20:13
 * @desc 抽取结果实体
 **/
@Data
public class Result {

    //文档名
    private String articleName;
    //句子Id
    private int sentenceId;
    //引文标记Id
    private int refId;
    //引文标记所在句子Id
    private int refAtSentence;
    //参考文献Id
    private String rid;
    //是否为引文上下文
    private boolean context;

    public Result(String articleName, int sentenceId, int refId, int refAtSentence,String rid) {
        this.articleName = articleName;
        this.sentenceId = sentenceId;
        this.refId = refId;
        this.refAtSentence = refAtSentence;
        this.rid=rid;
    }
}
