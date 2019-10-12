package cn.edu.whu.irlab.smart_cite.service.extractor;

import cn.edu.whu.irlab.smart_cite.vo.ExtractSourceVo;
import cn.edu.whu.irlab.smart_cite.vo.RecordVo;
import cn.edu.whu.irlab.smart_cite.vo.ReferenceVo;
import org.jdom2.Element;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author gcr19
 * @date 2019-08-14 20:34
 * @desc 抽取器接口
 **/
public interface Extractor {

    /**
     * 抽取引文上下文
     * @param article 文档节点
     * @return 抽取结果
     */
    Set<RecordVo> extract(Element article);

    /**
     * 抽取参考文献列表信息
     * @param article 文档节点
     * @return 参考文献列表
     */
    Map<String,ReferenceVo> extractReferences(Element article);

    /**
     * 抽取含有引文label的段落
     * @param element article节点
     * @return 含有引文label的段落
     */
    List<Element> extractParagraphs(Element element);

    /**
     * 清除非引文标签
     * @param paragraphAfterSplit 分句后的段落节点
     * @return 清除非引文标签后的段落节点
     */
    Element cleanLabel(Element paragraphAfterSplit);

    /**
     * 抽取引文上下文
     * @param paragraphAfterClean 清洗标签后的段落节点
     * @return 抽取到的引文上下文集合
     */
    Set<RecordVo> extractRefContext(Element paragraphAfterClean);

    Set<RecordVo> matchRefTitle(Set<RecordVo> recordVos, Map<String, ReferenceVo> referencesMap);
}
