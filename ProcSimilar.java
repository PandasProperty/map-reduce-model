import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/*
 *  Anda Nenu
 *  334CA
 */

public class ProcSimilar implements Runnable {

	static Map<String,Map<String,Integer>> MapFile = Collections.synchronizedMap(new HashMap<String,Map<String,Integer>>());
	
	private Map<String,Integer> map;
	
	static Map<String,Integer> nrCuvinte = Collections.synchronizedMap(new HashMap<String,Integer>());
	
	String fileName ;
	long startRead; 
	long endRead;
	
	public ProcSimilar(String fileName, long startRead, long endRead) {
		map = new HashMap<String,Integer>();
		this.fileName = fileName;
		this.startRead = startRead;
		this.endRead = endRead;	
	}
	
	public void run() {
		try {
			RandomAccessFile in = new RandomAccessFile(fileName,"r");

			String word = "";
			char c=' ';
			
			if (startRead<in.length()){
				in.seek(startRead);
				c = (char) in.read();
				c = Character.toLowerCase(c);
				startRead ++;
				if (c>='a' && c<='z'){
					
					if (startRead>0){
						in.seek(startRead-1);
						c = (char) in.read();
						c = Character.toLowerCase(c);
						if (c>='a' && c<='z'){
							while (c>='a' && c<='z'){
								c = (char) in.read();
								c = Character.toLowerCase(c);
								startRead ++;
							}
						}
						else {
							word += c;
						}
					}
				}
			}

			while (startRead<endRead && startRead<in.length()){
				while ((c<'a' || c>'z') && (startRead<endRead)){
					c = (char) in.read();
					c = Character.toLowerCase(c);
					startRead ++;
				}
				if (startRead<endRead){
					while (c>='a' && c<='z' && startRead<in.length()){
						word += c;
						c = (char) in.read();
						c = Character.toLowerCase(c);
						startRead ++;
					}
					
					if (map.containsKey(word)){
						int l = (int) map.get(word);
						map.put(word,l+1);
					}
					else {
						map.put(word,1);
					}
					synchronized (nrCuvinte){
						int cuv = nrCuvinte.get(fileName)+1;
						nrCuvinte.put(fileName, cuv);
					}
					word = "";
				}
			}
			
			synchronized (MapFile) {
				Map<String,Integer> aux = MapFile.get(fileName);
				for (String key : map.keySet() ){
					if (aux.containsKey(key)){
						int l = aux.get(key) + map.get(key);
						aux.put(key,l);
					} 
					else{
						aux.put(key,map.get(key));
					}
				}
				MapFile.remove(fileName);
				MapFile.put(fileName,aux);
			}
			
			
			
			
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		map.clear();
	}

}
