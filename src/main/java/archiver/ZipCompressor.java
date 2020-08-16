package archiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipCompressor implements Compressor {
	private static final int MAX_BUFFER_SIZE = 1024;
	private ZipOutputStream zip;

	@Override
	public void compress(Set<File> files, OutputStream os) throws IOException {
		try {
			zip = new ZipOutputStream(os);
			for(File file : files) {
				add(file, file.getName());
			}
		} finally {
			zip.close();
		}
	}

	private void add(File file, String path) throws FileNotFoundException, IOException {
		if(file.isDirectory()) {
			path = path + File.separator;
			zip.putNextEntry(new ZipEntry(path));
			File[] files = file.listFiles();
			for (File f : files) {
				add(f, path + f.getName());
			}
			zip.closeEntry();
		} else {
			try(FileInputStream fis = new FileInputStream(file)) {
				zip.putNextEntry(new ZipEntry(path));
				byte[] bytes = new byte[MAX_BUFFER_SIZE];
				int length;
				while((length = fis.read(bytes)) >= 0) {
					zip.write(bytes, 0, length);
				}
				zip.closeEntry();
			}
		}
	}

	@Override
	public void decompress(InputStream is, File destinationFolder) throws IOException {
		ZipInputStream zis = new ZipInputStream(is);
		ZipEntry zipEntry = zis.getNextEntry();
		byte[] bytes = new byte[MAX_BUFFER_SIZE];
		while (zipEntry != null) {
			File dest = new File(destinationFolder + File.separator + zipEntry.getName());
			if(zipEntry.isDirectory()) {
				dest.mkdirs();
			} else {
				FileOutputStream fos = new FileOutputStream(dest);
				int length;
				while ((length = zis.read(bytes)) > 0) {
					fos.write(bytes, 0, length);
				}
				fos.close();
			}
			zipEntry = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
	}
}