package cn.edu.whu.irlab.smart_cite.vo;

import java.util.List;

/**
 * @author gcr19
 * @date 2019-08-18 20:39
 * @desc 待分析的元数据
 **/
public class ExtractSourceVo {

    private List<String> sentence;

    private List<String> referenceTitle;

    public List<String> getSentence() {
        return sentence;
    }

    public void setSentence(List<String> sentence) {
        this.sentence = sentence;
    }

    public List<String> getReferenceTitle() {
        return referenceTitle;
    }

    public void setReferenceTitle(List<String> referenceTitle) {
        this.referenceTitle = referenceTitle;
    }

    @Override
    public String toString() {
        return "{" +
                "\"sentence\":\"" + sentence +
                "\", \"referenceTitle\":\"" + referenceTitle +
                "\"}";
    }
}
