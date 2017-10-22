package org.primaresearch.clc.phd.repository.search;


/**
 * Filter element for label filters and matchers
 * @author clc
 *
 */
public class LabelFilterElement {
	private boolean selected = false;
	private boolean enabled = false;
	private int count = 0;
	private LabelFilterElement parent = null;
	//private Set<Object> matchingObjects = new HashSet<Object>();
	
	public LabelFilterElement(LabelFilterElement parent) {
		this.parent = parent;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
		//if (selected && parent != null)
		//	parent.setSelected(selected);
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (enabled && parent != null)
			parent.setEnabled(enabled);
	}
	
	public int getCount() {
		return count;
		//return matchingObjects.size();
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	//public void addMatchingObject(Object obj) {
	//	matchingObjects.add(obj);
	//	if (parent != null)
	//		parent.addMatchingObject(obj);
	//}
	
	//public void resetMatchingObjects() {
	//	matchingObjects.clear();
	//}
}
