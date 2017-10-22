package org.primaresearch.clc.phd.workflow.activity;

import java.util.Collection;

/**
 * Interface for objects that can have one or more child activities.
 * 
 * @author clc
 *
 */
public interface HasChildActivities {

	/**
	 * Replaces the specified activity with the given new one (recursive).
	 */
	public void replaceActivity(Activity old, Activity replacement);
	
	/**
	 * Returns all direct child activities
	 * @return Collection of activities (might be empty)
	 */
	public Collection<Activity> getChildActivities();
}
