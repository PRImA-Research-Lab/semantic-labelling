package org.primaresearch.clc.phd.workflow.gui.model;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.AtomicActivity;
import org.primaresearch.clc.phd.workflow.activity.DirectedGraphActivity;
import org.primaresearch.clc.phd.workflow.activity.ForLoopActivity;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity;
import org.primaresearch.clc.phd.workflow.data.DataTable;

/**
 * Tree model for workflows (includes workflow root and all activities).
 * @author clc
 *
 */
public class WorkflowTreeModel extends DefaultTreeModel {

	private static final long serialVersionUID = 1L;

	public WorkflowTreeModel(TreeNode root) {
		super(root);
	}

	public ActivityTreeNode createActivityTreeNode(WorkflowTreeNode parentNode, Activity activity) {
		ActivityTreeNode newNode = null;
		
		//Atomic activity
		if (activity instanceof AtomicActivity)
			newNode = new AtomicActivityNode((AtomicActivity)activity);
		//For loop activity
		else if (activity instanceof ForLoopActivity)
			newNode = new ForLoopActivityNode((ForLoopActivity)activity);
		//Directed graph activity
		else if (activity instanceof DirectedGraphActivity)
			newNode = new DirectedGraphActivityNode((DirectedGraphActivity)activity, this);
		//If-else activity
		else if (activity instanceof IfElseActivity)
			newNode = new IfElseActivityNode((IfElseActivity)activity, this);
		
		if (newNode != null)
			parentNode.add(newNode);
		
		nodeStructureChanged(parentNode);
		
		return newNode;
	}
	
	public DataTableTreeNode createDataTableTreeNode(WorkflowTreeNode parentNode, DataTable dataTable) {
		DataTableTreeNode newNode = new DataTableTreeNode(dataTable);
		if (newNode != null)
			parentNode.insert(newNode, 0);
		
		nodeStructureChanged(parentNode);
		
		return newNode;
	}
	
	/**
	 * Tries to find an activity node within this tree that contains the specified activity
	 */
	public ActivityTreeNode findActivityNode(Activity activity) {
		if (getRoot() != null) {
			return findActivityNode(activity, (WorkflowTreeNode)getRoot());
		}
		return null;
	}
	
	/**
	 * Tries to find (recursively) an activity node within this subtree that contains the specified activity
	 * @param activity Activity to be found
	 * @param startNode Start searching from here
	 * @return Activity node or null
	 */
	private ActivityTreeNode findActivityNode(Activity activity, WorkflowTreeNode startNode) {
		if (startNode == null)
			return null;
		
		//Have we found the node?
		if (startNode instanceof ActivityTreeNode) {
			if (((ActivityTreeNode)startNode).getActivity() == activity)
				return (ActivityTreeNode)startNode;
		}
		
		//Check children
		for (int i=0; i<startNode.getChildCount(); i++) {
			ActivityTreeNode n = findActivityNode(activity, (WorkflowTreeNode) startNode.getChildAt(i));
			if (n != null)
				return n;
		}
		
		return null;
	}
}
