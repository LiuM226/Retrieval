package buaa.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.solr.client.solrj.SolrServerException;

import buaa.gui.SourceModel;
import buaa.liuyun.KeyWords;
import buaa.liuyun.OrderResult;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class Search
 */
@WebServlet("/Retrieval/Search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static List<SourceModel> sourceList = new ArrayList<SourceModel>();
	public static final String CONTENT_TYPE="text/html;charset=UTF-8";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Search() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
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
		sourceList.clear();
		
		String []keyString = new String[3];
		int []kind = new int[3];
		
		keyString[0]=request.getParameter("keywords1");
		keyString[1]=request.getParameter("keywords2");
		keyString[2]=request.getParameter("keywords3");
		
//		表单直接提交的时候用下面转换编码
//		keyString[0] = new String(keyString[0].getBytes("iso-8859-1"),"UTF-8");
//		keyString[1] = new String(keyString[1].getBytes("iso-8859-1"),"UTF-8");
//		keyString[2] = new String(keyString[2].getBytes("iso-8859-1"),"UTF-8");

//		用ajax提交时用的转换编码方法
		keyString[0] = URLDecoder.decode(keyString[0], "UTF-8");
		keyString[1] = URLDecoder.decode(keyString[1], "UTF-8");
		keyString[2] = URLDecoder.decode(keyString[2], "UTF-8");
		
		kind[0] = 0;
		kind[1] = Integer.parseInt(request.getParameter("link2"));
		kind[2] = Integer.parseInt(request.getParameter("link3"));

		
		long start = System.currentTimeMillis();
		System.out.println("_____________________"+keyString[0]+"  "+keyString[1]);
		try {
			KeyWords.getKeyWords(keyString,kind);
		} catch (SolrServerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		session.setAttribute("data", sourceList);
//		request.getRequestDispatcher("/interFace.jsp").forward(request, response);
		long end = System.currentTimeMillis();
		System.out.println("运行时间：" +(end-start));
		System.out.println("________________________________________________________________");
		
		
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
