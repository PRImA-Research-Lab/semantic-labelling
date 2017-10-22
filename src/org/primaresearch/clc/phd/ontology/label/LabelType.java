package org.primaresearch.clc.phd.ontology.label;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * Ontology label type. A type can be abstract (super type for one or more sub-types; equivalent to 'concept'/'class')
 * or it can be concrete (no sub-types; equivalent to 'individual'/'instance').
 * 
 * @author clc
 *
 */
public class LabelType implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String caption;
	private String description;
	private LabelType parent;
	private Set<LabelType> children = null;

	/**
	 * Default constructor (for GWT compatibility)
	 */
	public LabelType() {
	}
	
	/**
	 * Constructor
	 * @param parent Parent label (use <code>null</code> for root labels)
	 */
	public LabelType(String name, String caption, LabelType parent) {
		this.name = name;
		this.caption = caption;
		this.parent = parent;
	}
	
	/**
	 * Returns the ID of the label (concatenation of the parent ID and label name)
	 */
	public String getId() {
		//The ID is a concatenation of the parent ID and the name
		if (parent != null)
			return parent.getId() + "." + name;
		else
			return name;
	}
	
	/**
	 * Name of this label
	 */
	public String getName() {
		return name;
	}

	/**
	 * Caption of this label (e.g. for display in graphical user interface)
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Description of this label
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Description of this label
	 */
	public void setDescription(String description) {
		this.description = description;
	}
		
	/**
	 * Name of this label
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Caption of this label (e.g. for display in graphical user interface)
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * Adds a child label type (extending the taxonomy)
	 */
	public void addChildType(LabelType child) {
		if (children == null)
			children = new LinkedHashSet<LabelType>();
		children.add(child);
	}
	
	/**
	 * Removes a child label type 
	 */
	public void removeChildType(LabelType child) {
		if (children != null)
			children.remove(child);
	}
	
	/**
	 * Returns all child label types
	 * @return Set of label types or null
	 */
	public Set<LabelType> getChildren() {
		return children;
	}
	
	/**
	 * Checks if this type is a direct or indirect subtype (child type) of the given (potential parent) type.
	 */
	public boolean isSubtypeOf(LabelType labelType) {
		if (parent == null)
			return false; //This is a root
		//Direct subtype?
		if (parent.equals(labelType))
			return true;
		//Recursion to check for indirect relationship
		return parent.isSubtypeOf(labelType);
	}
	
	/**
	 * Checks if this type is a direct or indirect supertype (parent type) of the given (potential child) type.
	 */
	public boolean isSupertypeOf(LabelType labelType) {
		if (children == null || children.isEmpty())
			return false; //No children (concrete type)
		//Direct relationship?
		if (children.contains(labelType))
			return true;
		//Recursion to check for indirect relationship
		for (Iterator<LabelType> it=children.iterator(); it.hasNext(); ) {
			if (it.next().isSupertypeOf(labelType))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns the root label type of this label type (root of taxonomy).
	 * @return The root type (returns this type itself it has no parent)
	 */
	public LabelType getRootType() {
		if (parent == null)
			return this;
		return parent.getRootType();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof LabelType))
			return false;
		//Use ID
		return this.getId().equals(((LabelType)other).getId());
	}
	
	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}
	
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Returns the parent type of this label type
	 * @return Label type or null (if this is a root type)
	 */
	public LabelType getParent() {
		return parent;
	}
	
	
}
