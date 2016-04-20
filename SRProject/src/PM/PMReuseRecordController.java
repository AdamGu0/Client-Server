package PM;

import teamEleven.record.RecordController;

public class PMReuseRecordController {
	private RecordController recordController;
	
	public void getInstance(){
		recordController = RecordController.getInstance();
	}
	
	public void setAndStart(int saveCycle){
		recordController.getSaveCycle();
	}
	
	public void getSaveCycle(){
		recordController.getSaveCycle();
	}
	
	public void reset() {
		recordController.reset();
	}

	public void receivedNumberAdd() {
		recordController.receivedNumberAdd();
	}

	public void ignoredNumberAdd() {
		recordController.ignoredNumberAdd();
	}

	public void forwardedNumberAdd() {
		recordController.forwardedNumberAdd();
	}

	public void logsucceedNumberAdd() {
		recordController.logsucceedNumberAdd();
	}
	
	public void logfailedNumberAdd() {
		recordController.logfailedNumberAdd();
	}

	public void save() {
		recordController.save();
	}
	
	public void quit(){
		recordController.quit();
	}
	
}
