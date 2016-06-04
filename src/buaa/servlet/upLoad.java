package buaa.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.JOptionPane;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;

import com.jspsmart.upload.SmartUpload;

import buaa.gui.CutWords;

/**
 * Servlet implementation class upLoad
 */
@WebServlet("/Retrieval/upLoad")
public class upLoad extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	public static long start,end;
    public upLoad() {
        super();
        // TODO Auto-generated constructor stub
    }
		 
    public void destroy() {
		super.destroy();
	}
		 
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
			
	}
		 
	public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		System.out.println("Running upLoad.doPost()....");
		String CONTENT_TYPE = "text/html; charset=GBK";
		response.setContentType(CONTENT_TYPE);
		//�ļ��ϴ�����
		int count = 0;
		 //�ļ��ϴ���ַ
		String filePath = getServletContext().getRealPath("/")+ new String("Retrieval/test");
		filePath = filePath.replace("\\", "/");
		PrintWriter out = response.getWriter();
		//����ļ��в����� �򴴽�����ļ���
		
		
		File file = new File(filePath);
		if(!file.exists()){
			file.mkdir();
		}
			 //��ʼ������
		SmartUpload su = new SmartUpload();
	    su.initialize(getServletConfig(),request,response);
		
	    //�����ļ��������
	    su.setMaxFileSize(50*1024*1024);
		//���������ļ��������
		su.setTotalMaxFileSize(500*1024*1024);
	   //�����ϴ��ļ�����
		su.setAllowedFilesList("rar,zip,txt");
		try {
		   //���ý�ֹ�ϴ����ļ�����
			su.setDeniedFilesList("jsp,js,html,css");
		   //�ϴ��ļ�
			su.upload();
			count = su.save(filePath);
			String fileName=su.getFiles().getFile(0).getFileName();
			fileName = filePath + "/"+fileName;

			out.println("<script>parent.dealFile()</script>");
			
			start = System.currentTimeMillis();
			
			if(fileName.endsWith(".txt")){
				CutWords.cutWords(fileName);
			}
			else{//      �ļ���ѹ
				fileName = fileName.replace("\\", "/");
				filePath = filePath.replace("\\", "/")+"/decode";
				//���decode�ļ��в����� �򴴽�����ļ���
				file = new File(filePath);
				if(!file.exists()){
					file.mkdir();
				}
				
				unZipFiles(new File(fileName),filePath+"/");

				CutWords.cutWords(filePath);
			} 
			end = System.currentTimeMillis();
			System.out.println("����ʱ�䣺 "+ (end-start));
			out.println("<script>parent.callback('�ļ��ϴ��ɹ� ��')</script>");
		} catch (Exception e) {
			e.printStackTrace();
			out.println("<script>parent.callback('�ļ��ϴ�ʧ�� ���������ϴ�...')</script>");
		} 
			 
	}
		 
	public void init() throws ServletException {
		  // Put your code here
	}
	public static void unZipFiles(File zipFile,String descDir)throws IOException{  
        File pathFile = new File(descDir);
        if(pathFile.exists()){
        	deleteDirectory(descDir);
        }
        if(!pathFile.exists()){  
            pathFile.mkdirs();  
        }  
        ZipFile zip = new ZipFile(zipFile);  
        for(Enumeration entries = zip.entries();entries.hasMoreElements();){  
            ZipEntry entry = (ZipEntry)entries.nextElement();  
            String zipEntryName = entry.getName();  
            InputStream in = zip.getInputStream(entry);  
            String outPath = (descDir+zipEntryName).replaceAll("\\*", "/");;  
            //�ж�·���Ƿ����,�������򴴽��ļ�·��  
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));  
            if(!file.exists()){  
                file.mkdirs();  
            }  
            //�ж��ļ�ȫ·���Ƿ�Ϊ�ļ���,����������Ѿ��ϴ�,����Ҫ��ѹ  
            if(new File(outPath).isDirectory()){  
                continue;  
            }  
            //����ļ�·����Ϣ                
            OutputStream out = new FileOutputStream(outPath);  
            byte[] buf1 = new byte[1024];  
            int len;  
            while((len=in.read(buf1))>0){  
                out.write(buf1,0,len);  
            }  
            in.close();  
            out.close();  
         }  

    }
	//ɾ���ļ���
	public static boolean deleteDirectory(String sPath) {  
	    //���sPath�����ļ��ָ�����β���Զ�����ļ��ָ���  
	    if (!sPath.endsWith(File.separator)) {  
	        sPath = sPath + File.separator;  
	    }  
	    File dirFile = new File(sPath);  
	    //���dir��Ӧ���ļ������ڣ����߲���һ��Ŀ¼�����˳�  
	    if (!dirFile.exists() || !dirFile.isDirectory()) {  
	        return false;  
	    }  
	    boolean flag = true;  
	    //ɾ���ļ����µ������ļ�(������Ŀ¼)  
	    File[] files = dirFile.listFiles();  
	    for (int i = 0; i < files.length; i++) {  
	        //ɾ�����ļ�  
	        if (files[i].isFile()) {  
	            flag = deleteFile(files[i].getAbsolutePath());  
	            if (!flag) break;  
	        } //ɾ����Ŀ¼  
	        else {  
	            flag = deleteDirectory(files[i].getAbsolutePath());  
	            if (!flag) break;  
	        }  
	    }  
	    if (!flag) return false;  
	    //ɾ����ǰĿ¼  
	    if (dirFile.delete()) {  
	        return true;  
	    } else {  
	        return false;  
	    }  
	}
	//ɾ���ļ�
	public static boolean deleteFile(String sPath) {  
	    boolean flag = false;  
	    File file = new File(sPath);  
	    // ·��Ϊ�ļ��Ҳ�Ϊ�������ɾ��  
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	        flag = true;  
	    }  
	    return flag;  
	} 
}
