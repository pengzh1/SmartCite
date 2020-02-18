package cn.edu.whu.irlab.smart_cite.vo;

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
}