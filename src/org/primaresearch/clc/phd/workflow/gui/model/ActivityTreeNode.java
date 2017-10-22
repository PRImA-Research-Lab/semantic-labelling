package org.primaresearch.clc.phd.workflow.gui.model;

import org.primaresearch.clc.phd.workflow.activity.Activity;

/**
 * Abstract tree node for workflow activities
 * 
 * @author clc
 *
 */
public abstract class ActivityTreeNode extends WorkflowTreeNode {

	public static final long serialVersionUID = 1L;

	protected Activity activity;

	public ActivityTreeNode(Activity activity) {
		super();
		this.activity = activity;
	}

	public Activity getActivity() {
		return activity;
	}
	
	public void setActivity(Activity a) {
		this.activity = a;
	}
	
	@Override
	public boolean isRemovable() {
		return true;
	}
	
	@Override
	public String toString() {
		return "Activity '" + activity.getCaption() + "'";
	}
}
