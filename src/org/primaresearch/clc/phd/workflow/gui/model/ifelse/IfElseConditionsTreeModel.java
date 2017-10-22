package org.primaresearch.clc.phd.workflow.gui.model.ifelse;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ifelse.CombinedCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.ComparisonCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.InputPortCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.NotCondition;
import org.primaresearch.clc.phd.workflow.gui.model.ActivityTreeNode;
import org.primaresearch.clc.phd.workflow.gui.model.WorkflowTreeNode;

/**
 * Tree model for if-else conditions
 * @author clc
 *
 */
public class IfElseConditionsTreeModel extends DefaultTreeModel {

	private static final long serialVersionUID = 1L;

	public IfElseConditionsTreeModel(TreeNode root) {
		super(root);
	}

	public IfElseConditionTreeNode createConditionTreeNode(IfElseConditionTreeNode parentNode, IfCondition condition) {
		IfElseConditionTreeNode newNode = null;
		
		//Combined condition
		if (condition instanceof CombinedCondition)
			newNode = new CombinedConditionNode((CombinedCondition)condition, this);
		//Comparison condition
		else if (condition instanceof ComparisonCondition)
			newNode = new ComparisonConditionNode((ComparisonCondition)condition);
		//InputPort condition
		else if (condition instanceof InputPortCondition)
			newNode = new InputPortConditionNode((InputPortCondition)condition);
		//Not-condition
		else if (condition instanceof NotCondition)
			newNode = new NotConditionNode((NotCondition)condition);
		
		if (newNode != null)
			parentNode.add(newNode);
		
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
