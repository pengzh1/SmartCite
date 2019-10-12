package cn.edu.whu.irlab.smart_cite.vo;

import java.util.List;

/**
 * @author gcr19
 * @date 2019-09-29 22:35
 * @desc 待分析的一条数据
 **/
public class RecordVo {

    private int no;
    private String doc_id;
    private List<String> ref_rid;
    private List<String> ref_title;
    private String sentence;
    private int position;
    private double similarity;

    public RecordVo(List<String> ref_rid, String sentence, int position) {
        this.ref_rid = ref_rid;
        this.sentence = sentence;
        this.position = position;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public List<String> getRef_rid() {
        return ref_rid;
    }

    public void setRef_rid(List<String> ref_rid) {
        this.ref_rid = ref_rid;
    }

    public List<String> getRef_title() {
        return ref_title;
    }

    public void setRef_title(List<String> ref_title) {
        this.ref_title = ref_title;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    @Override
    public String toString() {
        return "RecordVo{" +
                "no=" + no +
                ", doc_id='" + doc_id + '\'' +
                ", ref_rid=" + ref_rid +
                ", ref_title=" + ref_title +
                ", sentence='" + sentence + '\'' +
                ", position=" + position +
                ", similarity=" + similarity +
                '}';
    }
}
