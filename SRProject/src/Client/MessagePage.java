package Client;

import java.awt.BorderLayout;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MessagePage extends JFrame {

	private JPanel contentPane;
	private JTextField messageField;
	private JTextArea messageArea;
	
	public Socket socket;
	private PrintWriter writer;
	public BufferedReader reader;
	public int receiveCount;
	public int sendCount;
	/**
	 * Create the frame.
	 */
	public MessagePage(Socket s, PrintWriter w, BufferedReader r) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				logout();
			}
		});
		socket = s;
		writer = w;
		reader = r;
		receiveCount = 0;
		sendCount = 0;
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 412, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		Main m = Main.getMain();
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		messageArea = new JTextArea();
		scrollPane.setViewportView(messageArea);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		JLabel idLabel = new JLabel("id");
		idLabel.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(idLabel);
		idLabel.setText("用户名:" + m.id + " 群组:" + m.group + " 服务器:" + m.serverIP);
		
		JButton logoutButton = new JButton("\u767B\u51FA");
		logoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logout();
			}
		});
		logoutButton.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(logoutButton);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		
		messageField = new JTextField();
		messageField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if ( e.getKeyCode() == KeyEvent.VK_ENTER) {
					String message = messageField.getText();
					if ( !message.isEmpty() ) sendMessage(message);
					messageField.setText("");
				}
			}
		});
		messageField.setColumns(20);
		panel_1.add(messageField);
		
		JButton sendButton = new JButton("\u53D1\u9001");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = messageField.getText();
				if ( !message.isEmpty() ) sendMessage(message);
				messageField.setText("");
			}
		});
		sendButton.setHorizontalAlignment(SwingConstants.TRAILING);
		panel_1.add(sendButton);
		
		new ReadThread();
	}

	private void logout() {
		try {
			writer.println("LOGOUT");
			writer.flush();
			writer.close();
			reader.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Main.getMain().closeFile();
		dispose();
		LoginPage lp = new LoginPage();
		lp.setVisible(true);
	}

	private void sendMessage(String m) {
		showMessage("\t\t\t" + m);
		writer.println(m);
		writer.flush();
		sendCount++;
	}
	
	private void showMessage(String m) {
		messageArea.setText(messageArea.getText() + m + "\n");
		messageArea.setCaretPosition(messageArea.getText().length());//使滚动条到最底部
	}
	
	class ReadThread extends Thread {
		
		public ReadThread() {
			start();
		}
		
		public void run() {
			try {
				while (true) {
					String line = reader.readLine();
					if (line == null) {
						showMessage("连接已断开，请重新登录。");
						return;
					}
					receiveCount++;
					showMessage(line);
					Main.getMain().writeMessageToFile(line + "\n");
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				showMessage("连接已断开，请重新登录。");
			}
		}
	}

}
