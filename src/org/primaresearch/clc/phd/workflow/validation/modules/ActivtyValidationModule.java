package org.primaresearch.clc.phd.workflow.validation.modules;

import java.util.Collection;
import java.util.LinkedList;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityIterator;
import org.primaresearch.clc.phd.workflow.validation.WorkflowValidationResult;

/**
 * Checks for missing activity data (name, caption, ports)
 * 
 * @author clc
 *
 */
public class ActivtyValidationModule implements WorkflowValidationModule {

	public static final String TYPE_MISSING_NAME = "MissingActivityName";
	public static final String TYPE_MISSING_DESCRIPTION = "MissingActivityDescription";
	public static final String TYPE_MISSING_PORTS = "MissingActivityDataPorts";
	
	@Override
	public Collection<WorkflowValidationResult> validate(Workflow workflow) {
		Collection<WorkflowValidationResult> res = new LinkedList<WorkflowValidationResult>();
		
		WorkflowValidationResult globalInfoContainer = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_INFO, null, 
				"Missing activity data", 
				"Some data fields of one or more activities are not filled");
	
		WorkflowValidationResult globalWarningContainer = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_WARNING, null,
				"Missing activity data", 
				"Some data fields of one or more activities are not filled");

		for (ActivityIterator it = workflow.getActivities(); it.hasNext(); ) {
			Activity act = it.next();
			
			WorkflowValidationResult actInfoContainer = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_INFO, null,
					"Activity '"+act.getCaption()+"'", 
					"Empty data fields",
					act);

			WorkflowValidationResult actWarningContainer = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_WARNING, null,
					"Activity '"+act.getCaption()+"'", 
					"Empty data fields",
					act);

			//Name
			if (act.getCaption() == null || act.getCaption().isEmpty())
				actWarningContainer.addSubResult(new WorkflowValidationResult(WorkflowValidationResult.LEVEL_WARNING, 
						TYPE_MISSING_NAME,
						"Unnamed activity", 
						"No caption has been specified for the activity.", act));
			//Description
			if (act.getDescription() == null || act.getDescription().isEmpty())
				actInfoContainer.addSubResult(new WorkflowValidationResult(WorkflowValidationResult.LEVEL_INFO, 
						TYPE_MISSING_DESCRIPTION,
						"Missing activity description",
						"No description has been specified for the activity.", act));
			
			//Ports
			if ((act.getInputPorts() == null || act.getInputPorts().isEmpty())
					&& (act.getOutputPorts() == null || act.getOutputPorts().isEmpty())) {
				actWarningContainer.addSubResult(new WorkflowValidationResult(WorkflowValidationResult.LEVEL_WARNING, 
						TYPE_MISSING_PORTS,
						"No data ports", 
						"The activity has no ingoing or outgoing data ports.", act));
			}
			
			if (actInfoContainer.getChildren() != null && !actInfoContainer.getChildren().isEmpty())
				globalInfoContainer.addSubResult(actInfoContainer);
			if (actWarningContainer.getChildren() != null && !actWarningContainer.getChildren().isEmpty())
				globalWarningContainer.addSubResult(actWarningContainer);
		}
			
		if (globalInfoContainer.getChildren() != null && !globalInfoContainer.getChildren().isEmpty())
			res.add(globalInfoContainer);
		if (globalWarningContainer.getChildren() != null && !globalWarningContainer.getChildren().isEmpty())
			res.add(globalWarningContainer);
		return res;
	}

}
