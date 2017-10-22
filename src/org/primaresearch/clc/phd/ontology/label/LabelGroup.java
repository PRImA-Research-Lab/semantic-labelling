package org.primaresearch.clc.phd.ontology.label;

import java.util.ArrayList;
import java.util.List;

/**
 * Group of labels of one type (e.g. a taxonomy).
 * 
 * @author clc
 *
 */
public class LabelGroup {

	private LabelType labelType;
	private int maxLabels;
	
	private List<Label> labels = new ArrayList<Label>();
	
	/**
	 * Constructor
	 * @param labelType Type of labels allowed in this group
	 * @param maxLabels Maximum number of labels that can be added to this group
	 */
	public LabelGroup(LabelType labelType, int maxLabels) {
		this.labelType = labelType;
		this.maxLabels = maxLabels;
	}
	
	/**
	 * Creates a deep copy
	 */
	public LabelGroup clone() {
		LabelGroup copy = new LabelGroup(labelType, maxLabels);
		
		for (int i=0; i<labels.size(); i++) {
			try {
				copy.addLabel(labels.get(i).clone());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return copy;
	}
	
	/**
	 * Adds a label to this group
	 * @param label Label to add
	 * @throws TooManyLabelsInGroupException Maximum number of labels reached (group is full)
	 * @throws IllegalArgumentException Label is of wrong type
	 */
	public void addLabel(Label label) throws TooManyLabelsInGroupException, IllegalArgumentException {
		if (labels.size() >= maxLabels)
			throw new TooManyLabelsInGroupException(maxLabels);
		if (label == null || !label.getType().isSubtypeOf(labelType))
			throw new IllegalArgumentException("The label type "+label.getType()+" is not a subtype of "+labelType);
		labels.add(label);
	}
	
	/**
	 * Removes the given label from this group
	 * @param label
	 */
	public void removeLabel(Label label) {
		for (int i=0; i<labels.size(); i++) {
			if (label.equals(labels.get(i))) {
				labels.remove(i);
				break;
			}
		}
	}
	
	/**
	 * Returns true if there is a free slot for a new label (label groups have have a maximum number of labels they can hold).
	 */
	public boolean canAddLabel() {
		return labels.size() < maxLabels;
	}
	
	public int getMaxLabels() {
		return maxLabels;
	}
	
	public void setMaxLabels(int maxLabels) {
		this.maxLabels = maxLabels;
	}

	/**
	 * Returns the type of labels allowed in this group
	 */
	public LabelType getType() {
		return labelType;
	}
	
	/**
	 * Returns a list of all labels in this group
	 */
	public List<Label> getLabels() {
		return labels;
	}

	@Override
	public String toString() {
		return labelType.getCaption() + " ("+maxLabels+")";
	}



	/**
	 * Exception indicating that the label group is full and no more labels can be added.
	 * 
	 * @author clc
	 *
	 */
	public static class TooManyLabelsInGroupException extends Exception {
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor
		 * @param max Maximum number of labels allowed
		 */
		public TooManyLabelsInGroupException(int max) {
			super("The label group can hold a maximum of "+max+" labels.");
		}
	}
}
