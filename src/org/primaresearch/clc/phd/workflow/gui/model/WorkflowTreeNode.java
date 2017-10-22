package org.primaresearch.clc.phd.workflow.gui.model;

import javax.swing.tree.DefaultMutableTreeNode;

import org.primaresearch.clc.phd.workflow.activity.Activity;

/**
 * Abstract tree node for workflow trees
 * 
 * @author clc
 *
 */
public abstract class WorkflowTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;

	abstract public boolean canCreateChildActivity();
	
	abstract public boolean canCreateChildDataTable();
	
	public WorkflowTreeNode() {
		super();
	}
	
	/**
	 * Optional handler for newly created child activities. Override if needed.
	 * @param activity The new child activity.
	 */
	public void onChildActivityCreated(Activity activity) {
		//Do nothing
	}
	
	/**
	 * Adds child nodes according to the model
	 */
	abstract public void addChildNodes(WorkflowTreeModel model);
	
	abstract public boolean isRemovable();
}
