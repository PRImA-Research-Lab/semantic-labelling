package org.primaresearch.clc.phd.workflow.gui.model.ifelse;

import javax.swing.tree.DefaultMutableTreeNode;

import org.primaresearch.clc.phd.workflow.activity.ifelse.IfCondition;

/**
 * Abstract tree node for if-else condition trees.
 * 
 * @author clc
 *
 */
public abstract class IfElseConditionTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;

	abstract public boolean canCreateChildCondition();
	
	protected IfCondition condition;
	
	public IfElseConditionTreeNode(IfCondition condition) {
		super();
		this.condition = condition;
	}
	
	/**
	 * Optional handler for newly created child conditions. Override if needed.
	 * @param condition The new child activity.
	 */
	public void onChildConditionCreated(IfCondition condition) {
		//Do nothing
	}
	
	/**
	 * Adds child nodes according to the model
	 */
	abstract public void addChildNodes(IfElseConditionsTreeModel model);
	
	abstract public boolean isRemovable();

	public IfCondition getCondition() {
		return condition;
	}
	
	
}
