package Server;

import java.util.*;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JButton;
import java.io.*;
import java.net.*;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import SR.AdamGu0.License;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Server extends JFrame {
	private JPanel contentPane;
	public JLabel logLabel;
	private ArrayList<String> userList;
	private ArrayList<MessageThread> messageThreads;
	public int validLoginCount;
	public int invalidLoginCount;
	public int forwardCount;
	private License license;
	private ServerSocket serverSocket;
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
		userList = new ArrayList<String>();
		messageThreads = new ArrayList<MessageThread>();
		validLoginCount = 0;
		invalidLoginCount = 0;
		forwardCount = 0;
		license = new License(100, 2); // TODO read parameters from file @RebertRen
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		final Thread myThread = new Thread(new Runnable(){

			@Override
			public void run() {
				runServer();
			}}
		);
		
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
		
		logLabel = new JLabel("服务器未启动");
		logLabel.setHorizontalAlignment(SwingConstants.CENTER);
		logLabel.setBounds(10, 44, 414, 23);
		contentPane.add(logLabel);

	}
	


	private void closeServer() {
		try {
			closeThreads();
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closeThreads() throws IOException {
		synchronized(messageThreads) { for (MessageThread t : messageThreads) t.close(); }
	}

	private void runServer() {
		try {
			   // 建立一个服务器绑定指定端口Socket(ServerSocket)并开始监听
			serverSocket = new ServerSocket(9000);

			while(true) {
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
				if(validateUser(username, password)) {
					validLoginCount++;
					logLabel.setText(username + "登陆成功");
					pw.write("accept\n");
					pw.flush();
					license.newId(username);
					synchronized(messageThreads) { messageThreads.add(new MessageThread(socket, pw, br, username)); }
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

			//serverSocket.close(); //应移到窗口关闭时
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean validateUser(String username, String password) {
		synchronized(userList) { if (userList.contains(username)) return false; }//查重复
		if (!validateLogin(username, password)) return false; //验证密码
		synchronized(userList) { userList.add(username); }
		return true;
	}
	
	private boolean validateLogin(String username, String password) {
		
		return true; //测试时 跳过登陆验证 直接返回true
		/*
		try{
			
			Class.forName("com.mysql.jdbc.Driver");  // MySQL database connection
			Connection conn;
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/USER?" + "user=root&password=since1997");
			PreparedStatement pst = conn.prepareStatement("Select * from user where username=? and password=?");
			pst.setString(1, username);
			pst.setString(2, password);
			ResultSet rs = pst.executeQuery();
			if(rs.next())
				return true;
			else
				return false;
			}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		*/
	}
	
	private void sendMessages(String id, String message) {
		synchronized(messageThreads) { for (MessageThread t : messageThreads) t.forwardMessage(id, message); }
	}
	
	private synchronized void forwardCount() {
		forwardCount++;
	}
	
	class MessageThread extends Thread {
		private Socket client;
		private BufferedReader reader;
		private PrintWriter writer;
		private String id;
		
		public MessageThread(Socket s, PrintWriter w, BufferedReader r, String _id) throws IOException {
			client = s;
			writer = w;
			reader = r;
			id = _id;

			start();
		}
		
		public void run() {
			try {
				while (true) {
					String line = reader.readLine();
					
					if ( line.equals("LOGOUT")) {
						logout();
						return;
					}
					
					if ( !license.count(id) ) {
						sendMessage("服务器：您已超过可发送的消息上限，即将断开连接。");
						logout();
						return;
					}
					if ( !license.allowInSecond(id) ) {
						sendMessage("服务器：您发送消息太过频繁，请稍后再试。");
						continue;
					}
					
					logLabel.setText(id + ": " + line);
					sendMessages(id, line);
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logLabel.setText(id + " 已登出");
			synchronized(messageThreads) { messageThreads.remove(this); }
			synchronized(userList) { userList.remove(id); }
			license.removeId(id);
		}
		
		protected void close() throws IOException {
			writer.close();
			reader.close();
			client.close();
		}

		public void forwardMessage(String _id, String message) {
			if (_id.equals(id)) return;
			sendMessage(_id + ": " + message);
			forwardCount();
		}
		
		public void sendMessage(String message) {
			writer.write(message + "\n");
			writer.flush();
		}
		
	}
}
