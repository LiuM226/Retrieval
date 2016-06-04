package buaa.liuyun;

import java.util.ArrayList;
import java.util.List;

import buaa.define.Node.Node;
import buaa.solr.Actuary;

public class SearchNode {
	public static Node titleNode(String cs, int index) {
		// TODO Auto-generated method stub
		Node node  = new Node(0,0);
		if(!Actuary.title.containsKey(cs)) return node;
		List<Node> list = new ArrayList<Node>();
		list = Actuary.title.get(cs);
		int low = 0 , high = list.size()-1;
		int mid = 0 ;
		while(low<=high){
			mid =( low + high )/2 ;
			node = list.get(mid);
			if(node.index == index) return node;
			if(node.index < index)	low = mid+1 ;
			else if(node.index > index) high = mid-1;
		}
		node = new Node(0,0);
		return node;
	}
	public static Node articleNode(String cs, int index) {
		// TODO Auto-generated method stub
		Node node = new Node(0,0);
		if(!Actuary.article.containsKey(cs)) return node;
		
		List<Node> list = new ArrayList<Node>();
		list = Actuary.article.get(cs);
		int low = 0 , high = list.size()-1;
		int mid = 0 ;
		while(low<=high){
			mid =( low + high )/2 ;
			node = list.get(mid);
			if(node.index == index) return node;
			if(node.index < index)	low = mid+1 ;
			else if(node.index > index) high = mid-1;
		}
		node = new Node(0,0);
		return node;
	}
//	寻找不在article中含有，却在title中含有的词
	public static double titleValue(int index,int rate,String cs){
		double value = 0;
		double tidf =0 , tit = 0;
		int len = Actuary.DocLen.get(index);
		Node node = articleNode(cs, index);
		double idf = 0;
		if(node.index==0){
			idf = GetIdf.doc_idf.get(cs).title;
			tidf = rate*1.0/Actuary.TFmax.get(index).tit * idf;
			tit = tidf/(0.6+0.4*len/GetIdf.docLength_avg);
			value = tit*0.64;
		}
		return value;
	}
//	寻找在article中有，不一定在title中含有的词
	public static double articleValue(int index,int rate,String cs){
		double value = 0; 
		double tidf = 0 , tit = 0 , art = 0 ;
		int len = Actuary.DocLen.get(index);
		Node node = titleNode(cs,index);
		double idf = 0;
		if(node.index!=0){
			idf = GetIdf.doc_idf.get(cs).title;
			tidf = node.rate*1.0/Actuary.TFmax.get(node.index).tit*idf;
			tit = tidf/(0.6+0.4*len/GetIdf.docLength_avg);
		}
		if(GetIdf.doc_idf.containsKey(cs)){
			idf = GetIdf.doc_idf.get(cs).article;
			tidf = rate*1.0/Actuary.TFmax.get(index).art*idf;
			art = tidf/(0.6+0.4*len/GetIdf.docLength_avg);
			
//			System.out.println(cs+" idf  "+GetIdf.doc_idf.get(cs).article);
//			System.out.println(cs+" tidf  "+Actuary.TFmax.get(index).art);
//			System.out.println(cs+" art  "+GetIdf.docLength_avg);
		}
		value = tit*0.64+art*0.36;
		return value;
	}
	
	
}
