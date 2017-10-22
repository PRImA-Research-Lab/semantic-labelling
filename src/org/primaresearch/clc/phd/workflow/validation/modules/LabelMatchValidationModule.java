package org.primaresearch.clc.phd.workflow.validation.modules;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.primaresearch.clc.phd.ontology.label.HasLabels;
import org.primaresearch.clc.phd.repository.search.matching.ActivityLabelMatcher;
import org.primaresearch.clc.phd.repository.search.matching.LabellableObjectMatcher;
import org.primaresearch.clc.phd.repository.search.matching.MatchValue;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityIterator;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;
import org.primaresearch.clc.phd.workflow.validation.WorkflowValidationResult;

/**
 * Checks that data sources or forwarded data matches the label type of the target port.
 * 
 * @author clc
 *
 */
public class LabelMatchValidationModule implements WorkflowValidationModule {

	public static final String TYPE_INPUT_PORT_LABEL_MISMATCH = "InputPortLabelMismatch";

	@Override
	public Collection<WorkflowValidationResult> validate(Workflow workflow) {
		Collection<WorkflowValidationResult> res = new LinkedList<WorkflowValidationResult>();
		
		WorkflowValidationResult globalContainer = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_WARNING, null,
				"Label mismatches", 
				"Some activities data port labels don't match their source/forwarded port's labels.");
		
		for (ActivityIterator it = workflow.getActivities(); it.hasNext(); ) {
			Activity act = it.next();
			
			WorkflowValidationResult actContainer = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_WARNING, null,
					"Activity '"+act.getCaption()+"'", 
					"Some of the activity's data ports labels don't match their source/forwarded port's labels.",
					act);

			//Input ports
			for (Iterator<InputPort> itP = act.getInputPorts().iterator(); itP.hasNext(); )
				checkInputPort(itP.next(), actContainer);
			
			//Output ports
			for (Iterator<OutputPort> itP = act.getOutputPorts().iterator(); itP.hasNext(); )
				checkOutputPort(itP.next(), actContainer);

			//Loop ports
			/*if (act instanceof ForLoopActivity) {
				ForLoopActivity loop = (ForLoopActivity)act;
				checkInputPort(loop.getLoopStart(), actContainer);
				checkOutputPort(loop.getLoopStart(), actContainer);
				checkInputPort(loop.getLoopEnd(), actContainer);
				checkOutputPort(loop.getLoopEnd(), actContainer);
				checkInputPort(loop.getLoopStep(), actContainer);
				checkOutputPort(loop.getLoopStep(), actContainer);
				checkInputPort(loop.getLoopPosition(), actContainer);
				checkOutputPort(loop.getLoopPosition(), actContainer);
			}*/
			
			if (actContainer.getChildren() != null && !actContainer.getChildren().isEmpty())
				globalContainer.addSubResult(actContainer);
		}
		
		if (globalContainer.getChildren() != null && !globalContainer.getChildren().isEmpty())
			res.add(globalContainer);
		return res;
	}

	
	private void checkInputPort(InputPort port, WorkflowValidationResult container) {
		DataPort source = ActivityLabelMatcher.lookForInheritedLabels(port.getSource());
		if (source != null && source instanceof OutputPort) {
			LabellableObjectMatcher matcher = new LabellableObjectMatcher(port.getDataObject());
			MatchValue<HasLabels> matchRes = matcher.match(source.getDataObject());
			
			if (matchRes != null && matchRes.getMatchScore() < 100.0) {
				String caption = source.getDataObject().getCaption() + " -> " + port.getDataObject().getCaption() + ": "+matchRes.getMatchScore();
				
				container.addSubResult(new WorkflowValidationResult(
												WorkflowValidationResult.LEVEL_WARNING, 
												TYPE_INPUT_PORT_LABEL_MISMATCH,
												caption, 
												matchRes.getMatchDescription(),
												port));
			}
		}
	}
	
	private void checkOutputPort(OutputPort port, WorkflowValidationResult container) {
		/*DataPort forwarded = ActivityLabelMatcher.lookForInheritedLabels(port.getForwardedPort());
		if (forwarded != null) {
			LabellableObjectMatcher matcher = new LabellableObjectMatcher(port.getDataObject());
			MatchValue<HasLabels> matchRes = matcher.match(forwarded.getDataObject());
			
			if (matchRes != null && matchRes.getMatchScore() < 1.0) {
				String caption = forwarded.getDataObject().getCaption() + " --> " + port.getDataObject().getCaption() + ": "+matchRes.getMatchScore();

				container.addSubResult(new WorkflowValidationResult(
												WorkflowValidationResult.TYPE_WARNING, 
												caption, 
												matchRes.getMatchDescription(),
												port));
			}
		}*/
	}
	

}
