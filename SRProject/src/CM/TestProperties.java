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
            		 System.out.println("����ʼִ����");
            		 }else{
            			 System.out.println("ʱ��δ��");
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

        //����ִ��ʱ��
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);//ÿ��
        //����ÿ���....ִ�У�
        String t[] = time.split(":");
        calendar.set(year, month, day, Integer.parseInt(t[0]), Integer.parseInt(t[1]), 00);
      
        Date date = calendar.getTime();
        Timer timer = new Timer();
        
        int period = 60 * 1000;
        //ÿ���dateʱ��ִ��task��ÿ��2���ظ�ִ��
        timer.schedule(task, date, period);
    }


    	
    
    
    //��ȡProperties��ȫ����Ϣ
    public static void GetAllProperties(String filePath) throws IOException {
        Properties pps = new Properties();
        InputStream in = new BufferedInputStream(new FileInputStream(filePath));
        pps.load(in);
        Enumeration en = pps.propertyNames(); //�õ������ļ�������
        
        while(en.hasMoreElements()) {
            String strKey = (String) en.nextElement();
            String strValue = pps.getProperty(strKey);
            System.out.println(strKey + "=" + strValue);
        }
        
    }
  //����Key��ȡValue
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
    
    //д��Properties��Ϣ
    public static void WriteProperties (String filePath, String pKey, String pValue) throws IOException {
        Properties pps = new Properties();
        
        InputStream in = new FileInputStream(filePath);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm-ss");
   	    String str = sdf.format(new Date());
        //���������ж�ȡ�����б�����Ԫ�ضԣ� 
        pps.load(in);
        //���� Hashtable �ķ��� put��ʹ�� getProperty �����ṩ�����ԡ�  
        //ǿ��Ҫ��Ϊ���Եļ���ֵʹ���ַ���������ֵ�� Hashtable ���� put �Ľ����
        OutputStream out = new FileOutputStream(filePath);
        pps.setProperty(pKey, pValue);
        pps.setProperty("time",str);
        //���ʺ�ʹ�� load �������ص� Properties ���еĸ�ʽ��  
        //���� Properties ���е������б�����Ԫ�ضԣ�д�������  
        pps.store(out, "Update " + pKey + " name");
    }
    
    public static void main(String [] args) throws IOException{
        String value = GetValueByKey("Test.properties", "speed");
       // System.out.println(value);
        //GetAllProperties("Test.properties");
        //WriteProperties("Test.properties","long", "212");
    }
}