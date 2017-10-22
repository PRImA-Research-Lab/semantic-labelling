package org.primaresearch.clc.phd.repository.search.matching;

import java.util.List;

import javax.swing.JComponent;

import org.primaresearch.clc.phd.workflow.activity.Activity;

/**
 * Interface for user interface views showing activity matching results.
 * 
 * @author clc
 *
 */
public interface ActivityMatchingResultView {

	public void update(List<MatchValue<Activity>> matchingResult);
	
	public JComponent getComponent();
	
	/**
	 * Removes all results from the view
	 */
	public void clear();
	
	public void addListener(ActivityMatchingResultItemListener listener);
	
	public void removeListener(ActivityMatchingResultItemListener listener);

	
	/**
	 * Listener interface for result items of workflow search or activity matching
	 * 
	 * @author clc
	 *
	 */
	public static interface ActivityMatchingResultItemListener {
		/** Result item has been clicked on */
		public void activityMatchingResultItemClicked(MatchValue<Activity> itemValue);
	}
}
