package PM;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
* @author      Yuting Chen
* @since       2016-04-10
*/
public class PM {
    private static HashMap<String,Integer> dataMap  = new HashMap<>();
    private static int period = 1;
    private static TimeUnit periodUnit = TimeUnit.MINUTES;
    private static Date startDate = new Date();
    private static Date endDate = new Date(startDate.getTime() + periodUnit.toMillis(period));
    private static String pathName = "LOG";
    
    public static Map<String, Integer> getMap(){
        return dataMap;
    }

    public static void setPathName(String name){
        if(name!=null&&!name.equals(""))
        {
            pathName=name;
        }
    }

    public static void setPeriod(int period){
        setPeriod(period, TimeUnit.MINUTES);
    }

    public static void setPeriod(int period, TimeUnit periodUnit){
            PM.period = period;
            PM.periodUnit = periodUnit;
    }

    public synchronized static void sendPMMessage(String name,int value){
        if(value < 0){
            return;
        }
        updateData(name, value);
        Date date = new Date();
        if(!dataMap.isEmpty()) {
            output();
        }
    }

    private static void updateData(String indexName, int value){
        if(endDate.getTime()<new Date().getTime()){
            dataMap.clear();
        }
        if(dataMap.get(indexName)!=null){
            value += dataMap.get(indexName);
        }
        dataMap.put(indexName, value);
    }
    public static String getFileName() {
        Date date = new Date();
        if (date.getTime() > endDate.getTime()) {
            startDate = new Date(date.getTime());
            endDate = new Date(startDate.getTime() + periodUnit.toMillis(period));
        }
        SimpleDateFormat startDateFmt = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        return startDateFmt.format(startDate)+".log";
    }


    private static synchronized void output() {
        File pathDir = new File(pathName);
        if(!pathDir.exists())
        {
            pathDir.mkdirs();
        }
        String fileName = getFileName();
        File file = new File(pathDir, fileName);

        try {
            FileOutputStream out = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
            for (String key: dataMap.keySet()) {
                pw.println(key+":"+dataMap.get(key).toString());
            }
            pw.flush();
            pw.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void Reset(){
            System.gc();
            dataMap.clear();
            File lastFile=new File(pathName+"//"+getFileName());
            if(lastFile.exists())
                lastFile.delete();
    }

    
}