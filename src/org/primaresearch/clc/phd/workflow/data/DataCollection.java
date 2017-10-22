package org.primaresearch.clc.phd.workflow.data;

/**
 * Interface for a collection of data objects. A collection is a data object itself.
 * 
 * @author clc
 *
 */
public interface DataCollection extends DataObject {

	/**
	 * Number of data items in this collection
	 */
	public int getSize();
	
	/**
	 * Returns a data item from within this collection
	 */
	public DataObject getDataItem(int index);

	/**
	 * Adds a new child data item
	 */
	public void addDataItem(DataObject item);
	
	/**
	 * Removes a child data item
	 */
	public void removeDataItem(int index);
}
