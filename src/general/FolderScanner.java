package general;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class FolderScanner extends RecursiveTask<Long>{

	private Path path = null;
	private String filter = "*";
	private long result = 0;
	
	public FolderScanner() {	}
	
	public FolderScanner(Path p, String f) {
		path = p;
		filter = f;
	}
	
	public long sequentialScan() throws ScanException{
		
		if(path == null || path.equals("")) throw new ScanException("Chemin à scanner non valide (vide ou null) !");
		
		System.out.println("Scan du dossier : " + path + " à la recherche des fichiers portant l'extension " + filter);
		
		try(DirectoryStream<Path> listing = Files.newDirectoryStream(path)){
			
			for(Path nom : listing) {
				if(Files.isDirectory(nom.toAbsolutePath())) {
					FolderScanner f = new FolderScanner(nom.toAbsolutePath(), filter);
					result += f.sequentialScan();
				}
			}
		
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		try(DirectoryStream<Path> listing = Files.newDirectoryStream(path, filter)){
			
			for(Path nom : listing) {
				result++;
			}
			
		}
		catch(IOException ioe) {
			ioe.printStackTrace();			
		}
		
		return result;
		
	}

	public Long parallelScan() throws ScanException {
		
		List<FolderScanner> list = new ArrayList<>();
		
		if(path == null || path.equals("")) throw new ScanException("Chemin à scanner non valide (vide ou null) !");
		
		System.out.println("Scan du dossier : " + path + " à la recherche des fichiers portant l'extension " + filter);
		
		try(DirectoryStream<Path> listing = Files.newDirectoryStream(path)){
			
			for(Path nom : listing) {
				if(Files.isDirectory(nom.toAbsolutePath())) {
					FolderScanner f = new FolderScanner(nom.toAbsolutePath(), filter);
					list.add(f);
					f.fork();
				}
			}
		
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		try(DirectoryStream<Path> listing = Files.newDirectoryStream(path, filter)){
			
			for(Path nom : listing) {
				result++;
			}
			
		}
		catch(IOException ioe) {
			ioe.printStackTrace();			
		}
		
		for(FolderScanner f : list) result += f.join();
		
		return result;
	}
	
	
	
	
	@Override
	protected Long compute() {
		
		long resultat = 0;
		
		try {
			resultat = this.parallelScan();
		}
		catch(ScanException se) {
			se.printStackTrace();
		}

		return resultat;
		
	}
	
	
	public long getResultat() {
		return this.result;
	}
	
}
