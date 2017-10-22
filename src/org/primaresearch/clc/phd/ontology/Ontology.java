package org.primaresearch.clc.phd.ontology;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.primaresearch.clc.phd.ontology.io.XmlLabelTypeHierarchyInitialiser;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.ontology.label.LabelType;

/**
 * Ontology of labels for activities and data ports.
 * Singleton
 * 
 * @author clc
 *
 */
public class Ontology {

	/** Initialiser for label type hierarchies (taxonomies) */
	private static OntologyInitialiser labelTypeHierarchyInitialiser = new XmlLabelTypeHierarchyInitialiser();
	
	private static Ontology instance = null;
	
	private File sourceFile;
	
	
	/**
	 * Map [type id, label type] (all label types)
	 */
	private Map<String, LabelType> typeRegister = new HashMap<String, LabelType>();
	
	/** List of root label types (no parents), each being the entry point for a taxonomy */
	private Collection<LabelType> rootTypes = new LinkedList<LabelType>();
	
	private List<LabelGroup> activityLabelSlots = new ArrayList<LabelGroup>();
	private List<LabelGroup> dataObjectLabelSlots = new ArrayList<LabelGroup>();
	private List<LabelGroup> userLabelSlots = new ArrayList<LabelGroup>();
	
	private int version = 1;
	
	/** Map[ontology version, Map[source label ID, Set[target label ID]]] */
	private Map<Integer, Map<String, Set<String>>> migrationRules = new HashMap<Integer, Map<String, Set<String>>>(); 
	
	/**
	 * Returns the instance of this singleton (creates it if necessary)
	 * @return
	 */
	public static Ontology getInstance() {
		if (instance == null)
			instance = new Ontology();
		return instance;
	}
	
	/**
	 * Constructor
	 */
	private Ontology() {
		labelTypeHierarchyInitialiser.init(this);
		//Collection<LabelType> types = labelTypeHierarchyInitialiser.getTypeHierarchies();
		//for (Iterator<LabelType> it = types.iterator(); it.hasNext(); ) {
		//	addTypeAndSubtypes(it.next());
		//}
	}
	
	/**
	 * Constructor for use with OtherOntology (not singleton)
	 * @param sourceFile
	 */
	protected Ontology(File sourceFile) {
		this.sourceFile = sourceFile;
	}
	
	/**
	 * Returns the file this ontology has been loaded from or <code>null</code> if no such file was specified.
	 */
	public File getSourceFile() {
		return sourceFile;
	}

	/**
	 * Sets the file this ontology has been loaded from.
	 */
	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	/**
	 * Recursively adds the given type and its children to the register.
	 * @param type Label type to add
	 */
	public void addTypeAndSubtypes(LabelType type) {
		typeRegister.put(type.getId(), type);
		if (type.getParent() == null)
			rootTypes.add(type);
		//Children
		Set<LabelType> children = type.getChildren();
		if (children != null) {
			for (Iterator<LabelType> it = children.iterator(); it.hasNext(); ) {
				addTypeAndSubtypes(it.next());
			}
		}
	}
	
	/**
	 * Returns the root label type with the given name
	 * @param name Label type name
	 * @return A label type or null
	 */
	public LabelType getRootType(String name) {
		return typeRegister.get(name);
	}
	
	/**
	 * Adds the given label type to this ontology (as root type (must have no parent!))
	 * @param type
	 */
	public void addRootType(LabelType type) {
		addTypeAndSubtypes(type);
	}
	
	/**
	 * Removes the given label type and all its children from this ontology
	 * @param type Label type to remove
	 * @param removeFromParent If true, also remove it from its parent label type
	 */
	public void removeTypeAndSubtypes(LabelType type, boolean removeFromParent) {
		if (type.getParent() == null) //Root type
			rootTypes.remove(type);
		else if (removeFromParent)
			type.getParent().removeChildType(type);
		typeRegister.remove(type.getId());
		//Children
		Set<LabelType> children = type.getChildren();
		if (children != null) {
			for (Iterator<LabelType> it = children.iterator(); it.hasNext(); ) {
				removeTypeAndSubtypes(it.next(), false);
			}
		}
	}
	
