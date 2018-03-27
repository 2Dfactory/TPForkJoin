package general;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ForkJoinPool;

public class Main {

	public static void main(String[] args) {

		Path chemin = Paths.get("C:\\Users\\vzlskl\\Documents");
		String filtre = "*.psd";
		FolderScanner fs = new FolderScanner(chemin, filtre);
		
		int processeurs = Runtime.getRuntime().availableProcessors();
		
		ForkJoinPool pool = new ForkJoinPool(processeurs);

		Long start = System.currentTimeMillis();
		
		pool.invoke(fs);
		
		
		//Long resultat = fs.sequentialScan();
		Long end = System.currentTimeMillis();
		
		System.out.println("Il y a " + fs.getResultat() + "fichier(s) portant l'extension " + filtre);
		System.out.println("Temps de traitement : " + (end-start) + "ms.");
						
		System.out.println("Nbre de processeurs : " + processeurs);	
		
		
		
		
		
	}

}
