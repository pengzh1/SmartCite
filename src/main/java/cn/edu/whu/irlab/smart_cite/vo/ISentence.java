package cn.edu.whu.irlab.smart_cite.vo;

import java.util.Optional;

/**
 * 句子的接口（TODO 貌似没多大用啊）
 * Created by Lei Shengwei (Leo) on 2015/3/29.
 */
public interface ISentence {
    /**
     * 得到两个句子之间的距离
     *
     * @param other
     * @return <br>
     * >0则other在当前句的后面，
     * <0表示前面，
     * =0表示同一个句子，
     */
    public int distance(Sentence other);

    /**
     * 得到当前句的第后N句
     *
     * @param distance >=1
     * @return
     */
    public Optional<Sentence> next(int distance);

    public Optional<Sentence> next();

    /**
     * 得到当前句的第前N句
     *
     * @param distance
     * @return
     */
    public Optional<Sentence> previous(int distance);

    public Optional<Sentence> previous();
}
