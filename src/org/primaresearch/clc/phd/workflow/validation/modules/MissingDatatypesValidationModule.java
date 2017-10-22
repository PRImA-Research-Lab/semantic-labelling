package org.primaresearch.clc.phd.workflow.validation.modules;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityIterator;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;
import org.primaresearch.clc.phd.workflow.validation.WorkflowValidationResult;

/**
 * Checks whether all data ports have a data type specified. Ignores abstract activities
 * 
 * @author clc
 *
 */
public class MissingDatatypesValidationModule implements WorkflowValidationModule {

	public static final String TYPE_MISSING_INPUT_PORT_TYPE = "MissingInputPortType";
	public static final String TYPE_MISSING_OUTPUT_PORT_TYPE = "MissingOutputPortType";

	@Override
	public Collection<WorkflowValidationResult> validate(Workflow workflow) {
		Collection<WorkflowValidationResult> res = new LinkedList<WorkflowValidationResult>();
		
		WorkflowValidationResult globalContainer = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_WARNING, null,
				"No data type", 
				"Some data ports have no data type");
		
		for (ActivityIterator it = workflow.getActivities(); it.hasNext(); ) {
			Activity act = it.next();
			
			if (act.isAbstract())
				continue;
			
			WorkflowValidationResult actContainer = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_WARNING, null,
					"Activity '"+act.getCaption()+"'", 
					"Some of the activity's data ports are missing the data type.",
					act);

			//Input ports
			for (Iterator<InputPort> itP = act.getInputPorts().iterator(); itP.hasNext(); )
				checkInputPort(itP.next(), actContainer);
			
			//Output ports
			for (Iterator<OutputPort> itP = act.getOutputPorts().iterator(); itP.hasNext(); )
				checkOutputPort(itP.next(), actContainer);

			if (actContainer.getChildren() != null && !actContainer.getChildren().isEmpty())
				globalContainer.addSubResult(actContainer);
		}
		
		if (globalContainer.getChildren() != null && !globalContainer.getChildren().isEmpty())
			res.add(globalContainer);
		return res;
	}

	
	private void checkInputPort(InputPort port, WorkflowValidationResult container) {
		boolean hasTypes = false;
		Collection<String> types = port.getAllowedTypes();
		if (types != null) {
			for (Iterator<String> it = types.iterator(); it.hasNext(); ) {
				String t = it.next();
				if (t != null && !t.isEmpty()) {
					hasTypes = true;
					break;
				}
			}
		}
		if (!hasTypes) { 
			container.addSubResult(new WorkflowValidationResult(
											WorkflowValidationResult.LEVEL_WARNING, 
											TYPE_MISSING_INPUT_PORT_TYPE,
											"Input Port '" + (port.getDataObject() != null ? port.getDataObject().getCaption() : "?") + "'", 
											"No data type defined",
											port));
		}
		
	}
	
	private void checkOutputPort(OutputPort port, WorkflowValidationResult container) {
		if (port.getType() == null || port.getType().isEmpty()) {
			container.addSubResult(new WorkflowValidationResult(
											WorkflowValidationResult.LEVEL_WARNING, 
											TYPE_MISSING_OUTPUT_PORT_TYPE,
											"Output Port '" + (port.getDataObject() != null ? port.getDataObject().getCaption() : "?") + "'", 
											"Data type not defined",
											port));
		}
	}
	

}
