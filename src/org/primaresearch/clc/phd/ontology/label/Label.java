package org.primaresearch.clc.phd.ontology.label;

/**
 * A semantic label (e.g. for an activity or a data port)
 * 
 * @author clc
 *
 */
public class Label {

	private LabelType type;
	private String comments;
	
	/**
	 * Constructor
	 */
	public Label(LabelType type) {
		this.type = type;
	}
	
	/**
	 * Creates a deep copy
	 */
	public Label clone() {
		Label copy = new Label(type);
		copy.setComments(comments);
		return copy;
	}
	
	/**
	 * Sets the type of this label
	 */
	public void setType(LabelType type) {
		this.type = type;
	}

	/**
	 * Returns the type of this label
	 */
	public LabelType getType() {
		return type;
	}

	/**
	 * Comments for this label
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * Comments for this label
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
		
}
