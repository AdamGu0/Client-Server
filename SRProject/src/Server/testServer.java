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
			logLabel.setText("����9000");
			   // ����һ����������ָ���˿�Socket(ServerSocket)����ʼ����
			ServerSocket serverSocket = new ServerSocket(9000);

			   // ʹ��accept()���������ȴ�����������µ�����
			Socket socket = serverSocket.accept();

			   // ���������
			InputStream is = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			   // ��������
			OutputStream os = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(os);

			logLabel.setText("�ȴ���Ϣ");
			   // ��ȡ�û�������Ϣ
			String info = null;
			while (!((info = br.readLine()) == null)) {
				logLabel.setText("���Ƿ��������û���ϢΪ��" + info);
				   // ��Ӧ��Ϣ
				String reply;
				if(validateUser(info)) {
					reply = openNewConnection();//��������ڵ�˵��
				}
				else reply = "error";
				pw.write(reply);
				pw.flush();
				logLabel.setText("reply");
				
				info = null;
			}


			   // �ر���
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
		
		//TODO:�˴�Ӧ��֤user�Ƿ��ظ�
		
		return validateLogin(username, password);
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

	private String openNewConnection() {
		//TODO: �˴�Ӧ�½��̡߳������¶˿ڽ���socket����message�շ��������ض˿ں�
		
		return "9001";
	}
	
}
