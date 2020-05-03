package cn.edu.whu.irlab.smart_cite.service.citesentExtract;

import cn.edu.whu.irlab.smart_cite.util.ReadUtil;
import cn.edu.whu.irlab.smart_cite.util.WriteUtil;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.util.List;


public class CitesentExtract {

    private static FileWriter fw;
    private static BufferedWriter writeText;
    //private static String docpath = "d:/2.xml";
    private static String datapath="d:/cite_data/Lei_reformatted";

    public static void main(String[] args) throws IOException, JDOMException {

        File file= new File(datapath);
        File[] files = file.listFiles();
        fw = new FileWriter("d:/1.csv",false);
        writeText = new BufferedWriter(fw);
        for (File file2 : files) {
            Element root = CitesentExtract.getcitesent(file2);
            WriteUtil.writeXml(root, "d:/cite_data/change_data/"+file2.getName());
        }
        writeText.close();

    }

    public static Element getcitesent(File docPath) throws IOException, JDOMException{
        //读取根节点
        Element root = ReadUtil.read2xml(docPath);
        //获得body节点
        Element bodyroot = root.getChild("body");
        CitesentExtract.remove_sent(bodyroot,docPath);
        return root;
    }



    public static void remove_sent(Element root,File docpath) throws IOException, JDOMException{
        //最高层的sec元素集合
        List<Element> secList = root.getChildren("sec");
        Element secRoot = null;

        //p元素集合
        List<Element> pList = null;
        Element pRoot = null;

        //s元素集合
        List<Element> sList = null;
        Element sRoot = null;

        //xref元素集合
        List<Element> xrefList = null;
        Element xrefRoot = null;

        //遍历sec
        for (int sec_num = 0; sec_num < secList.size(); sec_num++) {
            secRoot = secList.get(sec_num);
            //如果存在下层的sec，递归
            if(secRoot.getChild("sec")!=null){
                CitesentExtract.remove_sent(secRoot,docpath);
            }
            pList = secRoot.getChildren("p");

            //遍历子根元素的子元素集合(即遍历p元素)
            for (int j = 0; j < pList.size(); j++) {
                pRoot = pList.get(j);
                sList = pRoot.getChildren("s");
                //遍历子根元素的子元素集合(即遍历s元素)
                for (int k = 0; k < sList.size(); k++) {
                    sRoot = sList.get(k);
                    xrefList = sRoot.getChildren("xref");
                    //element.getAttributeValue("name")：获取s中c_type属性的值
                    if (!sRoot.getAttributeValue("c_type").equals("r")) {        //如果c_type的值不为r
                        pRoot.removeContent(sList.get(k));//删除该句
                        k--;
                    }
                    if (sRoot.getAttributeValue("c_type").equals("r")) {        //如果c_type的值为r
                        //遍历子根元素的子元素集合(即遍历xref元素)
                        //System.out.println(sRoot.getContent());
                        if(sRoot.getAttributeValue("id").contains(",")) break;
                        int sentid = Integer.parseInt(sRoot.getAttributeValue("id"));
                        for (int l = 0; l < xrefList.size(); l++) {
                            int bignum = 0;
                            int smallnum = 0;
                            xrefRoot = xrefList.get(l);
                            String context = xrefRoot.getAttributeValue("context");
                            if (context != null) {
                                String[] context_nums = context.split(",");
                                for (int cnum = 0; cnum < context_nums.length; cnum++) {
                                    if(context_nums[cnum]!="") {
                                        int b = Integer.parseInt(context_nums[cnum]);
                                        if (b > sentid) bignum++;
                                        else smallnum++;
                                    }
                                }
                                writeText.write("文档"+docpath+"句子id为"+sentid + "," + bignum + "," + smallnum);
                                writeText.newLine();    //换行
                                //调用write的方法将字符串写到流中
                                writeText.flush();

                            }
                        }
                    }
                }
            }
        }
        System.out.println(docpath.getName());
    }
}
