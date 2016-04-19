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
	
	public Boolean login(String serream os = verAddress , String _id, String password) {
		try {
			configTest = new ConfigController("properties.json");
			pNumber = configTest.getInt("ServerPortNumber", 9000);
			Socket s = new Socket(serverAddress, pNumber);
			
			OutputSts.getOutputStream();
			PrintWriter writer = new PrintWriter(os,true);

			InputStream is = s.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			writer.write(_id + ";;;" + password + "\n");
			writer.flush();

			String reply = reader.readLine();
			
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
