package org.primaresearch.clc.phd.workflow.data.port;

import java.util.Collection;
import java.util.LinkedList;

import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.data.DataObject;

/**
 * Implementation for if-else condition ports mostly delegating to an InputPortImpl object
 * @author clc
 *
 */
public class IfElseConditionPort implements IfElsePort {

	private InputPortImpl portImpl;
	private Collection<String> allowedTypes = new LinkedList<String>();
	
	public IfElseConditionPort(Activity activity, DataObject dataObject, IdGenerator idRegister) {
		portImpl = new InputPortImpl(activity, dataObject, idRegister);
		allowedTypes.add("bool");
		allowedTypes.add("int");
	}
	
	private IfElseConditionPort(InputPortImpl other) {
		portImpl = (InputPortImpl)other.clone();
		allowedTypes.add("bool");
		allowedTypes.add("int");
	}

	public IfElsePort clone() {
		return new IfElseConditionPort(portImpl);
	}
	
	@Override
	public DataPort getSource() {
		return portImpl.getSource();
	}

	@Override
	public void setSource(DataPort source) {
		portImpl.setSource(source);
	}

	@Override
	public Collection<String> getAllowedTypes() {
		return allowedTypes;
	}

	@Override
	public void addAllowedType(String type) {
		//Not supported
	}

	@Override
	public void removeAllowedType(String type) {
		//Not supported
	}

	@Override
	public Activity getActivity() {
		return portImpl.getActivity();
	}

	@Override
	public DataObject getDataObject() {
		return portImpl.getDataObject();
	}

	@Override
	public void setDataObject(DataObject dataObject) {
		portImpl.setDataObject(dataObject);
	}

	@Override
	public String getId() {
		return portImpl.getId();
	}

	@Override
	public void setId(String id) {
		portImpl.setId(id);
	}

	@Override
	public DataPort getCollectionPositionProvider() {
		return portImpl.getCollectionPositionProvider();
	}

	@Override
	public void setCollectionPositionProvider(DataPort port) {
		port.setCollectionPositionProvider(port);
	}
	
	

}
