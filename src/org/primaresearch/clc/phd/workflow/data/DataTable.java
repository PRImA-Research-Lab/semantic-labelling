package org.primaresearch.clc.phd.workflow.data;

import java.util.List;

import org.primaresearch.clc.phd.workflow.data.port.DataTableColumn;

/**
 * Aggregation of data collections which represent table columns
 * 
 * @author clc
 *
 */
public interface DataTable {

	/**
	 * The ID of this data table
	 */
	public String getId();
	
	/**
	 * The ID of this data table
	 */
	public void setId(String id);
	
	/** Caption of this data table (e.g. for displaying in graphical user interface) */
	public String getCaption();
	/** Caption of this data table (e.g. for displaying in graphical user interface) */
	public void setCaption(String caption);

	/** Description of this data table */
	public String getDescription();
	/** Description of this data table (e.g. for displaying in graphical user interface) */
	public void setDescription(String descr);

	/**
	 * Returns all data ports of this data source
	 * @return Collection of output ports
	 */
	public List<DataTableColumn> getColumns();
	
	/**
	 * Adds a new data column
	 */
	public DataTableColumn addColumn();

	/**
	 * Adds the given data column
	 */
	public void addColumn(DataTableColumn column);
	
	/**
	 * Gets a data column by ID
	 * @return The column or <code>null</code>
	 */
	public DataTableColumn getColumnById(String id);

	/**
	 * Gets a data column by caption
	 * @return The column or <code>null</code>
	 */
	public DataTableColumn getColumnByCaption(String caption);
}
