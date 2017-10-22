package org.primaresearch.clc.phd.ontology.label;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.primaresearch.clc.phd.ontology.label.LabelGroup.TooManyLabelsInGroupException;

/**
 * Container for labels (can be used in conjunction with interface 'HasLabels')
 * 
 * @author clc
 *
 */
public class Labels {
	
	/** Map [type name, label group] */
	Map<String, LabelGroup> labels = new HashMap<String, LabelGroup>();

	/**
	 * Constructor
	 * @param allowedLabels Template for label groups (allowed label types)
	 */
	public Labels(Collection<LabelGroup> allowedLabels) {
		for (Iterator<LabelGroup> it=allowedLabels.iterator(); it.hasNext(); ) {
			LabelGroup group = it.next();
			labels.put(group.getType().toString(), group.clone());
		}
	}
	
	/**
	 * Creates a deep copy
	 */
	public Labels clone() {
		Labels copy = new Labels(labels.values());
		return copy;
	}
	
	/**
	 * Returns all labels
	 */
	public Collection<LabelGroup> getAllLabels() {
		return labels.values();
	}
	
	/**
	 * Creates and adds a label of the given type
	 * @param labelType Type of new label
	 * @return The new label
	 * @throws TooManyLabelsInGroupException The corresponding label group is full (maximum number of labels)
	 */
	public Label add(LabelType labelType) throws TooManyLabelsInGroupException {
		LabelGroup group = labels.get(labelType.getRootType().toString());
		Label label = null;
		if (group != null) {
			group.addLabel(label = new Label(labelType));
		}
		return label;
	}
	
	/**
	 * Removes the given label
	 */
	public void remove(Label label) {
		LabelGroup group = labels.get(label.getType().getRootType().toString());
		if (group != null) {
			group.removeLabel(label);
		}
	}
	
	/**
	 * Checks if there are labels
	 * @return <code>false</code> if there are no labels (only empty label groups), <code>true</code> otherwise
	 */
	public boolean isEmpty() {
		for (Iterator<LabelGroup> it=labels.values().iterator(); it.hasNext(); ) {
			LabelGroup group = it.next();
			if (!group.getLabels().isEmpty())
				return false;
		}
		return true;
	}
}
