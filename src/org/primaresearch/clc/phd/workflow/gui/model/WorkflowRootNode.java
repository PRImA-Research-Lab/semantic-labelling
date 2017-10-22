package org.primaresearch.clc.phd.workflow.gui.model;

import java.util.Iterator;

import javax.swing.tree.TreeNode;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.data.DataTable;

/**
 * Root node for workflow tree
 * 
 * @author clc
 *
 */
public class WorkflowRootNode extends WorkflowTreeNode {

	private static final long serialVersionUID = 1L;

	private Workflow workflow;

	public WorkflowRootNode(Workflow workflow) {
		super();
		this.workflow = workflow;
	}

	@Override
	public String toString() {
		return "Workflow '" + workflow.getName() + "'";
	}
	
	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public TreeNode getParent() {
		return null;
	}

	@Override
	public boolean canCreateChildActivity() {
		return workflow != null && workflow.getRootActivity() == null;
	}
	
	@Override
	public boolean canCreateChildDataTable() {
		return true;
	}
	
	@Override
	public void onChildActivityCreated(Activity activity) {
		workflow.setRootActivity(activity);
	}
	
	public void addChildNodes(WorkflowTreeModel model) {
		//Data tables
		if (workflow.getDataTables() != null) {
			for (Iterator<DataTable> it = workflow.getDataTables().iterator(); it.hasNext(); ) {
				WorkflowTreeNode newNode = model.createDataTableTreeNode(this, it.next());
				newNode.addChildNodes(model);
			}
		}
		
		//Activities
		if (workflow.getRootActivity() != null) {
			WorkflowTreeNode newNode = model.createActivityTreeNode(this, workflow.getRootActivity());
			newNode.addChildNodes(model);
		}
	}

	@Override
	public boolean isRemovable() {
		return false;
	}

	public Workflow getWorkflow() {
		return workflow;
	}
	
	
}
