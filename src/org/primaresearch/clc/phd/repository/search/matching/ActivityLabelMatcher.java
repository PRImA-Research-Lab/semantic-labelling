package org.primaresearch.clc.phd.repository.search.matching;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.primaresearch.clc.phd.ontology.label.HasLabels;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;

/**
 * Matcher implementation comparing semantic labels of the activity itself and all data ports
 * 
 * @author clc
 *
 */
public class ActivityLabelMatcher implements Matcher<Activity> {
	
	private Activity referenceActivity;
	private int matchingType;
	
	/**
	 * Constructor
	 * @param referenceActivity
	 */
	public ActivityLabelMatcher(Activity referenceActivity, int matchingType) {
		this.referenceActivity = referenceActivity;
		this.matchingType = matchingType;
	}

	@Override
	public List<MatchValue<Activity>> match(Collection<Activity> objectsToMatch) {
		if (matchingType == Matcher.MATCHING_FOR_REPLACING)
			return matchForReplacing(objectsToMatch);
		else if (matchingType == Matcher.MATCHING_FOR_ADDING_CHILD)
			return matchForAddingChild(objectsToMatch);
		return null;
	}
	
	/**
	 * Matching to find a similar activity.
	 */
	private List<MatchValue<Activity>> matchForReplacing(Collection<Activity> objectsToMatch) {
		
		List<MatchValue<Activity>> ret = new LinkedList<MatchValue<Activity>>();
		
		//Create label matchers for the activity and all ports
		LabellableObjectMatcher activityLabelMatcher = new LabellableObjectMatcher(referenceActivity);
		Collection<LabellableObjectMatcher> refInputPortMatchers = new LinkedList<LabellableObjectMatcher>();
		for (Iterator<InputPort> it = referenceActivity.getInputPorts().iterator(); it.hasNext(); )
			refInputPortMatchers.add(new LabellableObjectMatcher(it.next().getDataObject()));
		Collection<LabellableObjectMatcher> refOutputPortMatchers = new LinkedList<LabellableObjectMatcher>();
		for (Iterator<OutputPort> it = referenceActivity.getOutputPorts().iterator(); it.hasNext(); )
			refOutputPortMatchers.add(new LabellableObjectMatcher(it.next().getDataObject()));
		
		//Matching
		for (Iterator<Activity> it=objectsToMatch.iterator(); it.hasNext(); ) {
			Activity objectToMatch = it.next();
			
			//Create label matchers for the all ports of the object to match
			Collection<LabellableObjectMatcher> objToMatchInputPortMatchers = new LinkedList<LabellableObjectMatcher>();
			for (Iterator<InputPort> it2 = objectToMatch.getInputPorts().iterator(); it2.hasNext(); )
				objToMatchInputPortMatchers.add(new LabellableObjectMatcher(it2.next().getDataObject()));
			Collection<LabellableObjectMatcher> objToMatchOutputPortMatchers = new LinkedList<LabellableObjectMatcher>();
			for (Iterator<OutputPort> it2 = objectToMatch.getOutputPorts().iterator(); it2.hasNext(); )
				objToMatchOutputPortMatchers.add(new LabellableObjectMatcher(it2.next().getDataObject()));

			int count = 1;
			double combinedPercentage = 0.0;
			List<MatchValue<?>> subValues = new LinkedList<MatchValue<?>>();
			//Activity labels
			MatchValue<HasLabels> activityLabelMatchResult = activityLabelMatcher.match(objectToMatch);
			combinedPercentage += activityLabelMatchResult.getMatchScore();
			subValues.add(new ActivityMatchValue(objectToMatch, combinedPercentage, 0, 
						"Activity Labels", activityLabelMatchResult.getSubValues()));
			
			//Input port labels
			// Reference object
			List<MatchValue<?>> categorySubValues = new LinkedList<MatchValue<?>>();
			double categoryPercentage = 0.0;
			int categoryCount = 0;
			for (Iterator<LabellableObjectMatcher> itM = refInputPortMatchers.iterator(); itM.hasNext(); ) {
				//Find best match
				List<MatchValue<HasLabels>> matches = new LinkedList<MatchValue<HasLabels>>();
				LabellableObjectMatcher matcher = itM.next();
				for (Iterator<InputPort> itP = objectToMatch.getInputPorts().iterator(); itP.hasNext(); )
					matches.add(matcher.match(itP.next().getDataObject()));
				Collections.sort(matches);
				if (matches.size() > 0) {
					combinedPercentage += matches.get(0).getMatchScore();
					categoryPercentage += matches.get(0).getMatchScore();
					categorySubValues.add(matches.get(0));
					//categorySubValues.addAll(matches.get(0).getSubValues());
				}
				count++;
				categoryCount++;
			}
			if (categoryCount > 0)
				categoryPercentage /= categoryCount;
			subValues.add(new ActivityMatchValue(objectToMatch, categoryPercentage, 2,
					"Input ports of reference activity", categorySubValues));
			
			// Object to match
			//  Extra input ports are a problem (where is the extra data coming from?)
			categorySubValues = new LinkedList<MatchValue<?>>();
			categoryPercentage = 0.0;
			categoryCount = 0;
			for (Iterator<LabellableObjectMatcher> itM = objToMatchInputPortMatchers.iterator(); itM.hasNext(); ) {
				//Find best match
				List<MatchValue<HasLabels>> matches = new LinkedList<MatchValue<HasLabels>>();
				LabellableObjectMatcher matcher = itM.next();
				for (Iterator<InputPort> itP = referenceActivity.getInputPorts().iterator(); itP.hasNext(); )
					matches.add(matcher.match(itP.next().getDataObject()));
				Collections.sort(matches);
				if (matches.size() > 0) {
					if (matches.get(0).getMatchScore() < 0.001) {
						combinedPercentage += matches.get(0).getMatchScore();
						categoryPercentage += matches.get(0).getMatchScore();
						categorySubValues.add(matches.get(0));
						//categorySubValues.addAll(matches.get(0).getSubValues());
						count++;
						categoryCount++;
					}
				} else {
					count++;
					categoryCount++;
				}
			}
			if (categoryCount > 0)
				categoryPercentage /= categoryCount;
			subValues.add(new ActivityMatchValue(objectToMatch, categoryPercentage, 0,
					"Unwanted input ports", categorySubValues));
			
			//Output port labels
			// Reference object
			categorySubValues = new LinkedList<MatchValue<?>>();
			categoryPercentage = 0.0;
			categoryCount = 0;
			for (Iterator<LabellableObjectMatcher> itM = refOutputPortMatchers.iterator(); itM.hasNext(); ) {
				//Find best match
				List<MatchValue<HasLabels>> matches = new LinkedList<MatchValue<HasLabels>>();
				LabellableObjectMatcher matcher = itM.next();
				for (Iterator<OutputPort> itP = objectToMatch.getOutputPorts().iterator(); itP.hasNext(); )
					matches.add(matcher.match(itP.next().getDataObject()));
				Collections.sort(matches);
				if (matches.size() > 0) {
					combinedPercentage += matches.get(0).getMatchScore();
					categoryPercentage += matches.get(0).getMatchScore();
					categorySubValues.add(matches.get(0));
					//categorySubValues.addAll(matches.get(0).getSubValues());
				}
				count++;
				categoryCount++;
			}			
			if (categoryCount > 0)
				categoryPercentage /= categoryCount;
			subValues.add(new ActivityMatchValue(objectToMatch, categoryPercentage, 2,
					"Output ports of reference activity", categorySubValues));

			// Object to match
			//  Not used (extra output ports are not a problem)
			/*for (Iterator<LabellableObjectMatcher> itM = objToMatchOutputPortMatchers.iterator(); itM.hasNext(); ) {
				//Find best match
				List<MatchValue<HasLabels>> matches = new LinkedList<MatchValue<HasLabels>>();
				LabellableObjectMatcher matcher = itM.next();
				for (Iterator<OutputPort> itP = referenceActivity.getOutputPorts().iterator(); itP.hasNext(); )
					matches.add(matcher.match(itP.next().getDataObject()));
				Collections.sort(matches);
				if (matches.size() > 0)
					combinedPercentage += matches.get(0).getMatchScore();
				count++;
			}*/						
			combinedPercentage /= (double)count;
			
			ret.add(new ActivityMatchValue(objectToMatch, combinedPercentage, -1, "All Labels", subValues));
		}
		
		return ret;		
	}
	
