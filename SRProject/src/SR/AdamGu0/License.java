/**
 * 
 */
package SR.AdamGu0;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author AdamGu0
 *
 */
public class License {
	
	private ConcurrentHashMap<String, IdCount> countMap;
	private int capacity;
	private int capacityPerSecond;

	public License(int _capacityTotal, int _capacityPerSecond) {
		capacity = _capacityTotal;
		capacityPerSecond = _capacityPerSecond;
		countMap = new ConcurrentHashMap<String, IdCount>(42);
	}
	
	/**
	 * _idCapacity is the estimated amount of id.<br/><br/>
	 * _idCapacity为预期的id总数。
	 */
	public License(int _capacityTotal, int _capacityPerSecond, int _idCapacity) {
		capacity = _capacityTotal;
		capacityPerSecond = _capacityPerSecond;
		countMap = new ConcurrentHashMap<String, IdCount>(_idCapacity * 2);
	}
	
	/**
	 * Count for the given id; return false if beyond total capacity.<br/>
	 * ( The given id should be added by newId(String id) first )<br/><br/>
	 * 为给出的id计数一次；若超过预设的总容量，则返回false。<br/>
	 * ( 给出的id需要先用 newId(String id)添加  )
	 */
	public boolean count(String id) {
		IdCount idCount = countMap.get(id);
		if (idCount == null) return true;
		
		return idCount.count();
	}
	
	/**
	 * Return false if beyond capacity per second.<br/><br/>
	 * 若超过预设的每秒容量，则返回false。
	 */
	public boolean allowInSecond(String id) {
		IdCount idCount = countMap.get(id);
		if (idCount == null) return true;
		
		return idCount.allowInSecond();
	}
	
	/**
	 * Return false if beyond total capacity.<br/><br/>
	 * 若超过预设的总容量，则返回false。
	 */
	public boolean allowInTotal(String id) {
		IdCount idCount = countMap.get(id);
		if (idCount == null) return true;
		
		return idCount.allowInTotal();
	}
	
	/**
	 * Crate a new id record, or clear the existing record of this id.<br/><br/>
	 * 新建id记录，或清空此id的记录。
	 */
	public void newId(String id) {
		IdCount idCount = countMap.get(id);
		if (idCount == null) {
			idCount = new IdCount(id, capacity, capacityPerSecond);
			countMap.put(id, idCount);
		} else {
			idCount.clear();
		}
	}
	
	/**
	 * Clear the existing record of this id.<br/><br/>
	 * 清空此id的记录。
	 */
	public void clearId(String id) {
		IdCount idCount = countMap.get(id);
		if (idCount != null) idCount.clear();
	}
	
	/**
	 * Remove this id.<br/><br/>
	 * 移除此id。
	 */
	public void removeId(String id) {
		countMap.remove(id);
	}
}
