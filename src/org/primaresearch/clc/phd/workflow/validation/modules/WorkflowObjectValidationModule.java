package org.primaresearch.clc.phd.workflow.validation.modules;

import java.util.Collection;
import java.util.LinkedList;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.validation.WorkflowValidationResult;

/**
 * Workflow validation module that checks the main workflow object itself.
 * 
 * @author clc
 *
 */
public class WorkflowObjectValidationModule implements WorkflowValidationModule {

	public static final String TYPE_MISSING_ROOT_ACTIVITY = "MissingWorkflowRootActivity";
	public static final String TYPE_ABSTRACT_WORKFLOW = "AbstractWorkflow";
	public static final String TYPE_MISSING_NAME = "MissingWorkflowName";
	public static final String TYPE_MISSING_DESCRIPTION = "MissingWorkflowDescription";

	@Override
	public Collection<WorkflowValidationResult> validate(Workflow workflow) {
		
		//Has the workflow a root activity?
		Collection<WorkflowValidationResult> res = new LinkedList<WorkflowValidationResult>();
		if (workflow.getRootActivity() == null)
			res.add(new WorkflowValidationResult(WorkflowValidationResult.LEVEL_ERROR, 
					TYPE_MISSING_ROOT_ACTIVITY,
					"Empty workflow", 
						"The workflow has no root activity.", workflow));
		else {
			//Is the root activity abstract?
			if (workflow.getRootActivity().isAbstract())
				res.add(new WorkflowValidationResult(WorkflowValidationResult.LEVEL_INFO, 
						TYPE_ABSTRACT_WORKFLOW, 
						"Abstract workflow", 
						"The workflow has at least one abstract activity and cannot be executed.", workflow));
		}
		
		//Name
		if (workflow.getName() == null || workflow.getName().isEmpty())
			res.add(new WorkflowValidationResult(WorkflowValidationResult.LEVEL_WARNING, 
					TYPE_MISSING_NAME,
					"Unnamed workflow", 
					"No name has been specified for the workflow.", workflow));
		//Description
		if (workflow.getDescription(0).getText() == null || workflow.getDescription(0).getText().isEmpty())
			res.add(new WorkflowValidationResult(WorkflowValidationResult.LEVEL_WARNING, 
					TYPE_MISSING_DESCRIPTION,
					"Missing workflow description", 
					"No description has been specified for the workflow.", workflow));
		
		return res;
	}

}