	/**
	 * Matching to find suitable child activity.
	 */
	private List<MatchValue<Activity>> matchForAddingChild(Collection<Activity> objectsToMatch) {
		
		List<MatchValue<Activity>> ret = new LinkedList<MatchValue<Activity>>();
		
		//Create label matchers for the activity and all ports
		/*Collection<LabellableObjectMatcher> refSourcePortMatchers = new LinkedList<LabellableObjectMatcher>();
		for (Iterator<DataPort> it = referenceActivity.getSourcePortsForChildren().iterator(); it.hasNext(); )
			refSourcePortMatchers.add(new LabellableObjectMatcher(it.next().getDataObject()));
		Collection<LabellableObjectMatcher> refTargetPortMatchers = new LinkedList<LabellableObjectMatcher>();
		for (Iterator<DataPort> it = referenceActivity.getTargetPortsForChildren().iterator(); it.hasNext(); )
			refTargetPortMatchers.add(new LabellableObjectMatcher(it.next().getDataObject()));
		*/
		
		//Matching
		for (Iterator<Activity> it=objectsToMatch.iterator(); it.hasNext(); ) {
			Activity objectToMatch = it.next();
			
			//Create label matchers for the all ports of the object to match
			Collection<LabellableObjectMatcher> objToMatchInputPortMatchers = new LinkedList<LabellableObjectMatcher>();
			for (Iterator<InputPort> it2 = objectToMatch.getInputPorts().iterator(); it2.hasNext(); )
				objToMatchInputPortMatchers.add(new LabellableObjectMatcher(it2.next().getDataObject()));
			Collection<LabellableObjectMatcher> objToMatchOutputPortMatchers = new LinkedList<LabellableObjectMatcher>();
			for (Iterator<OutputPort> it2 = objectToMatch.getOutputPorts().iterator(); it2.hasNext(); )
				objToMatchOutputPortMatchers.add(new LabellableObjectMatcher(it2.next().getDataObject()));

			int count = 0;
			double combinedPercentage = 0.0;
			List<MatchValue<?>> subValues = new LinkedList<MatchValue<?>>();
			
			//Input port labels
			// Reference object
			List<MatchValue<?>> categorySubValues = new LinkedList<MatchValue<?>>();
			double categoryPercentage = 0.0;
			int categoryCount = 0;
			/*for (Iterator<LabellableObjectMatcher> itM = refSourcePortMatchers.iterator(); itM.hasNext(); ) {
				//Find best match
				List<MatchValue<HasLabels>> matches = new LinkedList<MatchValue<HasLabels>>();
				LabellableObjectMatcher matcher = itM.next();
				for (Iterator<InputPort> itP = objectToMatch.getInputPorts().iterator(); itP.hasNext(); )
					matches.add(matcher.match(itP.next().getDataObject()));
				Collections.sort(matches);
				if (matches.size() > 0) {
					combinedPercentage += matches.get(0).getMatchScore();
					categoryPercentage += matches.get(0).getMatchScore();
					categorySubValues.addAll(matches.get(0).getSubValues());
					count++;
					categoryCount++;
				}
			}
			if (categoryCount > 0)
				categoryPercentage /= categoryCount;
			subValues.add(new ActivityMatchValue(objectToMatch, categoryPercentage, 
					"Source ports of parent activity", categorySubValues));
			*/
			
			// Object to match
			//  Extra input ports are a problem (where is the extra data coming from?)
			//categorySubValues = new LinkedList<MatchValue<?>>();
			//categoryPercentage = 0.0;
			//categoryCount = 0;
			for (Iterator<LabellableObjectMatcher> itM = objToMatchInputPortMatchers.iterator(); itM.hasNext(); ) {
				//Find best match
				List<MatchValue<HasLabels>> matches = new LinkedList<MatchValue<HasLabels>>();
				LabellableObjectMatcher matcher = itM.next();
				for (Iterator<DataPort> itP = referenceActivity.getSourcePortsForChildren().iterator(); itP.hasNext(); )
					matches.add(matcher.match(lookForInheritedLabels(itP.next()).getDataObject()));
				Collections.sort(matches);
				if (matches.size() > 0) {
					combinedPercentage += matches.get(0).getMatchScore();
					categoryPercentage += matches.get(0).getMatchScore();
					categorySubValues.add(matches.get(0));
					//categorySubValues.addAll(matches.get(0).getSubValues());
				} 
				count++;
				categoryCount++;
			}
			if (categoryCount > 0) {
				categoryPercentage /= categoryCount;
				subValues.add(new ActivityMatchValue(objectToMatch, categoryPercentage, 1,
					"Input ports", categorySubValues));
			}
			
			//Output port labels
			// Object to match
			categorySubValues = new LinkedList<MatchValue<?>>();
			categoryPercentage = 0.0;
			categoryCount = 0;
			for (Iterator<LabellableObjectMatcher> itM = objToMatchOutputPortMatchers.iterator(); itM.hasNext(); ) {
				//Find best match
				List<MatchValue<HasLabels>> matches = new LinkedList<MatchValue<HasLabels>>();
				LabellableObjectMatcher matcher = itM.next();
				for (Iterator<DataPort> itP = referenceActivity.getTargetPortsForChildren().iterator(); itP.hasNext(); )
					matches.add(matcher.match(lookForInheritedLabels(itP.next()).getDataObject()));
				Collections.sort(matches);
				if (matches.size() > 0) {
					combinedPercentage += matches.get(0).getMatchScore();
					categoryPercentage += matches.get(0).getMatchScore();
					//categorySubValues.addAll(matches.get(0).getSubValues());
					categorySubValues.add(matches.get(0));
					count++;
					categoryCount++;
				} 
			}
			if (categoryCount > 0) {
				categoryPercentage /= categoryCount;
				subValues.add(new ActivityMatchValue(objectToMatch, categoryPercentage, 1,
						"Output ports", categorySubValues));
			}
			/*for (Iterator<LabellableObjectMatcher> itM = refTargetPortMatchers.iterator(); itM.hasNext(); ) {
				//Find best match
				List<MatchValue<HasLabels>> matches = new LinkedList<MatchValue<HasLabels>>();
				LabellableObjectMatcher matcher = itM.next();
				for (Iterator<OutputPort> itP = objectToMatch.getOutputPorts().iterator(); itP.hasNext(); )
					matches.add(matcher.match(itP.next().getDataObject()));
				Collections.sort(matches);
				if (matches.size() > 0) {
					combinedPercentage += matches.get(0).getMatchScore();
					categoryPercentage += matches.get(0).getMatchScore();
					categorySubValues.addAll(matches.get(0).getSubValues());
					count++;
					categoryCount++;
				}
			}			
			if (categoryCount > 0)
				categoryPercentage /= categoryCount;
			subValues.add(new ActivityMatchValue(objectToMatch, categoryPercentage, 
					"Output ports", categorySubValues));
					*/

			if (count > 0) {
				combinedPercentage /= (double)count;
			
				ret.add(new ActivityMatchValue(objectToMatch, combinedPercentage, 1, "All Labels", subValues));
			} else
				ret.add(new ActivityMatchValue(objectToMatch, 100.0, 1, "All Labels", subValues));
		}
		
		return ret;		
	}
	
	public static DataPort lookForInheritedLabels(DataPort port) {
		if (port == null || port.getDataObject() == null || port.getDataObject().hasLabels())
			return port;
		
		if (port instanceof InputPort) {
			DataPort source = ((InputPort)port).getSource();
			if (source == null || source.getDataObject() == null)
				return port;
			return lookForInheritedLabels(source);
		}
		if (port instanceof OutputPort) {
			if (!((OutputPort)port).getForwardedPorts().isEmpty()) {
				//TODO: Use all forwarded ports?
				DataPort forwarded = ((OutputPort)port).getForwardedPorts().iterator().next();
				if (forwarded == null || forwarded.getDataObject() == null)
					return port;
				return lookForInheritedLabels(forwarded);
			}
		}
		return port;
	}
	
	@Override
	public String getCaption() {
		return "Activity label matcher";
	}

}
