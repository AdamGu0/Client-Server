package Server;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class testServer extends JFrame {

	private JPanel contentPane;
	private JLabel logLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					testServer frame = new testServer();
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
	public testServer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnRun = new JButton("run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runServer();
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
			logLabel.setText("监听9000");
			   // 建立一个服务器绑定指定端口Socket(ServerSocket)并开始监听
			ServerSocket serverSocket = new ServerSocket(9000);

			   // 使用accept()方法阻塞等待监听，获得新的连接
			Socket socket = serverSocket.accept();

			   // 获得输入流
			InputStream is = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			   // 获得输出流
			OutputStream os = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(os);

			logLabel.setText("等待消息");
			   // 读取用户输入信息
			String info = null;
			while (!((info = br.readLine()) == null)) {
				logLabel.setText("我是服务器，用户信息为：" + info);
				   // 响应信息
				String reply;
				if(validateUser(info)) {
					reply = openNewConnection();//详见函数内的说明
				}
				else reply = "error";
				pw.write(reply);
				pw.flush();
				logLabel.setText("reply");
				
				info = null;
			}


			   // 关闭流
			pw.close();
			os.close();
			br.close();
			is.close();
			socket.close();
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean validateUser(String userInfo) {
		String[] userInfos = userInfo.split(";;;");
		String username = userInfos[0];
		String password = userInfos[1];
		
		//TODO:此处应验证user是否重复
		
		return validateLogin(username, password);
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

	private String openNewConnection() {
		//TODO: 此处应新建线程、并在新端口建立socket用于message收发，并返回端口号
		
		return "9001";
	}
	
}
