package file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.print.DocFlavor.STRING;

public class MessageFileForWeek {
	private static final int BUFFER_SIZE = 4096;
	
	private static String getDate(){
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dFormat.format(new Date());
	}
	//extracts content of a zip file specified by zipFilePath to a directory specified by destDirectory.
	public static void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }
	private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
	    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
	    byte[] bytesIn = new byte[BUFFER_SIZE];
	    int read = 0;
	    while ((read = zipIn.read(bytesIn)) != -1) {
	        bos.write(bytesIn, 0, read);
	    }
	    bos.close();
	}
	
	static public void zipFolder(String srcFolder, String destZipFile) throws Exception {
	    ZipOutputStream zip = null;
	    FileOutputStream fileWriter = null;

	    fileWriter = new FileOutputStream(destZipFile);
	    zip = new ZipOutputStream(fileWriter);

	    addFolderToZip("", srcFolder, zip);
	    zip.flush();
	    zip.close();
	  }

	  static private void addFileToZip(String path, String srcFile, ZipOutputStream zip)
	      throws Exception {

	    File folder = new File(srcFile);
	    if (folder.isDirectory()) {
	      addFolderToZip(path, srcFile, zip);
	    } else {
	      byte[] buf = new byte[1024];
	      int len;
	      FileInputStream in = new FileInputStream(srcFile);
	      zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
	      while ((len = in.read(buf)) > 0) {
	        zip.write(buf, 0, len);
	      }
	    }
	  }
	  
	  static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip)
		      throws Exception {
		    File folder = new File(srcFolder);

		    for (String fileName : folder.list()) {
		      if (path.equals("")) {
		        addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
		      } else {
		        addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
		      }
		    }
		  }
	  
	public static void zipWeekFile() throws Exception{
		File dataFolder = new File("data");
		File[] files = dataFolder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if(pathname.isDirectory())
					return false;
				return true;
			}
		});
		
		String dateNow = getDate();
		List<String> dateList = null;
		for(int i = 1; i <= 7 ; ++i)
		{
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -i);
			SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
			dateList.add(dFormat.format(cal.getTime()));	
		}
		for(File file:files){
			String date = file.getName().substring(0,10);
			if(dateList.contains(date)){
				unzip(file.getPath(),file.getPath()+"temp");
			}
		}
		zipFolder(dataFolder.getPath() + "temp",dataFolder.getPath() + dateList.get(0) + "-" + dateList.get(dateList.size()-1));
	}
}
