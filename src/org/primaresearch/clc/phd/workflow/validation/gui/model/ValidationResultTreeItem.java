package org.primaresearch.clc.phd.workflow.validation.gui.model;

import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;

import org.primaresearch.clc.phd.workflow.validation.WorkflowValidationResult;

/**
 * Tree item specialisation for workflow validation result items (errors, warnings etc.).
 * 
 * @author clc
 *
 */
public class ValidationResultTreeItem extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;
	private WorkflowValidationResult validationResult;
	
	public ValidationResultTreeItem(WorkflowValidationResult res) {
		this.validationResult = res;
		
		if (res.getChildren() != null) {
			for (Iterator<WorkflowValidationResult> it=res.getChildren().iterator(); it.hasNext(); )
				add(new ValidationResultTreeItem(it.next()));
		}
		
		
	}
	
	public String toString() {
		return validationResult.getCaption();
	}

	public WorkflowValidationResult getValidationResult() {
		return validationResult;
	}
	
	
}
