package buaa.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;

import buaa.define.Node.Node;
import buaa.liuyun.DataSet;
import buaa.liuyun.GetIdf;
import buaa.liuyun.SearchNode;

public class WeightSquare {
	public static HashMap<Integer,Double> doc_square = new HashMap<Integer,Double>();
	public static HashMap<Integer,HashMap<String,Double>> weight = new HashMap<Integer,HashMap<String,Double>>();
	
	private static List<Node> list= new ArrayList<Node>();
	private static HashMap<String ,Double>  hash = new HashMap<String,Double>();
	public static void init(){
		doc_square.clear();
		weight.clear();
		list.clear();
		hash.clear();
	}
	public static void deal() throws IOException, SolrServerException{
		System.out.println("Running WeightSquare.deal()......");
		long start = System.currentTimeMillis();
		init();
		String cs = null ;
		Iterator it = Actuary.StringSet.iterator() ;
		while(it.hasNext()){
			cs = it.next().toString() ;
			if(Actuary.title.containsKey(cs))  dealTitle(cs);
			if(Actuary.article.containsKey(cs)) dealArticle(cs);
		}

		long end = System.currentTimeMillis();
		System.out.println("WeightSquare: "+(end-start)+ " ms");
		DataSet.deal(GetIdf.FIRST_QUERY);
	}
	public static void dealTitle(String cs){
		int index ,rate ;
		double value ;
		list = Actuary.title.get(cs);
		for(int i = 0 ; i < list.size() ; i++ ){
			index = list.get(i).index;
			rate = list.get(i).rate;
				
			value = SearchNode.titleValue(index,rate,cs);
			if(value!=0){
				if(!weight.containsKey(index)){
					hash = new HashMap<String,Double>();
					hash.put(cs, value);
					weight.put(index, hash);
				}
				else{
					hash = weight.get(index);
					hash.put(cs,value);
					weight.put(index, hash);
				}
				if(!doc_square.containsKey(index)){
					doc_square.put(index, value*value);
				}
				else doc_square.put(index, doc_square.get(index)+value*value);
			}
		}
	}
	public static void dealArticle(String cs){
		int index ,rate ;
		double value ;
		list = Actuary.article.get(cs);
		for(int i =  0 ; i < list.size() ; i++){
			index = list.get(i).index;
			rate = list.get(i).rate;
			value = SearchNode.articleValue(index, rate, cs);
			if(!weight.containsKey(index)){
				hash = new HashMap<String,Double>();
				hash.put(cs, value);
				weight.put(index, hash);
			}
			else{
				hash = weight.get(index);
				hash.put(cs,value);
				weight.put(index, hash);
			}
			if(!doc_square.containsKey(index))
				doc_square.put(index, value*value);
			else doc_square.put(index, doc_square.get(index)+value*value);
		}
	}
}
