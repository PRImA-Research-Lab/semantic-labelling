package org.primaresearch.clc.phd.workflow.gui.model;

import java.awt.EventQueue;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.SwingUtilities;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityFactory;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity.IfBranch;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity.IfElseActivityListener;
import org.primaresearch.clc.phd.workflow.gui.dialog.CreateActivityDialog;

/**
 * Tree node for 'if-else' activities
 * 
 * @author clc
 *
 */
public class IfElseActivityNode extends ActivityTreeNode implements IfElseActivityListener {

	private static final long serialVersionUID = 1L;
	private WorkflowTreeModel treeModel;

	public IfElseActivityNode(IfElseActivity activity, WorkflowTreeModel treeModel) {
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
		return "If-else '" + activity.getCaption() + "'";
	}

	/**
	 * Handler for newly created child activities.
	 *
	 * @param activity The new child activity.
	 */
	@Override
	public void onChildActivityCreated(Activity activity) {
		((IfElseActivity)this.activity).createBranch(activity);
	}

	@Override
	public void addChildNodes(WorkflowTreeModel model) {
		Collection<IfBranch> branches = ((IfElseActivity)this.activity).getBranches();
		
		for (Iterator<IfBranch> it = branches.iterator(); it.hasNext(); ) {
			IfBranch branch = it.next();
			
			WorkflowTreeNode newNode = model.createActivityTreeNode(this, branch.getActivity());
			newNode.addChildNodes(model);
		}
		
	}

	@Override
	public void onChildActivityRemovedFromIfElse(IfBranch removedBranch) {
		//Remove node from tree
		for (int i=0; i<getChildCount(); i++) {
			final TreeNode tn = getChildAt(i);
			if (tn instanceof ActivityTreeNode) {
				ActivityTreeNode an = (ActivityTreeNode)tn;
				if (an.getActivity() == removedBranch.getActivity()) {
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

	@Override
	public Activity onNewChildActivityRequest(final IfElseActivity parentActivity) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					
					CreateActivityDialog dlg = new CreateActivityDialog(null, ((WorkflowRootNode)treeModel.getRoot()).getWorkflow(), parentActivity);
					dlg.pack();
					//dlg.setLocation((int)(IfElseActivityNode.this.getLocationOnScreen().getX() + 5.0),
					//				(int)(btnNewActivity.getLocationOnScreen().getY() + btnNewActivity.getHeight() + 10.0));
					dlg.setVisible(true);
					
					Activity newActivity = null;
					WorkflowTreeNode parentNode = IfElseActivityNode.this;
					ActivityFactory activityFactory = new ActivityFactory(parentActivity.getIdRegister());
					
					if (dlg.getSelectedActivityType() != null) 
						newActivity = activityFactory.createActivity(parentActivity, dlg.getSelectedActivityType(), "[New activity]");
					else
						newActivity = dlg.getSelectedActivity();
					
					if (newActivity != null) {
						parentNode.onChildActivityCreated(newActivity);
						WorkflowTreeNode newNode = treeModel.createActivityTreeNode(parentNode, newActivity);
						
						if (newNode != null) {
							//Select the new item
					        //TreeNode[] nodes = treeModel.getPathToRoot(newNode);    
					        //IfElseActivityNode.this.
					        //IfElseActivityNode.this. workflowTree.setSelectionPath(new TreePath(nodes));    
						}
					}
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		});
		return null;
	}
}
