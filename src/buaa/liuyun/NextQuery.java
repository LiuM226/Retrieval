package buaa.liuyun;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import buaa.servlet.Reback;
import buaa.solr.Solr;
import buaa.solr.WeightSquare;

public class NextQuery {
	public static double vr_square,vi_square;
	public static Iterator it;
	public static HashMap<Integer,Double> rescore = new HashMap<Integer,Double>();
	public static void clear(){
		rescore.clear();
	}
	public static void square(){
		System.out.println("Running NextQuery.square()......");	
		String cs = null;
		double temp = 0 ;
		vr_square = 0 ; vi_square = 0;
		it = Feedback.relevant_vector.keySet().iterator();
		while(it.hasNext()){
			cs = it.next().toString();
			temp = Feedback.relevant_vector.get(cs)/Reback.relevant.size();
			vr_square += temp*temp;
		}
		
		it = Feedback.unRelevant_vector.keySet().iterator();
		while(it.hasNext()){
			cs = it.next().toString();
			temp = Feedback.unRelevant_vector.get(cs)/Reback.unRelevant.size();
			vi_square += temp*temp;
		}
	}
	public static void Rescore() throws FileNotFoundException{
		System.out.println("Running NextQuery.Rescore()......");	
		clear();
		square();
		
		String cs = null;
		int index;
		HashMap<String,Double> hash = new HashMap<String,Double>();
		Iterator iter = null ;
		double temp ;
		double fr ,fn;
		double simr , simn;
		it = DataSet.hashSet.iterator();
		while(it.hasNext()){
			index = Integer.parseInt(it.next().toString());
			hash = WeightSquare.weight.get(index);
			fr = 0 ; fn = 0; simr = 0; simn = 0;
			iter = hash.keySet().iterator();
			while(iter.hasNext()){
				cs = iter.next().toString();
				if(Feedback.relevant_vector.containsKey(cs)){
					temp = Feedback.relevant_vector.get(cs)/Reback.relevant.size();
					fr += temp*hash.get(cs);
				}
				if(Feedback.unRelevant_vector.containsKey(cs)){
					temp = Feedback.unRelevant_vector.get(cs)/Reback.unRelevant.size();
					fn +=temp*hash.get(cs);
				}
			}
			if(vr_square!=0){
				temp = Math.sqrt(vr_square)*Math.sqrt(WeightSquare.doc_square.get(index));
				simr  = 1.0*fr / temp ;
			}
			if(vi_square!=0){
				temp = Math.sqrt(vi_square)*Math.sqrt(WeightSquare.doc_square.get(index));
				simn = 1.0*fn / temp ;
			}
			rescore.put(index, simr - simn);
		}
		result_score();
	}
	public static void result_score(){
		System.out.println("Running NextQuery.result_score()......");	
		int index;
		double fscore = 0 , result_score = 0 ;
		it = DataSet.hashSet.iterator();
		while(it.hasNext()){
			fscore = 0 ;  result_score = 0;  
			index = Integer.parseInt(it.next().toString());
			if(DataSet.dis.containsKey(index))   
				fscore += 0.5*DataSet.dis.get(index);
			if(rescore.containsKey(index))   
				fscore += 0.5*rescore.get(index);
//			System.out.println(DataSet.dis.get(index)+"  "+rescore.get(index));
			
			result_score = 0.5*fscore ;
			if(DataSet.score.containsKey(index))
			      result_score += 0.5*DataSet.score.get(index);
//			result_score = fscore;
			
			if(result_score >= 0 ){
				DataSet.cos.add(new CosQueryDoc(index,result_score));
			} 		
			DataSet.score.put(index, result_score);
		}
		clear();
	}
}
