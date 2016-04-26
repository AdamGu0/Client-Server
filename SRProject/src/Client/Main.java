package Client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JLabel;

import teamEleven.configController.*;

import org.json.*;

public class Main {

	private static Main _m;
	private Main() {
		failCount = 0;
		successCount = 0;
	}

	public String serverIP;
	public String id;
	public int failCount;
	public int successCount;
	public JLabel logLabel;
	public ConfigController configTest;
	public int pNumber;
	public int tNumber;
	public int fNumber;
	
	public static Main getMain() {
		if ( _m == null) _m = new Main();
		return _m;
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			LoginPage lp = new LoginPage();
			lp.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void WriteMessageToFile(File file,String line){
		try{
			PrintStream ps = new PrintStream(new FileOutputStream(file));
			ps.append(line);
			ps.close();
		}catch(FileNotFoundException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public File FileExist(String filepath){
		File file=new File(filepath);    
		if(!file.exists())    
		{    
		    try {    
		        file.createNewFile();    
		    } catch (IOException e) {    
		        // TODO Auto-generated catch block    
		        e.printStackTrace();    
		    }    
		}
		return file;    
	
	}
	public Boolean login(String serverAddress , String _id, String password) {
		try {
			String filename = "data/Clientfile.txt";
		    File outputfile = FileExist(filename);
			configTest = new ConfigController("properties.json");
			pNumber = configTest.getInt("ServerPortNumber", 9000);
			Socket s = new Socket(serverAddress, pNumber);
			
			OutputStream os = s.getOutputStream();
			PrintWriter writer = new PrintWriter(os,true);

			InputStream is = s.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			writer.write(_id + ";;;" + password + "\n");
			writer.flush();

			String reply = reader.readLine();
			WriteMessageToFile(outputfile,reply);
			if (reply.equals("accept")) {
				
				serverIP = serverAddress;
				id = _id;
				
				openMessagePage(s, writer, reader);
				successCount++;
				return true;
			}

			writer.close();
			reader.close();
			is.close();
			os.close();
			s.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		failCount++;
		return false;
	}
	
	private void openMessagePage(Socket s, PrintWriter w, BufferedReader r) {
		try {
			MessagePage frame = new MessagePage(s, w, r);
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
