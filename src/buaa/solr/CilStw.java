package buaa.solr;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CilStw {
	//��ŵ����Դ�Ϊkey���Ըôʵı���Ϊvalues��List���ϣ�����һ���ʿ��ܻ��ж������
	 public static HashMap<String,ArrayList<String>> wordsEncode = new HashMap<String,ArrayList<String>>();
	//��ŵ����Ա���Ϊkey���Ըñ�����Ӧ�Ĵ�Ϊvalues��List���ϣ�����һ��������ܻ��ж����
	 public static HashMap<String,ArrayList<String>> encodeWords = new HashMap<String,ArrayList<String>>();
	 public static HashSet<String> stopWords = new HashSet<String>();
	 private static BufferedReader br_cilin ;
	 private static BufferedReader br_stopword;
	 private static BufferedReader cilin;
	 private static BufferedReader stopword;
	 private static void init() throws FileNotFoundException, UnsupportedEncodingException{
		 String cilin = CilStw.class.getClassLoader().getResource("../../cilin.txt").getPath();
		 String stopword = CilStw.class.getClassLoader().getResource("../../stopwords.txt").getPath();
		 
		 cilin = URLDecoder.decode(cilin,"utf-8");
		 stopword = URLDecoder.decode(stopword,"utf-8");
		 br_cilin = new BufferedReader(new InputStreamReader(new FileInputStream(cilin), "gbk"));
		 br_stopword = new BufferedReader(new InputStreamReader(new FileInputStream( stopword), "gbk"));
	 }
	 private static void readCiLin() throws IOException {
        String content = null;
        List<String> contents = null;
        while((content=br_cilin.readLine())!=null){
        	String[] strs = content.split(" ");
            String encode = null;
            int length = strs.length;
            if (length > 1) {
                encode = strs[0];//��ȡ����
            }
            ArrayList<String> encodeWords_values = new ArrayList<String>();
            for (int i = 1; i < length; i++) {
                encodeWords_values.add(strs[i]);
            }
            encodeWords.put(encode, encodeWords_values);//�Ա���Ϊkey���������ֵΪvalue
            for (int i = 1; i < length; i++) {
                String key = strs[i];
                if (wordsEncode.containsKey(strs[i])) {
                    ArrayList<String> values = wordsEncode.get(key);
                    values.add(encode);
                    //���·��û�ȥ
                    wordsEncode.put(key, values);//��ĳ��valueΪkey������ܵ����б���Ϊvalue
                } else {
                    ArrayList<String> temp = new ArrayList<String>();
                    temp.add(encode);
                    wordsEncode.put(key, temp);
                }
            }
        }
    }
	 public static void readStopWord() throws IOException{
		 String word = null;
		 while((word = br_stopword.readLine())!=null){
			 word.trim();
			 stopWords.add(word);
		 }
	 }
	 public static void CilinStopword() throws IOException{
		 System.out.println("Running CilStw.CilinStopword()...");
		 init();
		 readCiLin();
		 readStopWord(); 

	 }
	 public static boolean isStopWord(String word){
		 if(stopWords.contains(word))
			 return true;
		 else return false;
	 }
}
