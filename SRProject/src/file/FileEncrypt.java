package encryption;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream; 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.RandomAccessFile;
 
 
public class FileEncrypt {
   
    public static void encrypt(String filePath, String password) throws Exception {
        File file = new File(filePath);
        String path = file.getPath();
        if(!file.exists()){
            return;
        }
        int index = path.lastIndexOf("\\");
        String destFile = path.substring(0, index)+"\\"+"abc";
        File dest = new File(destFile);
        InputStream in = new FileInputStream(filePath);
        OutputStream out = new FileOutputStream(destFile);
        byte[] buffer = new byte[1024];
        int r;
        byte[] buffer2=new byte[1024];
        while (( r= in.read(buffer)) > 0) {
                for(int i=0;i<r;i++)
                {
                    byte b=buffer[i];
                    buffer2[i]=b==255?0:++b;
                }
                out.write(buffer2, 0, r);
                out.flush();
        }
        in.close();
        out.close();
        file.delete();
        dest.renameTo(new File(filePath));
        encryptMethod(filePath, password);
        System.out.println("Success encrypt");
    }
 
     public static void encryptMethod(String fileName, String password) {
            try {
                // random file stream, read & write
                RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
                long fileLength = randomFile.length();
               
                randomFile.seek(fileLength);
                randomFile.writeBytes(password);
                randomFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
     }

     public static String decrypt(String filePath, String tempFile, int pwdLength) throws Exception{
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            File dest = new File(tempFile);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
             
            InputStream is = new FileInputStream(filePath);
            OutputStream out = new FileOutputStream(tempFile);
             
            byte[] buffer = new byte[1024];
            byte[] buffer2=new byte[1024];
            byte bMax=(byte)255;
            long size = file.length() - pwdLength;
            int mod = (int) (size%1024);
            int div = (int) (size>>10);
            int count = mod==0?div:(div+1);
            int k = 1, r;
            while ((k <= count && ( r = is.read(buffer)) > 0)) {
                if(mod != 0 && k==count) {
                    r =  mod;
                }
                 
                for(int i = 0;i < r;i++)
                {
                    byte b=buffer[i];
                    buffer2[i]=b==0?bMax:--b;
                }
                out.write(buffer2, 0, r);
                k++;
            }
            out.close();
            is.close();
            return tempFile;
        }
}