	/**
	 * Returns the root label type with the given ID 
	 * @param typeId Default root type ID
	 * @return Label type or null
	 */
	public LabelType getRootType(DefaultRootType typeId) {
		return typeRegister.get(typeId.id);
	}
	
	/**
	 * Returns the label type with the given ID
	 * @param id Label type ID
	 * @return Label type or null
	 */
	public LabelType getLabelType(String id) {
		return typeRegister.get(id);
	}
	
	/**
	 * Returns a collection of all root label types
	 */
	public Collection<LabelType> getRootTypes() {
		return rootTypes;
	}

	/**
	 * Needs to be called when the ID of a label type has been changed (updates the internal type register)
	 * @param type Changed type
	 * @param oldId Old ID of the changed type
	 */
	public void onLabelTypeRenamed(LabelType type, String oldId) {
		typeRegister.remove(oldId);
		typeRegister.put(type.getId(), type);
	}

	public List<LabelGroup> getActivityLabelSlots() {
		return activityLabelSlots;
	}

	public void setActivityLabelSlots(List<LabelGroup> activityLabelSlots) {
		this.activityLabelSlots = activityLabelSlots;
	}

	public List<LabelGroup> getDataObjectLabelSlots() {
		return dataObjectLabelSlots;
	}

	public void setDataObjectLabelSlots(List<LabelGroup> dataObjectLabelSlots) {
		this.dataObjectLabelSlots = dataObjectLabelSlots;
	}
	
	public List<LabelGroup> getUserLabelSlots() {
		//Initialise
		/*if (userLabelSlots.isEmpty()) {
			//TODO: Add to Ontology file
			
			LabelType userGroupRootLabelType = new LabelType("users", "All users", null);
			
			LabelGroup userGroupLabelGroup = new LabelGroup(userGroupRootLabelType, 10);
			userLabelSlots.add(userGroupLabelGroup);
		}*/
		
		return userLabelSlots;
	}

	public void setUserLabelSlots(List<LabelGroup> userLabelSlots) {
		this.userLabelSlots = userLabelSlots;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	/**
	 * Returns the migration rules for the specified previous ontology version.
	 * @param oldOntologyVersion Version number
	 * @return A map with source and target label types or <code>null</code>
	 */
	public Map<String,Set<String>> getMigrationRules(int oldOntologyVersion, boolean createIfNotExists) {
		if (createIfNotExists && !migrationRules.containsKey(new Integer(oldOntologyVersion))) {
			Map<String,Set<String>> rules = new HashMap<String,Set<String>>();
			migrationRules.put(new Integer(oldOntologyVersion), rules);
			return rules;
		}
		return migrationRules.get(new Integer(oldOntologyVersion));
	}

	/**
	 * Map[ontology version, Map[source label ID, Set[target label ID]]]
	 */
	public Map<Integer,Map<String,Set<String>>> getAllMigrationRules() {
		return migrationRules;
	}


	/**
	 * Default root label types.
	 * 
	 * @author clc
	 *
	 */
	public static class DefaultRootType implements Serializable {

		private static final long serialVersionUID = 1L;

		public static final Collection<DefaultRootType> ALL_TYPES = new LinkedList<DefaultRootType>();
		
		public static final DefaultRootType PROCESSING_TYPE = new DefaultRootType("processing-type");
		public static final DefaultRootType LICENSE = new DefaultRootType("license");
		public static final DefaultRootType AUTOMATION = new DefaultRootType("automation");
		public static final DefaultRootType PRODUCTION_METHOD = new DefaultRootType("production-method");
		
		private String id;
		
		//For GWT compatibility
		public DefaultRootType() {
		}
		
		private DefaultRootType(String id) {
			this.id = id;
			ALL_TYPES.add(this);
		}
		
		public String getId() {
			return id;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other == null || !(other instanceof DefaultRootType))
				return false;
			return id.equals(((DefaultRootType)other).id);
		}
		
		@Override
		public int hashCode() {
			return id.hashCode();
		}
		
		@Override
		public String toString() {
			return id;
		}
	}
	
}
