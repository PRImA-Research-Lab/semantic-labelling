package org.primaresearch.clc.phd.workflow.data;

import java.util.Iterator;

import org.primaresearch.clc.phd.ontology.Ontology;
import org.primaresearch.clc.phd.ontology.label.Labels;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ForLoopActivity;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPortImpl;
import org.primaresearch.clc.phd.workflow.gui.WorkflowEditor;

/**
 * Functionality to add a loop activity to resolve a data cardinality mismatch
 * 
 * @author clc
 *
 */
public class LoopHelper {
	
	private Workflow workflow;
	private WorkflowEditor workflowEditor;

	/**
	 * Constructor
	 * @param workflow
	 */
	public LoopHelper(Workflow workflow, WorkflowEditor workflowEditor) {
		this.workflow = workflow;
		this.workflowEditor = workflowEditor;
	}
	
	public void addLoopActivity(DataPort port) {
		if (port instanceof InputPort)
			addLoopActivity((InputPort)port);
		//TODO
	}
	
	private void addLoopActivity(InputPort targetPort) {
		Activity targetActivity = targetPort.getActivity();
		
		DataPort sourcePort = targetPort.getSource();
		if (sourcePort == null)
			return; //No source port
		
		if (sourcePort.getDataObject() == null || targetPort.getDataObject() == null)
			return; //Invalid
		
		if (targetPort.getDataObject() instanceof DataCollection)
			return; //TODO: Not sure what to do in this case
		
		//Activity sourceActivity = sourcePort.getActivity();
		
		Activity parentActivity = targetActivity.getParentActivity();
		
		//Wrap in loop activity
		ForLoopActivity loopActivity = workflowEditor.getActivityFactory().createForLoopActivity(parentActivity, null);
		loopActivity.setCaption("Loop for "+targetActivity.getCaption());
		
		//Replace
		workflow.replaceActivity(targetActivity, loopActivity);
		
		//Add child
		loopActivity.setChildActivity(targetActivity);
		
		//Loop output
		for (Iterator<OutputPort> it = targetActivity.getOutputPorts().iterator(); it.hasNext(); ) {
			OutputPort singlePort = it.next();
			if (singlePort.getDataObject() == null || singlePort.getDataObject() instanceof DataCollection)
				continue;
			
			OutputPortImpl newPort = new OutputPortImpl(loopActivity, 
					new DataCollectionImpl(new Labels(Ontology.getInstance().getDataObjectLabelSlots())), 
					workflow.getIdRegister());
			newPort.getDataObject().setCaption("Collection for " + singlePort.getDataObject().getCaption());
			newPort.setType(singlePort.getType());
			loopActivity.addOutputPort(newPort);
			newPort.addForwardedPort(singlePort);
			newPort.setCollectionPositionProvider(loopActivity.getLoopPosition());
		}
		
		//Connect loop ports
		sourcePort.setCollectionPositionProvider(loopActivity.getLoopPosition());
		
		
		//TODO
	}
	

}
