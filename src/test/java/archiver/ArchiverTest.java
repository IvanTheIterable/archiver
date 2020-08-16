package archiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

public class ArchiverTest {
	private static final String FOLDER0 = "archiverTestDestination";
	private static final String FOLDER1 = "archiverTestFolder1";
	private static final String FOLDER2 = "archiverTestFolder2";
	private static final String FOLDER3 = "archiverTestFolder3";
	private static final String FILE1 = FOLDER1 + File.separator + "archiverTestFile1";
	private static final String FILE2 = FOLDER1 + File.separator + FOLDER2 + File.separator + "archiverTestFile2";
	private static final String FILE3 = FOLDER1 + File.separator + FOLDER2 + File.separator + FOLDER3 + File.separator + "archiverTestFile3";
	private static final String FILE1_CONTENT = "This is file 1";
	private static final String FILE2_CONTENT = "This is file 2";
	private static final String FILE3_CONTENT = "This is file 3";
	private static final File source = new File(FOLDER1);
	private static final File destination = new File(FOLDER0);
	private static final File folder1 = new File(FOLDER0 + File.separator + FOLDER1);
	private static final File folder2 = new File(FOLDER0 + File.separator + FOLDER1 + File.separator + FOLDER2);
	private static final File folder3 = new File(FOLDER0 + File.separator + FOLDER1 + File.separator + FOLDER2 + File.separator + FOLDER3);
		
	@Test
	public void zipCompressorTest1() throws Exception {
		assertTrue(!source.exists());
		assertTrue(!destination.exists());
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		new ZipCompressor().compress(setUpFiles(), baos);
		ByteArrayInputStream bios = new ByteArrayInputStream(baos.toByteArray());
		destination.mkdir();
		new ZipCompressor().decompress(bios, destination);
				
		assertTrue(destination.list().length == 1);
		assertTrue(destination.listFiles()[0].isDirectory());
		assertEquals(destination.list()[0], FOLDER1);
		assertTrue(folder1.isDirectory());
		assertTrue(folder1.list().length == 2);
		
		for(File f : folder1.listFiles()) {
			if(!f.isDirectory()) {
				assertEquals(f.getName(), new File(FILE1).getName());
				assertEquals(read(f), FILE1_CONTENT);
				break;
			}
			throw new Exception();
		}
		
		assertTrue(folder2.isDirectory());
		assertTrue(folder3.isDirectory());
		assertTrue(folder3.list().length == 1);

		cleanUp();
		
		assertTrue(!source.exists());
		assertTrue(!destination.exists());
	}
	
	private Set<File> setUpFiles() throws IOException {
		File file1 = new File(FILE1);
		File folder2 = new File(FOLDER1 + File.separator + FOLDER2);
		File file2 = new File(FILE2);
		File folder3 = new File(FOLDER1 + File.separator + FOLDER2 + File.separator + FOLDER3);
		File file3 = new File(FILE3);
		source.mkdir();
		fill(file1, FILE1_CONTENT);
		folder2.mkdir();
		fill(file2, FILE2_CONTENT);
		folder3.mkdir();
		fill(file3, FILE3_CONTENT);
		LinkedHashSet<File> files = new LinkedHashSet<>();
		files.add(source);
		return files;
	}
	
	private void fill(File file, String contents) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(file)) {
			byte[] bytes = contents.getBytes();
			fos.write(contents.getBytes(), 0, bytes.length);
		}
	}
	
	private void cleanUp() {
		rm(source);
		rm(destination);
	}
	
	private void rm(File file) {
		if(file.isDirectory())
			for(File f : file.listFiles())
				rm(f);
		file.delete();
	}
	
	private String read(File file) throws IOException {
		byte[] bytes = new byte[1024];
		int length;
		try (FileInputStream fis = new FileInputStream(file)) {
			length = fis.read(bytes);
		}
		return new String(Arrays.copyOf(bytes, length));
	}
}
