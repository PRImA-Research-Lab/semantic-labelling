package org.primaresearch.clc.phd.workflow.gui.panel;

import javax.swing.JPanel;
import javax.swing.tree.TreeNode;

import org.primaresearch.clc.phd.workflow.gui.model.WorkflowTreeModel;

/**
 * Abstract panel class for workflow details
 * 
 * @author clc
 *
 */
public abstract class DetailsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public abstract void refresh(TreeNode selectedNode, WorkflowTreeModel workflowTreeModel);
	
}
