package org.primaresearch.clc.phd.workflow.data.port;

import java.util.Collection;

/**
 * Data input port for workflow activities
 * 
 * @author clc
 *
 */
public interface InputPort extends DataPort {

	public InputPort clone();

	/**
	 * The source of the input port (usually an output port of another activity)
	 * @return a data port or null (no source)
	 */
	public DataPort getSource();
	
	/**
	 * The source of the input port (usually an output port of another activity)
	 */
	public void setSource(DataPort source);
	
	public Collection<String> getAllowedTypes();
	
	public void addAllowedType(String type);
	
	public void removeAllowedType(String type);

}
