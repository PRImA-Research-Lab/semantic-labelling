package org.primaresearch.clc.phd.workflow.activity;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Iterator for an activity tree (depth-first traversal)
 * 
 * @author clc
 *
 */
public class ActivityIterator implements Iterator<Activity> {
	
	private Collection<Activity> allActivities = new LinkedList<Activity>();
	private Iterator<Activity> iterator = null;

	public ActivityIterator(Activity startActivity) {
		addToCollection(startActivity);
		iterator = allActivities.iterator();
	}
	
	/**
	 * Collects all activities recursively
	 * @param activity Start activity
	 */
	private void addToCollection(Activity activity) {
		if (activity != null) {
			allActivities.add(activity);
			if (activity instanceof HasChildActivities) {
				Collection<Activity> children = ((HasChildActivities)activity).getChildActivities();
				for (Iterator<Activity> it = children.iterator(); it.hasNext(); ) {
					addToCollection(it.next());
				}
			}
		}
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public Activity next() {
		return iterator.next();
	}

}
