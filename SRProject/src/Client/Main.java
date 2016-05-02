package Client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

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
	public String group;
	public int failCount;
	public int successCount;
	public JLabel logLabel;
	public ConfigController configTest;
	public int pNumber;
	public int tNumber;
	public int fNumber;
	private FileOutputStream fo;
	public PrintStream ps;

	public static Main getMain() {
		if (_m == null)
			_m = new Main();
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

	public Boolean login(String serverAddress, String _id, String password) {
		try {
			configTest = new ConfigController("properties.json");
			pNumber = configTest.getInt("ServerPortNumber", 9000);
			Socket s = new Socket(serverAddress, pNumber);

			OutputStream os = s.getOutputStream();
			PrintWriter writer = new PrintWriter(os, true);

			InputStream is = s.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			writer.write(_id + ";;;" + password + "\n");
			writer.flush();

			String reply = reader.readLine();
			if (reply.equals("accept")) {

				serverIP = serverAddress;
				id = _id;

				String filename = "data/" + getDate() + "-UserOutput-" + id + ".txt";
				try {
					fo = new FileOutputStream(fileExist(filename), true);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				ps = new PrintStream(fo);
				
				// choose group
				reply = reader.readLine();
				String[] groups = reply.substring(6, reply.length() - 3).split(";;;");
				String hint = "请选择一个群组：";
				while (true) {
					String selection = (String) JOptionPane.showInputDialog(null, "若新建群组，请点击取消。", hint,
							JOptionPane.INFORMATION_MESSAGE, null, groups, groups[0]);
					if (selection == null) {
						selection = (String) JOptionPane.showInputDialog("请输入群组名：");
						if (selection == null) continue;
						writer.write("NEW:" + selection + "\n");
						writer.flush();
						reply = reader.readLine();
						if (reply.equals("accept")) {
							group = selection;
							break;
						} else {
							hint = "该群组已存在，请重新选择:";
							continue;
						}
					} else {
						writer.write("SEL:" + selection + "\n");
						writer.flush();
						group = selection;
						break;
					}
				}
				
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
	
	private File fileExist(String filepath) {
		File file = new File(filepath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	public void writeMessageToFile(String line) {
		synchronized (ps) {
			ps.append(line);
		}
	}
	
	public void closeFile() {
		try {
			fo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ps.close();
	}
	
	private String getDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(new Date());
	}
}
