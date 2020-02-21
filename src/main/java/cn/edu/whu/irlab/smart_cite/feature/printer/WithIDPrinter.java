package cn.edu.whu.irlab.smart_cite.feature.printer;

/**
 * 包含一个ID的打印
 * Created by lsw on 2015/4/26.
 */
public abstract class WithIDPrinter<ID> implements FeaturePrinter {
    public abstract IDGenerator<ID> getIdGenerator();
}
