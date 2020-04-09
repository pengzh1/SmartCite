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

    //句子
    private Sentence sentence;
    //引文标记
    private RefTag refTag;
    //是否为引文上下文
    private boolean context;

    public Result(Sentence sentence, RefTag refTag) {
        this.sentence = sentence;
        this.refTag = refTag;
    }
}
