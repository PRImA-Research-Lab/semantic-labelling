package org.primaresearch.clc.phd.repository.search;

import java.util.Collection;

import org.primaresearch.clc.phd.workflow.Workflow;

/**
 * Interface for filters that can be applied to an workflow list.
 * 
 * @author clc
 *
 */
public interface WorkflowFilter extends Filter {

	/**
	 * Applies the filter to the given list of workflows.
	 * @param toBeFiltered Input list
	 * @return Filtered list
	 */
	public Collection<Workflow> filterWorkflows(Collection<Workflow> toBeFiltered);
	
	/**
	 * Initialises the filter (filtering should let through all workflows)
	 * @param allWorkflows Unfiltered list of workflows
	 */
	public void init(Collection<Workflow> allWorkflows);

}
