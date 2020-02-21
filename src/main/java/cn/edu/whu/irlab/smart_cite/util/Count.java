package cn.edu.whu.irlab.smart_cite.util;

import com.leishengwei.jutils.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * 统计类
 * Created by lsw on 2015/4/29.
 */
public class Count {
    private long count = 0; //个数
    private long sum = 0;   //求和
    private double average = 0.0; //平均
    private List<Long> list = new ArrayList<>();

    public static Count count(long data) {
        return new Count(data);
    }

    public static Count count(int data) {
        return new Count(data);
    }

    public Count(long count) {
        this.count = count;
        list.add(count);
    }

    public Count add(int num) {
        add((long) num);
        return this;
    }

    public Count add(long num) {
        list.add(num);
        count += 1;
        sum += num;
        average = sum / (double) count;
        return this;
    }

    public long getCount() {
        return count;
    }

    public Long getSum() {
        return sum;
    }


    public Double getAverage() {
        return average;
    }

    /**
     * 得到一个只读的列表
     *
     * @return
     */
    public List<Long> getList() {
        return java.util.Collections.unmodifiableList(list);
    }

    @Override
    public String toString() {
        return Strings.s("{count:%s,avg:%s,sum:%s}", count, average, sum);
    }
}
