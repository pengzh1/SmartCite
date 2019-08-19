package cn.edu.whu.irlab.smart_cite.service.extractor;

import cn.edu.whu.irlab.smart_cite.vo.ExtractSourceVo;
import cn.edu.whu.irlab.smart_cite.vo.ReferenceVo;
import org.jdom2.Element;

import java.util.List;

/**
 * @author gcr19
 * @date 2019-08-14 20:34
 * @desc 抽取器接口
 **/
public interface Extractor {
    List<ExtractSourceVo> createExtractSource(Element paragraph, List<ReferenceVo> referenceList);
}
