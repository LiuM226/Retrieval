package buaa.servlet;

import java.io.IOException;

import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.solr.client.solrj.SolrServerException;

import buaa.gui.SourceModel;
import buaa.liuyun.DataSet;
import buaa.liuyun.Feedback;
import buaa.liuyun.GetIdf;
import buaa.liuyun.OrderResult;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class Reback
 */
@WebServlet("/Retrieval/Reback")
public class Reback extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String CONTENT_TYPE="text/html;charset=UTF-8";
	public static HashSet<Integer> relevant = new HashSet<Integer>();
	public static HashSet<Integer> unRelevant = new HashSet<Integer>();
	public static List<SourceModel> sourceList = new ArrayList<SourceModel>();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Reback() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		doGet(request, response);
		response.setContentType(CONTENT_TYPE);
		PrintWriter out =response.getWriter();
		Enumeration e=request.getParameterNames();
		HttpSession session = request.getSession();
		
		if(DataSet.INDEX_VISIT==GetIdf.FIRST_QUERY){
			relevant.clear();
			unRelevant.clear();
		}
		
		String str=request.getParameter("number");
		int num = Integer.parseInt(str);
		
		String value=null;
		int index , number;
		for(int i=0;i<num;i++){
			value=request.getParameter("radio"+i);
			if(value==null||value.isEmpty())	continue;
			
			index = Integer.parseInt(value);
			number = Integer.parseInt(request.getParameter("number"+i));
			if(index==1){
				relevant.add(number);
	    		if(unRelevant.contains(number))
	        		unRelevant.remove(number);
			}
			if(index==0){
				unRelevant.add(number);
				if(relevant.contains(number))
					relevant.remove(number);
			}
			
		}
		try {
			Feedback.feedBack();
		} catch (SolrServerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JSONArray array = JSONArray.fromObject(sourceList);
		JSONObject fre = new JSONObject();
		fre.put("freWords", OrderResult.wordFre);
		array.add(fre);
		out.println(array);
	}
	public static void setData(List<SourceModel> list){
		 sourceList = list;
	}

}
