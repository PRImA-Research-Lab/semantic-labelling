package org.primaresearch.clc.phd.workflow.gui.model.ifelse;

import org.primaresearch.clc.phd.workflow.activity.ifelse.InputPortCondition;

/**
 * Tree node for if-else conditions that evaluate an input data port
 * 
 * @author clc
 *
 */
public class InputPortConditionNode extends IfElseConditionTreeNode {

	private static final long serialVersionUID = 1L;
	private InputPortCondition inputPortCondition;

	public InputPortConditionNode(InputPortCondition condition) {
		super(condition);
		this.inputPortCondition = condition;
	}

	@Override
	public boolean canCreateChildCondition() {
		return false;
	}

	@Override
	public String toString() {
		if (inputPortCondition != null && inputPortCondition.getInputPort() != null)
			return "Input port condition '"+inputPortCondition.getInputPort().getDataObject().getCaption()+"'";
		return "Input port condition";
	}

	@Override
	public void addChildNodes(IfElseConditionsTreeModel model) {
		//No children allow
	}
	
	@Override
	public boolean isRemovable() {
		return true;
	}

}
