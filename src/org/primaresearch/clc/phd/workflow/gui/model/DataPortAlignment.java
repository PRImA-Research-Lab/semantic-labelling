package org.primaresearch.clc.phd.workflow.gui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.primaresearch.clc.phd.ontology.label.HasLabels;
import org.primaresearch.clc.phd.repository.search.matching.LabellableObjectMatcher;
import org.primaresearch.clc.phd.repository.search.matching.MatchValue;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityIterator;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;

/**
 * Container for aligning data ports (manual or automatic)
 * 
 * @author clc
 *
 */
public class DataPortAlignment<T extends DataPort> {

	private List<T> referencePorts = new ArrayList<T>();
	private Activity referenceActivity;
	private List<T> portsToBeAligned = new ArrayList<T>();
	private Activity activityOfPortsToBeAligned;
	private Workflow workflow;
	
	/**
	 * Constructor
	 * @param referencePorts
	 * @param referenceActivity
	 * @param portsToBeAligned
	 * @param activityOfPortsToBeAligned
	 */
	public DataPortAlignment(Workflow workflow, Collection<T> referencePorts, Activity referenceActivity,
							 Collection<T> portsToBeAligned, Activity activityOfPortsToBeAligned) {
		this.workflow = workflow;
		this.referencePorts.addAll(referencePorts);
		this.referenceActivity = referenceActivity;
		this.portsToBeAligned.addAll(portsToBeAligned);
		this.activityOfPortsToBeAligned = activityOfPortsToBeAligned;
	}

	public List<T> getReferencePorts() {
		return referencePorts;
	}

	public List<T> getPortsToBeAligned() {
		return portsToBeAligned;
	}

	public Activity getReferenceActivity() {
		return referenceActivity;
	}

	public Activity getActivityOfPortsToBeAligned() {
		return activityOfPortsToBeAligned;
	}
	
	/**
	 * Move the given port up in its list (if not at top position already)
	 */
	public void movePortUp(DataPort port) {
		if (portsToBeAligned.contains(port))
			movePortToBeAlignedUp(port);
		else
			moveReferencePortUp(port);
	}
	
	private void movePortToBeAlignedUp(DataPort port) {
		int index = portsToBeAligned.indexOf(port);
		if (index <= 0)
			return;
		
		T temp = portsToBeAligned.get(index-1);
		portsToBeAligned.set(index-1, portsToBeAligned.get(index));
		portsToBeAligned.set(index, temp);
	}

	private void moveReferencePortUp(DataPort port) {
		int index = referencePorts.indexOf(port);
		if (index <= 0)
			return;
		
		T temp = referencePorts.get(index-1);
		referencePorts.set(index-1, referencePorts.get(index));
		referencePorts.set(index, temp);
	}
	
	/**
	 * Move the given port down in its list. Reference ports cannot be moved down if they are at the bottom of their list.
	 * Ports to be aligned can be moved down further, so that they are not aligned to any reference port.
	 */
	public void movePortDown(DataPort port) {
		if (portsToBeAligned.contains(port))
			movePortToBeAlignedDown(port);
		else
			moveReferencePortDown(port);
	}
	
	private void movePortToBeAlignedDown(DataPort port) {
		int index = portsToBeAligned.indexOf(port);
		if (index < 0 || index >= referencePorts.size())
			return;
		
		T temp = portsToBeAligned.get(index);
		//Last?
		if (index == portsToBeAligned.size()-1) {
			portsToBeAligned.set(index, null);
			portsToBeAligned.add(temp);
		} else {
			portsToBeAligned.set(index, portsToBeAligned.get(index+1));
			portsToBeAligned.set(index+1, temp);
		}
	}

	private void moveReferencePortDown(DataPort port) {
		int index = referencePorts.indexOf(port);
		if (index >= referencePorts.size()-1)
			return;
		
		T temp = referencePorts.get(index+1);
		referencePorts.set(index+1, referencePorts.get(index));
		referencePorts.set(index, temp);
	}
	
