/**
 * 
 */
package SR.AdamGu0;

import java.util.LinkedList;

/**
 * @author AdamGu0
 *
 */
class IdCount {
	//private String id;
	private int capacity;
	private int capacityPerSecond;
	
	private int count;
	private LinkedList<Long> countTimestamps;
	
	protected IdCount(String _id, int _capacity, int _capacityPerSecond) {
		//id = _id;
		capacity = _capacity;
		capacityPerSecond = _capacityPerSecond;
		
		count = 0;
		countTimestamps = new LinkedList<Long>();
		for (int i = 0; i < capacityPerSecond + 1; i++) {
			countTimestamps.add(new Long(0));
		}
	}
	
	protected boolean count() {
		count++;
		addTimeStamp();
		return allowInTotal();
	}
	
	private void addTimeStamp() {
		countTimestamps.add(System.currentTimeMillis());
		countTimestamps.removeFirst();
	}
	
	protected boolean allowInSecond() {
		return ( ( countTimestamps.getLast().longValue() - countTimestamps.getFirst().longValue() ) > 1000 );
	}
	
	protected boolean allowInTotal() {
		return ( count <= capacity );
	}
	
	protected void clear() {
		count = 0;
		
		countTimestamps.clear();
		for (int i = 0; i < capacityPerSecond + 1; i++) {
			countTimestamps.add(new Long(0));
		}
	}
}
