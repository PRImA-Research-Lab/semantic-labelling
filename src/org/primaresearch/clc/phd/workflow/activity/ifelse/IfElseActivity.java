package org.primaresearch.clc.phd.workflow.activity.ifelse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.primaresearch.clc.phd.ontology.label.Labels;
import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityType;
import org.primaresearch.clc.phd.workflow.activity.BaseActivity;
import org.primaresearch.clc.phd.workflow.activity.HasChildActivities;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;

/**
 * Activity implementation for conditional branching using if else else ...
 * 
 * @author clc
 *
 */
public class IfElseActivity extends BaseActivity implements HasChildActivities {

	private List<IfBranch> branches = new ArrayList<IfBranch>();
	private ArrayList<IfElseActivityListener> listeners = new ArrayList<IfElseActivityListener>();
	
	/**
	 * Constructor
	 * @param parentActivity
	 * @param id
	 * @param idRegister
	 * @param type
	 * @param allowedLabels
	 */
	public IfElseActivity(Activity parentActivity, String id,
			IdGenerator idRegister, Labels allowedLabels) {
		super(parentActivity, id, idRegister, ActivityType.IF_ELSE_ACTIVITY, allowedLabels);
	}
	
	/**
	 * Copy constructor
	 */
	public IfElseActivity(IfElseActivity other) {
		super(other);
		for (int i=0; i<other.getBranches().size(); i++)
			branches.add(other.getBranches().get(i).clone());
	}

	public void addListener(IfElseActivityListener listener) {
		listeners.add(listener);
	}

	public void removeListener(IfElseActivityListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Creates and adds a graph node for a new child activity
	 * @param activity Child activity
	 * @return The graph node
	 */
	public void requestNewBranch() {
		notifyListenersNewChildActivityRequest();
	}
	
	/**
	 * Creates and adds a graph node for a new child activity
	 * @param activity Child activity
	 * @return The graph node
	 */
	public IfBranch createBranch(Activity activity) {
		IfBranch branch = new IfBranch(activity);
		addBranch(branch);
		return branch;
	}
	
	public void addBranch(IfBranch branch) {
		branches.add(branch);
	}

	@Override
	public Collection<DataPort> getSourcePortsForChildren() {
		Collection<DataPort> ret = new LinkedList<DataPort>();
		ret.addAll(getInputPorts());
		return ret;
	}

	@Override
	public Collection<DataPort> getTargetPortsForChildren() {
		Collection<DataPort> ret = new LinkedList<DataPort>();
		ret.addAll(getOutputPorts());
		return ret;
	}

	@Override
	public boolean isAbstract() {
		for (int i=0; i<branches.size(); i++)
			if (branches.get(i).getActivity() != null && branches.get(i).getActivity().isAbstract())
				return true;
		return false;
	}

	@Override
	public Activity clone() {
		return new IfElseActivity(this);
	}
	
	/**
	 * Returns all conditional branches of this activity. Each branch has one child activity
	 * @return
	 */
	public List<IfBranch> getBranches() {
		return branches;
	}
	
	@Override
	public Collection<Activity> getChildActivities() {
		Collection<Activity> ret = new LinkedList<Activity>();
		if (branches != null) {
			for (Iterator<IfBranch> it = branches.iterator(); it.hasNext(); ) {
				IfBranch branch = it.next();
				if (branch.getActivity() != null) 
					ret.add(branch.getActivity());
			}
		}
		return ret;
	}
	
	@Override
	public void replaceActivity(Activity old, Activity replacement) {
		if (branches == null || branches.isEmpty())
			return;
		
		for (Iterator<IfBranch> it = branches.iterator(); it.hasNext(); ) {
			IfBranch branch = it.next();
			if (branch.getActivity() == old) {
				branch.setActivity(replacement);
				break;
			}
			else if (branch.getActivity() instanceof HasChildActivities){
				((HasChildActivities)branch.getActivity()).replaceActivity(old, replacement);
			}
		}
	}
	
	private void notifyListenersBranchRemoved(IfBranch removedBranch) {
		for (Iterator<IfElseActivityListener> it = listeners.iterator(); it.hasNext(); ) {
			it.next().onChildActivityRemovedFromIfElse(removedBranch);
		}
		//TODO Call this method
	}

	private void notifyListenersNewChildActivityRequest() {
		for (Iterator<IfElseActivityListener> it = listeners.iterator(); it.hasNext(); ) {
			if (it.next().onNewChildActivityRequest(this) != null)
				return;
		}
	}
	
	/**
	 * One branch of an if-else activity
	 * 
	 * @author clc
	 *
	 */
	public static class IfBranch {
		private Activity activity;
		private IfCondition condition = null;
		
		/**
		 * Empty constructor. Use setCondition and setActivity
		 */
		public IfBranch() {
		}
		
		/**
		 * Constructor
		 * @param activity
		 */
		public IfBranch(Activity activity) {
			this.activity = activity;
		}
		
		public IfBranch clone() {
			IfBranch copy = new IfBranch();
			copy.setActivity(activity != null ? activity.clone() : null);
			copy.setCondition(condition != null ? condition.clone() : null);
			return copy;
		}

		public Activity getActivity() {
			return activity;
		}

		public void setActivity(Activity activity) {
			this.activity = activity;
		}

		public IfCondition getCondition() {
			return condition;
		}

		public void setCondition(IfCondition condition) {
			this.condition = condition;
		}
		
		public final String toString() {
			if (activity != null)
				return "Branch "+activity.getCaption();
			return "New branch";
		}
	}

	
	/**
	 * Listener interface for if-else activity events
	 * 
	 * @author clc
	 *
	 */
	public static interface IfElseActivityListener {
		/**
		 * Called when a conditional branch node (with a child activity) was removed from the if-else activity
		 * @param removedNode
		 */
		public void onChildActivityRemovedFromIfElse(IfBranch removedBranch);
		
		public Activity onNewChildActivityRequest(IfElseActivity parentActivity);
	}

}
