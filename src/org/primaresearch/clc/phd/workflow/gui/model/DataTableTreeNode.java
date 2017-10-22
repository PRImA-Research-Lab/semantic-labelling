package org.primaresearch.clc.phd.workflow.gui.model;

import org.primaresearch.clc.phd.workflow.data.DataTable;

/**
 * Tree node for data tables within the workflow
 * @author clc
 *
 */
public class DataTableTreeNode extends WorkflowTreeNode {

	private static final long serialVersionUID = 1L;
	private DataTable dataTable;

	public DataTableTreeNode(DataTable dataTable) {
		super();
		this.dataTable = dataTable;
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
	public void addChildNodes(WorkflowTreeModel model) {
		//No children allowed
	}

	@Override
	public boolean isRemovable() {
		return true;
	}

	@Override
	public String toString() {
		return "Data table '" + dataTable.getCaption() + "'";
	}

	public DataTable getDataTable() {
		return dataTable;
	}

}
