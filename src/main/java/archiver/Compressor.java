package archiver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public interface Compressor {
	void compress(Set<File> source, OutputStream os) throws IOException;
	void decompress(InputStream is, File destinationFolder) throws IOException;
}
