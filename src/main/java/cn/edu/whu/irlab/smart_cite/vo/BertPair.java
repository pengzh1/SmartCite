package cn.edu.whu.irlab.smart_cite.vo;

import lombok.Data;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/11/25 22:17
 * @desc 用于bert预测、训练、颜值的数据
 **/
@Data
public class BertPair {

    private String aroundSentence;
    private String refInformation;
    private boolean isContextPair;

    public BertPair(String aroundSentence, String refInformation, boolean isContextPair) {
        this.aroundSentence = aroundSentence;
        this.refInformation = refInformation;
        this.isContextPair = isContextPair;
    }
}
