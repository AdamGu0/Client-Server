package Client;

public class Main {

	private static Main _m;
	private Main() {}
	
	public static Main getMain() {
		if ( _m == null) _m = new Main();
		return _m;
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		
		try {
			LoginPage frame = new LoginPage();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void login(String serverAddress, String account, char[] password) {
		//
	}
	
}
