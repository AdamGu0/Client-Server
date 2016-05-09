package Server;

import java.util.*;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JButton;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import reuse.license.*;
import teamEleven.configController.*;

import org.json.*;

import file.MessageFile;

public class Server extends JFrame {
	private JPanel contentPane;
	public JLabel logLabel;
	public ConfigController configTest;
	private HashSet<User> userList;
	private HashSet<String> groupList;
	//private HashSet<MessageThread> messageThreads;
	public int validLoginCount;
	public int invalidLoginCount;
	public int forwardCount;
	public int pNumber;
	public int tNumber;
	public int fNumber;
	public long fileLimit;
	private ServerSocket serverSocket;
	private MultiMaxNumOfMessage totalLicense;
	private MultiFrequencyRestriction freqLicense;
	private FileOutputStream fo;
	private PrintStream ps;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server frame = new Server();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Server() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeServer();
			}
		});
		userList = new HashSet<User>();
		groupList = new HashSet<String>();
		groupList.add("Default");
		//messageThreads = new HashSet<MessageThread>();
		validLoginCount = 0;
		invalidLoginCount = 0;
		forwardCount = 0;
		pNumber = 0;
		fileLimit = 409600;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		final Thread myThread = new Thread(new Runnable() {

			@Override
			public void run() {
				runServer();
			}
		});

		final JButton btnRun = new JButton("启动");
		btnRun.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				btnRun.setEnabled(false);
				logLabel.setText("监听9000");
				myThread.start();
			}
		});
		btnRun.setBounds(10, 10, 93, 23);
		contentPane.add(btnRun);

		logLabel = new JLabel("服务器");
		logLabel.setHorizontalAlignment(SwingConstants.CENTER);
		logLabel.setBounds(10, 44, 414, 23);
		contentPane.add(logLabel);

		// TODO read parameters from file @RobertRen
		try {
			configTest = new ConfigController("properties.json");
			pNumber = configTest.getInt("ServerPortNumber", 9000);
			serverSocket = new ServerSocket(pNumber);
			logLabel.setText("服务器就绪。");
		} catch (IOException e1) {
			e1.printStackTrace();
			logLabel.setText("无法建立服务器。");
		}
		totalLicense = new MultiMaxNumOfMessage(100);
		freqLicense = new MultiFrequencyRestriction(1);
		
		String filename = "data/" + getDate() + "-ServerOutput.txt";
		if(fileLimit(filename,fileLimit)){
			try {
				fo = new FileOutputStream(fileExist(filename), true);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					} 
			}else{
				logLabel.setText("文件已超大小限制");
			}
		ps = new PrintStream(fo);
		
		try {
			MessageFile.zipFiles();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void closeServer() {
		try {
			closeThreads();
			serverSocket.close();
			ps.close();
			fo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closeThreads() throws IOException {
		synchronized (userList) {
			for (User u : userList)
				u.thread.close();
		}
	}

	private void runServer() {
		try {
			while (true) {
				// 使用accept()方法阻塞等待监听，获得新的连接
				Socket socket = serverSocket.accept();
				logLabel.setText("建立连接");

				// 获得输入流
				InputStream is = socket.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));

				// 获得输出流
				OutputStream os = socket.getOutputStream();
				PrintWriter pw = new PrintWriter(os, true);

				// 读取用户输入信息
				String info = br.readLine();

				String[] userInfos = info.split(";;;");
				if (userInfos.length != 2) {
					// 非法连接 关闭流
					pw.close();
					br.close();
					os.close();
					is.close();
					socket.close();
				}
				String username = userInfos[0];
				String password = userInfos[1];
				// 响应信息
				if (validateUser(username, password)) {
					validLoginCount++;
					logLabel.setText(username + "登陆成功");
					pw.write("accept\n");
					pw.flush();
					totalLicense.addMap(username);
					freqLicense.addMap(username);
					synchronized (userList) {
						User u = new User(username);
						u.thread = new MessageThread(socket, pw, br, u);
						userList.add(u);
					}
				} else {
					invalidLoginCount++;
					logLabel.setText(username + "登陆失败");
					pw.write("error\n");
					pw.flush();

					// 关闭流
					pw.close();
					br.close();
					os.close();
					is.close();
					socket.close();
				}
			}

			// serverSocket.close(); //应移到窗口关闭时
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean validateUser(String username, String password) {
		synchronized (userList) {
			for (User user : userList) {
				if (user.id.equals(username)) return false; // 查重复
			}
		}
		if (!validateLogin(username, password)) // 验证密码
			return false;
		return true;
	}

	private boolean validateLogin(String username, String password) {

		return true; // 测试时 跳过登陆验证 直接返回true
		/*
		 * try{
		 * 
		 * Class.forName("com.mysql.jdbc.Driver"); // MySQL database connection
		 * Connection conn; conn =
		 * DriverManager.getConnection("jdbc:mysql://localhost:3306/USER?" +
		 * "user=root&password=since1997"); PreparedStatement pst =
		 * conn.prepareStatement(
		 * "Select * from user where username=? and password=?");
		 * pst.setString(1, username); pst.setString(2, password); ResultSet rs
		 * = pst.executeQuery(); if(rs.next()) return true; else return false; }
		 * catch(Exception e){ e.printStackTrace(); return false; }
		 */
	}

	private void sendMessages(String id, String message) {
		synchronized (userList) {
			for (User u : userList)
				u.thread.forwardMessage(id, message);
		}
	}
	
	private void sendMessages(User from, String message) {
		synchronized (userList) {
			for (User to : userList) {
				if (to.group.equals(from.group)) to.thread.forwardMessage(from.id, message);
			}
		}
	}

	private synchronized void forwardCount() {
		forwardCount++;
	}
	
	private boolean fileLimit(String filepath,long lengthLimit){
		File file = new File(filepath);
		long length = file.length();
		if(length>lengthLimit){
			
			return false;
		}else return true;
		
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
			ps.append(getDate());
			ps.append(line);
		}
	}
	
	private String getDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(new Date());
	}

	class MessageThread extends Thread {
		private Socket client;
		private BufferedReader reader;
		private PrintWriter writer;
		private User user;

		public MessageThread(Socket s, PrintWriter w, BufferedReader r, User user) throws IOException {
			client = s;
			writer = w;
			reader = r;
			this.user = user;
			start();
		}

		private void chooseGroup() throws IOException {
			String groupInfo = "group:";
			for (String g : groupList) {
				groupInfo += g + ";;;";
			}
			sendMessage(groupInfo);
			String group;
			while (true) {
				String reply = reader.readLine();
				String type = reply.substring(0, 3);
				group = reply.substring(4);
				
				if (type.equals("SEL")) {
					break;
				} else if (type.equals("NEW")) {
					boolean added;
					synchronized (groupList) {
						added = groupList.add(group);
					}
					if (added) {
						sendMessage("accept");
						break;
					} else {
						sendMessage("refuse");
						continue;
					}
				}
			}
			user.group = group;
		}
		
		public void run() {
			try {
				chooseGroup();
				while (true) {
					String line = reader.readLine();

					if (line.equals("LOGOUT")) {
						logout();
						return;
					}

					if (!totalLicense.CheckByKey(user.id)) {
						sendMessage("服务器：您已超过可发送的消息上限，即将断开连接。");
						logout();
						return;
					}
					if (!freqLicense.CheckByKey(user.id)) {
						sendMessage("服务器：您发送消息太过频繁，请稍后再试。");
						continue;
					}

					logLabel.setText(user.id + ": " + line);
					sendMessages(user, line);
					writeMessageToFile(user.id + ": " + line + "\n");
				}

			} catch (IOException e) {
				e.printStackTrace();
				logout();
			}

		}

		private void logout() {
			try {
				writer.close();
				reader.close();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			logLabel.setText(user.id + " 已登出");
			
			synchronized (userList) {
				userList.remove(user);
			}

			totalLicense.deleteMap(user.id);
			freqLicense.deleteMap(user.id);
		}

		protected void close() throws IOException {
			writer.close();
			reader.close();
			client.close();
		}

		public void forwardMessage(String _id, String message) {
			if (_id.equals(user.id))
				return;
			sendMessage(_id + ": " + message);
			forwardCount();
		}

		public void sendMessage(String message) {
			writer.write(message + "\n");
			writer.flush();
		}

	}
}
