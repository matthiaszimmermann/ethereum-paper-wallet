package org.matthiaszimmermann.ethereum.pwg;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class FileUtility {

	public static void saveToFile(String buf, String fileName) {		
		try(PrintWriter out = new PrintWriter(fileName)) {
			out.println(buf);
		}
		catch(Exception e) {
			throw new RuntimeException("Failed to write content to file " + fileName, e);
		}
	}

	public static void saveToFile(byte [] buf, String fileName) {
		try (FileOutputStream fos = new FileOutputStream(fileName)) {
			fos.write(buf);
		}
		catch(Exception e) {
			throw new RuntimeException("Failed to write content to file " + fileName, e);
		}
	}

	public static List<String> getResourceAsStrings(String fileName) {
		try {
			InputStream is = PassPhraseUtility.class.getResourceAsStream(fileName);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			
			List<String> lines = new ArrayList<>();
			String line = br.readLine();

			while(line != null) {
				lines.add(line);
				line = br.readLine();
			}

			return lines;
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to read text from resource " + fileName, e);
		}
	}

	public static byte [] getResourceAsBytes(String fileName) {
		try {
			InputStream is = FileUtility.class.getResourceAsStream(fileName);
			byte [] buf = getBytesFromInputStream(is);

			return buf;
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to read content from resource " + fileName, e);
		}
	}

	private static byte[] getBytesFromInputStream(InputStream is) throws IOException {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {
			byte[] buffer = new byte[0xFFFF];

			for (int len; (len = is.read(buffer)) != -1;)
				os.write(buffer, 0, len);

			os.flush();

			return os.toByteArray();
		}
	}

}
