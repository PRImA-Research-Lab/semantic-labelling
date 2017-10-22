package org.primaresearch.clc.phd.workflow.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data type for workflow activity data ports. Has a parent and children, creating a tree structure.
 * 
 * @author clc
 *
 */
public class WorkflowDataType {
	private String caption;
	private String name;
	private boolean isPrimitiveType;
	private WorkflowDataType parent;
	private List<WorkflowDataType> children = new ArrayList<WorkflowDataType>();
	
	public WorkflowDataType(String caption, String name, boolean isPrimitiveType, WorkflowDataType parent) {
		super();
		this.caption = caption;
		this.name = name;
		this.isPrimitiveType = isPrimitiveType;
		this.parent = parent;
	}

	public String getId() {
		if (isPrimitiveType)
			return name;
		if (parent == null)
			return name;
		String id = parent.getId();
		if (id == null || id.isEmpty())
			return name;
		return parent.getId()+"."+name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return caption;
	}

	public WorkflowDataType getParent() {
		return parent;
	}
	
	public void addChildType(WorkflowDataType child) {
		children.add(child);
	}

	public List<WorkflowDataType> getChildren() {
		return children;
	}
	
	
}