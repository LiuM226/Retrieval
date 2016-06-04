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
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import buaa.define.Node.Node;
import buaa.gui.SourceModel;
import buaa.solr.Actuary;
import buaa.solr.CilStw;
import buaa.solr.Solr;

public class OrderResult {
	private static final int topNSize = 1000;
	
	private static PrintWriter out_cosine;


	public static List<SourceModel> sourceList = new ArrayList<SourceModel>();
	public static String  wordFre ;
	private static HashMap<String,Integer> wordf = new HashMap<String,Integer>();
	public static void init() throws FileNotFoundException{
		out_cosine = new PrintWriter(new File("Cosine.txt"));	
		sourceList.clear();
		wordFre = null;
		wordf.clear();
	}
	public static void closeFile() throws IOException{
		wordf.clear();
		out_cosine.close();

	}
	public static void Result() throws IOException, SolrServerException{
		init();
		getResult();
		sort_wordf();
		closeFile();
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
	public static void wordFre(String []tit, String []con){
		int k = 0;
		for(k = 0; k < tit.length; k ++){
			if(CilStw.isStopWord(tit[k]) || tit[k].length() <= 1) continue;
			if(wordf.containsKey(tit[k]))
				wordf.put(tit[k], wordf.get(tit[k])+1);
			else wordf.put(tit[k], 1);
		}
		for(k = 0; k < con.length; k ++){
			if(CilStw.isStopWord(con[k]) || con[k].length() <= 1) continue;
			if(wordf.containsKey(con[k]))
				wordf.put(con[k], wordf.get(con[k])+1);
			else wordf.put(con[k], 1);
		}
	}
	public static void sort_wordf(){
		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(wordf.entrySet());
        Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {  
            public int compare(Map.Entry<String, Integer> o1,  
                    Map.Entry<String, Integer> o2) {  
                return -( o1.getValue()-o2.getValue());  
            }  
        });    
        wordFre ="";
        Entry<String,Integer> ent = null;
        for (int i = 0; i < Math.min(infoIds.size(),20); i++) {  
            ent = infoIds.get(i);  
            if(i==0)	wordFre += ent.getKey();
            else wordFre += "¡¢" + ent.getKey();
        }
	}
	public static void getResult() throws IOException, SolrServerException{
		System.out.println("Running OrderResult.getResult()......");
		long start = System.currentTimeMillis();
		String title = null , content = null , qstr = null;
		String index = null , ans = null;
		
		int id ;
				
		int Min = Math.min(DataSet.cos.size(),topNSize);
		
		String []tit_words ;
		String []con_words ;
		java.text.DecimalFormat df = new  java.text.DecimalFormat("#.00000");
		SourceModel model = new SourceModel();
		for(int i = 0 ; i < Min ; i++ ){
			out_cosine.printf("%d\t%f\n",DataSet.cos.get(i).getLine(),DataSet.cos.get(i).getScore());
			
			id = queryId(DataSet.cos.get(i).getLine());
			title = Solr.results.get(id).getFieldValue("title_ss").toString();
			content = Solr.results.get(id).getFieldValue("article_ss").toString();
			
			tit_words = title.split(" ");
			con_words = content.split(" ");
			wordFre(tit_words, con_words);
			
			index = DataSet.cos.get(i).getLine()+"";
			title = new String(title.replaceAll(" ", "")) ;
			content = new String(content.replaceAll(" ", "")) ;
			
			model  = new SourceModel();
			
			model.setIndex((i+1)+"");
			model.setNumber(index);
			
			model.setTitle(title);
			model.setContent(content);
			ans = df.format(DataSet.cos.get(i).getScore()).toString();
			if(ans.charAt(0)=='.') ans = '0' +ans;

			model.setScore(ans);

			model.setSelect(false);
			model.setInActive(false);
			sourceList.add(model);		
		}

		long end = System.currentTimeMillis();
		System.out.println("getResult: "+(end-start)+"ms");
	}

}
