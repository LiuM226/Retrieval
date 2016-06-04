package buaa.gui;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrServerException;

import buaa.define.Node.News;
import buaa.solr.Solr;

public class CutWords{
	private static Scanner in_put ;
	public static List<News> infor = new ArrayList<News>();

	public static void cutWords(String filePath) throws IOException, InterruptedException, SolrServerException{
		System.out.println("CutWords.cutWords()...");
		System.out.println(filePath);
		infor.clear();
		
	    findFile(filePath);
	    
	    threadStart();
        in_put.close();
        Solr.SolrBegin();
	}
	private static void threadStart() throws InterruptedException {
		// TODO Auto-generated method stub
		int len = infor.size();
		int part,yu;
		// 多线程分词

		if(len>100){	
			part = len/5; yu = len%5;
			MyThread []mt = new MyThread[6];
			Thread []th = new Thread[6];
			
			mt[0] = new MyThread(0,part-1);
			mt[1] = new MyThread(part,2*part-1);
			mt[2] = new MyThread(2*part,3*part-1);
			mt[3] = new MyThread(3*part,4*part-1);
			mt[4] = new MyThread(4*part,5*part-1);
			if( yu > 0 )  mt[5] = new MyThread(5*part,5*part+yu-1);
			
			th[0] = new Thread(mt[0]);
			th[1] = new Thread(mt[1]);
			th[2] = new Thread(mt[2]);
			th[3] = new Thread(mt[3]);
			th[4] = new Thread(mt[4]);
			if( yu > 0 )  th[5] = new Thread(mt[5]);
			
			th[0].start();  th[1].start();  th[2].start();
			th[3].start();  th[4].start();  
			if( yu > 0 )  th[5].start();
			
			th[0].join();	 th[1].join();  th[2].join();
			th[3].join();	 th[4].join();  
			if( yu > 0)  th[5].join();
		}
		else{
			MyThread mt1 = new MyThread(0,len-1);
			Thread th1 = new Thread(mt1);
			th1.start();
			th1.join();
		}
	}
	public static void Ansj(int a,int b){
		String []article = new String[2];
		String title = null , content = null;
		String res = "";
		for( int i = a; i <= b; i ++ ){
			article[0] = infor.get(i).getTitle();
			article[1] = infor.get(i).getContent();
			title = "" ; content = "";
			for(int j = 0; j <= 1; j++){
				List<Term> parse = BaseAnalysis.parse(article[j]);
				res = "";
				for (Term term: parse) {  
					String item = term.getName().trim();
					if(item==" ") continue;
					if(res!="") res+=" "+item.trim();
					else res+=item.trim();
				}
				if(j==0)	title = res;
				else if(j==1) content = res;
			}
			infor.get(i).setTitle(title);
			infor.get(i).setContent(content);
		}
	}
	public static void readFiles(File file) throws IOException{
		String title = null,content = null;
		List<String> txt = FileUtils.readLines(file,Charset.forName("GBK"));
		for(int i = 0; i < txt.size(); i += 2){
			title = txt.get(i);
			
			title = title.replaceAll(" ", "");
		    if (title.isEmpty()){ i--; continue;}
		    
		    content = txt.get(i+1);
		    content = content.replaceAll(" ", "");
		    
		    infor.add(new News(title,content));
		}
		txt.clear();
	}
	public static void findFile(String path) throws IOException{
		File file = new File(path);
		if(!file.isDirectory()){ 	
//			System.out.println(file.getName());
			in_put = new Scanner(file);
			try {
				readFiles(file);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ;
		}
		File fa[] = file.listFiles();
		File fs = null;
		for(int i = 0 ; i < fa.length ; i++){
			findFile(path+"/"+fa[i].getName());
		}
	}
}
//多线程分词
class MyThread implements Runnable{
    int start,end;
    public MyThread(int a,int b){
    	start = a;
    	end = b;
    }
    public void run() {
    	CutWords.Ansj(start,end);
    }
}