	public double matchPorts(int index) {
		DataPort referencePort = index < referencePorts.size() ? referencePorts.get(index) : null;
		DataPort portToBeAligned = index < portsToBeAligned.size() ? portsToBeAligned.get(index) : null;
		
		if (referencePort == null || portToBeAligned == null)
			return -1.0;
		
		return matchPorts(referencePort, portToBeAligned);
	}
	
	private double matchPorts(DataPort refPort, DataPort otherPort) {
		LabellableObjectMatcher matcher1 = new LabellableObjectMatcher(refPort.getDataObject());
		MatchValue<HasLabels> res1 = matcher1.match(otherPort.getDataObject());
		
		return res1.getMatchScore();
	}
	
	/**
	 * Copies certain properties from the reference ports to the aligned ports.
	 */
	public void copyDataToAlignedPorts() {
		for (int i=0; i<referencePorts.size(); i++) {
			DataPort referencePort = referencePorts.get(i);
			if (referencePort == null || i >= portsToBeAligned.size())
				continue;
			DataPort alignedPort = portsToBeAligned.get(i);
			if (alignedPort == null)
				continue;
			
			copyDataToAlignedPort(referencePort, alignedPort);
		}
	}
	
	private void copyDataToAlignedPort(DataPort referencePort, DataPort alignedPort) {
		
		//TODO: Is this wise? Might break child activities, if there are any.
		//alignedPort.setId(alignedPort.getId());
		
		//Specialised properties
		if (referencePort instanceof InputPort && alignedPort instanceof InputPort)
			copyDataToAlignedInputPort((InputPort)referencePort, (InputPort)alignedPort);
		else if (referencePort instanceof OutputPort && alignedPort instanceof OutputPort)
			copyDataToAlignedOutputPort((OutputPort)referencePort, (OutputPort)alignedPort);
	}

	private void copyDataToAlignedInputPort(InputPort referencePort, InputPort alignedPort) {
		//Data source
		alignedPort.setSource(referencePort.getSource());

	}
	
	private void copyDataToAlignedOutputPort(OutputPort referencePort, OutputPort alignedPort) {
		//Data forwarding
		alignedPort.getForwardedPorts().clear();
		alignedPort.getForwardedPorts().addAll(referencePort.getForwardedPorts());
		
		//Find all other ports using the reference port as source
		for (ActivityIterator it = workflow.getActivities(); it.hasNext(); ) {
			Activity act = it.next();
			if (act == null)
				continue;
			
			for (Iterator<InputPort> itP = act.getInputPorts().iterator(); itP.hasNext(); ) {
				InputPort port = itP.next();
				if (port == null || port.getSource() == null)
					continue;
				
				//Add data type (but only if abstract activity)
				if (port.getSource().getId().equals(referencePort.getId()) && act.isAbstract()) {
					port.setSource(alignedPort);
					if (port.getDataObject() != null)
						port.addAllowedType(alignedPort.getType());
				}
			}
		}
	}
	
	/**
	 * Automatically aligns the ports using label matching
	 */
	public void autoAlign() {
		//One-to-one matching
		List<T> alignedPorts = new LinkedList<T>();
		
		//For each reference port
		for (Iterator<T> itRef = referencePorts.iterator(); itRef.hasNext(); ) {
			T referencePort = itRef.next();
			
			//Find best match from ports to be aligned
			double maxScore = 0.0;
			T bestMatch = null;
			for (Iterator<T> itAl = portsToBeAligned.iterator(); itAl.hasNext(); ) {
				T portToBeAligned = itAl.next();
				if (portToBeAligned == null)
					continue;
				double score = matchPorts(referencePort, portToBeAligned);
				if (score > maxScore) {
					maxScore = score;
					bestMatch = portToBeAligned;
				}
			}
			alignedPorts.add(bestMatch);
			if (bestMatch != null) 
				portsToBeAligned.remove(bestMatch);
			
				
		}
		//Add remaining ports at the end (no match)
		for (Iterator<T> itAl = portsToBeAligned.iterator(); itAl.hasNext(); ) {
			T port = itAl.next();
			if (port != null)
				alignedPorts.add(port);
		}
		//Done
		portsToBeAligned = alignedPorts;
	}
}
