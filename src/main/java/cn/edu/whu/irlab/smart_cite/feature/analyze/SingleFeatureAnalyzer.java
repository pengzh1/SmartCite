package cn.edu.whu.irlab.smart_cite.feature.analyze;

import cn.edu.whu.irlab.smart_cite.feature.printer.FeaturePrinter;

/**
 * 最简单的特征输出方法
 * Created by lsw on 2015/4/26.
 */
public class SingleFeatureAnalyzer implements FeatureAnalyzer<Object> {

    @Override
    public FeaturePrinter printer() {
        return null;
    }

    @Override
    public String print(Object o) {
        return o == null ? "" : o.toString();
    }
}
