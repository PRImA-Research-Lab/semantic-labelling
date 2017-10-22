package org.primaresearch.clc.phd.workflow.validation.modules;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityIterator;
import org.primaresearch.clc.phd.workflow.activity.ForLoopActivity;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;
import org.primaresearch.clc.phd.workflow.validation.WorkflowValidationResult;

/**
 * Checks that data sources or forwarded data matches the data type of the target port.
 * @author clc
 *
 */
public class DatatypeMatchValidationModule implements WorkflowValidationModule {

	public static final String TYPE_INPUT_PORT_DATA_TYPE_MISMATCH = "InputPortDataMismatch";
	public static final String TYPE_OUTPUT_PORT_DATA_TYPE_MISMATCH = "OutputPortDataMismatch";

	@Override
	public Collection<WorkflowValidationResult> validate(Workflow workflow) {
		Collection<WorkflowValidationResult> res = new LinkedList<WorkflowValidationResult>();
		
		WorkflowValidationResult globalContainer = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_ERROR, null,
				"Data type mismatches", 
				"Some activities data ports don't match their source/forwarded port's data type.\nCheck and correct the data types (if wrong), replace the activity, or add a converter.");
		
		for (ActivityIterator it = workflow.getActivities(); it.hasNext(); ) {
			Activity act = it.next();
			
			WorkflowValidationResult actContainer = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_ERROR, null,
					"Activity '"+act.getCaption()+"'", 
					"Some of the activity's data ports don't match their source/forwarded port's data type.",
					act);

			//Input ports
			for (Iterator<InputPort> itP = act.getInputPorts().iterator(); itP.hasNext(); )
				checkInputPort(itP.next(), actContainer);
			
			//Output ports
			for (Iterator<OutputPort> itP = act.getOutputPorts().iterator(); itP.hasNext(); )
				checkOutputPort(itP.next(), actContainer);

			//Loop ports
			if (act instanceof ForLoopActivity) {
				ForLoopActivity loop = (ForLoopActivity)act;
				checkInputPort(loop.getLoopStart(), actContainer);
				checkOutputPort(loop.getLoopStart(), actContainer);
				checkInputPort(loop.getLoopEnd(), actContainer);
				checkOutputPort(loop.getLoopEnd(), actContainer);
				checkInputPort(loop.getLoopStep(), actContainer);
				checkOutputPort(loop.getLoopStep(), actContainer);
				checkInputPort(loop.getLoopPosition(), actContainer);
				checkOutputPort(loop.getLoopPosition(), actContainer);
			}
			
