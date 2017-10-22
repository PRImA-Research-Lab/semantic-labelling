package org.primaresearch.clc.phd.workflow.validation.modules;

import java.util.Collection;
import java.util.LinkedList;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityIterator;
import org.primaresearch.clc.phd.workflow.activity.DirectedGraphActivity;
import org.primaresearch.clc.phd.workflow.validation.WorkflowValidationResult;

/**
 * Checks for cycles in acyclic graph activities
 * 
 * @author clc
 *
 */
public class CycleDetectionValidationModule implements WorkflowValidationModule {

	public static final String TYPE_CYCLE_IN_GRAPH_ACTICITY = "CycleInGraph";
	
	@Override
	public Collection<WorkflowValidationResult> validate(Workflow workflow) {
		Collection<WorkflowValidationResult> res = new LinkedList<WorkflowValidationResult>();
		
		WorkflowValidationResult globalContainer = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_ERROR, null,
				"Cycles in acyclic graph", 
				"One or more cycles were detected in acyclic graph activites");
		
		for (ActivityIterator it = workflow.getActivities(); it.hasNext(); ) {
			Activity act = it.next();
			
			if (act instanceof DirectedGraphActivity) {
				
				if (((DirectedGraphActivity)act).hasCycles()) {
					globalContainer.addSubResult( new WorkflowValidationResult(WorkflowValidationResult.LEVEL_ERROR, 
							TYPE_CYCLE_IN_GRAPH_ACTICITY,
						act.getCaption(), 
						"Cycle(s) detected",
						act));
				}
			}
		}
			
		if (globalContainer.getChildren() != null && !globalContainer.getChildren().isEmpty())
			res.add(globalContainer);
		return res;
	}

}
