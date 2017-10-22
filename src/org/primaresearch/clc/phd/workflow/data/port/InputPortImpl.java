package org.primaresearch.clc.phd.workflow.data.port;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.data.DataObject;

/**
 * Default implementation of activity input port.
 * 
 * @author clc
 *
 */
public class InputPortImpl extends DataPortImpl implements InputPort {

	private DataPort source = null;
	private Collection<String> allowedTypes = new LinkedList<String>();
	
	public InputPortImpl(Activity activity, DataObject dataObject, IdGenerator idRegister) {
		super(activity, dataObject, idRegister);
	}
	
	/**
	 * Copy constructor
	 * @param other
	 */
	public InputPortImpl(InputPort other) {
		super((DataPortImpl)other);
		this.source = other.getSource() != null ? other.getSource().clone() : null;
		this.allowedTypes.addAll(other.getAllowedTypes());
	}
	
	public InputPort clone() {
		return new InputPortImpl(this);
	}

	@Override
	public DataPort getSource() {
		return source;
	}

	public void setSource(DataPort source) {
		this.source = source;
	}

	@Override
	public Collection<String> getAllowedTypes() {
		return allowedTypes;
	}

	@Override
	public void addAllowedType(String type) {
		for (Iterator<String> it = allowedTypes.iterator(); it.hasNext(); )
			if (type.equals(it.next())) //Already there
				return;
		allowedTypes.add(type);
	}
	
	@Override
	public void removeAllowedType(String type) {
		allowedTypes.remove(type);
	}

}
