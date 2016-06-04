package buaa.liuyun;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JOptionPane;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.apache.solr.client.solrj.SolrServerException;

import buaa.define.Node.Node;
import buaa.liuyun.GetIdf;
import buaa.liuyun.SearchNode;
import buaa.solr.Actuary;
import buaa.solr.CilStw;
import buaa.solr.Solr;
public class KeyWords {

	public static HashMap<String,Double> queryMap = new HashMap<String,Double>();
	private static List[] setTerm = new ArrayList[3];
	public static void getKeyWords(String[]keyString,int []kind) throws IOException, SolrServerException{
		for(int i=0;i<3;i++)	setTerm[i] = new ArrayList();
		
		queryMap.clear();
		if(!getQuery(keyString)){
//			JOptionPane.showMessageDialog(null, "未找到相关内容,请重新输入...", "alert", JOptionPane.ERROR_MESSAGE);
			return ;
		}
		Iterator it = queryMap.keySet().iterator();
		while(it.hasNext()){
			String ss = it.next().toString();
			System.out.println(ss+"   "+queryMap.get(ss));
		}
		Solr.Search(setTerm,kind);
	}

	public static boolean getQuery(String []keyString) throws SolrServerException, IOException{
		System.out.println("Running KeyWords.getQuery()......");
		
		List tp = new ArrayList();
    	String item = null ,tyc = null;
    	String codeString = null;
		for(int i=0;i<3;i++){
			if(keyString[i]=="") continue;
			List<Term> parse = BaseAnalysis.parse(keyString[i]);
			for (Term term: parse) {  
				tp = new ArrayList();
				item = term.getName().trim();
				if(GetIdf.wordDoc.containsKey(item)){
					queryMap.put(item,GetIdf.idf.get(item));
					tp.add(item) ; 
				}
				if(!tp.isEmpty())
					setTerm[i].add(tp);
			}
		}
		if(setTerm[0].isEmpty()&&setTerm[1].isEmpty()&&setTerm[2].isEmpty())
			return false;
		return true;
	}
}
class CosQueryDoc  {
	public int line;
	public double score;
	public CosQueryDoc(int line, double score) {
		this.line = line;
		this.score = score;
	}
	public int getLine(){
		return line;
	}
	public double getScore(){
		return score;
	}
}
class Compare implements Comparator<CosQueryDoc>{

	@Override
	public int compare(CosQueryDoc tp1, CosQueryDoc tp2) {
		// TODO Auto-generated method stub
		if( tp1.score > tp2.score )	return -1;
		else return 1;
	}
}


