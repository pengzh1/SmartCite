package cn.edu.whu.irlab.smart_cite.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
public class Reference
{
	private String id="";
	private String label = "";
	private List<Author> authors = new ArrayList<>();
	private String year=""; //发表年份
	private String article_title=""; //标题
	private String source=""; //来源
	private String volume=""; //卷号
	private String issue; //期号
	private String fpage=""; //起始页码
	private String lpage=""; //终止页码

	public void addAuthor(Author author){
		authors.add(author);
	}

	private JSONArray authors2Json(){
		JSONArray array = new JSONArray();
		for (Author a :
				authors) {
			array.add(JSON.parse(a.toString()));
		}
		return array;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("{");
		sb.append("\"id\":\"")
				.append(id).append('\"');
		sb.append(",\"label\":\"")
				.append(label).append('\"');
		sb.append(",\"authors\":")
				.append(authors2Json());
		sb.append(",\"year\":\"")
				.append(year).append('\"');
		sb.append(",\"article_title\":\"")
				.append(article_title).append('\"');
		sb.append(",\"source\":\"")
				.append(source).append('\"');
		sb.append(",\"volume\":\"")
				.append(volume).append('\"');
		sb.append(",\"issue\":\"")
				.append(issue).append('\"');
		sb.append(",\"fpage\":\"")
				.append(fpage).append('\"');
		sb.append(",\"lpage\":\"")
				.append(lpage).append('\"');
		sb.append('}');
		return sb.toString();
	}
}