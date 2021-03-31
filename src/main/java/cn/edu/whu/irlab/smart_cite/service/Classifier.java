package cn.edu.whu.irlab.smart_cite.service;

import cn.edu.whu.irlab.smart_cite.vo.Result;

import java.io.File;
import java.util.List;

/**
 * 分类器接口
 */
public interface Classifier {
    List<Result> classify(List<Result> results, File file);
}
