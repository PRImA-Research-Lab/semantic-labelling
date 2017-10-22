package org.primaresearch.clc.phd.workflow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Generator for unique IDs (activities, data ports etc.).
 * 
 * @author clc
 *
 */
public class IdGenerator {

	//private static IdGenerator instance;
	
	/** Map [ID prefix, current number] */
	private Map<String,Integer> counters = new HashMap<String, Integer>();
	
	private Set<String> register = new HashSet<String>();
	
	public IdGenerator() {
	}
	
	//public static IdGenerator getInstance() {
	//	if (instance == null)
	//		instance = new IdGenerator();
	//	return instance;
	//}
	
	/**
	 * Returns a unique ID
	 * 
	 * @param associatedObject Object that will get the ID
	 * @return The ID
	 */
	public String generateId(Object associatedObject) {
		return doGenerateId(associatedObject);
	}
	
	/**
	 * Registers an existing ID
	 * @param id ID to register
	 * @throws IllegalArgumentException ID collision
	 */
	public void registerId(String id) throws IllegalArgumentException {
		doRegisterId(id);
	}
	
	/**
	 * Unregisters an existing ID
	 * @param id ID to unregister
	 */
	public void unregisterId(String id) {
		doUnregisterId(id);
	}
	
	/**
	 * Returns a unique ID
	 * 
	 * @param associatedObject Object that will get the ID
	 * @return The ID
	 */
	private String doGenerateId(Object associatedObject) {
		//Determine prefix
		String prefix = "id";
		if (associatedObject != null)
			prefix = associatedObject.getClass().getSimpleName();
		
		//Get current number for prefix
		Integer counter = counters.get(prefix);
		if (counter == null) //First time
			counter = new Integer(0);
		
		//Find unused ID
		String id = null;
		do {
			//Build ID
			id = prefix + counter.toString();
			
			//Increment counter
			counter = new Integer(counter.intValue() + 1);
		} while (register.contains(id));
		
		//Save counter
		counters.put(prefix, counter);
		
		//Register ID
		register.add(id);
		
		return id;
	}
	
	/**
	 * Registers an existing ID
	 * @param id ID to register
	 * @throws IllegalArgumentException ID collision
	 */
	private void doRegisterId(String id) throws IllegalArgumentException {
		if (register.contains(id))
			throw new IllegalArgumentException("ID collision: "+id);
		
		//Register ID
		register.add(id);
	}
	
	/**
	 * Unregisters an existing ID
	 * @param id ID to unregister
	 */
	private void doUnregisterId(String id) {
		//Unregister ID
		register.remove(id);
	}
}
