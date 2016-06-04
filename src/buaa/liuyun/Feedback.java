package buaa.liuyun;

import java.io.BufferedReader;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import buaa.define.Node.Node;
import buaa.liuyun.KeyWords;
import buaa.servlet.Reback;
import buaa.solr.Actuary;
import buaa.solr.Solr;

public class Feedback {
	public static HashMap<String,Double> relevant_vector = new HashMap<String,Double>();
	public static HashMap<String,Double> unRelevant_vector = new HashMap<String,Double>();
	public static HashSet<Integer> mark = new HashSet<Integer>();
	public static void init() throws FileNotFoundException{
		
		return;
	}
	public static void clear() throws IOException{
		relevant_vector.clear();
	    unRelevant_vector.clear();
		if(DataSet.INDEX_VISIT==GetIdf.FIRST_QUERY){
			mark.clear();    
		}
	}
	public static void feedBack() throws IOException, SolrServerException{
		Iterator it = Reback.relevant.iterator();
		System.out.println("相关：");
		while(it.hasNext()){
			String str = it.next().toString();
			System.out.print(str+"   ");
		}
		System.out.println();
		
		it = Reback.unRelevant.iterator();
		System.out.println("不相关：");
		while(it.hasNext()){
			String str = it.next().toString();
			System.out.print(str+"   ");
		}
		System.out.println();
		
		clear();
		init();
		getMark();
		getNewQuery();
			
		DataSet.deal(GetIdf.NEXT_QUERY);
	}
	public static void getMark(){
		Iterator it = Reback.relevant.iterator();
		int index ;
		while(it.hasNext()){
			index = Integer.parseInt(it.next().toString());
			if(index <= GetIdf.doc_count && mark.contains(index))
				mark.remove(index);
		}
		
		it = Reback.unRelevant.iterator();
		while(it.hasNext()){
			index = Integer.parseInt(it.next().toString());
			if( index <= GetIdf.doc_count ){
				mark.add(index);
			}
		}

	}
	public static int queryId(int id) {
		// TODO Auto-generated method stub
		int low = 0 , high = Solr.results.size()-1;
		int mid = 0 ;
		int value ;
		while(low<=high){
			mid =( low + high )/2 ;
			value = Integer.parseInt(Solr.results.get(mid).getFieldValue("id").toString());
			if(value == id) return mid;
			if(value < id)	low = mid+1 ;
			else if(value > id) high = mid-1;
		}
		return -1;
	}
	public static void getNewQuery() throws IOException, SolrServerException{
		System.out.println("Running Feedback.getNewQuery()......");
		String [] str = null;
		String file = null , qstr = "";
		String _tit , _art ;
		Node node ;
		double tit, art, value;
		int len , index , rate ;
		double  tidf = 0 ,idf = 0;
		HashSet<String> set = new HashSet<String>();
		HashSet<String>  hset = new HashSet<String>();
				
		Iterator it = Reback.relevant.iterator();

		while(it.hasNext()){
			index = Integer.parseInt(it.next().toString());
			
			int id = queryId(index);
			
			_tit=Solr.results.get(id).getFieldValue("title_ss").toString();
			_art=Solr.results.get(id).getFieldValue("article_ss").toString();			
			
			_tit = _tit.substring(1 , _tit.length()-1);
			_art = _art.substring(1 , _art.length()-1);
			
			file = _tit+" "+_art;
			str = file.split(" ");
			set.clear();
			
			len = Actuary.DocLen.get(index);
			
			for(int j=0;j<str.length;j++){
				hset.add(str[j]);
				
				if(set.contains(str[j]))   continue;
				else set.add(str[j]);
				
				tit = 0 ; art = 0 ;
				node = SearchNode.titleNode(str[j],index);
				if(node.index!=0){
					idf =  GetIdf.doc_idf.get(str[j]).title;
					tidf = node.rate*1.0/Actuary.TFmax.get(node.index).tit*idf ;
					tit = tidf/(0.6+0.4*len/GetIdf.docLength_avg);
				}
				node = SearchNode.articleNode(str[j], index);
				if(node.index!=0){
					idf = GetIdf.doc_idf.get(str[j]).article;
					tidf = node.rate*1.0/Actuary.TFmax.get(node.index).art*idf ;
					art = tidf/(0.6+0.4*len/GetIdf.docLength_avg);
				}
				value = tit*0.64+art*0.36;
				
				if(!relevant_vector.containsKey(str[j]))
					relevant_vector.put(str[j], value);	
				else  relevant_vector.put(str[j], relevant_vector.get(str[j])+value);

			}
		}
		it = Reback.unRelevant.iterator();
		while(it.hasNext()){
			index = Integer.parseInt(it.next().toString());
			
			int id = queryId(index);
			
			_tit=Solr.results.get(id).getFieldValue("title_ss").toString();
			_art=Solr.results.get(id).getFieldValue("article_ss").toString();
			
			
			_tit = _tit.substring(1 , _tit.length()-1);
			_art = _art.substring(1 , _art.length()-1);
			
			file = _tit+" "+_art;
			
			str = file.split(" ");
			set.clear();
			

			len = Actuary.DocLen.get(index);
			
			for(int j=0;j<str.length;j++){
				hset.add(str[j]);
				
				if(set.contains(str[j]))   continue;
				else set.add(str[j]);
		
				tit = 0 ; art = 0 ;

				node = SearchNode.titleNode(str[j],index);
				if(node.index!=0){
					idf = GetIdf.doc_idf.get(str[j]).title;
					tidf = node.rate*1.0/Actuary.TFmax.get(node.index).tit*idf;
					tit = tidf/(0.6+0.4*len/GetIdf.docLength_avg);	
				}
				node = SearchNode.articleNode(str[j], index);
				if(node.index!=0){
					idf = GetIdf.doc_idf.get(str[j]).article;
					tidf = node.rate*1.0/Actuary.TFmax.get(node.index).art*idf ;
					art = tidf/(0.6+0.4*len/GetIdf.docLength_avg);
				}
				value = tit*0.64+art*0.36;
				
				if(!unRelevant_vector.containsKey(str[j]))
					unRelevant_vector.put(str[j], value);
				else unRelevant_vector.put(str[j], unRelevant_vector.get(str[j])+value);
			}
		}
		
		String tp = null;
		double re = 0 , ur = 0 , result = 0;
		double start = 0;
		it = hset.iterator();
		while(it.hasNext()){
			tp = it.next().toString();
//			start = 0; 
			re = 0; ur = 0;
			if(relevant_vector.containsKey(tp))
				re = 1.0*relevant_vector.get(tp)/Reback.relevant.size();
//			if(KeyWords.queryMap.containsKey(tp)) 
//				start = KeyWords.queryMap.get(tp);
			if(unRelevant_vector.containsKey(tp))
				ur = 1.0*unRelevant_vector.get(tp)/Reback.unRelevant.size();
//			result = start + re - ur ;
			result = re - ur ;
			if( result > 0 )
				KeyWords.queryMap.put(tp, result);
		}

//		it = KeyWords.queryMap.keySet().iterator();
//		while(it.hasNext()){
//			String ll = it.next().toString();
//			double cc = KeyWords.queryMap.get(ll);
//			System.out.println(ll+"		"+cc);
//		}
	}
}
