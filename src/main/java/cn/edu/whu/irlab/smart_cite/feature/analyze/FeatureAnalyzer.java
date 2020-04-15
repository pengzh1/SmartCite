package cn.edu.whu.irlab.smart_cite.feature.analyze;

import cn.edu.whu.irlab.smart_cite.feature.printer.FeaturePrinter;

/**
 * 打印特征值
 * Created by lsw on 2015/4/26.
 */
public interface FeatureAnalyzer<T> {
    /**
     * 得到一个打印器
     *
     * @return
     */
    public FeaturePrinter printer();

    /**
     * 通过打印器得到打印的结果
     *
     * @param t
     * @return
     */
    public String print(T t);
}
