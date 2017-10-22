package org.primaresearch.clc.phd.workflow.data;

import java.util.ArrayList;
import java.util.List;

import org.primaresearch.clc.phd.ontology.label.Labels;

/**
 * Default implementation for data collections. Contains a list of sub-items.
 * @author clc
 *
 */
public class DataCollectionImpl extends BaseDataObject implements DataCollection {

	private List<DataObject> items = null;

	public DataCollectionImpl(Labels allowedLabels) {
		super(allowedLabels);
		setCaption("[New data collection]");
	}
	
	/**
	 * Copy constructor
	 */
	public DataCollectionImpl(DataCollectionImpl other) {
		super(other);
		if (other.items != null)
			for (int i=0; i<other.items.size(); i++)
				addDataItem(other.getDataItem(i).clone());
	}
	
	public DataObject clone() {
		return new DataCollectionImpl(this);
	}

	@Override
	public int getSize() {
		return items == null ? 0 : items.size();
	}

	@Override
	public DataObject getDataItem(int index) {
		return items == null ? null : items.get(index);
	}

	@Override
	public void addDataItem(DataObject item) {
		if (items == null)
			items = new ArrayList<DataObject>();
		items.add(item);
		
	}

	@Override
	public void removeDataItem(int index) {
		if (items == null)
			return;
		items.remove(index);
	}


}
