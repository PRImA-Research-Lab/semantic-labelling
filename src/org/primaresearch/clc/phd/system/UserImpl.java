package org.primaresearch.clc.phd.system;

import java.util.Collection;

import org.primaresearch.clc.phd.ontology.Ontology;
import org.primaresearch.clc.phd.ontology.label.Label;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.ontology.label.LabelGroup.TooManyLabelsInGroupException;
import org.primaresearch.clc.phd.ontology.label.LabelType;
import org.primaresearch.clc.phd.ontology.label.Labels;

/**
 * Implementation for the 'User' interface
 * @author clc
 *
 */
public class UserImpl implements User {

	private Labels labels;
	private String name;
	
	/**
	 * Constructor
	 */
	public UserImpl(String name) {
		this.name = name;
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

	@Override
	public String getName() {
		return name;
	}

}
