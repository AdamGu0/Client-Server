package Server;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
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
				}

			   // ��Ӧ��Ϣ
			String reply = "9001";
			pw.write(reply);
			pw.flush();
			logLabel.setText("�ظ��˿ں�");
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
}
