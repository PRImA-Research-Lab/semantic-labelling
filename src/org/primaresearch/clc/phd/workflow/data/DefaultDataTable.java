package org.primaresearch.clc.phd.workflow.data;

import java.util.ArrayList;
import java.util.List;

import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.data.port.DataTableColumn;

/**
 * Default implementation for DataTable
 * @author clc
 *
 */
public class DefaultDataTable implements DataTable {
	
	private List<DataTableColumn> columns = new ArrayList<DataTableColumn>();
	private String id;
	private String caption;
	private String description;
	private IdGenerator idRegister;
	private DataObjectFactory dataObjectFactory;
	
	public DefaultDataTable(IdGenerator idRegister) {
		this.idRegister = idRegister;
		this.id = idRegister.generateId(this);
		dataObjectFactory = new DataObjectFactory(idRegister);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		idRegister.unregisterId(this.id);
		try {
			idRegister.registerId(id);
			this.id = id;
		} catch (Exception exc) {
			this.id = idRegister.generateId(this);
		}
	}

	@Override
	public String getCaption() {
		return caption;
	}

	@Override
	public void setCaption(String caption) {
		this.caption = caption;
	}

	@Override
	public List<DataTableColumn> getColumns() {
		return columns;
	}

	@Override
	public DataTableColumn addColumn() {
		DataTableColumn colPort = dataObjectFactory.createDataTableColumn();
		columns.add(colPort);
		return colPort;
	}

	@Override
	public void addColumn(DataTableColumn column) {
		columns.add(column);
	}

	@Override
	public DataTableColumn getColumnById(String id) {
		for (int i=0; i<columns.size(); i++)
			if (id.equals(columns.get(i).getId()))
				return columns.get(i);
		return null;
	}

	@Override
	public DataTableColumn getColumnByCaption(String caption) {
		for (int i=0; i<columns.size(); i++)
			if (caption.equals(columns.get(i).getDataObject().getCaption()))
				return columns.get(i);
		return null;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String descr) {
		description = descr;		
	}

}
