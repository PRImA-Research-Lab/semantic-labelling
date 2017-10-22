package org.primaresearch.clc.phd.workflow.data.port;

import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.data.DataObject;

/**
 * Abstract base implementation for data ports, providing common fields such as data object, id, parent activity.
 * 
 * @author clc
 *
 */
public abstract class DataPortImpl implements DataPort {

	private Activity activity;
	private DataObject dataObject;
	private String id;
	private DataPort collectionPositionProvider;
	private IdGenerator idRegister;

	public DataPortImpl(Activity activity, DataObject dataObject, IdGenerator idRegister) {
		this.activity = activity;
		this.dataObject = dataObject;
		this.idRegister = idRegister;
		this.id = idRegister.generateId(this);
	}
	
	/**
	 * Copy constructor
	 * @param other
	 */
	public DataPortImpl(DataPortImpl other) {
		this.activity = other.getActivity();
		this.dataObject = other.getDataObject().clone();
		this.idRegister = other.idRegister;
		this.id = other.id;
		if (other.collectionPositionProvider != null)
			this.collectionPositionProvider = other.collectionPositionProvider.clone();
	}
	
	public abstract DataPort clone();
	
	@Override
	public DataObject getDataObject() {
		return dataObject;
	}

	public void setDataObject(DataObject dataObject) {
		this.dataObject = dataObject;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		idRegister.unregisterId(this.id);
		try {
			idRegister.registerId(id);
			this.id = id;
		} catch (Exception exc) {
			this.id = idRegister.generateId(this);
		}
		
	}

	@Override
	public DataPort getCollectionPositionProvider() {
		return collectionPositionProvider;
	}

	@Override
	public void setCollectionPositionProvider(DataPort port) {
		collectionPositionProvider = port;
	}

	public Activity getActivity() {
		return activity;
	}

}
