package Client;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

public class MessagePage extends JFrame {

	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public MessagePage() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		Main m = Main.getMain();
		
		JLabel idLabel = new JLabel("id");
		contentPane.add(idLabel, BorderLayout.NORTH);
		idLabel.setText("id:" + m._id + " at port:" + m._serverPort);
	}

}
