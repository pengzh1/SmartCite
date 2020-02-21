package cn.edu.whu.irlab.smart_cite.feature.analyze;

import cn.edu.whu.irlab.smart_cite.feature.printer.FeaturePrinter;

/**
 * 整数打印器
 * Created by lsw on 2015/4/26.
 */
public class LongAnalyzer implements FeatureAnalyzer<Long> {
    @Override
    public FeaturePrinter printer() {
        return null;
    }

    @Override
    public String print(Long aLong) {
        return aLong == null ? "0" : aLong.toString();
    }
}
