package cn.edu.whu.irlab.smart_cite.service.splitter;

import cn.edu.whu.irlab.smart_cite.exception.SplitSentenceException;
import cn.edu.whu.irlab.smart_cite.vo.ReferenceVo;
import org.jdom2.Element;

import java.util.List;

/**
 * @author gcr19
 * @date 2019-08-16 19:55
 * @desc 句子分割器 接口
 **/
public interface Splitter {

    List<String> splitSentence(String text) throws SplitSentenceException;

    /**
     * 对段落中的句子进行拆分
     * @param paragraph 段落节点p
     * @return 拆分句子后的的段落节点p
     */
    Element splitSentence(Element paragraph);

}
