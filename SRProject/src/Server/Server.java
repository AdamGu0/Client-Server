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

public class Server extends JFrame {
	private JPanel contentPane;
	public JLabel logLabel;
	private Vector<String> userList;
	private Vector<MessageThread> messageThreads;
	public int validLoginCount;
	public int invalidLoginCount;
	public int receiveCount;
	public int forwardCount;
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
		userList = new Vector<String>();
		messageThreads = new Vector<MessageThread>();
		validLoginCount = 0;
		invalidLoginCount = 0;
		receiveCount = 0;
		forwardCount = 0;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		final Thread myThread = new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				runServer();
			}}
		);
		
		final JButton btnRun = new JButton("run");
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
		
		logLabel = new JLabel("New label");
		logLabel.setHorizontalAlignment(SwingConstants.CENTER);
		logLabel.setBounds(10, 44, 414, 23);
		contentPane.add(logLabel);

	}
	
	private void runServer() {
		try {
			   // 建立一个服务器绑定指定端口Socket(ServerSocket)并开始监听
			ServerSocket serverSocket = new ServerSocket(9000);

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
				
				logLabel.setText("用户信息为：" + info);
				
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
					logLabel.setText("登陆成功");
					pw.write("accept\n");
					pw.flush();
					messageThreads.addElement(new MessageThread(socket, pw, br, username));
				} else {
					invalidLoginCount++;
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

		
		if (userList.contains(username)) return false; //查重复
		if (!validateLogin(username, password)) return false; //验证密码
		
		userList.addElement(username);
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
	
	public void sendMessages(String id, String message) {
		Enumeration<MessageThread> e = messageThreads.elements();
		while (e.hasMoreElements()) {
			MessageThread t = e.nextElement();
			t.forwardMessage(id, message);
		}
	}
	
	class MessageThread extends Thread {
		private Socket client;
		private BufferedReader reader;
		private PrintWriter writer;
		private String _id;
		
		public MessageThread(Socket s, PrintWriter w, BufferedReader r, String id) throws IOException {
			client = s;
			writer = w;
			reader = r;
			_id = id;

			start();
		}
		
		public void run() {
			try {
				while (true) {
					String line = reader.readLine();
					count(1);
					logLabel.setText(_id + ": " + line);
					sendMessages(_id, line);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void forwardMessage(String id, String message) {
			if (id.equals(_id)) return;
			writer.write(id + ": " + message + "\n");
			writer.flush();
			count(2);
		}
		
		private synchronized void count(int kind) {
			if (kind == 1) receiveCount++;
			if (kind == 2) forwardCount++;
		}
		
	}


	
}
