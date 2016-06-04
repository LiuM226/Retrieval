package buaa.liuyun;

import java.io.BufferedReader;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrServerException;

import buaa.gui.CutWords;
import buaa.solr.CilStw;
import buaa.solr.Solr;
public class GetIdf {
	public static final int FIRST_QUERY = 1;
	public static final int NEXT_QUERY = 2;
	public static HashMap<String, Integer> wordArt = new HashMap<String, Integer>();
	public static HashMap<String, Integer> wordTit = new HashMap<String, Integer>();

	public static HashMap<String,Idf> doc_idf = new HashMap<String,Idf>();
	public static HashMap<String,Double> idf = new HashMap<String,Double>();
	public static HashMap<String,Integer> wordDoc = new HashMap<String,Integer>();
		
	private static HashSet<String> art = new HashSet<String>() ;
	private static HashSet<String> tit = new HashSet<String>() ;
	
	public static long doc_count;
	private static long title_num_number , article_num_number;
	public static double docLength_avg;

	public static void init() throws FileNotFoundException{
    	
    	wordArt.clear();   wordTit.clear();   doc_idf.clear();
		idf.clear();    wordDoc.clear();   art.clear(); tit.clear();
	}
	public static void Tidf() throws IOException, SolrServerException{
		init();
		calculate();
		closeFile();
		
	}
	public static void closeFile() throws IOException{
		art.clear();
		tit.clear();
	}
	public static void calculate() throws IOException {
		System.out.println("Running GetIdf.calculate()......");
		String [] str_article = null; String [] str_title = null;
		String word = null , pass = null;
		String Wtit = null , Wart =null;
		int  i = 0;
		doc_count = 0;  title_num_number = 0 ; article_num_number=0;   
		Iterator it = null;
		HashSet<String> hset = new HashSet<String>();
		for(int k=0;k<CutWords.infor.size();k++){
			
			Wtit = CutWords.infor.get(k).getTitle();
			Wart = CutWords.infor.get(k).getContent();
			str_title = Wtit.split(" ");
			str_article = Wart.split(" ");
			
			doc_count++;
			art.clear();
			tit.clear();
			hset.clear();
			
			for(i=0;i<str_title.length;i++){
				title_num_number++;
				word = str_title[i];
				if(!CilStw.isStopWord(word))
					tit.add(word);
			}
			for(i =0;i<str_article.length;i++){
				article_num_number++;
				word = str_article[i];
				if(!CilStw.isStopWord(word))
					art.add(word);

			}
			
			it = tit.iterator();
			while(it.hasNext()){
				pass = it.next().toString();
				hset.add(pass);
				if(!wordTit.containsKey(pass))   wordTit.put(pass, 1);
				else wordTit.put(pass, wordTit.get(pass)+1);
			}

			it = art.iterator();
			while(it.hasNext()){
				pass = it.next().toString();
				hset.add(pass);
				if(!wordArt.containsKey(pass))   wordArt.put(pass, 1) ; 
				else wordArt.put(pass, wordArt.get(pass)+1);	
			}
			it = hset.iterator();
			while(it.hasNext()){
				pass =it.next().toString();
				if(!wordDoc.containsKey(pass)) wordDoc.put(pass, 1);
				else wordDoc.put(pass, wordDoc.get(pass)+1);
			}
		}
		
		String obj = null;
		Idf tp = null;
		it = wordDoc.keySet().iterator();
		while(it.hasNext()){
			obj = it.next().toString();
			tp = new Idf(0,0);
			if(wordTit.containsKey(obj))	tp.title = Math.log(1.0*doc_count/wordTit.get(obj)+1.0);
			if(wordArt.containsKey(obj))	tp.article = Math.log(1.0*doc_count/wordArt.get(obj)+1.0);
			doc_idf.put(obj, tp);
			idf.put(obj, Math.log(1.0*doc_count/wordDoc.get(obj)+1.0));
		}
	 	docLength_avg = (title_num_number+article_num_number*1.0)/doc_count;
	 	System.out.println("GetIdf.calculate() ended......");
	}
}
class Idf{
	public double title;
	public double article;
	public Idf(double a , double b){
		title = a;
		article =  b;
	}
}
