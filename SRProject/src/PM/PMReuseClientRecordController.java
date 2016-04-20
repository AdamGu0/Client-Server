package PM;

import teamEleven.record.ClientRecordController;

public class PMReuseClientRecordController {
	private ClientRecordController clientRecordController;
	
	public void getInstance(){
		clientRecordController = ClientRecordController.getInstance();
	}
	
	public void setAndStart(int saveCycle){
		clientRecordController.setAndStart(saveCycle);
	}
	
	public void reset(){
		clientRecordController.reset();
	}
	
	public void setUser(String username){
		clientRecordController.setUser(username);
	}
	
	public void loginFailedCountAdd(){
		clientRecordController.loginFailedCountAdd();
	}
	
	public void loginSucceedCountAdd(){
		clientRecordController.loginSucceedCountAdd();
	}
	
	public void receiveNumAdd(){
		clientRecordController.receiveNumAdd();
	}
	
	public void sendNumAdd(){
		clientRecordController.sendNumAdd();
	}
	
	public void save(){
		clientRecordController.save();
	}
	
	public void quit(){
		clientRecordController.quit();
	}
	
	
	
}
