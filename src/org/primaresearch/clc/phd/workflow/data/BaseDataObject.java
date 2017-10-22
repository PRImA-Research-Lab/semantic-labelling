package org.primaresearch.clc.phd.workflow.data;

import java.util.Collection;

import org.primaresearch.clc.phd.ontology.label.Label;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.ontology.label.LabelType;
import org.primaresearch.clc.phd.ontology.label.Labels;
import org.primaresearch.clc.phd.ontology.label.LabelGroup.TooManyLabelsInGroupException;

/**
 * Abstract base class for data objects (e.g. single data object) with common properties such as labels and caption.
 * 
 * @author clc
 *
 */
public abstract class BaseDataObject implements DataObject {

	private Labels labels;
	private String caption;
	private String description;
	
	public BaseDataObject(Labels allowedLabels) {
		this.labels = allowedLabels;
		this.caption = "[New data object]";
	}
	
	/**
	 * Copy constructor
	 * @param other
	 */
	public BaseDataObject(BaseDataObject other) {
		this.labels = other.labels.clone();
		this.caption = other.caption;
		this.description = other.description;
	}
	
	public abstract DataObject clone();
	
	@Override
	public Collection<LabelGroup> getLabels() {
		return labels.getAllLabels();
	}

	public Label addLabel(LabelType labelType) throws TooManyLabelsInGroupException {
		return labels.add(labelType);
	}
	
	public void removeLabel(Label label) {
		labels.remove(label);
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean hasLabels() {
		if (labels == null)
			return false;
		return !labels.isEmpty();
	}


	
}
