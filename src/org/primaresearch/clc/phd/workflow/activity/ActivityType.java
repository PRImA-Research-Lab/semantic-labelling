package org.primaresearch.clc.phd.workflow.activity;

import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity;

/**
 * Type of workflow activity. Contains constants for all available types.
 * 
 * @author clc
 *
 */
public class ActivityType {

	public static final ActivityType ATOMIC_ACTIVITY = new ActivityType(AtomicActivity.class, "Atomic activity", "");
	public static final ActivityType DIRECTED_GRAPH_ACTIVITY = new ActivityType(DirectedGraphActivity.class, "Directed acyclic graph", "");
	public static final ActivityType FOR_LOOP_ACTIVITY = new ActivityType(ForLoopActivity.class, "For loop", "");
	public static final ActivityType IF_ELSE_ACTIVITY = new ActivityType(IfElseActivity.class, "If-Else", "");
	
	private Class<? extends Activity> activityClass;
	private String caption;
	private String description;
	
	/**
	 * Constructor
	 */
	public ActivityType(Class<? extends Activity> activityClass, String caption, String description) {
		this.activityClass = activityClass;
		this.caption = caption;
		this.description = description;
	}

	public Class<? extends Activity> getActivityClass() {
		return activityClass;
	}

	/**
	 * Caption of the activity (e.g. for displaying in graphical user interface)
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Description of the activity
	 */
	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activityClass == null) ? 0 : activityClass.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivityType other = (ActivityType) obj;
		if (activityClass == null) {
			if (other.activityClass != null)
				return false;
		} else if (!activityClass.equals(other.activityClass))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return caption;
	}


	
	
}
