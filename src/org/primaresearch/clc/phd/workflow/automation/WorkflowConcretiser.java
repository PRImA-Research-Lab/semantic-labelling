package org.primaresearch.clc.phd.workflow.automation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.primaresearch.clc.phd.Pair;
import org.primaresearch.clc.phd.repository.search.matching.ActivityDataTypeMatcher;
import org.primaresearch.clc.phd.repository.search.matching.ActivityLabelMatcher;
import org.primaresearch.clc.phd.repository.search.matching.CompositeActivityMatcher;
import org.primaresearch.clc.phd.repository.search.matching.MatchValue;
import org.primaresearch.clc.phd.repository.search.matching.Matcher;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityIterator;
import org.primaresearch.clc.phd.workflow.activity.AtomicActivity;
import org.primaresearch.clc.phd.workflow.activity.HasChildActivities;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;
import org.primaresearch.clc.phd.workflow.gui.model.DataPortAlignment;

/**
 * Tries to make an abstract workflow concrete by replacing all abstract atomic activities
 * with concrete ones from a activity repository.
 * 
 * @author clc
 *
 */
public class WorkflowConcretiser {

	
	private Map<Activity, Workflow> allAvailableAcitivities;
	
	private boolean automatedConcretisation;
	
	private WorkflowConcretisationManager concretisationManager;
	
	private static IntegerActivityPairComparator matchCountComparator = new IntegerActivityPairComparator();
	
	private double strictness = 5.0;
	
	/**
	 * Constructor
	 * @param activitySource Repository with concrete activities
	 * @param automatedConcretisation If set to <code>true</code> no user interaction is called upon, even if there are ambiguities. 
	 */
	public WorkflowConcretiser(boolean automatedConcretisation, WorkflowConcretisationManager concretisationManager) {
		this.automatedConcretisation = automatedConcretisation;
		this.concretisationManager = concretisationManager;
		allAvailableAcitivities = getActivitiesFromRepository(); 
	}
	
	/**
	 * Concretise all abstract atomic activities of the workflow
	 * @return A collection of all replaced activities and their replacements
	 */
	public Collection<Pair<Activity, Activity>> concretise(Workflow workflow) {
		
		long start = System.nanoTime();
		
		Collection<Pair<Activity, Activity>> ret = new LinkedList<Pair<Activity, Activity>>();
		
		//Start with all abstract atomic activities
		Collection<Activity> activitiesToProcess = new LinkedList<Activity>();
		for (ActivityIterator it = workflow.getActivities(); it.hasNext(); ) {
			Activity act = it.next();
			if (act instanceof AtomicActivity && act.isAbstract()) 
				activitiesToProcess.add(act);
		}
		
		//Recursion until no more changes
		while (!activitiesToProcess.isEmpty()) {
		
			//Find all abstract activities and calculate the number of matches
			List<Pair<Integer,Activity>> abstractActivities = new ArrayList<Pair<Integer,Activity>>();
			for (Iterator<Activity> it = activitiesToProcess.iterator(); it.hasNext(); ) {
				Activity act = it.next();
				
				if (act instanceof AtomicActivity && act.isAbstract()) {
					int numberOfMatches = calculateNumberOfMatches(workflow, (AtomicActivity)act);
					abstractActivities.add(new Pair<Integer,Activity>(numberOfMatches, act));
				}
			}
			activitiesToProcess.clear();
			
			//Sort by matches found
			Collections.sort(abstractActivities, matchCountComparator);
			
			//Run the matching / concretisation
			for (Iterator<Pair<Integer,Activity>> it = abstractActivities.iterator(); it.hasNext(); ) {
				Activity act = it.next().right;
				
				Pair<Activity, Activity> res = concretise(workflow, (AtomicActivity)act);
				if (res != null) {
					ret.add(res);
				
					//Still abstract after being replaced?
					if (res.right.isAbstract()) {
						addActivities(res.right, activitiesToProcess);
					}
				}
			}
		}
		
		long end = System.nanoTime();
		long nanoseconds = end - start;
		System.out.println(nanoseconds);
		
		return ret;
	}
		
	/**
	 * Specifies how high a match score has to be for an activity to be considered to replace an abstract activity
	 * @param strictness
	 */
	public void setStrictness(double strictness) {
		this.strictness = strictness;
	}

