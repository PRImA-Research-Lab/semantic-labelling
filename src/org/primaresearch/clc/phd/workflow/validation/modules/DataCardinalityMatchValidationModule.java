package org.primaresearch.clc.phd.workflow.validation.modules;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityIterator;
import org.primaresearch.clc.phd.workflow.activity.ForLoopActivity;
import org.primaresearch.clc.phd.workflow.data.DataCollection;
import org.primaresearch.clc.phd.workflow.data.SingleDataObject;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;
import org.primaresearch.clc.phd.workflow.validation.WorkflowValidationResult;

/**
 * Checks that data sources or forwarded data matches the data cardinality of the target port (single data object vs. data collection)
 * @author clc
 *
 */
public class DataCardinalityMatchValidationModule implements WorkflowValidationModule {

	public static final String TYPE_INPUT_PORT_DATA_CARDINALITY_MISMATCH = "InputPortDataCardinalityMismatch";
	public static final String TYPE_OUTPUT_PORT_DATA_CARDINALITY_MISMATCH = "OutputPortDataCardinalityMismatch";

	@Override
	public Collection<WorkflowValidationResult> validate(Workflow workflow) {
		Collection<WorkflowValidationResult> res = new LinkedList<WorkflowValidationResult>();
		
		WorkflowValidationResult globalContainer = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_ERROR, null,
				"Data cardinality mismatches", 
				"Some activities data ports don't match their source/forwarded port's data cardinality (single vs. collection).\nCheck and correct or add a loop activity.");
		
