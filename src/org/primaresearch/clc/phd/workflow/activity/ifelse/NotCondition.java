package org.primaresearch.clc.phd.workflow.activity.ifelse;

/**
 * Implements NOT operator for a child condition
 *  
 * @author clc
 *
 */
public class NotCondition implements IfCondition {

	private IfCondition childCondition;
	
	/**
	 * Empty constructor. Call 'setChildCondition' before using this condition
	 */
	public NotCondition() {
	}

	/**
	 * Constructor
	 * @param childCondition One and only child condition that is being negated by this condition
	 */
	public NotCondition(IfCondition childCondition) {
		this.childCondition = childCondition;
	}
	
	/**
	 * Sets the one and only child condition that is being negated by this condition
	 * @param childCondition
	 */
	public void setChildCondition(IfCondition childCondition) {
		this.childCondition = childCondition;
	}
	
	/**
	 * Returns the the one and only child condition that is being negated by this condition
	 */
	public IfCondition getChildCondition() {
		return childCondition;
	}

	@Override
	public boolean IsTrue() {
		return !childCondition.IsTrue();
	}

	@Override
	public IfCondition clone() {
		NotCondition copy = new NotCondition();
		copy.setChildCondition(childCondition.clone());
		return copy;
	}
}
