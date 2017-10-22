package org.primaresearch.clc.phd.workflow.data.port;

import java.util.Collection;

/**
 * Data output port for workflow activities
 * 
 * @author clc
 *
 */
public interface OutputPort extends DataPort {

	public OutputPort clone();

	public void addForwardedPort(OutputPort forwarded);
	public void removeForwardedPort(OutputPort forwarded);
	public Collection<OutputPort> getForwardedPorts();
	
	public String getType();
	
	public void setType(String type);

}
