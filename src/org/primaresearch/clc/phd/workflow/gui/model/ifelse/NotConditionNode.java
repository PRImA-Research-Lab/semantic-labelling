package org.primaresearch.clc.phd.workflow.gui.model.ifelse;

import javax.swing.tree.TreeNode;

import org.primaresearch.clc.phd.workflow.activity.ifelse.IfCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.NotCondition;

/**
 * Tree node for 'NOT' if-else condition
 * 
 * @author clc
 *
 */
public class NotConditionNode extends IfElseConditionTreeNode implements TreeNode {

	private static final long serialVersionUID = 1L;
	private NotCondition notCondition;

	public NotConditionNode(NotCondition condition) {
		super(condition);
		notCondition = condition;
	}

	@Override
	public boolean canCreateChildCondition() {
		return notCondition.getChildCondition() == null;
	}

	@Override
	public String toString() {
		return "NOT";
	}
	
	@Override
	public void onChildConditionCreated(IfCondition condition) {
		notCondition.setChildCondition(condition);
	}

	@Override
	public void addChildNodes(IfElseConditionsTreeModel model) {
		if (notCondition.getChildCondition() != null) {
			IfElseConditionTreeNode newNode = model.createConditionTreeNode(this, notCondition.getChildCondition());
			newNode.addChildNodes(model);
		}
	}

	@Override
	public boolean isRemovable() {
		return true;
	}
}
