package org.primaresearch.clc.phd.workflow.data.port;

import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.data.DataObject;

/**
 * Specialised data port implementation for output ports of data tables (representing the columns)
 * @author clc
 *
 */
public class DataTableColumnImpl extends OutputPortImpl implements
		DataTableColumn {

	public DataTableColumnImpl(Activity activity, DataObject dataObject,
			IdGenerator idRegister) {
		super(activity, dataObject, idRegister);
	}

	public DataTableColumnImpl(DataTableColumnImpl other) {
		super(other);
	}
}
