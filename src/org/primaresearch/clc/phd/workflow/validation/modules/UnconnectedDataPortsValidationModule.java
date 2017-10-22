package org.primaresearch.clc.phd.workflow.validation.modules;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityIterator;
import org.primaresearch.clc.phd.workflow.activity.ForLoopActivity;
import org.primaresearch.clc.phd.workflow.data.SingleDataObject;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;
import org.primaresearch.clc.phd.workflow.validation.WorkflowValidationResult;

/**
 * Checks for 'loose ends' (input ports that have no source or output ports that are not forwarded anywhere)
 * 
 * @author clc
 *
 */
public class UnconnectedDataPortsValidationModule implements
		WorkflowValidationModule {

	public static final String TYPE_UNCONNECTED_INPUT_PORT = "UnconnectedInputPort";
	public static final String TYPE_UNCONNECTED_OUTPUT_PORT = "UnconnectedOutputPort";

	@Override
	public Collection<WorkflowValidationResult> validate(Workflow workflow) {
		Collection<WorkflowValidationResult> res = new LinkedList<WorkflowValidationResult>();
		
		WorkflowValidationResult globalContainer = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_WARNING, null,
				"Unconnected data ports", 
				"Input ports without source or output ports that are not forwarded anywhere");
		
		for (ActivityIterator it = workflow.getActivities(); it.hasNext(); ) {
			Activity act = it.next();
			
			WorkflowValidationResult actContainer = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_WARNING, null,
					act.getCaption(), 
					"Some of the activity's data ports are unconnected (loose end)",
					act);

			//if (act != workflow.getRootActivity()) {
				//Input ports
				if (act.getInputPorts() != null) {
					for (Iterator<InputPort> itP = act.getInputPorts().iterator(); itP.hasNext(); ) 
						checkInputPort(itP.next(), actContainer);
				}
				
				//Output ports
				if (act.getOutputPorts() != null) {
					for (Iterator<OutputPort> itP = act.getOutputPorts().iterator(); itP.hasNext(); ) 
						checkOutputPort(workflow, itP.next(), actContainer);
				}
			//}
			
			//Loop ports
			if (act instanceof ForLoopActivity) {
				ForLoopActivity loop = (ForLoopActivity)act;
				checkInputPort(loop.getLoopStart(), actContainer);
				checkOutputPort(workflow, loop.getLoopStart(), actContainer);
				checkInputPort(loop.getLoopEnd(), actContainer);
				checkOutputPort(workflow, loop.getLoopEnd(), actContainer);
				checkInputPort(loop.getLoopStep(), actContainer);
				checkOutputPort(workflow, loop.getLoopStep(), actContainer);
				//checkInputPort(loop.getLoopPosition(), actContainer);
				checkOutputPort(workflow, loop.getLoopPosition(), actContainer);
			}
			
			if (actContainer.getChildren() != null && !actContainer.getChildren().isEmpty())
				globalContainer.addSubResult(actContainer);

		}
			
		if (globalContainer.getChildren() != null && !globalContainer.getChildren().isEmpty())
			res.add(globalContainer);
		return res;
	}
		
	private void checkInputPort(InputPort port, WorkflowValidationResult container) {
		if (port.getSource() == null && (!(port.getDataObject() instanceof SingleDataObject) || ((SingleDataObject)port.getDataObject()).getValue() == null)) {
			container.addSubResult(new WorkflowValidationResult(WorkflowValidationResult.LEVEL_WARNING, 
					TYPE_UNCONNECTED_INPUT_PORT,
					"Input port '" + port.getDataObject().getCaption()+"'", 
					"No source specified for input port", port));
		}
	}

	private void checkOutputPort(Workflow workflow, OutputPort port, WorkflowValidationResult container) {
		boolean foundTarget = false;
		for (ActivityIterator it = workflow.getActivities(); it.hasNext(); ) {
			Activity act = it.next();
			//Input ports
			if (act.getInputPorts() != null) {
				for (Iterator<InputPort> itP = act.getInputPorts().iterator(); itP.hasNext(); ) {
					InputPort ip = itP.next();
					if (ip.getSource() == port || ip.getCollectionPositionProvider() == port) {
						foundTarget = true;
						break;
					}
				}
			}
			if (foundTarget)
				break;
			
			//Output ports
			if (act.getOutputPorts() != null) {
				for (Iterator<OutputPort> itP = act.getOutputPorts().iterator(); itP.hasNext(); ) {
					OutputPort op = itP.next();
					
					if (op.getForwardedPorts() != null) {
						for (Iterator<OutputPort> itF=op.getForwardedPorts().iterator(); itF.hasNext(); ) {
							OutputPort forwarded = itF.next();

							if (forwarded == port || op.getCollectionPositionProvider() == port) {
								foundTarget = true;
								break;
							}
						}
					}
				}
			}
			if (foundTarget)
				break;
		
			//Loop ports
			if (act instanceof ForLoopActivity) {
				ForLoopActivity loop = (ForLoopActivity)act;
				
				if (loop.getLoopStart().getSource() == port || loop.getLoopStart().getCollectionPositionProvider() == port) {
					foundTarget = true;
					break;
				}
				if (loop.getLoopEnd().getSource() == port || loop.getLoopEnd().getCollectionPositionProvider() == port) {
					foundTarget = true;
					break;
				}
				if (loop.getLoopStep().getSource() == port || loop.getLoopStep().getCollectionPositionProvider() == port) {
					foundTarget = true;
					break;
				}
				if (loop.getLoopPosition().getSource() == port || loop.getLoopPosition().getCollectionPositionProvider() == port) {
					foundTarget = true;
					break;
				}

				/*
				if (!loop.getLoopStart().getForwardedPorts().isEmpty() && loop.getLoopStart().getForwardedPorts().iterator().next() == port) {
					foundTarget = true;
					break;
				}
				if (!loop.getLoopEnd().getForwardedPorts().isEmpty() && loop.getLoopEnd().getForwardedPorts().iterator().next() == port) {
					foundTarget = true;
					break;
				}				
				if (!loop.getLoopStep().getForwardedPorts().isEmpty() && loop.getLoopStep().getForwardedPorts().iterator().next() == port) {
					foundTarget = true;
					break;
				}				
				if (!loop.getLoopPosition().getForwardedPorts().isEmpty() && loop.getLoopPosition().getForwardedPorts().iterator().next() == port) {
					foundTarget = true;
					break;
				}*/
			}
		}
		
		if (!foundTarget) {
			container.addSubResult(new WorkflowValidationResult(WorkflowValidationResult.LEVEL_WARNING, 
					TYPE_UNCONNECTED_OUTPUT_PORT,
					"Output port '" + port.getDataObject().getCaption()+"'", 
					"Output port never used", port));
		}
	}

}
