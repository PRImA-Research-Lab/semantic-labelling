package org.primaresearch.clc.phd.workflow.activity.ifelse;

/**
 * Interface for if-else conditions such as NOT condition or input port condition.
 * 
 * @author clc
 *
 */
public interface IfCondition {
	/** Checks if this condition resolves to <code>true</code> */
	public boolean IsTrue();
	
	/** Creates a deep copy */
	public IfCondition clone();
}