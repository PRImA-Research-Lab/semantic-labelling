package org.primaresearch.clc.phd.ontology.label;

import java.util.Collection;

import org.primaresearch.clc.phd.ontology.label.LabelGroup.TooManyLabelsInGroupException;

/**
 * Interface for objects that can have semantic labels.
 * 
 * @author clc
 *
 */
public interface HasLabels {

	/**
	 * Returns a collection of all labels (arranged in label groups)
	 */
	public Collection<LabelGroup> getLabels();
	
	/**
	 * Creates and adds a label of the given type
	 * @param labelType Type of the new label
	 * @return The new label
	 * @throws TooManyLabelsInGroupException The corresponding label group is full (maximum number of labels)
	 */
	public Label addLabel(LabelType labelType) throws TooManyLabelsInGroupException;
	
	/**
	 * Removes the specified label
	 */
	public void removeLabel(Label label);
	
	/**
	 * Checks if the labellable object has at least one label
	 */
	public boolean hasLabels();

}
