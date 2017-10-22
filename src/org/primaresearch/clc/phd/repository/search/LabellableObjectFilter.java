package org.primaresearch.clc.phd.repository.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.primaresearch.clc.phd.ontology.label.HasLabels;
import org.primaresearch.clc.phd.ontology.label.Label;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.ontology.label.LabelType;

/**
 * Filter for objects that have labels.
 * 
 * @author clc
 *
 */
public class LabellableObjectFilter implements Filter {
	
	private Collection<LabelGroup> rootLabels;
	/** Map [Label type ID, filter element] */
	private Map<String, LabelFilterElement> labelFilterElements = new HashMap<String, LabelFilterElement>();
	
	private Collection<FilterChangeListener> listeners = new LinkedList<FilterChangeListener>();

	/**
	 * Constructor
	 * @param rootLabels Root label types to be used for this filter (other types will be ignored)
	 */
	public LabellableObjectFilter(Collection<LabelGroup> rootLabels) {
		this.rootLabels = rootLabels;
		
		for (Iterator<LabelGroup> it = rootLabels.iterator(); it.hasNext(); )
			initLabelFilterElements(it.next().getType(), null);
	}
	
	public void addListener(FilterChangeListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(FilterChangeListener listener) {
		listeners.remove(listener);
	}
	
	private void notifyListeners() {
		for (Iterator<FilterChangeListener> it = listeners.iterator(); it.hasNext(); )
			it.next().filterChanged(this);
	}

	/**
	 * Returns a list of all objects from the input list that apply to this label filter.
	 * Also refreshes the counts of the internal filter elements (number of objects per label).
	 * @param toBeFiltered Input objects
	 * @return A new list
	 */
	public List<HasLabels> filterObjects(Collection<HasLabels> toBeFiltered) {
		return filterAndCountObjects(toBeFiltered, true);
	}
	
	/**
	 * Refreshes the counts of the internal filter elements (number of objects per label).
	 * @param objectsWithLabels Input objects
	 */
	public void countObjects(Collection<HasLabels> objectsWithLabels) {
		filterAndCountObjects(objectsWithLabels, false);
	}
	
	/**
	 * Returns a list of all objects from the input list that apply to this label filter.
	 * Also refreshes the counts of the internal filter elements (number of objects per label).
	 * @param toBeFiltered Input objects
	 * @param doFilter If set to false, the filter is not applied to the input list
	 * @return A new list
	 */
	private List<HasLabels> filterAndCountObjects(Collection<HasLabels> toBeFiltered, boolean doFilter) {
		resetCounts();
		
		List<HasLabels> filtered = new LinkedList<HasLabels>();
		for (Iterator<HasLabels> it = toBeFiltered.iterator(); it.hasNext(); ) {
			HasLabels obj = it.next();
			
			Collection<LabelGroup> labelGroups = obj.getLabels();
			if (labelGroups != null) {
				for (Iterator<LabelGroup> itLabelGroups = labelGroups.iterator(); itLabelGroups.hasNext(); ) {
					LabelGroup labelGroup = itLabelGroups.next();
					
					if (labelGroup.getLabels() == null)
						continue;
					for (Iterator<Label> itLabels = labelGroup.getLabels().iterator(); itLabels.hasNext(); ) {
						Label label = itLabels.next();
						LabelFilterElement LabelFilterElement = labelFilterElements.get(label.getType().getId());
						if (LabelFilterElement == null || !LabelFilterElement.isEnabled()) {
							//If there's no filter element for this label or
							//the filter element is disabled then this filter
							//does not cover the labels' root type. In that case
							//we have to add the object to the result list.
							if (doFilter)
								filtered.add(obj);
						}
						else { //if (LabelFilterElement != null)
							//Count
							LabelFilterElement.setCount(LabelFilterElement.getCount()+1);
							//LabelFilterElement.addMatchingObject(obj);
							//Filter
							if (doFilter) {
								if (LabelFilterElement.isSelected())
									filtered.add(obj);
							}
						}
					}
				}
			}
		}
		return filtered;
	}
	
	/**
	 * Checks a group of objects if they go through or are filtered out all together.
	 * @param objects Group of objects with labels
	 * @return True if the group would go through and not be filtered out
	 */
	public boolean filterAsGroup(Collection<HasLabels> objects, Object groupParent) {
		
		if (objects == null)
			return true;
		
		boolean objectsGoThrough = true;
		
		if (objects.isEmpty()) {
			//Filter out objects with no labels as soon as one filter element is selected
			for (Iterator<String> it = labelFilterElements.keySet().iterator(); it.hasNext(); ) {
				String labelTypeId = it.next();
				LabelFilterElement LabelFilterElement = labelFilterElements.get(labelTypeId);
				
				if (LabelFilterElement.isEnabled() && LabelFilterElement.isSelected()) {
					objectsGoThrough = false;
					break;
				}
			}
		} else {
			//Go through all filter elements
			for (Iterator<String> it = labelFilterElements.keySet().iterator(); it.hasNext(); ) {
				String labelTypeId = it.next();
				LabelFilterElement LabelFilterElement = labelFilterElements.get(labelTypeId);
				
				if (!LabelFilterElement.isEnabled())
					continue;
				
				//Now check all objects of the given group
				if (checkGroupForOneLabel(objects,labelTypeId)) {
					//LabelFilterElement.setCount(LabelFilterElement.getCount()+1);
					//LabelFilterElement.addMatchingObject(groupParent);
				} else {
					if (LabelFilterElement.isSelected())
						objectsGoThrough = false;
				}
			}
		}
		return objectsGoThrough;
	}
	
	/**
	 * Checks if at least one member of a group of objects has a specific label.
	 * @param objects Group of objects with labels
	 * @param labelTypeToUse Label type ID
	 * @return <code>true</code> if at least one of the given objects has a label of the specified type
	 */
	private boolean checkGroupForOneLabel(Collection<HasLabels> objects, String labelTypeToUse) {
		for (Iterator<HasLabels> it = objects.iterator(); it.hasNext(); ) {
			HasLabels obj = it.next();
			Collection<LabelGroup> labelGroups = obj.getLabels();
			if (labelGroups != null) {
				for (Iterator<LabelGroup> itLabelGroups = labelGroups.iterator(); itLabelGroups.hasNext(); ) {
					LabelGroup labelGroup = itLabelGroups.next();
					
					if (labelGroup.getLabels() == null)
						continue;
					for (Iterator<Label> itLabels = labelGroup.getLabels().iterator(); itLabels.hasNext(); ) {
						Label label = itLabels.next();
						
						//if (label.getType().getId().equals(labelTypeToUse)) {
						if (label.getType().getId().startsWith(labelTypeToUse)) { //Type or one one of it's subtypes
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Updates the counters for all label types
	 * Should be used in conjunction with <code>resetCounts()</code>
	 * @param objects Group of objects with labels
	 */
	public void countAsGroup(Collection<HasLabels> objects, Object groupParent) {
		
		if (objects == null || objects.isEmpty())
			return;
		
		//Go through all filter elements
		for (Iterator<String> it = labelFilterElements.keySet().iterator(); it.hasNext(); ) {
			String labelTypeId = it.next();
			LabelFilterElement LabelFilterElement = labelFilterElements.get(labelTypeId);
			
			if (!LabelFilterElement.isEnabled())
				continue;
			
			//Now check all objects of the given group
			if (checkGroupForOneLabel(objects,labelTypeId)) {
				LabelFilterElement.setCount(LabelFilterElement.getCount()+1);
			}
		}
	}
	
	/**
	 * Returns the selection state of this filter for the given label type.
	 * @return <code>true</code> if this filter does let through objects with the given type; <code>false</code> if objects are filtered out.
	 */
	public boolean isLabelTypeSelected(LabelType labelType) {
		LabelFilterElement LabelFilterElement = labelFilterElements.get(labelType.getId());
		
		if (LabelFilterElement == null) //Filter does not cover the type
			return true;
		
		return LabelFilterElement.isSelected();
	}
	
	public void setLabelTypeSelected(LabelType labelType, boolean selected) {
		LabelFilterElement LabelFilterElement = labelFilterElements.get(labelType.getId());
		
		if (LabelFilterElement == null) //Filter does not cover the type
			return;
		
		boolean changed = LabelFilterElement.isSelected() != selected;
		LabelFilterElement.setSelected(selected);
		
		if (changed)
			notifyListeners();
	}
	
	/**
	 * Returns the enabled state of this filter for the given label type.
	 * @return <code>true</code> if enabled; <code>false</code> otherwise.
	 */
	public boolean isLabelTypeEnabled(LabelType labelType) {
		LabelFilterElement LabelFilterElement = labelFilterElements.get(labelType.getId());
		
		if (LabelFilterElement == null) //Filter does not cover the type
			return false;
		
		return LabelFilterElement.isEnabled();
	}

	/**
	 * Returns the number of objects with the given this filter lets through.
	 * @return Number of objects from last count (calls to <code>filterObjects</code> or <code>countObjects</code>). Returns -1 if the label type is not covered by this filter.
	 */
	public int getObjectCount(LabelType labelType) {
		LabelFilterElement LabelFilterElement = labelFilterElements.get(labelType.getId());
		
		if (LabelFilterElement == null) //Filter does not cover the type
			return -1;
		
		return LabelFilterElement.getCount();
	}
	
	/**
	 * Sets the count of each filter element to zero.
	 */
	public void resetCounts() {
		for (Iterator<LabelFilterElement> it = labelFilterElements.values().iterator(); it.hasNext(); )
			//it.next().resetMatchingObjects();
			it.next().setCount(0);
	}
	
	/**
	 * Recursively creates filter elements for the given label type and its children
	 */
	private void initLabelFilterElements(LabelType labelType, LabelFilterElement parent) {
		if (labelType == null)
			return;
		LabelFilterElement el = new LabelFilterElement(parent);
		labelFilterElements.put(labelType.getId(), el);
		
		//Children
		if (labelType.getChildren() != null) {
			for (Iterator<LabelType> it = labelType.getChildren().iterator(); it.hasNext(); ) {
				initLabelFilterElements(it.next(), el);
			}
		}
	}
	
	/**
	 * Returns the root label types of this filter (other label types are ignored when applying the filter).
	 */
	public Collection<LabelGroup> getRootLabels() {
		return rootLabels;
	}
	
	/**
	 * Initialises the filter (filtering should let through all objects)
	 * @param allWorkflows Unfiltered list of objects
	 */
	public void init(Collection<HasLabels> allObjects) {
		
		//Collect all label types of the given objects
		Set<String> foundLabelTypes = new HashSet<String>(); //Set of type IDs
		
		for (Iterator<HasLabels> it = allObjects.iterator(); it.hasNext(); ) {
			HasLabels objectWithLabels = it.next();
			
			if (objectWithLabels == null)
				continue;
			for (Iterator<LabelGroup> itLabelGroups = objectWithLabels.getLabels().iterator(); itLabelGroups.hasNext(); ) {
				LabelGroup grp = itLabelGroups.next();
				
				if (grp == null)
					continue;
				for (Iterator<Label> itLabels = grp.getLabels().iterator(); itLabels.hasNext(); )
					foundLabelTypes.add(itLabels.next().getType().getId());
			}
		}
		
		//Enable all filter elements that correspond with the found label types
		// Go through all filter elements
		for (Iterator<String> it = labelFilterElements.keySet().iterator(); it.hasNext(); ) {
			String labelTypeId = it.next();
			LabelFilterElement labelFilterElement = labelFilterElements.get(labelTypeId);
			
			if (foundLabelTypes.contains(labelTypeId))
				labelFilterElement.setEnabled(true);
		}
	}




}
