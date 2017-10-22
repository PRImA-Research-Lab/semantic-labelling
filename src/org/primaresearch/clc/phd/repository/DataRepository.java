package org.primaresearch.clc.phd.repository;

import java.util.Collection;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.data.DataTable;

/**
 * Interface for repositories holding data sources that can be used in workflows
 * 
 * @author clc
 *
 */
public interface DataRepository {

	/** The number of data sources in this repository */
	public int getDataSourceCount();
	
	/** Returns the data source at the given position */
	public Workflow getDataSource(int index);
	
	/** Deletes the data source at the given position */
	public void deleteDataSource(int index);
	
	/** Refresh the list of data sources (add/remove/update) */
	public void refresh();
	
	/**
	 * Returns all data sources of this repository
	 */
	public Collection<DataTable> getDataSources();
}
