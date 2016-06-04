package buaa.solr;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;

import buaa.gui.CutWords;
import buaa.liuyun.GetIdf;

public class Solr {
	public static SolrDocumentList results;
	public static void init() throws IOException{
		CilStw.CilinStopword();
	}
	public static void Insert() throws IOException, SolrServerException {
		System.out.println("Running Solr.Insert()......");
		String url = "http://localhost:8080/solr";
		SolrServer  server = new HttpSolrServer(url);
		deleteAllIndex(server);	server.commit();
		
		SolrInputDocument doc = null;
		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		
		String title = null  , article = null ;
		HashSet<Integer> set = new HashSet<Integer>();
		int index = 0;
//		while((title = br.readLine())!=null){
		for(int k =0;k<CutWords.infor.size();k++){
			
			title = CutWords.infor.get(k).getTitle();
			
			article = CutWords.infor.get(k).getContent();
			 
			index ++;
			
			if(article.length()>10000)	article = article.substring(0,10000);
			doc = new SolrInputDocument();
			doc.addField("id", index);
			
			doc.addField("title_ss", title);
			doc.addField("article_ss", article);
			doc.addField("index_i", index);
			docs.add(doc);
			if(index%10000==0){
				server.add(docs);
				server.commit();
				System.out.println(index);
				docs.clear();
			}
		}
		
		server.add(docs);
		server.commit();
		System.out.println(index);
		docs.clear();
		
//		br.close();
	}
	public static void Search(List []setTerm,int []kind) throws SolrServerException, IOException {
		System.out.println("Running Solr.Search()......");
		
		
		HttpSolrServer solr = new HttpSolrServer("http://localhost:8080/solr");
		SolrQuery query = new SolrQuery();
		String key = null;
		String querySentence = "";
		
		String []part = new String[3];
		String []link = {"AND","OR","NOT"};
		List setList = new ArrayList<String>();
		long start = System.currentTimeMillis();
		
		for(int k = 0; k < 3 ;k++){
			if(setTerm[k].isEmpty())	continue;
			for(int v = 0; v < setTerm[k].size(); v++){
				key = setTerm[k].get(v).toString();
				key = key.substring(1,key.length()-1);
				if(part[k] == null) {
					part[k] = "";
					part[k] = "title_ss:"+"*"+key+"*" + " OR " + "article_ss:"+"*"+key+"*";
				}
				else part[k] += " OR "+"title_ss:"+"*"+key+"*" + " OR " + "article_ss:"+"*"+key+"*";
			}

		}
		for(int i=0;i<3;i++){
			if(part[i]==null) continue;
			if(querySentence=="")	querySentence += "("+part[i]+")";
			else querySentence += " "+link[kind[i]]+" "+"("+part[i]+")";
			
		}
//		if(setTerm.size()==1){
//			str = part[0];
//			query.setQuery(part[0]);
//			System.out.println(str);
//		}
//		else{
//			for(int i = 0 ; i < setTerm.size()-1 ; i++){
//				if(str == "") str += "("+part[i]+")" ;
//				else str += " AND " + "("+part[i]+")";
//			}
//			query.addFilterQuery(str);
//			query.setQuery(part[setTerm.size()-1]);
//			System.out.println(str + " AND " + "(" + part[setTerm.size()-1] + ")");
//		}
		
		query.setQuery(querySentence);
		System.out.println(querySentence);
		
		query.setRows(1000000);
		query.addSort(new SortClause("index_i",ORDER.asc));  //排序
		
		QueryResponse response = solr.query(query);
		results = response.getResults();
		solr.commit();
		
		long end = System.currentTimeMillis();
		System.out.println("查询时间：" + (end - start)+ " ms");
		Actuary.Deal();
	}
	//删除所有索引......
	public static void deleteAllIndex(SolrServer server){
        try {
            server.deleteByQuery("*:*");
            server.commit(false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	public static void SolrBegin() throws IOException, SolrServerException{
		init();
		Insert();
		GetIdf.Tidf();
	}
}
