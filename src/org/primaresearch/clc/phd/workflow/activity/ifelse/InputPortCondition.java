package org.primaresearch.clc.phd.workflow.activity.ifelse;

import org.primaresearch.clc.phd.workflow.data.port.IfElsePort;

/**
 * Condition that evaluates the value of an input port (that can be of type bool or int)
 * 
 * @author clc
 *
 */
public class InputPortCondition implements IfCondition {

	private IfElsePort inputPort;
	
	public InputPortCondition(IfElsePort inputPort) {
		this.inputPort = inputPort;
	}

	/**
	 * Empty constructor. Call setInputPort later
	 */
	public InputPortCondition() {
	}
	
	@Override
	public boolean IsTrue() {
		//TODO
		return false;
	}

	@Override
	public IfCondition clone() {
		return new InputPortCondition(inputPort != null ? inputPort.clone() : null);
	}

	public IfElsePort getInputPort() {
		return inputPort;
	}

	public void setInputPort(IfElsePort inputPort) {
		this.inputPort = inputPort;
	}
	
}
