package org.primaresearch.clc.phd.workflow.automation;

import java.util.List;
import java.util.Map;

import org.primaresearch.clc.phd.Pair;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;

/**
 * Interface for concretisation events, possibly requiring user interaction
 * 
 * @author clc
 *
 */
public interface WorkflowConcretisationManager {

	/**
	 * Returns activities the contretiser can use as source for finding suitable matches
	 * @return Workflow activities (use <code>null</code> to indicate the user wishes to cancel)
	 */
	public Map<Activity, Workflow> getAvailableActivities();

	/**
	 * 
	 * @param bestMatchingActivities
	 * @param message Optional message for the user (if refineByUser is true)
	 * @return Refined activity list (if the user selects an activity, this list will contain only one item)
	 */
	public List<Pair<Activity, Workflow>> refineMatch(List<Pair<Activity, Workflow>> bestMatchingActivities, String message);
	
	/**
	 * Callback function used for on-the-fly replacement information
	 * @param oldActivity
	 * @param replacement
	 */
	public void onActivityReplaced(Activity oldActivity, Activity replacement);
	
}
