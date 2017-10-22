package org.primaresearch.clc.phd.repository;

import java.util.Collection;

import org.primaresearch.clc.phd.workflow.Workflow;

/**
 * Interface for repositories holding workflows
 * 
 * @author clc
 *
 */
public interface WorkflowRepository {

	/** The number of workflows in this repository */
	public int getWorkflowCount();
	
	/** Returns the workflow with the given position */
	public Workflow getWorkflow(int index);
	
	/** Deletes the workflow with the given position */
	public void deleteWorkflow(int index);
	
	/** Refresh the list of workflows (add/remove/update) */
	public void refresh();
	
	/**
	 * Returns all workflows of this repository
	 */
	public Collection<Workflow> getWorkflows();
	
	/**
	 * ID of the repository
	 */
	public String getId();
}
