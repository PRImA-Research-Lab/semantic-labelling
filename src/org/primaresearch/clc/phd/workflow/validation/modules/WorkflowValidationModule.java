package org.primaresearch.clc.phd.workflow.validation.modules;

import java.util.Collection;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.validation.WorkflowValidationResult;

/**
 * Interface for workflow validation module that check a single issue.
 * 
 * @author clc
 *
 */
public interface WorkflowValidationModule {

	/**
	 * Validate the workflow
	 * 
	 * @param workflow Workflow to be validated
	 * @return Validation result items (can have sub items)
	 */
	public Collection<WorkflowValidationResult> validate(Workflow workflow);
}
