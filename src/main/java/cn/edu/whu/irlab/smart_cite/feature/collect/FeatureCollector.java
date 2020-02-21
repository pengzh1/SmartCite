package cn.edu.whu.irlab.smart_cite.feature.collect;

import cn.edu.whu.irlab.smart_cite.feature.analyze.FeatureAnalyzer;

/**
 * 特征序列搜集器
 * Created by lsw on 2015/4/26.
 */
public interface FeatureCollector {
    /**
     * 得到分割器
     *
     * @return
     */
    public String splitter();

    /**
     * 得到特征值解析器
     *
     * @return
     */
    public FeatureAnalyzer getAnalyzer();
}
