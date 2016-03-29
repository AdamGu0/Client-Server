package Server;

import java.util.*;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class testServer extends JFrame {

	private JPanel contentPane;
	public JLabel logLabel;
	 Vector<String> userList;
	
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
		userList = new Vector<String>();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		Thread myThread = new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				runServer();
			}}
		);
		
		JButton btnRun = new JButton("run");
		btnRun.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				btnRun.setEnabled(false);
				logLabel.setText("����9000");
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
			   // ����һ����������ָ���˿�Socket(ServerSocket)����ʼ����
			ServerSocket serverSocket = new ServerSocket(9000);

			while(true) {
				   // ʹ��accept()���������ȴ�����������µ�����
				Socket socket = serverSocket.accept();
				logLabel.setText("��������");

				   // ���������
				InputStream is = socket.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));

				   // ��������
				OutputStream os = socket.getOutputStream();
				PrintWriter pw = new PrintWriter(os, true);

				   // ��ȡ�û�������Ϣ
				String info = br.readLine();
				
				logLabel.setText("�û���ϢΪ��" + info);
				
				   // ��Ӧ��Ϣ
				if(validateUser(info)) {
					logLabel.setText("��½�ɹ�");
					pw.write("accept\n");
					pw.flush();
					new MessageThread(socket, pw, br);
				} else {
					pw.write("error\n");
					pw.flush();
					
					   // �ر���
					pw.close();
					br.close();
					os.close();
					is.close();
					socket.close();
				}
			}

			//serverSocket.close(); //Ӧ�Ƶ����ڹر�ʱ
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean validateUser(String userInfo) {
		String[] userInfos = userInfo.split(";;;");
		String username = userInfos[0];
		String password = userInfos[1];
		
		if (userList.contains(username)) return false; //���ظ�
		if (!validateLogin(username, password)) return false; //��֤����
		
		userList.addElement(username);
		return true;
	}
	
	private boolean validateLogin(String username, String password) {
		
		return true; //����ʱ ������½��֤ ֱ�ӷ���true
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
	
	class MessageThread extends Thread {
		private Socket client;
		private BufferedReader reader;
		private PrintWriter writer;
		
		public MessageThread(Socket s, PrintWriter w, BufferedReader r) throws IOException {
			client = s;
			writer = w;
			reader = r;

			start();
		}
		
		public void run() {
			try {
				while (true) {
					String line = reader.readLine();
				
					logLabel.setText(line);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
