package cn.edu.whu.irlab.smart_cite.feature.analyze;

import cn.edu.whu.irlab.smart_cite.feature.printer.FeaturePrinter;

import java.util.Collection;

/**
 * 列表输出器
 * Created by lsw on 2015/4/26.
 */
public class ListAnalyzer implements FeatureAnalyzer<Collection> {
    @Override
    public FeaturePrinter printer() {
        return null;
    }

    @Override
    public String print(Collection collection) {
        return null;
    }
}
