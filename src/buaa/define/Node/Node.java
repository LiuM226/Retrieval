package buaa.define.Node;

public class Node {
	public int index;
	public int rate;
	public Node(int a,int b){
		this.index = a;
		this.rate = b; 
	}
	public void setIndex(int index){
		this.index = index ;
	}
	public void setrate(int rate){
		this.rate = rate;
	}
	public int getIndex(){
		return index ;
	}
	public int getrate(){
		return rate;
	}
}
