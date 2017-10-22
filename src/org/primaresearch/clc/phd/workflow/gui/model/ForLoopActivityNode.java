package org.primaresearch.clc.phd.workflow.gui.model;

import javax.swing.tree.TreeNode;

import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ForLoopActivity;

/**
 * Tree node for 'for loop' activities
 * 
 * @author clc
 *
 */
public class ForLoopActivityNode extends ActivityTreeNode implements TreeNode {

	private static final long serialVersionUID = 1L;
	private ForLoopActivity forLoopActivity;

	public ForLoopActivityNode(Activity activity) {
		super(activity);
		forLoopActivity = (ForLoopActivity)activity;
	}

	@Override
	public boolean canCreateChildActivity() {
		return forLoopActivity.getChildActivity() == null;
	}

	@Override
	public boolean canCreateChildDataTable() {
		return false;
	}

	@Override
	public String toString() {
		return "For loop '" + activity.getCaption() + "'";
	}
	
	/**
	 * Handler for newly created child activities.
	 *
	 * @param activity The new child activity.
	 */
	@Override
	public void onChildActivityCreated(Activity activity) {
		forLoopActivity.setChildActivity(activity);
	}

	@Override
	public void addChildNodes(WorkflowTreeModel model) {
		if (forLoopActivity.getChildActivity() != null) {
			WorkflowTreeNode newNode = model.createActivityTreeNode(this, forLoopActivity.getChildActivity());
			newNode.addChildNodes(model);
		}
	}
}
