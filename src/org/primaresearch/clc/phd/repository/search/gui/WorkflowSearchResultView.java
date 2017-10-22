package org.primaresearch.clc.phd.repository.search.gui;

import java.util.Collection;

import javax.swing.JComponent;

import org.primaresearch.clc.phd.workflow.Workflow;

/**
 * View interface for workflow search results from a repository.
 * 
 * @author clc
 *
 */
public interface WorkflowSearchResultView {

	public void update(Collection<Workflow> workflows);
	
	public JComponent getComponent();
	
	public void addListener(WorkflowSearchResultItemListener listener);
	
	public void removeListener(WorkflowSearchResultItemListener listener);

	
	/**
	 * Listener interface for result items of workflow search 
	 * 
	 * @author clc
	 *
	 */
	public static interface WorkflowSearchResultItemListener {
		/** Result item has been clicked on */
		public void workflowSearchResultItemClicked(Workflow workflow);
	}
}
