package org.primaresearch.clc.phd.workflow.activity.ifelse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * IfCondition implementation that combined several child conditions using a specified operator (AND, OR).
 * 
 * @author clc
 *
 */
public class CombinedCondition implements IfCondition {
	private boolean usesAndOperator;
	private List<IfCondition> childConditions = new ArrayList<IfCondition>();
	
	/**
	 * Constructor
	 * @param usesAndOperator If <code>true</code> the child conditions are linked with the AND operator, otherwise with the OR operator.
	 */
	public CombinedCondition(boolean usesAndOperator) {
		this.usesAndOperator = usesAndOperator;
	}
	
	/**
	 * Adds a child condition that is evaluated and combined with its siblings using the predefined operator (AND or OR)
	 * @param cond Condition
	 */
	public void addChildCondition(IfCondition cond) {
		childConditions.add(cond);
	}
	
	/**
	 * Returns all child conditions of this condition
	 */
	public List<IfCondition> getChildConditions() {
		return childConditions;
	}
	
	/**
	 * If <code>true</code> the child conditions are linked with the AND operator, otherwise with the OR operator.
	 */
	public boolean usesAndOperator() {
		return usesAndOperator;
	}

	@Override
	public boolean IsTrue() {
		if (usesAndOperator) {
			for (Iterator<IfCondition> it = childConditions.iterator(); it.hasNext(); ) {
				if (!it.next().IsTrue())
					return false;
			}
			return true;
		} //Or 
		else {
			for (Iterator<IfCondition> it = childConditions.iterator(); it.hasNext(); ) {
				if (it.next().IsTrue())
					return true;
			}
		}
		return false;
	}
	
	@Override
	public IfCondition clone() {
		CombinedCondition copy = new CombinedCondition(usesAndOperator);
		for (int i=0; i<childConditions.size(); i++)
			copy.addChildCondition(childConditions.get(i).clone());
		return copy;
	}
	
}