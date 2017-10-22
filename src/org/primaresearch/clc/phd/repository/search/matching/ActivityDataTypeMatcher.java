package org.primaresearch.clc.phd.repository.search.matching;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;

/**
 * Matcher implementation using the data type of activity ports
 * 
 * @author clc
 *
 */
public class ActivityDataTypeMatcher implements Matcher<Activity> {

	private Activity referenceActivity;
	private int matchingType;
	private boolean strict;
	
	/**
	 * Constructor
	 * @param referenceActivity
	 * @param matchingType Use Matcher.MATCHING_FOR_REPLACING to find activities similar to the reference activity. Use Matcher.MATCHING_FOR_ADDING_CHILD to find suitable child activities.
	 */
	public ActivityDataTypeMatcher(Activity referenceActivity, int matchingType) {
		this(referenceActivity, matchingType, false);
	}

	/**
	 * Constructor
	 * @param referenceActivity
	 * @param matchingType Use Matcher.MATCHING_FOR_REPLACING to find activities similar to the reference activity. Use Matcher.MATCHING_FOR_ADDING_CHILD to find suitable child activities.
	 * @param strict If set to <code>true</code> any non-match will result in a total match score of 0.0 
	 */
	public ActivityDataTypeMatcher(Activity referenceActivity, int matchingType, boolean strict) {
		this.referenceActivity = referenceActivity;
		this.matchingType = matchingType;
		this.strict = strict;
	}

	@Override
	public List<MatchValue<Activity>> match(Collection<Activity> objectsToMatch) {
		List<MatchValue<Activity>> ret = new LinkedList<MatchValue<Activity>>();

		//Matching
		for (Iterator<Activity> it=objectsToMatch.iterator(); it.hasNext(); ) {
			Activity objectToMatch = it.next();

			if (matchingType == Matcher.MATCHING_FOR_REPLACING)
				matchingForReplacing(objectToMatch, ret);
			else if (matchingType == Matcher.MATCHING_FOR_ADDING_CHILD)
				matchingForAddingChild(objectToMatch, ret);
		}
		
		return ret;
	}
	
	/**
	 * Match to find similar activities. Matches input ports with input ports and output ports with output ports.
	 */
	private void matchingForReplacing(Activity objectToMatch, List<MatchValue<Activity>> ret) {
		int count = 0;
		int matches = 0;
		List<MatchValue<?>> subValues = new LinkedList<MatchValue<?>>();

		//Input ports
		int weight = 0;
		for (Iterator<InputPort> itP = referenceActivity.getInputPorts().iterator(); itP.hasNext(); ) {
			InputPort referenceInputPort = itP.next();
			count++;
			weight += getWeight(referenceInputPort);
			if (hasMatch(referenceInputPort, objectToMatch.getInputPorts()))
				matches++;
			else
				subValues.add(new ActivityMatchValue(objectToMatch, 0.0, getWeight(referenceInputPort),
						"No type match for input port "+referenceInputPort.getId()));
		}

		//Output ports
		for (Iterator<OutputPort> itP = referenceActivity.getOutputPorts().iterator(); itP.hasNext(); ) {
			OutputPort referenceOutputPort = itP.next();
			count++;
			weight += getWeight(referenceOutputPort);
			if (hasMatch2(referenceOutputPort, objectToMatch.getOutputPorts()))
				matches++;
			else
				subValues.add(new ActivityMatchValue(objectToMatch, 0.0, getWeight(referenceOutputPort),
						"No type match for output port "+referenceOutputPort.getId()));
		}
		
		double score = 0.0;
		if (strict && matches < count)
			score = 0.0;
		else if (count > 0)
			score = (double)matches / (double)count;
		
		if (weight == 0)
			weight = 1; //Minimum weight is 1
		
		ret.add(new ActivityMatchValue(objectToMatch, score * 100.0, weight,
				"Port Data Types", subValues));
	}
	
	/**
	 * Match to find suitable child activities. Matches available source ports with input ports and available target ports with output ports.
	 */
	private void matchingForAddingChild(Activity objectToMatch, List<MatchValue<Activity>> ret) {
		int count = 0;
		int matches = 0;
		List<MatchValue<?>> subValues = new LinkedList<MatchValue<?>>();
		
		//Input ports
		for (Iterator<DataPort> itP = referenceActivity.getSourcePortsForChildren().iterator(); itP.hasNext(); ) {
			DataPort referencePort = itP.next();
			count++;
			if (hasMatch(referencePort, objectToMatch.getInputPorts()))
				matches++;
			else
				subValues.add(new ActivityMatchValue(objectToMatch, 0.0, getWeight(referencePort),
						"No type match for port "+referencePort.getId()));
		}

		//Output ports
		for (Iterator<DataPort> itP = referenceActivity.getTargetPortsForChildren().iterator(); itP.hasNext(); ) {
			DataPort referencePort = itP.next();
			count++;
			if (hasMatch2(referencePort, objectToMatch.getOutputPorts()))
				matches++;
			else
				subValues.add(new ActivityMatchValue(objectToMatch, 0.0, getWeight(referencePort),
						"No type match for port "+referencePort.getId()));
		}
		
		double score = 0.0;
		if (count > 0)
			score = (double)matches / (double)count;
		
		ret.add(new ActivityMatchValue(objectToMatch, score * 100.0, -1,
				"Port Data Types", subValues));
	}
	
