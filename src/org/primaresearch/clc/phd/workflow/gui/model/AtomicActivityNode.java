package org.primaresearch.clc.phd.workflow.gui.model;

import org.primaresearch.clc.phd.workflow.activity.AtomicActivity;

/**
 * Tree node for atomic workflow activities
 * 
 * @author clc
 *
 */
public class AtomicActivityNode extends ActivityTreeNode {

	private static final long serialVersionUID = 1L;

	public AtomicActivityNode(AtomicActivity activity) {
		super(activity);
	}

	@Override
	public boolean canCreateChildActivity() {
		return false;
	}

	@Override
	public boolean canCreateChildDataTable() {
		return false;
	}

	@Override
	public String toString() {
		return "Atomic activity '" + activity.getCaption() + "'";
	}

	@Override
	public void addChildNodes(WorkflowTreeModel model) {
		//No children allowed
	}

}
