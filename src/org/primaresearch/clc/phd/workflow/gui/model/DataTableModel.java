package org.primaresearch.clc.phd.workflow.gui.model;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.primaresearch.clc.phd.workflow.data.DataCollection;
import org.primaresearch.clc.phd.workflow.data.DataObject;
import org.primaresearch.clc.phd.workflow.data.DataObjectFactory;
import org.primaresearch.clc.phd.workflow.data.DataTable;
import org.primaresearch.clc.phd.workflow.data.SingleDataObject;
import org.primaresearch.clc.phd.workflow.data.port.DataTableColumn;

/**
 * Implementation of a table model for workflow data tables.
 * 
 * @author clc
 *
 */
public class DataTableModel extends AbstractTableModel implements TableModel {

	private static final long serialVersionUID = 1L;
	private DataTable dataTable;
	private DataObjectFactory objectFactory;
	
	public DataTableModel(DataTable dataTable, DataObjectFactory objectFactory) {
		this.dataTable = dataTable;
		this.objectFactory = objectFactory;
	}

	//@Override
	//public Class<?> getColumnClass(int arg0) {
	//	return null;
	//}

	@Override
	public int getColumnCount() {
		return dataTable.getColumns().size();
	}

	@Override
	public String getColumnName(int colIndex) {
		return dataTable.getColumns().get(colIndex).getDataObject().getCaption();
	}

	@Override
	public int getRowCount() {
		if (dataTable.getColumns() == null || dataTable.getColumns().isEmpty())
			return 0;
		return ((DataCollection)dataTable.getColumns().get(0).getDataObject()).getSize();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		DataObject obj = ((DataCollection)dataTable.getColumns().get(columnIndex).getDataObject()).getDataItem(rowIndex);
		if (obj instanceof SingleDataObject)
			return ((SingleDataObject)obj).getValue();
		return "...";
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return true;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		DataObject obj = ((DataCollection)dataTable.getColumns().get(columnIndex).getDataObject()).getDataItem(rowIndex);
		if (obj instanceof SingleDataObject)
			((SingleDataObject)obj).setValue(value.toString());
	}
	
	/**
	 * Adds a new row (one data object for each column)
	 */
	public void addRow() {
		for (int i=0; i<dataTable.getColumns().size(); i++) {
			DataTableColumn col = dataTable.getColumns().get(i);
			((DataCollection)col.getDataObject()).addDataItem(objectFactory.createSingleDataObject());
			this.fireTableDataChanged();
		}
	}

}
