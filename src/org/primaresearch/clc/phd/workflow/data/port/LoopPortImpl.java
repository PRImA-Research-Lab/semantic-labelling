package org.primaresearch.clc.phd.workflow.data.port;

import java.util.Collection;

import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.data.DataObject;

/**
 * Default implementation for LoopPort. Based on default input port implementation.
 * 
 * @author clc
 *
 */
public class LoopPortImpl extends InputPortImpl implements LoopPort {

	public LoopPortImpl(Activity activity, DataObject dataObject, IdGenerator idRegister) {
		super(activity, dataObject, idRegister);
	}
	
	public LoopPortImpl(LoopPort other) {
		super((InputPortImpl)other);
	}
	
	public LoopPort clone() {
		return new LoopPortImpl(this);
	}

	@Override
	public Collection<OutputPort> getForwardedPorts() {
		return null;
	}

	@Override
	public void addForwardedPort(OutputPort forwarded) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeForwardedPort(OutputPort forwarded) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getType() {
		//Return first element of allowed types
		return getAllowedTypes().isEmpty() ? null : getAllowedTypes().iterator().next();
	}

	@Override
	public void setType(String type) {
		//Set first element of allowed types
		getAllowedTypes().clear();
		getAllowedTypes().add(type);
	}

}