	/**
	 * Checks if one of the given input ports matches the specified reference port.
	 * Match criteria is the data type. The reference port must have a data type that is 
	 * equal to a type of one of the given ports or it must have a super type.
	 * 
	 * @param referencePort
	 * @param portsToMatch
	 * @return
	 */
	private boolean hasMatch(DataPort referencePort, List<InputPort> portsToMatch) {
		if (referencePort instanceof InputPort)
			return hasInputPortMatch((InputPort)referencePort, portsToMatch);
		else
			return hasOutputPortMatch((OutputPort)referencePort, portsToMatch);
	}
	
	private int getWeight(DataPort referencePort) {
		if (referencePort instanceof InputPort) {
			return ((InputPort)referencePort).getAllowedTypes().isEmpty() ? 0 : 2;
		}
		if (referencePort instanceof OutputPort) {
			return ((OutputPort)referencePort).getType() == null || ((OutputPort)referencePort).getType().isEmpty() ? 0 : 2;
		}
		return 1;
	}
	
	/**
	 * Checks if one of the given input ports matches the specified reference input port.
	 * Match criteria is the data type. The reference port must have a data type that is 
	 * equal to a type of one of the given ports or it must have a super type.
	 * @param referencePort
	 * @param portsToMatch
	 * @return
	 */
	private boolean hasInputPortMatch(InputPort referencePort, List<InputPort> portsToMatch) {
		
		for (Iterator<InputPort> it = portsToMatch.iterator(); it.hasNext(); ) {
			InputPort portToMatch = it.next();
			
			Collection<String> allowedRefTypes = referencePort.getAllowedTypes();
			if (allowedRefTypes.isEmpty())
				return true; //No reference types defined
			
			Collection<String> allowedTypesToMatch = portToMatch.getAllowedTypes();
			if (allowedTypesToMatch.isEmpty())
				return false; //No types to match defined
			
			for (Iterator<String> itRefType = allowedRefTypes.iterator(); itRefType.hasNext(); ) {
				String allowedRefType = itRefType.next();
				
				for (Iterator<String> itMatchType = allowedTypesToMatch.iterator(); itMatchType.hasNext(); ) {
					String typeToMatch = itMatchType.next();
					
					if (typeToMatch.startsWith(allowedRefType) || allowedRefType.startsWith(typeToMatch))
						return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * Checks if one of the given input ports matches the specified reference output port.
	 * Match criteria is the data type. The reference port must have a data type that is 
	 * equal to a type of one of the given ports or it must have a super type.
	 * @param referencePort
	 * @param portsToMatch
	 * @return
	 */
	private boolean hasOutputPortMatch(OutputPort referencePort, List<InputPort> portsToMatch) {
		
		for (Iterator<InputPort> it = portsToMatch.iterator(); it.hasNext(); ) {
			InputPort portToMatch = it.next();
			
			String refType = referencePort.getType();
			if (refType == null || refType.isEmpty())
				return true; //No reference type defined
			
			Collection<String> allowedTypesToMatch = portToMatch.getAllowedTypes();
			if (allowedTypesToMatch.isEmpty())
				return false; //No types to match defined
			
			for (Iterator<String> itMatchType = allowedTypesToMatch.iterator(); itMatchType.hasNext(); ) {
				String typeToMatch = itMatchType.next();
				
				if (typeToMatch.startsWith(refType))
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if one of the given output ports matches the specified reference port.
	 * Match criteria is the data type. The reference port must have a data type that is 
	 * equal to a type of one of the given ports or it must have a super type.
	 * @param referencePort
	 * @param portsToMatch
	 * @return
	 */
	private boolean hasMatch2(DataPort referencePort, List<OutputPort> portsToMatch) {
		if (referencePort instanceof InputPort)
			return hasInputPortMatch2((InputPort)referencePort, portsToMatch);
		else
			return hasOutputPortMatch2((OutputPort)referencePort, portsToMatch);
	}
	
	/**
	 * Checks if one of the given output ports matches the specified reference input port.
	 * Match criteria is the data type. The reference port must have a data type that is 
	 * equal to a type of one of the given ports or it must have a super type.
	 * @param referencePort
	 * @param portsToMatch
	 * @return
	 */
	private boolean hasInputPortMatch2(InputPort referencePort, List<OutputPort> portsToMatch) {
		
		for (Iterator<OutputPort> it = portsToMatch.iterator(); it.hasNext(); ) {
			OutputPort portToMatch = it.next();
			
			Collection<String> allowedRefTypes = referencePort.getAllowedTypes();
			if (allowedRefTypes.isEmpty())
				return true; //No reference types defined
			
			String typeToMatch = portToMatch.getType();
			if (typeToMatch == null || typeToMatch.isEmpty())
				return false; //No type to match defined
			
			for (Iterator<String> itRefType = allowedRefTypes.iterator(); itRefType.hasNext(); ) {
				String allowedRefType = itRefType.next();
				
				if (typeToMatch.startsWith(allowedRefType))
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if one of the given output ports matches the specified reference output port.
	 * Match criteria is the data type. The reference port must have a data type that is 
	 * equal to a type of one of the given ports or it must have a super type.
	 * @param referencePort
	 * @param portsToMatch
	 * @return
	 */
	private boolean hasOutputPortMatch2(OutputPort referencePort, List<OutputPort> portsToMatch) {
		
		for (Iterator<OutputPort> it = portsToMatch.iterator(); it.hasNext(); ) {
			OutputPort portToMatch = it.next();
			
			String refType = referencePort.getType();
			if (refType == null || refType.isEmpty())
				return true; //No reference type defined
			
			String typeToMatch = portToMatch.getType();
			if (typeToMatch == null || typeToMatch.isEmpty())
				return false; //No type to match defined
			
			if (typeToMatch.startsWith(refType))
				return true;
		}
		
		return false;
	}
	
	@Override
	public String getCaption() {
		return "Activity data type matcher";
	}

}
