package archiver;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class Archiver {

	public static void main(String[] args) throws IOException  {
		if(args.length == 0) {
			File workingDir = new File(System.getProperty("user.dir"));
			new ZipCompressor().decompress(System.in, workingDir);
		} else {
			new ZipCompressor().compress(getFiles(args), System.out);
		} 
	}
	
	private static Set<File> getFiles(String[] args) {
		LinkedHashSet<File> files = new LinkedHashSet<>();
		for(String name : args) {
			File f = new File(name);
			if(f.exists() && f.canRead())
				files.add(f);
		}
		return files;
	}
}

