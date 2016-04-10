/**
 * 
 */
package SR.AdamGu0;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 本类用于记录每个id的请求次数，并依据预设的总容量和每秒容量，判断是否应响应其请求。<br/>
 * 对同一id的操作不是线程安全的。
 * @author AdamGu0
 */
public class License {
	
	private ConcurrentHashMap<String, IdCount> countMap;
	private int capacity;
	private int capacityPerSecond;

	/**
	 * @param _capacityTotal Total capacity allowed for each id.<br/>
	 * _capacityTotal：每个id允许的总容量。<br/><br/>
	 * @param _capacityPerSecond Capacity allowed per second for each id.<br/>
	 * _capacityPerSecond：每个id每秒允许的容量。<br/><br/>
	 * @exception RuntimeException if any parameter is invalid(<=0).<br/>
	 * 如果任何参数无效（<=0），则抛出异常。
	 */
	public License(int _capacityTotal, int _capacityPerSecond) {
		if (_capacityTotal <= 0 || _capacityPerSecond <= 0)
			throw new RuntimeException("wrong parameter when crate Lincense.");
		capacity = _capacityTotal;
		capacityPerSecond = _capacityPerSecond;
		countMap = new ConcurrentHashMap<String, IdCount>(42);
	}
	
	/**
	 * @param _capacityTotal Total capacity allowed for each id.<br/>
	 * _capacityTotal：每个id允许的总容量。<br/><br/>
	 * @param _capacityPerSecond Capacity allowed per second for each id.<br/>
	 * _capacityPerSecond：每个id每秒允许的容量。<br/><br/>
	 * @param _idCapacity The estimated amount of id.<br/>_idCapacity：预期的id总数。<br/><br/>
	 * @exception RuntimeException if any parameter is invalid(<=0).<br/>
	 * 如果任何参数无效（<=0），则抛出异常。
	 */
	public License(int _capacityTotal, int _capacityPerSecond, int _idCapacity) {
		if (_capacityTotal <= 0 || _capacityPerSecond <= 0 || _idCapacity <= 0)
			throw new RuntimeException("wrong parameter when crate Lincense.");
		capacity = _capacityTotal;
		capacityPerSecond = _capacityPerSecond;
		countMap = new ConcurrentHashMap<String, IdCount>(_idCapacity * 2);
	}
	
	/**
	 * Count for the given id. <br/>为给出的id计数一次。
	 * @param id should be added by newId(String id) first.<br/>id需要先用 newId(String id)添加。
	 * @return Return false if beyond total capacity.<br/>若超过预设的总容量，则返回false。<br/>
	 */
	public boolean count(String id) {
		IdCount idCount = countMap.get(id);
		if (idCount == null) return true;
		
		return idCount.count();
	}
	
	/**
	 * @return Return false if beyond capacity per second.<br/>若超过预设的每秒容量，则返回false。
	 */
	public boolean allowInSecond(String id) {
		IdCount idCount = countMap.get(id);
		if (idCount == null) return true;
		
		return idCount.allowInSecond();
	}
	
	/**
	 * @return Return false if beyond total capacity.<br/>若超过预设的总容量，则返回false。<br/>
	 */
	public boolean allowInTotal(String id) {
		IdCount idCount = countMap.get(id);
		if (idCount == null) return true;
		
		return idCount.allowInTotal();
	}
	
	/**
	 * Crate a new id record, or clear the existing record of this id.<br/>
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
	 * Clear the existing record of this id.<br/>
	 * 清空此id的记录。
	 */
	public void clearId(String id) {
		IdCount idCount = countMap.get(id);
		if (idCount != null) idCount.clear();
	}
	
	/**
	 * Remove this id.<br/>
	 * 移除此id。
	 */
	public void removeId(String id) {
		countMap.remove(id);
	}
}
