package org.primaresearch.clc.phd.workflow.validation.modules;

import java.util.Collection;
import java.util.LinkedList;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityIterator;
import org.primaresearch.clc.phd.workflow.activity.DirectedGraphActivity;
import org.primaresearch.clc.phd.workflow.activity.ForLoopActivity;
import org.primaresearch.clc.phd.workflow.activity.HasChildActivities;
import org.primaresearch.clc.phd.workflow.validation.WorkflowValidationResult;

/**
 * Checks if there are any activities that should have a child activity but don't have one. 
 * 
 * @author clc
 *
 */
public class MissingChildActivitiesValidationModule implements WorkflowValidationModule {

	public static final String TYPE_MISSING_FOR_LOOP_CHILD = "MissingForLoopChild";
	public static final String TYPE_MISSING_GRAPH_CHILD = "MissingGraphChild";
	public static final String TYPE_MISSING_CHILD = "MissingGenericChild";

	@Override
	public Collection<WorkflowValidationResult> validate(Workflow workflow) {
		Collection<WorkflowValidationResult> res = new LinkedList<WorkflowValidationResult>();
		
		WorkflowValidationResult container = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_ERROR, null,
				"Missing child activities", 
				"Some activities should have child activities but don't have any.");
		
		for (ActivityIterator it = workflow.getActivities(); it.hasNext(); ) {
			Activity act = it.next();
			if (act instanceof HasChildActivities && ((HasChildActivities)act).getChildActivities().isEmpty()) {
				if (act instanceof ForLoopActivity)
					container.addSubResult(new WorkflowValidationResult(WorkflowValidationResult.LEVEL_ERROR, 
							TYPE_MISSING_FOR_LOOP_CHILD,
							"Activity '"+act.getCaption()+"'", 
							"A For Loop activity requires a child activity.", act));
				else if (act instanceof DirectedGraphActivity)
					container.addSubResult(new WorkflowValidationResult(WorkflowValidationResult.LEVEL_ERROR,
							TYPE_MISSING_GRAPH_CHILD,
							"Activity '"+act.getCaption()+"'", 
							"A Directed Graph activity requires at least one child activity.", act));
				else //Unknown
					container.addSubResult(new WorkflowValidationResult(WorkflowValidationResult.LEVEL_ERROR,
							TYPE_MISSING_CHILD,
							"Activity '"+act.getCaption()+"'", 
							"An activity implementing 'HasChildActivities' requires at least one child activity.", act));
			}
		}
		
		if (container.getChildren() != null && !container.getChildren().isEmpty())
			res.add(container);
		return res;
	}

}
