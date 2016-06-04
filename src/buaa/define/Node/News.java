package buaa.define.Node;

public class News {
	public String title;
	public String content;
	public News(String title,String content){
		this.title = title;
		this.content = content;
	}
	public void setTitle(String title){
		this.title = title;
	}
	public void setContent(String content){
		this.content = content;
	}
	public String getTitle(){
		return title;
	}
	public String getContent(){
		return content;
	}
}
