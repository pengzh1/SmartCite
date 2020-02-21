package cn.edu.whu.irlab.smart_cite.feature;

/**
 * 特征接口
 * Created by Lei Shengwei (Leo) on 2015/3/29.
 */
@FunctionalInterface
public interface IFeature<DATA, REF, V> {
    /**
     * 传入特征D，返回特征结果V
     *
     * @param data
     * @return
     */
    V f(DATA data, REF target);
}
