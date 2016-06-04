package buaa.solr;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;

import buaa.define.Node.Node;
import buaa.define.Node.TMAX;


public class Actuary {
	public static HashSet<String> StringSet = new HashSet<String>();
	public static HashMap<String,List<Node>> title = new HashMap<String,List<Node>>();
	public static HashMap<String,List<Node>> article = new HashMap<String,List<Node>>();
	public static HashMap<Integer,TMAX>	TFmax = new HashMap<Integer,TMAX>();
	public static HashMap<Integer,Integer> DocLen = new HashMap<Integer,Integer>();
	
	private static HashMap<String,Integer> map = new HashMap<String,Integer>();
	public static void init(){
		StringSet.clear();
		title.clear();
		article.clear();
		TFmax.clear();
		DocLen.clear();
		
		map.clear();
		
	}
	public static void Deal() throws IOException, SolrServerException{
		System.out.println("Running Actuary.Deal()......");
		
		init();
		String id_number;
		String tit = null , art = null ;
		String [] str_title , str_article;
		int MAX_tit = 0 , MAX_art = 0;
		System.out.println("ÊýÁ¿£º "+Solr.results.size());
		for (int i = 0; i < Solr.results.size(); ++i) {
			id_number = Solr.results.get(i).getFieldValue("id").toString();
			tit = Solr.results.get(i).getFieldValue("title_ss").toString();
			art = Solr.results.get(i).getFieldValue("article_ss").toString();
			
			tit =tit.substring(1,tit.length()-1);
			art =art.substring(1,art.length()-1);
					
			str_title = tit.split(" ");
			str_article = art.split(" ");
			MAX_tit  = ActTitle(Integer.parseInt(id_number) , str_title);
			MAX_art = ActArt(Integer.parseInt(id_number) , str_article);
				
			TFmax.put(Integer.parseInt(id_number), new TMAX(MAX_tit,MAX_art));
			DocLen.put(Integer.parseInt(id_number), str_title.length + str_article.length) ;
		}
		WeightSquare.deal();
	}
	public static int ActTitle(int index , String [] str_title){
		map.clear(); 
		int MAX = 1  , temp =0;
		for(int i = 0 ; i < str_title.length ; i++){
			if(CilStw.isStopWord(str_title[i])) continue;
			StringSet.add(str_title[i]);
			if(!map.containsKey(str_title[i])){
				map.put(str_title[i], 1);
			}
			else{
				temp = map.get(str_title[i]) + 1;
				map.put(str_title[i], temp);
				if( temp > MAX ) MAX = temp ;
			}
		}
		
		String str = null;
		List<Node> list= new ArrayList<Node>();
		Node node = null;
		Iterator it = map.keySet().iterator();
		while(it.hasNext()){
			str = it.next().toString();
			if(!title.containsKey(str)){
				list = new ArrayList<Node>();
				node = new Node(index,map.get(str));
				list.add(node);
				title.put(str, list);
			}
			else{
				list = title.get(str);
				node = new Node(index,map.get(str));
				list.add(node);
				title.put(str, list);
			}
		}
		return MAX ;
	}
	public static int ActArt(int index , String [] str_article){
		map.clear();
		int MAX = 1 , temp =0;  
		for(int i = 0 ; i < str_article.length ; i++){
			if(CilStw.isStopWord(str_article[i]))	continue;
			StringSet.add(str_article[i]);
			if(!map.containsKey(str_article[i])){
				map.put(str_article[i], 1);
			}
			else{
				temp = map.get(str_article[i])+1 ;
				map.put(str_article[i], temp);
				if(temp > MAX) MAX = temp ;
			}
		}
		String str = null;
		List<Node> list= new ArrayList<Node>();
		Node node = null;
		Iterator it = map.keySet().iterator();
		while(it.hasNext()){
			str = it.next().toString();
			if(!article.containsKey(str)){
				list = new ArrayList<Node>();
				node = new Node(index,map.get(str));
				list.add(node);
				article.put(str, list);
			}
			else{
				list = article.get(str);
				node = new Node(index,map.get(str));
				list.add(node);
				article.put(str, list);
			}
		}
		return MAX ;
	}
}
