package org.primaresearch.clc.phd.workflow.data.port;

/**
 * Special data port for 'for loop' activities (e.g. for start, end, or step width).
 *  
 * @author clc
 *
 */
public interface LoopPort extends InputPort, OutputPort {

	public LoopPort clone();
}
