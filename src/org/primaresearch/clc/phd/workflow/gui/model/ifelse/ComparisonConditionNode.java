package org.primaresearch.clc.phd.workflow.gui.model.ifelse;

import org.primaresearch.clc.phd.workflow.activity.ifelse.ComparisonCondition;

/**
 * Tree node for conditions that compare two values using a specific operator
 * 
 * @author clc
 *
 */
public class ComparisonConditionNode extends IfElseConditionTreeNode {

	private static final long serialVersionUID = 1L;
	private ComparisonCondition comparisonCondition;

	public ComparisonConditionNode(ComparisonCondition condition) {
		super(condition);
		this.comparisonCondition = condition;
	}

	@Override
	public boolean canCreateChildCondition() {
		return false;
	}

	@Override
	public String toString() {
		return "Comparison condition";
	}

	@Override
	public void addChildNodes(IfElseConditionsTreeModel model) {
		//No children allowed
	}

	@Override
	public boolean isRemovable() {
		return true;
	}

}
