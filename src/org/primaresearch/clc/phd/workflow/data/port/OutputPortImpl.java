package org.primaresearch.clc.phd.workflow.data.port;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.data.DataObject;

/**
 * Default implementation for activity output ports.
 * @author clc
 *
 */
public class OutputPortImpl extends DataPortImpl implements OutputPort {

	private Collection<OutputPort> forwardedPorts = new LinkedList<OutputPort>();
	private String type;
	
	/**
	 * Constructor
	 * @param activity
	 * @param dataObject
	 * @param idRegister
	 */
	public OutputPortImpl(Activity activity, DataObject dataObject, IdGenerator idRegister) {
		super(activity, dataObject, idRegister);
	}
	
	/**
	 * Copy constructor
	 * @param other
	 */
	public OutputPortImpl(OutputPortImpl other) {
		super((DataPortImpl)other);
		if (other.forwardedPorts != null) {
			for (Iterator<OutputPort> it=other.forwardedPorts.iterator(); it.hasNext();)
				this.forwardedPorts.add(it.next().clone());
		}
		this.type = other.type;
	}
	
	public OutputPort clone() {
		return new OutputPortImpl(this);
	}

	@Override
	public Collection<OutputPort> getForwardedPorts() {
		return forwardedPorts;
	}
	
	@Override
	public void addForwardedPort(OutputPort forwarded) {
		forwardedPorts.add(forwarded);
	}
	
	@Override
	public void removeForwardedPort(OutputPort forwarded) {
		forwardedPorts.remove(forwarded);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
