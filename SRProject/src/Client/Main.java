package Client;

import java.io.*;
import java.net.Socket;

public class Main {

	private static Main _m;
	private Main() {}
	
	private static LoginPage _lp;
	public int _serverPort;
	public String _serverIP;
	public String _id;
	
	public static Main getMain() {
		if ( _m == null) _m = new Main();
		return _m;
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		
		try {
			_lp = new LoginPage();
			_lp.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Boolean login(String serverAddress, String id, char[] password) {
		try {
			Socket s = new Socket(serverAddress, 9000);
			
			OutputStream os = s.getOutputStream();
			PrintWriter writer = new PrintWriter(os);

			InputStream is = s.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			writer.write("id:" + id + "; password:");
			writer.write(password);
			writer.flush();
			s.shutdownOutput();

			String reply = null;
			while (!((reply = reader.readLine()) == null)) {
				
				reader.close();
				is.close();
				writer.close();
				os.close();
				s.close();
				
				if (reply == "error") return false;
				
				_lp.dispose();
				_serverPort = Integer.parseInt(reply); //登陆成功后，服务端返回后续使用的端口号
				_serverIP = serverAddress;
				_id = id;
				//TODO 建立连接
				openMessagePage();
				
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void openMessagePage() {
		try {
			MessagePage frame = new MessagePage();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
