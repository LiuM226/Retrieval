package buaa.liuyun;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import buaa.liuyun.KeyWords;
import buaa.servlet.Reback;
import buaa.servlet.Search;
import buaa.solr.Actuary;
import buaa.solr.Solr;
import buaa.solr.WeightSquare;
import buaa.define.Node.Node;
import buaa.liuyun.Feedback;
public class DataSet {
	public static int INDEX_VISIT;

	public static List<CosQueryDoc> cos = new ArrayList();
	public static HashMap<String,Double> queryMap = new HashMap<String,Double>();
	public static HashMap<Integer,Double>sim_fenZi = new HashMap<Integer,Double>();
	public static double query_square;
//	public static HashMap<Integer,Double>prob = new HashMap<Integer,Double>();
	public static HashMap<Integer,Double>score = new HashMap<Integer,Double>();
	public static HashSet <Integer>hashSet = new HashSet<Integer>();
	
	public static HashMap<Integer,Double>dis = new HashMap<Integer,Double>();
	private static Iterator it ;
	
	public static PrintWriter out;
	public static void init(int where) throws FileNotFoundException{
		INDEX_VISIT = where;
		if(where == GetIdf.FIRST_QUERY){
			hashSet.clear();
			score.clear();
		}
		queryMap = new HashMap(KeyWords.queryMap);
		out = new PrintWriter(new File("tp.txt"));	
	}
	
	public static void deal(int where) throws IOException, SolrServerException{		
		init(where);
		SetSimFenZi(where);
		if(where==GetIdf.FIRST_QUERY)   SetProb();
		getScore(where);
		if(where == GetIdf.NEXT_QUERY){
			cos.clear();
			NextQuery.Rescore();
		}
		deal_sort();
		OrderResult.Result();
		clear();
//		GUI.setTable(OrderResult.sourceList);
		if(INDEX_VISIT==GetIdf.FIRST_QUERY)
			Search.setData(OrderResult.sourceList);
		else if(INDEX_VISIT==GetIdf.NEXT_QUERY)
			Reback.setData(OrderResult.sourceList);
			
	}
	public static void clear(){
		cos.clear();   
		queryMap.clear();
		sim_fenZi.clear();
//		prob.clear();
		dis.clear();
	}
	public static void SetSimFenZi(int where) throws IOException, SolrServerException{
		System.out.println("Running DataSet.SetSimFenZi()......");	
		long start = System.currentTimeMillis();
		double temp , fenZi = 0 , fenMu = 0 ;
		query_square = 0;
		String cs = null;
		List<Node> list = new ArrayList<Node>();
		int index , rate; int len;
		double tit = 0 , art = 0, value = 0;
		Node node = null;
		double  tidf = 0;
		Iterator iter =null;
		it = queryMap.keySet().iterator();
		while(it.hasNext()){
			cs = it.next().toString();
			query_square += queryMap.get(cs)*queryMap.get(cs);
					
			if(Actuary.title.containsKey(cs)){
				list = Actuary.title.get(cs);
				fenZi = 0 ;
				for(int i=0;i<list.size();i++){
					index = list.get(i).index;
					rate = list.get(i).rate;
					if(where == GetIdf.FIRST_QUERY)	hashSet.add(index);
					else if(where == GetIdf.NEXT_QUERY && !hashSet.contains(index))   continue;
				
					value = SearchNode.titleValue(index, rate, cs);
					if(value!=0){
						fenZi = queryMap.get(cs)*value;
						if(!sim_fenZi.containsKey(index))
							sim_fenZi.put(index, fenZi);
						else	sim_fenZi.put(index, sim_fenZi.get(index)+fenZi);
					}
				}
			}		
			if(Actuary.article.containsKey(cs)){
				list = Actuary.article.get(cs);
				fenZi = 0;
				for(int i=0;i<list.size();i++){
					index = list.get(i).index;
					rate = list.get(i).rate;
					if(where == GetIdf.FIRST_QUERY)	hashSet.add(index);
					else if(where == GetIdf.NEXT_QUERY && !hashSet.contains(index))   continue;    
					
					value = SearchNode.articleValue(index, rate, cs);
					fenZi = queryMap.get(cs)*value;
					if(!sim_fenZi.containsKey(index)){
						sim_fenZi.put(index, fenZi);
					}
					else	sim_fenZi.put(index, sim_fenZi.get(index)+fenZi);
				}
			}
		}	
		long end = System.currentTimeMillis();
		System.out.println("SetSimFenZi: "+ (end-start) +"ms");
		
	}
	public static void SetProb(){
		System.out.println("Running DataSet.SetProb()......");
		long start = System.currentTimeMillis();
		int index ; int len;
		double Wdtt = 0, Wdta = 0 , Wdt = 0;
		double W = 0, sim  = 0;
		it = hashSet.iterator();
		Iterator iter = null;
		String cs;
		Node node ;
		while(it.hasNext()){
			index =Integer.parseInt(it.next().toString());
			iter =queryMap.keySet().iterator();
			sim  = 0;
			while(iter.hasNext()){
				cs = iter.next().toString();
				Wdt = 0 ; Wdtt = 0 ; Wdta = 0;
				W = Math.log(( GetIdf.doc_count - GetIdf.wordDoc.get(cs) + 0.5 )/(GetIdf.wordDoc.get(cs)+0.5));
				len = Actuary.DocLen.get(index);
				
				node = SearchNode.titleNode(cs, index) ;
				if(node.index!=0)
					Wdtt = node.rate/Actuary.TFmax.get(node.index).tit*0.62/(0.6+0.4*len/GetIdf.docLength_avg);
				node = SearchNode.articleNode(cs, index);
				if(node.index!=0)
					Wdta = node.rate/Actuary.TFmax.get(node.index).art*0.38/(0.6+0.4*len/GetIdf.docLength_avg);
				Wdt = Wdtt + Wdta;
				sim += W*Wdt/(0.5+Wdt);
			}
//			prob.put(index, sim);
		}
		long end = System.currentTimeMillis();
		System.out.println("SetProb: "+(end- start)+" ms");
	}
	public static void getScore(int where){
		System.out.println("Running DataSet.getScore......");	
		long start = System.currentTimeMillis();
		double _score;
		int index;
		Double fenZi,fenMu;
		it = hashSet.iterator();
		while(it.hasNext()){
			index = Integer.parseInt(it.next().toString());
			fenZi = sim_fenZi.get(index);
			fenMu = Math.sqrt(WeightSquare.doc_square.get(index))*Math.sqrt(query_square);
			if(where==GetIdf.FIRST_QUERY){
//				_score = 0.5*fenZi/fenMu+0.5*prob.get(index);
				_score = 1.0*fenZi/fenMu;
//				System.out.println(0.5*fenZi/fenMu+"     "+0.5*prob.get(index));
				if(_score!=0){
					score.put(index, _score);
					cos.add(new CosQueryDoc(index, _score));
				}
			}
			else if(where==GetIdf.NEXT_QUERY){
				if(!Feedback.mark.contains(index))
					dis.put(index , 1.0*fenZi/fenMu);          
			}
		}	
		long end = System.currentTimeMillis();
		System.out.println("getScore: "+(end-start)+"ms");
	}
	private static void deal_sort() {
//		改一下系统设置，还是选择使用老版本的排序方法，在代码前面加上这么一句话：
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		Comparator  cmp = new Compare();
		Collections.sort(cos,cmp);
	}
	
}
