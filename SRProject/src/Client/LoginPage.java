package Client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginPage extends JFrame {

	private static final long serialVersionUID = -8492871005690051762L;

	private JPanel contentPanel;
	private JTextField accountField;
	private JPasswordField passwordField;
	private JTextField serverField;

	/**
	 * Create the frame.
	 */
	public LoginPage() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 250, 250);
		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);
		contentPanel.setLayout(null);
		
		JLabel serverLabel = new JLabel("\u670D\u52A1\u5668\uFF1A");
		serverLabel.setBounds(30, 30, 60, 20);
		contentPanel.add(serverLabel);
		
		JLabel accountLabel = new JLabel("\u7528\u6237\u540D\uFF1A");
		accountLabel.setBounds(30, 60, 60, 20);
		contentPanel.add(accountLabel);
		
		JLabel passwordLabel = new JLabel("\u5BC6\u7801\uFF1A");
		passwordLabel.setBounds(30, 90, 60, 20);
		contentPanel.add(passwordLabel);
		
		JLabel hintLabel = new JLabel("");
		hintLabel.setBounds(30, 166, 174, 20);
		contentPanel.add(hintLabel);
		
		serverField = new JTextField();
		serverField.setText("127.0.0.1");
		serverField.setBounds(90, 30, 114, 20);
		contentPanel.add(serverField);
		
		accountField = new JTextField();
		accountField.setBounds(90, 60, 114, 20);
		contentPanel.add(accountField);

		passwordField = new JPasswordField();
		passwordField.setBounds(90, 90, 114, 20);
		contentPanel.add(passwordField);
		
		JButton loginButton = new JButton("\u767B\u9646");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				hintLabel.setText("µÇÂ½ÖÐ...");
				Main m = Main.getMain();
                                String username = accountField.getText();
                                char[] pass = passwordField.getPassword();
                                String password = String.copyValueOf(pass);
				if(!validate_login(username,password)) {
					hintLabel.setText("µÇÂ½Ê§°Ü£¬ÇëÖØÊÔ¡£");
				}
			}
		});
		loginButton.setBounds(87, 136, 60, 20);
		contentPanel.add(loginButton);
		

	}
        private boolean validate_login(String username,String password) {
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
}
}

