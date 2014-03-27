import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/*
 * 	Anda Nenu
 * 	334CA
 */

public class calculateSim implements Runnable{

	static Map<String,Float> frecvente = Collections.synchronizedMap(new HashMap<String,Float>());
	
	private Map<String,Integer> map1 = new Hashtable<String,Integer>();
	private Map<String,Integer> map2 = new Hashtable<String,Integer>();
	
	int nr1;
	int nr2;
	
	String filename;
	
	calculateSim(Map<String, Integer> m1,Map<String, Integer> m2, int nr1, int nr2, String filename){		
		this.filename = filename;
		map1 = m1;
		map2 = m2;
		this.nr1 = nr1;
		this.nr2 = nr2;
	}
	
	public void run() {
		float sum = 0;
		for (String key : map1.keySet()){
			if (map2.containsKey(key)){ 
				float f1 = (float) 100*map1.get(key)/nr1;
				float f2 = (float) 100*map2.get(key)/nr2;
				sum += (f1*f2)/100;
			}
		}
		synchronized (frecvente) {
			frecvente.put(filename, sum);
		}
		
	}

}
