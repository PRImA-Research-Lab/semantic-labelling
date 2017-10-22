package org.primaresearch.clc.phd.workflow.data;

import org.primaresearch.clc.phd.ontology.label.Labels;

/**
 * Data object implementation that represents a single piece of data such as a file, an integer, or a text string.
 * 
 * @author clc
 *
 */
public class SingleDataObject extends BaseDataObject {
	
	/** Type id for fixed value data objects of type integer */
	public static final String TYPE_INTEGER = "int";

	private String value;
	
	public SingleDataObject(Labels allowedLabels) {
		super(allowedLabels);
	}
	
	/**
	 * Copy constructor
	 * @param other
	 */
	public SingleDataObject(SingleDataObject other) {
		super(other);
		this.value = other.value;
	}
	
	public DataObject clone() {
		return new SingleDataObject(this);
	}


	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	
}
