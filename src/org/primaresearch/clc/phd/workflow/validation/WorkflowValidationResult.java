package org.primaresearch.clc.phd.workflow.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Result item of workflow validation. Can have nested items.
 * 
 * @author clc
 *
 */
public class WorkflowValidationResult {
	
	public static final int LEVEL_NONE = 0;
	public static final int LEVEL_ERROR = 1;
	public static final int LEVEL_WARNING = 2;
	public static final int LEVEL_INFO = 3;
	

	private int level;
	private String type = null;
	private String caption;
	private String description;
	private List<WorkflowValidationResult> children;
	private Object relatedObject = null;
	
	/**
	 * Constructor
	 * @param level
	 * @param caption
	 * @param description
	 */
	public WorkflowValidationResult(int level, String type, String caption, String description) {
		this.level = level;
		this.type = type;
		this.caption = caption;
		this.description = description;
	}
	
	/**
	 * Constructor
	 * @param level
	 * @param caption
	 * @param description
	 */
	public WorkflowValidationResult(int level, String type, String caption, String description, Object relatedObject) {
		this.level = level;
		this.type = type;
		this.caption = caption;
		this.description = description;
		this.relatedObject = relatedObject;
	}
	
	public int getLevel() {
		return level;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<WorkflowValidationResult> getChildren() {
		return children;
	}
	
	public void addSubResult(WorkflowValidationResult res) {
		if (children == null)
			children = new ArrayList<WorkflowValidationResult>();
		children.add(res);
	}

	/**
	 * Returns the object where a problem was detected
	 * @return relatedObject E.g. Activity or Workflow (or null)
	 */
	public Object getRelatedObject() {
		return relatedObject;
	}

	/**
	 * Sets the object where a problem was detected
	 * @param relatedObject E.g. Activity or Workflow (or null)
	 */
	public void setRelatedObject(Object relatedObject) {
		this.relatedObject = relatedObject;
	}

	/**
	 * The detailed type of the problem
	 * @return Type ID or <code>null</code> in case it's a parent result item
	 */
	public String getType() {
		return type;
	}

	/**
	 * The detailed type of the problem
	 * @param type Type ID or <code>null</code> in case it's a parent result item
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	
}