	/**
	 * Adds the given activity and all children (recursively) to the specified collection
	 * @param act
	 * @param targetCollection
	 */
	private void addActivities(Activity act, Collection<Activity> targetCollection) {
		if (act == null)
			return;
		
		targetCollection.add(act);
		
		if (act instanceof HasChildActivities) {
			HasChildActivities container = (HasChildActivities)act;
			for (Iterator<Activity> it = container.getChildActivities().iterator(); it.hasNext(); ) {
				addActivities(it.next(), targetCollection);
			}
		}
	}
	
	/**
	 * Only calculates the number of good matches for the given activity without doing the actual replacement
	 * @param workflow
	 * @param act
	 * @return
	 */
	int calculateNumberOfMatches(Workflow workflow, AtomicActivity act) {
		if (act == null)
			return 0;

		//Matching
		// Create matcher
		Matcher<Activity> matcher = null;
		CompositeActivityMatcher compMatcher = new CompositeActivityMatcher();
		compMatcher.addSubMatcher(new ActivityLabelMatcher(act, Matcher.MATCHING_FOR_REPLACING));
		compMatcher.addSubMatcher(new ActivityDataTypeMatcher(act, Matcher.MATCHING_FOR_REPLACING, automatedConcretisation), 
									automatedConcretisation);
		matcher = compMatcher;
		
		Collection<Activity> activitiesOnly = new LinkedList<Activity>();
		for (Iterator<Activity> it = allAvailableAcitivities.keySet().iterator(); it.hasNext(); )
			activitiesOnly.add(it.next());
		
		// Run matching
		List<MatchValue<Activity>> result = matcher.match(activitiesOnly);

		// Get best result
		List<MatchValue<Activity>> bestMatches = new ArrayList<MatchValue<Activity>>();
		if (!result.isEmpty()) {
			Collections.sort(result);
			
			MatchValue<Activity> resItem = result.get(0);
			double bestMatchScore = resItem.getMatchScore();
			if (bestMatchScore >= strictness) {
				
				//Are there multiple best matches?
				for (Iterator<MatchValue<Activity>> it = result.iterator(); it.hasNext(); ) {
					MatchValue<Activity> currentMatchItem = it.next();
					
					if (Math.abs(currentMatchItem.getMatchScore() - bestMatchScore) < 0.1)
						bestMatches.add(currentMatchItem);
				}
			}
		}
		
		return bestMatches.size();	
	}
	
	/**
	 * Replace the given abstract activity with a concrete one from the repository
	 * @param workflow Parent workflow of activity that is to be replaced
	 * @param act To be replaced
	 * @return Replaced activities and its replacement or <code>null</code> if not replaced
	 */
	private Pair<Activity, Activity> concretise(Workflow workflow, AtomicActivity act) {
		
		if (act == null)
			return null;

		//Matching
		// Create matcher
		Matcher<Activity> matcher = null;
		CompositeActivityMatcher compMatcher = new CompositeActivityMatcher();
		compMatcher.addSubMatcher(new ActivityLabelMatcher(act, Matcher.MATCHING_FOR_REPLACING));
		compMatcher.addSubMatcher(new ActivityDataTypeMatcher(act, Matcher.MATCHING_FOR_REPLACING, automatedConcretisation),
								automatedConcretisation);
		matcher = compMatcher;
		
		Pair<Activity, Activity> match = concretise(workflow, act, allAvailableAcitivities, !automatedConcretisation, matcher);

		return match;
	}
		
