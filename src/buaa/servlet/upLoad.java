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
		//文件上传个数
		int count = 0;
		 //文件上传地址
		String filePath = getServletContext().getRealPath("/")+ new String("Retrieval/test");
		filePath = filePath.replace("\\", "/");
		PrintWriter out = response.getWriter();
		//如果文件夹不存在 则创建这个文件夹
		
		
		File file = new File(filePath);
		if(!file.exists()){
			file.mkdir();
		}
			 //初始化对象
		SmartUpload su = new SmartUpload();
	    su.initialize(getServletConfig(),request,response);
		
	    //设置文件最大容量
	    su.setMaxFileSize(50*1024*1024);
		//设置所有文件最大容量
		su.setTotalMaxFileSize(500*1024*1024);
	   //设置上传文件类型
		su.setAllowedFilesList("rar,zip,txt");
		try {
		   //设置禁止上传的文件类型
			su.setDeniedFilesList("jsp,js,html,css");
		   //上传文件
			su.upload();
			count = su.save(filePath);
			String fileName=su.getFiles().getFile(0).getFileName();
			fileName = filePath + "/"+fileName;

			out.println("<script>parent.dealFile()</script>");
			
			start = System.currentTimeMillis();
			
			if(fileName.endsWith(".txt")){
				CutWords.cutWords(fileName);
			}
			else{//      文件解压
				fileName = fileName.replace("\\", "/");
				filePath = filePath.replace("\\", "/")+"/decode";
				//如果decode文件夹不存在 则创建这个文件夹
				file = new File(filePath);
				if(!file.exists()){
					file.mkdir();
				}
				
				unZipFiles(new File(fileName),filePath+"/");

				CutWords.cutWords(filePath);
			} 
			end = System.currentTimeMillis();
			System.out.println("处理时间： "+ (end-start));
			out.println("<script>parent.callback('文件上传成功 ！')</script>");
		} catch (Exception e) {
			e.printStackTrace();
			out.println("<script>parent.callback('文件上传失败 ！请重新上传...')</script>");
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
            //判断路径是否存在,不存在则创建文件路径  
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));  
            if(!file.exists()){  
                file.mkdirs();  
            }  
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压  
            if(new File(outPath).isDirectory()){  
                continue;  
            }  
            //输出文件路径信息                
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
	//删除文件夹
	public static boolean deleteDirectory(String sPath) {  
	    //如果sPath不以文件分隔符结尾，自动添加文件分隔符  
	    if (!sPath.endsWith(File.separator)) {  
	        sPath = sPath + File.separator;  
	    }  
	    File dirFile = new File(sPath);  
	    //如果dir对应的文件不存在，或者不是一个目录，则退出  
	    if (!dirFile.exists() || !dirFile.isDirectory()) {  
	        return false;  
	    }  
	    boolean flag = true;  
	    //删除文件夹下的所有文件(包括子目录)  
	    File[] files = dirFile.listFiles();  
	    for (int i = 0; i < files.length; i++) {  
	        //删除子文件  
	        if (files[i].isFile()) {  
	            flag = deleteFile(files[i].getAbsolutePath());  
	            if (!flag) break;  
	        } //删除子目录  
	        else {  
	            flag = deleteDirectory(files[i].getAbsolutePath());  
	            if (!flag) break;  
	        }  
	    }  
	    if (!flag) return false;  
	    //删除当前目录  
	    if (dirFile.delete()) {  
	        return true;  
	    } else {  
	        return false;  
	    }  
	}
	//删除文件
	public static boolean deleteFile(String sPath) {  
	    boolean flag = false;  
	    File file = new File(sPath);  
	    // 路径为文件且不为空则进行删除  
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	        flag = true;  
	    }  
	    return flag;  
	} 
}