			if (actContainer.getChildren() != null && !actContainer.getChildren().isEmpty())
				globalContainer.addSubResult(actContainer);
		}
		
		if (globalContainer.getChildren() != null && !globalContainer.getChildren().isEmpty())
			res.add(globalContainer);
		return res;
	}

	
	private void checkInputPort(InputPort port, WorkflowValidationResult container) {
		DataPort source = port.getSource();
		if (source != null) {
			String matchRes = checkIfMatch(source, port);
			if (matchRes != null) { //No match
				container.addSubResult(new WorkflowValidationResult(
												WorkflowValidationResult.LEVEL_ERROR, 
												TYPE_INPUT_PORT_DATA_TYPE_MISMATCH,
												matchRes, 
												"Data type of input port source doesn't match input port data type.",
												port));
			}
		}
	}
	
	private void checkOutputPort(OutputPort port, WorkflowValidationResult container) {
		if (port.getForwardedPorts() != null) {
			for (Iterator<OutputPort> it=port.getForwardedPorts().iterator(); it.hasNext(); ) {
				OutputPort forwarded = it.next();
			
				String matchRes = checkIfMatch2(forwarded, port);
				if (matchRes != null) { //No match
					container.addSubResult(new WorkflowValidationResult(
													WorkflowValidationResult.LEVEL_ERROR, 
													TYPE_OUTPUT_PORT_DATA_TYPE_MISMATCH,
													matchRes, 
													"Data type of port taht is forwarded to output port doesn't match output port data type.",
													port));
				}
			}
		}
	}
	
	/**
	 * Checks if the given input port matches the specified reference port.
	 * Match criteria is the data type. The reference port must have a data type that is 
	 * equal to a type of one of the given ports or it must have a super type.
	 * 
	 * @param referencePort
	 * @param portToMatch
	 * @return <code>null</code> if match, port captions otherwise
	 */
	private String checkIfMatch(DataPort referencePort, InputPort portToMatch) {
		if (referencePort instanceof InputPort)
			return checkIfInputPortMatch((InputPort)referencePort, portToMatch);
		else
			return checkIfOutputPortMatch((OutputPort)referencePort, portToMatch);
	}
	
	/**
	 * Checks if the given input port matches the specified reference input port.
	 * Match criteria is the data type. The reference port must have a data type that is 
	 * equal to a type of one of the given ports or it must have a super type.
	 * @param referencePort
	 * @param portToMatch
	 * @return <code>null</code> if match, port captions otherwise
	 */
	private String checkIfInputPortMatch(InputPort referencePort, InputPort portToMatch) {
		
		Collection<String> allowedRefTypes = referencePort.getAllowedTypes();
		if (allowedRefTypes.isEmpty())
			return null; //No reference types defined
		
		Collection<String> allowedTypesToMatch = portToMatch.getAllowedTypes();
		if (allowedTypesToMatch.isEmpty())
			return null; //No types to match defined
		
		for (Iterator<String> itRefType = allowedRefTypes.iterator(); itRefType.hasNext(); ) {
			String allowedRefType = itRefType.next();
			
			for (Iterator<String> itMatchType = allowedTypesToMatch.iterator(); itMatchType.hasNext(); ) {
				String typeToMatch = itMatchType.next();
				
				if (typeToMatch.startsWith(allowedRefType))
					return null;
			}
		}
		
		return portToMatch.getDataObject().getCaption() + " - " + referencePort.getDataObject().getCaption();
	}

	/**
	 * Checks if the given input port matches the specified reference output port.
	 * Match criteria is the data type. The reference port must have a data type that is 
	 * equal to a type of one of the given ports or it must have a super type.
	 * @param referencePort
	 * @param portToMatch
	 * @return <code>null</code> if match, port captions otherwise
	 */
	private String checkIfOutputPortMatch(OutputPort referencePort, InputPort portToMatch) {
		
		String refType = referencePort.getType();
		if (refType == null || refType.isEmpty())
			return null; //No reference type defined
		
		Collection<String> allowedTypesToMatch = portToMatch.getAllowedTypes();
		if (allowedTypesToMatch.isEmpty())
			return null; //No types to match defined
		
		for (Iterator<String> itMatchType = allowedTypesToMatch.iterator(); itMatchType.hasNext(); ) {
			String typeToMatch = itMatchType.next();
			
			if (typeToMatch.startsWith(refType))
				return null;
		}
		
		return portToMatch.getDataObject().getCaption() + " - " + referencePort.getDataObject().getCaption();
	}
	
	/**
	 * Checks if the given output port matches the specified reference port.
	 * Match criteria is the data type. The reference port must have a data type that is 
	 * equal to a type of one of the given ports or it must have a super type.
	 * @param referencePort
	 * @param portToMatch
	 * @return <code>null</code> if match, port captions otherwise
	 */
	private String checkIfMatch2(DataPort referencePort, OutputPort portToMatch) {
		if (referencePort instanceof InputPort)
			return checkIfInputPortMatch2((InputPort)referencePort, portToMatch);
		else
			return checkIfOutputPortMatch2((OutputPort)referencePort, portToMatch);
	}
	
	/**
	 * Checks if the given output port matches the specified reference input port.
	 * Match criteria is the data type. The reference port must have a data type that is 
	 * equal to a type of one of the given ports or it must have a super type.
	 * @param referencePort
	 * @param portToMatch
	 * @return <code>null</code> if match, port captions otherwise
	 */
	private String checkIfInputPortMatch2(InputPort referencePort, OutputPort portToMatch) {
		
		Collection<String> allowedRefTypes = referencePort.getAllowedTypes();
		if (allowedRefTypes.isEmpty())
			return null; //No reference types defined
		
		String typeToMatch = portToMatch.getType();
		if (typeToMatch == null || typeToMatch.isEmpty())
			return null; //No type to match defined
		
		for (Iterator<String> itRefType = allowedRefTypes.iterator(); itRefType.hasNext(); ) {
			String allowedRefType = itRefType.next();
			
			if (typeToMatch.startsWith(allowedRefType))
				return null;
		}
		
		return portToMatch.getDataObject().getCaption() + " - " + referencePort.getDataObject().getCaption();
	}
	
	/**
	 * Checks if the given output port matches the specified reference output port.
	 * Match criteria is the data type. The reference port must have a data type that is 
	 * equal to a type of one of the given ports or it must have a super type.
	 * @param referencePort
	 * @param portToMatch
	 * @return <code>null</code> if match, port captions otherwise
	 */
	private String checkIfOutputPortMatch2(OutputPort referencePort, OutputPort portToMatch) {
		
		String refType = referencePort.getType();
		if (refType == null || refType.isEmpty())
			return null; //No reference type defined
		
		String typeToMatch = portToMatch.getType();
		if (typeToMatch == null || typeToMatch.isEmpty())
			return null; //No type to match defined
		
		if (typeToMatch.startsWith(refType))
			return null;
		
		return portToMatch.getDataObject().getCaption() + " - " + referencePort.getDataObject().getCaption();
	}
}
