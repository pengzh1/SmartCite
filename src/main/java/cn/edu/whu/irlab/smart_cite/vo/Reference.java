package cn.edu.whu.irlab.smart_cite.vo;

import cn.edu.whu.irlab.smart_cite.util.TypeConverter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gcr19
 * @version 1.0
 * @date 2020/2/14 11:33
 * @desc 引文实体
 **/
@Data
public class Reference implements ToJsonAble {
    private String id = "";
    private String label = "";
    private List<Author> authors = new ArrayList<>();
    private String year = ""; //发表年份
    private String article_title = ""; //标题
    private String source = ""; //来源
    private String volume = ""; //卷号
    private String issue; //期号
    private String fpage = ""; //起始页码
    private String lpage = ""; //终止页码

    public void addAuthor(Author author) {
        authors.add(author);

    }

    @Override
    public String toString() {
        return "{" + "\"id\":\"" + id + '\"' +
                ",\"label\":\"" + label + '\"' +
                ",\"authors\":" + TypeConverter.list2JsonArray(authors) +
                ",\"year\":\"" + year + '\"' +
                ",\"article_title\":\"" + article_title + '\"' +
                ",\"source\":\"" + source + '\"' +
                ",\"volume\":\"" + volume + '\"' +
                ",\"issue\":\"" + issue + '\"' +
                ",\"fpage\":\"" + fpage + '\"' +
                ",\"lpage\":\"" + lpage + '\"' +
                '}';
    }

	@Override
	public JSONObject toJson() {
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("id",id);
		jsonObject.put("label",label);
		jsonObject.put("authors",TypeConverter.list2JsonArray(authors));
		jsonObject.put("year",year);
		jsonObject.put("article_title",article_title);
		jsonObject.put("source",source);
		jsonObject.put("volume",volume);
		jsonObject.put("issue",issue);
		jsonObject.put("fpage",fpage);
		jsonObject.put("lpage",lpage);
    	return jsonObject;
	}
}