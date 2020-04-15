package cn.edu.whu.irlab.smart_cite.feature.analyze;

import cn.edu.whu.irlab.smart_cite.feature.printer.FeaturePrinter;

import java.util.Collection;
import java.util.Map;

/**
 * 特征输出器
 * Created by lsw on 2015/4/26.
 */
public class MyFeatureAnalyzer<T> implements FeatureAnalyzer<T> {
    FeatureAnalyzer featureAnalyzer;

    @Override
    public FeaturePrinter printer() {
        return null;
    }

    @Override
    public String print(T t) {
        if (t instanceof Map) { //mpa
            featureAnalyzer = new MapAnalyzer();
        } else if (t instanceof Collection) {   //集合
            featureAnalyzer = new ListAnalyzer();
        } else if (t instanceof Integer || t instanceof Long) { //整型
            featureAnalyzer = new LongAnalyzer();
        } else if (t instanceof Float || t instanceof Double) { //浮点型
            featureAnalyzer = new DoubleAnalyzer();
        } else if (t instanceof Boolean) {  //布尔型
            featureAnalyzer = new BooleanAnalyzer();
        } else {
            featureAnalyzer = new SingleFeatureAnalyzer();
        }
        return featureAnalyzer.print(t);
    }
}
