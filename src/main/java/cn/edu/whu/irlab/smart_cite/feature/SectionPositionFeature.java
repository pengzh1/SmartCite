package cn.edu.whu.irlab.smart_cite.feature;

import cn.edu.whu.irlab.smart_cite.vo.Sentence;
import cn.edu.whu.irlab.smart_cite.vo.RefTag;
import com.leishengwei.jutils.Arrays;

import java.util.HashMap;
import java.util.Map;

/**
 * 特征：当前句子所在区域的位置，前面的区域的上下文可能性更大
 * 实现方法：当前区域划分，归一化为6以后确定当前所在sectionId所在的区域.
 * Created by Lei Shengwei (Leo) on 2015/3/30.
 */
public class SectionPositionFeature extends SVFeature<Integer> {
    //test 统计信息用
    public static Map<Integer, Integer> sectionCount = new HashMap<>();

    @Override
    public Integer f(Sentence data, RefTag target) {
        int sectionNum = new Long((long) Math.ceil(data.getArticle().sectIndex(data) * (6 / (double) data.getArticle().sectionCount()))).intValue();
        //统计该section下的引文上下文关系个数
        if (Arrays.list(target.getContexts().split(",")).contains(data.getId() + "")) {   //属于上下文关系
            sectionCount.putIfAbsent(sectionNum, 1);
            sectionCount.computeIfPresent(sectionNum, (k, v) -> v + 1);
        }
        return sectionNum;
    }
}
