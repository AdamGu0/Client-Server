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

public class MessagePage extends JFrame {

	private JPanel contentPane;
	private JTextField messageField;
	public JTextArea messageArea;
	
	public Socket socket;
	private PrintWriter writer;
	public BufferedReader reader;
	/**
	 * Create the frame.
	 */
	public MessagePage(Socket s, PrintWriter w, BufferedReader r) {
		socket = s;
		writer = w;
		reader = r;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 412, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		Main m = Main.getMain();
		
		messageArea = new JTextArea();
		messageArea.setEditable(false);
		contentPane.add(messageArea, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		JLabel idLabel = new JLabel("id");
		idLabel.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(idLabel);
		idLabel.setText("用户名:" + m._id + " 服务器:" + m._serverIP);
		
		JButton logoutButton = new JButton("\u767B\u51FA");
		logoutButton.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(logoutButton);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		
		messageField = new JTextField();
		messageField.setColumns(20);
		panel_1.add(messageField);
		
		JButton sendButton = new JButton("\u53D1\u9001");
		sendButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String message = messageField.getText();
				if (!message.isEmpty()) sendMessage(message);
				messageField.setText("");
			}
		});
		sendButton.setHorizontalAlignment(SwingConstants.TRAILING);
		panel_1.add(sendButton);
		new ReadThread();
	}

	private void sendMessage(String m) {
		messageArea.setText(messageArea.getText() + "\t\t\t" + m + "\n");
		writer.println(m);
		writer.flush();
	}
	
	class ReadThread extends Thread {
		
		public ReadThread() {
			start();
		}
		
		public void run() {
			try {
				while (true) {
					String line = reader.readLine();
					messageArea.setText(messageArea.getText() + line + "\n");
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
