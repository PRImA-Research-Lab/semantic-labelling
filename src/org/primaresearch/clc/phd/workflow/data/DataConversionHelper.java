package org.primaresearch.clc.phd.workflow.data;

import java.util.Iterator;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.AtomicActivity;
import org.primaresearch.clc.phd.workflow.activity.DirectedGraphActivity;
import org.primaresearch.clc.phd.workflow.activity.DirectedGraphActivity.ActivityNode;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.InputPortImpl;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPortImpl;
import org.primaresearch.clc.phd.workflow.gui.WorkflowEditor;

/**
 * Functionality to add a data conversion step for a specific port to a workflow
 * 
 * @author clc
 *
 */
public class DataConversionHelper {
	
	private Workflow workflow;
	private WorkflowEditor workflowEditor;

	/**
	 * Constructor
	 * @param workflow
	 */
	public DataConversionHelper(Workflow workflow, WorkflowEditor workflowEditor) {
		this.workflow = workflow;
		this.workflowEditor = workflowEditor;
	}
	
	public void addConversionStep(DataPort port) {
		if (port instanceof InputPort)
			addConversionStep((InputPort)port);
		//TODO
	}
	
	private void addConversionStep(InputPort targetPort) {
		Activity targetActivity = targetPort.getActivity();
		
		DataPort sourcePort = targetPort.getSource();
		if (sourcePort == null)
			return; //No source port
		
		//Activity sourceActivity = sourcePort.getActivity();
		
		Activity parentActivity = targetActivity.getParentActivity();
		
		//Wrap in graph activity (if not already inside a graph activity)
		DirectedGraphActivity graphActivity = null;
		if (!(parentActivity instanceof DirectedGraphActivity)) {
			graphActivity = wrapInDirectedGraphActivity(targetActivity);
		}
		else
			graphActivity = (DirectedGraphActivity)parentActivity;
		
		//Add abstract converter activity
		AtomicActivity converterActivity = workflowEditor.getActivityFactory().createAtomicActivity(graphActivity, null, true);
		//converterActivity.setId(workflow.getIdRegister().generateId(converterActivity));
		ActivityNode converterNode = new ActivityNode();
		converterNode.setActivity(converterActivity);
		graphActivity.addNode(converterNode);
		converterActivity.setCaption("Converter");
		
		//Link graph nodes
		for (Iterator<ActivityNode> it = graphActivity.getGraphNodes().iterator(); it.hasNext(); ) {
			ActivityNode node = it.next();
			if (node.getActivity() == targetActivity) {
				node.addPredecessor(converterNode);
				break;
			}
		}
		
		//Re-route data
		// Ports
		InputPort converterInput = new InputPortImpl(converterActivity, sourcePort.getDataObject().clone(), workflow.getIdRegister());
		converterActivity.addInputPort(converterInput);
		OutputPort converterOutput = new OutputPortImpl(converterActivity, targetPort.getDataObject().clone(), workflow.getIdRegister());
		converterActivity.addOutputPort(converterOutput);
		
		// Source and target
		converterInput.setSource(sourcePort);
		targetPort.setSource(converterOutput);
		
		// Data type
		//  Input
		if (sourcePort instanceof InputPort)
			converterInput.getAllowedTypes().addAll(((InputPort)sourcePort).getAllowedTypes());
		else if (sourcePort instanceof OutputPort) {
			if (((OutputPort)sourcePort).getType() != null && !((OutputPort)sourcePort).getType().isEmpty())
				converterInput.addAllowedType(((OutputPort)sourcePort).getType());
		}
		//  Output
		if (!targetPort.getAllowedTypes().isEmpty()) {
			//TODO Which to choose if multiple allowed types?
			converterOutput.setType(targetPort.getAllowedTypes().iterator().next());
		}
		
		//TODO
	}
	
	private DirectedGraphActivity wrapInDirectedGraphActivity(Activity activity) {
		//Create directed graph
		DirectedGraphActivity graphActivity = workflowEditor.getActivityFactory().createDirectedGraphActivity(activity.getParentActivity(), 
					null);
		//graphActivity.setId(workflow.getIdRegister().generateId(graphActivity));
		
		//Replace
		workflow.replaceActivity(activity, graphActivity);
		
		//Add child
		ActivityNode node = new ActivityNode();
		node.setActivity(activity);
		graphActivity.addNode(node);
		
		return graphActivity;
	}
}
