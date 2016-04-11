package CM;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;


public class TestProperties {
    
    public static boolean valiRun(String filePath) throws Exception{
    	 String fileStr = Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath();
    	 System.out.println(fileStr);
    	 String file = fileStr
    	 + filePath;
     	 InputStream in = new FileInputStream(new File(file));
  		 Properties prop = new Properties();
    	 prop.load(in);
    	 String time = prop.get("time").toString();
    	 System.out.println(time);
    	 SimpleDateFormat sdf = new SimpleDateFormat("HH:mm-ss");
    	 String str = sdf.format(new Date());
    	 if(str.split("-")[0].equals(time)){
    		 return true;
    		 }
    	 return false;
    }
    
    public static void showTimer(final String filePath) throws Exception {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
             try {
            	 if(TestProperties.valiRun(filePath)){
            		 System.out.println("任务开始执行了");
            		 }else{
            			 System.out.println("时间未到");
            			 }
            	 } catch (Exception e) {
            		 e.printStackTrace();
            		 }
             }
        };
        
        String fileStr = Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath();
        String file = fileStr+ filePath;
        InputStream in = new FileInputStream(new File(file));
        Properties prop = new Properties();
        prop.load(in);
        String time = prop.get("time").toString();

        //设置执行时间
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);//每天
        //定制每天的....执行，
        String t[] = time.split(":");
        calendar.set(year, month, day, Integer.parseInt(t[0]), Integer.parseInt(t[1]), 00);
      
        Date date = calendar.getTime();
        Timer timer = new Timer();
        
        int period = 60 * 1000;
        //每天的date时刻执行task，每隔2秒重复执行
        timer.schedule(task, date, period);
    }


    	
    
    
    //读取Properties的全部信息
    public static void GetAllProperties(String filePath) throws IOException {
        Properties pps = new Properties();
        InputStream in = new BufferedInputStream(new FileInputStream(filePath));
        pps.load(in);
        Enumeration en = pps.propertyNames(); //得到配置文件的名字
        
        while(en.hasMoreElements()) {
            String strKey = (String) en.nextElement();
            String strValue = pps.getProperty(strKey);
            System.out.println(strKey + "=" + strValue);
        }
        
    }
  //根据Key读取Value
    public static String GetValueByKey(String filePath, String key) {
        Properties pps = new Properties();
        try {
            InputStream in = new BufferedInputStream (new FileInputStream(filePath));  
            pps.load(in);
            String value = pps.getProperty(key);
            System.out.println(key + " = " + value);
            return value;
            
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    //写入Properties信息
    public static void WriteProperties (String filePath, String pKey, String pValue) throws IOException {
        Properties pps = new Properties();
        
        InputStream in = new FileInputStream(filePath);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm-ss");
   	    String str = sdf.format(new Date());
        //从输入流中读取属性列表（键和元素对） 
        pps.load(in);
        //调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。  
        //强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
        OutputStream out = new FileOutputStream(filePath);
        pps.setProperty(pKey, pValue);
        pps.setProperty("time",str);
        //以适合使用 load 方法加载到 Properties 表中的格式，  
        //将此 Properties 表中的属性列表（键和元素对）写入输出流  
        pps.store(out, "Update " + pKey + " name");
    }
    
    public static void main(String [] args) throws IOException{
        String value = GetValueByKey("Test.properties", "speed");
       // System.out.println(value);
        //GetAllProperties("Test.properties");
        //WriteProperties("Test.properties","long", "212");
    }
}