package org.primaresearch.clc.phd.workflow.gui.model.ifelse;

import javax.swing.tree.TreeNode;

import org.primaresearch.clc.phd.workflow.activity.ifelse.IfCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity.IfBranch;

/**
 * Root node for if-else condition tree
 * 
 * @author clc
 *
 */
public class ConditionsRootNode extends IfElseConditionTreeNode {

	private static final long serialVersionUID = 1L;

	private IfBranch ifBranch;

	public ConditionsRootNode(IfBranch ifBranch) {
		super(null);
		this.ifBranch = ifBranch;
	}

	@Override
	public String toString() {
		if (ifBranch.getCondition() == null)
			return "TRUE";
		return "Conditions";
	}
	
	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public TreeNode getParent() {
		return null;
	}

	@Override
	public boolean canCreateChildCondition() {
		return ifBranch != null && ifBranch.getCondition() == null;
	}
	
	@Override
	public void onChildConditionCreated(IfCondition condition) {
		ifBranch.setCondition(condition);
	}
	
	@Override
	public void addChildNodes(IfElseConditionsTreeModel model) {
		if (ifBranch.getCondition() != null) {
			IfElseConditionTreeNode newNode = model.createConditionTreeNode(this, ifBranch.getCondition());
			newNode.addChildNodes(model);
		}
	}

	@Override
	public boolean isRemovable() {
		return false;
	}
	
	
}
