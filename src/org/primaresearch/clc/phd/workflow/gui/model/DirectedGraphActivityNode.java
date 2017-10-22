package org.primaresearch.clc.phd.workflow.gui.model;

import java.awt.EventQueue;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.DirectedGraphActivity;
import org.primaresearch.clc.phd.workflow.activity.DirectedGraphActivity.ActivityNode;
import org.primaresearch.clc.phd.workflow.activity.DirectedGraphActivity.DirectedGraphActivityListener;

/**
 * Tree node for 'directed acyclic graph' activities
 * 
 * @author clc
 *
 */
public class DirectedGraphActivityNode extends ActivityTreeNode implements DirectedGraphActivityListener {

	private static final long serialVersionUID = 1L;
	private WorkflowTreeModel treeModel;

	public DirectedGraphActivityNode(DirectedGraphActivity activity, WorkflowTreeModel treeModel) {
		super(activity);
		activity.addListener(this);
		this.treeModel = treeModel;
	}

	@Override
	public boolean canCreateChildActivity() {
		return true;
	}

	@Override
	public boolean canCreateChildDataTable() {
		return false;
	}

	@Override
	public String toString() {
		return "Directed graph '" + activity.getCaption() + "'";
	}

	/**
	 * Handler for newly created child activities.
	 *
	 * @param activity The new child activity.
	 */
	@Override
	public void onChildActivityCreated(Activity activity) {
		((DirectedGraphActivity)this.activity).createNode(activity);
	}

	@Override
	public void addChildNodes(WorkflowTreeModel model) {
		Collection<ActivityNode> graphNodes = ((DirectedGraphActivity)this.activity).getGraphNodes();
		
		for (Iterator<ActivityNode> it = graphNodes.iterator(); it.hasNext(); ) {
			ActivityNode activityNode = it.next();
			
			WorkflowTreeNode newNode = model.createActivityTreeNode(this, activityNode.getActivity());
			newNode.addChildNodes(model);
		}
		
	}

	@Override
	public void onChildActivityRemovedFromDirectedGraph(ActivityNode removedNode) {
		//Remove node from tree
		for (int i=0; i<getChildCount(); i++) {
			final TreeNode tn = getChildAt(i);
			if (tn instanceof ActivityTreeNode) {
				ActivityTreeNode an = (ActivityTreeNode)tn;
				if (an.getActivity() == removedNode.getActivity()) {
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								treeModel.removeNodeFromParent((MutableTreeNode)tn);
							} 
							catch(IllegalArgumentException exc) { //Happens if the node is tried to be removed twice
							}
						}
					});						
					break;
				}
			}
		}
	}
}
