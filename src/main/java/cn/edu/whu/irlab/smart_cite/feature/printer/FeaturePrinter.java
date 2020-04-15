package cn.edu.whu.irlab.smart_cite.feature.printer;

/**
 * 特征输出器
 * Created by lsw on 2015/4/26.
 */
public interface FeaturePrinter<T> {

    public String print(T t);
}
