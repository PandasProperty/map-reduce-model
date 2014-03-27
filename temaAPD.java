import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * 	Anda Nenu
 *  334CA
 */

public class temaAPD {

	public static void main(String[] args) {

		int nrThreads = Integer.parseInt(args[0]);
		String f_in = args[1];
		String f_out = args[2];
		
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(f_in));
			
			String sursa = in.readLine();
			long dim = Integer.parseInt(in.readLine());
			float similaritateMax = Float.parseFloat(in.readLine());			
			int nrDocumente = Integer.parseInt(in.readLine());			
			String documente[] = new String[nrDocumente];
			
			int checkDocument = -1;
			for (int i=0;i<nrDocumente;i++){
				documente[i] = in.readLine();
				if (documente[i].compareTo(sursa)==0){
					checkDocument = i;
				}
			}
			in.close();
			
			for (int i=0;i<nrDocumente;i++){
				ProcSimilar.nrCuvinte.put(documente[i], 0);
				ProcSimilar.MapFile.put(documente[i], new ConcurrentHashMap<String,Integer>());
			}
			
			ExecutorService executor1 = Executors.newFixedThreadPool(nrThreads);

			long startChar = 0;
			int crtDoc = 0;
			ProcSimilar proc = null;
			while (true){ 
				proc = new ProcSimilar(documente[crtDoc], startChar, startChar + dim);
				executor1.execute(proc);
				startChar += dim;
				RandomAccessFile f = new RandomAccessFile(documente[crtDoc],"r");	
				if (startChar>=f.length()){
					f.close();
					startChar = 0;
					crtDoc++;
					if (crtDoc>=nrDocumente){
						break;
					}
				}
				else {
					f.close();
				}
				
			}
			executor1.shutdown();
			while (!executor1.isTerminated()){
			}
			
			/*
			for (String key : ProcSimilar.MapFile.keySet()){
				System.out.println(key + " : " +  ProcSimilar.MapFile.get(key));
			}
			
			for (String key : ProcSimilar.nrCuvinte.keySet()){
				System.out.println(key + " : " +  ProcSimilar.nrCuvinte.get(key));
			}*/
		
			ExecutorService executor2 = Executors.newFixedThreadPool(nrThreads);
			calculateSim similar = null;
			
			for (crtDoc=0;crtDoc<nrDocumente;crtDoc++){
				if (crtDoc==checkDocument)
					crtDoc ++;
				similar = new calculateSim(
						ProcSimilar.MapFile.get(documente[checkDocument]),
						ProcSimilar.MapFile.get(documente[crtDoc]),
						ProcSimilar.nrCuvinte.get(documente[checkDocument]),
						ProcSimilar.nrCuvinte.get(documente[crtDoc]),
						documente[crtDoc]);
				executor2.execute(similar);
			}
			
			executor2.shutdown();
			while (!executor2.isTerminated()){
			}
			
			
			BufferedWriter out = new BufferedWriter(new FileWriter(f_out));
			out.write("Rezultate pentru: ("+documente[checkDocument]+")\n");
			out.write("\n");
		
			for (int i=0;i<nrDocumente;i++){
				if (i!=checkDocument){
					float f = calculateSim.frecvente.get(documente[i]);
					String trunc = String.format("%.4f",f);
					trunc = trunc.substring(0, trunc.length()-1);
					//System.out.print((documente[i]+" ("+trunc+"%)\n"));
					if (f>=similaritateMax){
						out.write(documente[i]+" ("+trunc+"%)\n");
					}
				}
			}
			
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