	/**
	 * Replace the given abstract activity with a concrete one from the repository
	 * @param workflow Parent workflow of activity that is to be replaced
	 * @param act To be replaced
	 * @param userInteration
	 * @return Replaced activities and its replacement or <code>null</code> if not replaced
	 */
	private Pair<Activity, Activity> concretise(Workflow workflow, AtomicActivity act, Map<Activity, Workflow> activitiesToConsider,
			boolean userInteration, Matcher<Activity> matcher) {
		
		Collection<Activity> activitiesOnly = new LinkedList<Activity>();
		for (Iterator<Activity> it = activitiesToConsider.keySet().iterator(); it.hasNext(); )
			activitiesOnly.add(it.next());
		
		// Run matching
		List<MatchValue<Activity>> result = matcher.match(activitiesOnly);
		
		// Get best result
		double maxMatchscoreDiff = 5.0;
		if (!result.isEmpty()) {
			Collections.sort(result);
			
			MatchValue<Activity> resItem = result.get(0);
			Activity matchingActivity = resItem.getObject();
			double bestMatchScore = resItem.getMatchScore();
			if (automatedConcretisation && bestMatchScore >= strictness
				|| !automatedConcretisation && bestMatchScore > 0.0) {
				
				if (bestMatchScore < strictness)
					maxMatchscoreDiff = 10.0; //Show more results if assisted matching and low match score
				
				//Are there multiple best matches?
				List<MatchValue<Activity>> bestMatches = new ArrayList<MatchValue<Activity>>();
				for (Iterator<MatchValue<Activity>> it = result.iterator(); it.hasNext(); ) {
					MatchValue<Activity> currentMatchItem = it.next();
					
					if (Math.abs(currentMatchItem.getMatchScore() - bestMatchScore) < maxMatchscoreDiff)
						bestMatches.add(currentMatchItem);
				}
				
				//User interaction?
				if (userInteration) {
					//In interactive mode we also consider lower match scores, but we will ask the user
					if (bestMatchScore < strictness) {
						String userMessageForRefinement = "Matching activities were found but the match score is lower than desired ('"+act.getCaption()+"')";
						
						List<Pair<Activity, Workflow>> bestActivities = new LinkedList<Pair<Activity, Workflow>>();
						for (Iterator<MatchValue<Activity>> it = bestMatches.iterator(); it.hasNext(); ) {
							Activity currAct = it.next().getObject();
							bestActivities.add(new Pair<Activity, Workflow>(currAct, activitiesToConsider.get(currAct)));
						}
						
						bestActivities = concretisationManager.refineMatch(bestActivities, userMessageForRefinement);
						
						if (bestActivities == null || bestActivities.isEmpty())
							return null;
						
						//TODO: Select 'good' activity according to certain criteria (usage in other workflows, for example)
						matchingActivity = bestActivities.iterator().next().left;
					}
					//If there are multiple best matches and we forbid ambiguities, return null to indicate that the user has to interact
					else if (bestMatches.size() > 1) {
						String userMessageForRefinement = "Multiple activities match the placeholder '"+act.getCaption()+"'";
						
						List<Pair<Activity, Workflow>> bestActivities = new LinkedList<Pair<Activity, Workflow>>();
						for (Iterator<MatchValue<Activity>> it = bestMatches.iterator(); it.hasNext(); ) {
							Activity currAct = it.next().getObject();
							bestActivities.add(new Pair<Activity, Workflow>(currAct, activitiesToConsider.get(currAct)));
						}
						
						bestActivities = concretisationManager.refineMatch(bestActivities, userMessageForRefinement);
						
						if (bestActivities == null || bestActivities.isEmpty())
							return null;
						
						//TODO: Select 'good' activity according to certain criteria (usage in other workflows, for example)
						matchingActivity = bestActivities.iterator().next().left;
					}
				}
				
				//Replace
				if (matchingActivity != null) {
					Activity replacementActivity = matchingActivity.clone(); //Clone (so the original is not changed)
					//Port alignment
					DataPortAlignment<InputPort> inputPortAlignment 
													= new DataPortAlignment<InputPort>(	workflow, 
																						act.getInputPorts(), 
																						act,
																						replacementActivity.getInputPorts(), 
																						replacementActivity);
					inputPortAlignment.autoAlign();
					inputPortAlignment.copyDataToAlignedPorts();
					DataPortAlignment<OutputPort> outputPortAlignment 
													= new DataPortAlignment<OutputPort>(workflow,
																						act.getOutputPorts(), 
																						act,
																						replacementActivity.getOutputPorts(), 
																						replacementActivity);
					outputPortAlignment.autoAlign();
					outputPortAlignment.copyDataToAlignedPorts();
					
					//Replace activity
					workflow.replaceActivity(act, replacementActivity);
					
					//Activities are only used once for now -> remove from list of available activities
					//TODO Should this be limited to siblings in a acyclic graph activity? 
					//     Some activities might occur several times (e.g. converters)
					allAvailableAcitivities.remove(matchingActivity);
					
					concretisationManager.onActivityReplaced(act,  replacementActivity);
					
					return new Pair<Activity, Activity>(act, replacementActivity);
				}
			}
		}
		return null;
	}
	
	/**
	 * Collects all root activities from the given workflow repository
	 */
	private Map<Activity, Workflow> getActivitiesFromRepository() {
		
		return concretisationManager.getAvailableActivities();
	}
	
	/**
	 * Comparator implementation to sort activities by match count
	 * 
	 * @author clc
	 *
	 */
	private static class IntegerActivityPairComparator implements Comparator<Pair<Integer,Activity>> {

		@Override
		public int compare(Pair<Integer, Activity> p1, Pair<Integer, Activity> p2) {
			
			return p1.left.intValue() - p2.left.intValue();
		}
		
	}
}