		for (ActivityIterator it = workflow.getActivities(); it.hasNext(); ) {
			Activity act = it.next();
			
			WorkflowValidationResult actContainer = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_ERROR, null,
					"Activity '"+act.getCaption()+"'", 
					"Some of the activity's data ports don't match their source/forwarded port's data cardinality (single vs. collection).",
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
												TYPE_INPUT_PORT_DATA_CARDINALITY_MISMATCH,
												matchRes, 
												"Cardinality of the input port source doesn't match input port data cardinality.",
												port));
			}
		}
	}
	
	private void checkOutputPort(OutputPort port, WorkflowValidationResult container) {
		
		if (port.getForwardedPorts() != null) {
			for (Iterator<OutputPort> it=port.getForwardedPorts().iterator(); it.hasNext(); ) {
				OutputPort forwarded = it.next();
			
				String matchRes = checkIfMatch(forwarded, port);
				if (matchRes != null) { //No match
					container.addSubResult(new WorkflowValidationResult(
													WorkflowValidationResult.LEVEL_ERROR, 
													TYPE_OUTPUT_PORT_DATA_CARDINALITY_MISMATCH,
													matchRes, 
													"Cardinality of port taht is forwarded to output port doesn't match output port data cardinality.",
													port));
				}
			}
		}
	}
	
	/**
	 * Checks if the given input port matches the specified reference port.
	 * Match criteria is the data cardinality. 
	 * 
	 * @param referencePort
	 * @param portToMatch
	 * @return <code>null</code> if match, port captions otherwise
	 */
	private String checkIfMatch(DataPort referencePort, DataPort portToMatch) {
		if (referencePort instanceof InputPort)
			return checkIfInputPortMatch((InputPort)referencePort, portToMatch);
		else
			return checkIfOutputPortMatch((OutputPort)referencePort, portToMatch);
	}
	
	/**
	 * Checks if the given input port matches the specified reference input port.
	 * Match criteria is the data cardinality. 
	 * @param referencePort
	 * @param portToMatch
	 * @return <code>null</code> if match, port captions otherwise
	 */
	private String checkIfInputPortMatch(InputPort referencePort, DataPort portToMatch) {
		
		if (referencePort.getDataObject() == null && portToMatch.getDataObject() == null)
			return null; //No data objects = match
		
		if (referencePort.getDataObject() == null)
			return "null - " + referencePort.getDataObject().getCaption(); //One side is null

		if (portToMatch.getDataObject() == null)
			return portToMatch.getDataObject().getCaption() + " - null"; //One side is null

		if (referencePort.getDataObject() instanceof DataCollection && portToMatch.getDataObject() instanceof SingleDataObject)
			return "Collection '"+referencePort.getDataObject().getCaption() + "' - Single object '" + portToMatch.getDataObject().getCaption()+"'";

		if (referencePort.getDataObject() instanceof SingleDataObject && portToMatch.getDataObject() instanceof DataCollection)
			return "Single object '"+referencePort.getDataObject().getCaption() + "' - Collection '" + portToMatch.getDataObject().getCaption()+"'";

		return null; //Match
	}

	/**
	 * Checks if the given input port matches the specified reference output port.
	 * Match criteria is the data type. The reference port must have a data type that is 
	 * equal to a type of one of the given ports or it must have a super type.
	 * @param referencePort
	 * @param portToMatch
	 * @return <code>null</code> if match, port captions otherwise
	 */
	private String checkIfOutputPortMatch(OutputPort referencePort, DataPort portToMatch) {
		
		if (referencePort.getDataObject() == null && portToMatch.getDataObject() == null)
			return null; //No data objects = match
		
		if (referencePort.getDataObject() == null)
			return "null - " + referencePort.getDataObject().getCaption(); //One side is null

		if (portToMatch.getDataObject() == null)
			return portToMatch.getDataObject().getCaption() + " - null"; //One side is null

		//Mismatch
		if (referencePort.getDataObject() instanceof DataCollection && portToMatch.getDataObject() instanceof SingleDataObject) {
			//Is there a position provider?
			if (portToMatch.getCollectionPositionProvider() != null)
				return null; //No problem
			return "Collection '"+referencePort.getDataObject().getCaption() + "' - Single object '" + portToMatch.getDataObject().getCaption()+"'";
		}

		if (referencePort.getDataObject() instanceof SingleDataObject && portToMatch.getDataObject() instanceof DataCollection) {
			//Is there a position provider?
			if (referencePort.getCollectionPositionProvider() != null)
				return null; //No problem
			return "Single object '"+referencePort.getDataObject().getCaption() + "' - Collection '" + portToMatch.getDataObject().getCaption()+"'";
		}
		
		return null; //Match
	}
	
	/**
	 * Checks if the given output port matches the specified reference port.
	 * Match criteria is the data type. The reference port must have a data type that is 
	 * equal to a type of one of the given ports or it must have a super type.
	 * @param referencePort
	 * @param portToMatch
	 * @return <code>null</code> if match, port captions otherwise
	 */
	/*private String checkIfMatch2(DataPort referencePort, OutputPort portToMatch) {
		if (referencePort instanceof InputPort)
			return checkIfInputPortMatch2((InputPort)referencePort, portToMatch);
		else
			return checkIfOutputPortMatch2((OutputPort)referencePort, portToMatch);
	}*/
	
	/**
	 * Checks if the given output port matches the specified reference input port.
	 * Match criteria is the data type. The reference port must have a data type that is 
	 * equal to a type of one of the given ports or it must have a super type.
	 * @param referencePort
	 * @param portToMatch
	 * @return <code>null</code> if match, port captions otherwise
	 */
	/*private String checkIfInputPortMatch2(InputPort referencePort, OutputPort portToMatch) {
		
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
	}*/
	
	/**
	 * Checks if the given output port matches the specified reference output port.
	 * Match criteria is the data type. The reference port must have a data type that is 
	 * equal to a type of one of the given ports or it must have a super type.
	 * @param referencePort
	 * @param portToMatch
	 * @return <code>null</code> if match, port captions otherwise
	 */
	/*private String checkIfOutputPortMatch2(OutputPort referencePort, OutputPort portToMatch) {
		
		String refType = referencePort.getType();
		if (refType == null || refType.isEmpty())
			return null; //No reference type defined
		
		String typeToMatch = portToMatch.getType();
		if (typeToMatch == null || typeToMatch.isEmpty())
			return null; //No type to match defined
		
		if (typeToMatch.startsWith(refType))
			return null;
		
		return portToMatch.getDataObject().getCaption() + " - " + referencePort.getDataObject().getCaption();
	}*/
}
