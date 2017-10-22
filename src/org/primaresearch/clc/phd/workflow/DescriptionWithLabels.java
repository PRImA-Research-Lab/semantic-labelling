package org.primaresearch.clc.phd.workflow;

import java.util.Collection;

import org.primaresearch.clc.phd.ontology.Ontology;
import org.primaresearch.clc.phd.ontology.label.HasLabels;
import org.primaresearch.clc.phd.ontology.label.Label;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.ontology.label.LabelGroup.TooManyLabelsInGroupException;
import org.primaresearch.clc.phd.ontology.label.LabelType;
import org.primaresearch.clc.phd.ontology.label.Labels;

/**
 * Special description that can be labelled (for user perspectives)
 * 
 * @author clc
 *
 */
public class DescriptionWithLabels implements HasLabels {
	
	private String description;
	private Labels labels;
	
	/**
	 * Constructor
	 * @param description
	 */
	public DescriptionWithLabels(String description) {
		this.description = description;
		labels = new Labels(Ontology.getInstance().getUserLabelSlots());
	}

	@Override
	public Collection<LabelGroup> getLabels() {
		return labels.getAllLabels();
	}

	@Override
	public Label addLabel(LabelType labelType)
			throws TooManyLabelsInGroupException {
		return labels.add(labelType);
	}

	@Override
	public void removeLabel(Label label) {
		labels.remove(label);
	}

	@Override
	public boolean hasLabels() {
		return !labels.isEmpty();
	}

	public String getText() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
