package org.primaresearch.clc.phd.workflow.gui.model.ifelse;

import java.util.Iterator;

import org.primaresearch.clc.phd.workflow.activity.ifelse.CombinedCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfCondition;

/**
 * Tree node for 'AND' or 'OR' multi-conditions
 * 
 * @author clc
 *
 */
public class CombinedConditionNode extends IfElseConditionTreeNode  {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private IfElseConditionsTreeModel treeModel;
	CombinedCondition combinedCondition;

	public CombinedConditionNode(CombinedCondition combinedCondition, IfElseConditionsTreeModel treeModel) {
		super(combinedCondition);
		this.combinedCondition = combinedCondition;
		this.treeModel = treeModel;
	}

	@Override
	public boolean canCreateChildCondition() {
		return true;
	}

	@Override
	public String toString() {
		return combinedCondition.usesAndOperator() ? "AND" : "OR";
	}

	@Override
	public void onChildConditionCreated(IfCondition condition) {
		combinedCondition.addChildCondition(condition);
	}

	@Override
	public void addChildNodes(IfElseConditionsTreeModel model) {
		
		for (Iterator<IfCondition> it = combinedCondition.getChildConditions().iterator(); it.hasNext(); ) {
			IfCondition cond = it.next();
			
			IfElseConditionTreeNode newNode = model.createConditionTreeNode(this, cond);
			newNode.addChildNodes(model);
		}
	}

	@Override
	public boolean isRemovable() {
		return true;
	}


}
