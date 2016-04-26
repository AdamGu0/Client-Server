package file;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MessageFile {

	public static void zipFiles() throws IOException {
		File dataFlolder = new File("data");
		File[] files = dataFlolder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory())
					return false;

				return true;
			}
		});
		
		String dateNow = getDate();
		Map<String, ZipOutputStream> fileMap = new HashMap<String, ZipOutputStream>();
		for ( File file : files) {
			String date = file.getName().substring(0, 10);
			if ( dateNow.equals(date)) continue;

			ZipOutputStream zipOut;
			if (!fileMap.containsKey(date)) {
				zipOut = new ZipOutputStream(new FileOutputStream(new File("data/" + date + ".zip")));
				fileMap.put(date, zipOut);
			} else
				zipOut = fileMap.get(date);

			InputStream input = new FileInputStream(file);
			zipOut.putNextEntry(new ZipEntry(file.getName()));
			int temp = 0;
			while ((temp = input.read()) != -1) {
				zipOut.write(temp);
				
			}
			input.close();
			file.delete();
		}
		
		for (ZipOutputStream zipOut : fileMap.values()) {
			zipOut.close();
		}
	}
	
	private static String getDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(new Date());
	}

}
