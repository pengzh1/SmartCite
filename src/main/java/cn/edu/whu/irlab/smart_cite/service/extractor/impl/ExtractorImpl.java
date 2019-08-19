package cn.edu.whu.irlab.smart_cite.service.extractor.impl;

import cn.edu.whu.irlab.smart_cite.service.extractor.Extractor;
import cn.edu.whu.irlab.smart_cite.vo.ExtractSourceVo;
import cn.edu.whu.irlab.smart_cite.vo.ReferenceVo;
import org.apache.commons.collections4.CollectionUtils;
import org.jdom2.Element;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author gcr19
 * @date 2019-08-16 19:08
 * @desc 抽取器实现类
 **/
@Service("extractor")
public class ExtractorImpl implements Extractor {

    @Override
    public List<ExtractSourceVo> createExtractSource(Element paragraph, List<ReferenceVo> referenceList){
        List<ExtractSourceVo> extractSourceList=new ArrayList<>();

        List<String> sentenceList=new ArrayList<>();

        List<String> preRefTitleList=new ArrayList<>();

        List<String> nextRefTitleList=new ArrayList<>();

        List<Element> sentences=paragraph.getChildren();

        for (int i = 0; i <sentences.size() ; i++) {
            Element sentence=sentences.get(i);
            List<Element> refList=sentence.getChildren("xref");

            if (refList.size()!=0||(i==(sentences.size()-1))){
                sentenceList.add(sentence.getText());

                ExtractSourceVo extractSource=new ExtractSourceVo();

                List<String> temp=new ArrayList<>();
                temp.addAll(sentenceList);
//                CollectionUtils.addAll(temp,sentenceList.iterator());
//                Collections.copy(temp,sentenceList);
                extractSource.setSentence(temp);
                sentenceList.clear();

                for (Element oneOfRef:
                        refList) {
                    nextRefTitleList.add(selectRefTitle(oneOfRef.getAttribute("rid").getValue(),referenceList));
                }

                List<String> refTitleList=new ArrayList<>();
                refTitleList.addAll(preRefTitleList);
                refTitleList.addAll(nextRefTitleList);
                extractSource.setReferenceTitle(refTitleList);

                extractSourceList.add(extractSource);

                preRefTitleList.clear();
                preRefTitleList.addAll(nextRefTitleList);
                nextRefTitleList.clear();
            }
        }
        return extractSourceList;
    }

    private String selectRefTitle(String id,List<ReferenceVo> referenceList){
        String title="";
        for (ReferenceVo ref:
             referenceList) {
            if (id.equals(ref.getID())){
                title= ref.getTitle();
                break;
            }
        }
        return title;
    }
}
