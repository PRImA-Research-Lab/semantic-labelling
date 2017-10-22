package org.primaresearch.clc.phd.ontology.io;

/**
 * Collection of element and attribute names for XML containing an ontology.
 * 
 * @author clc
 *
 */
public interface DefaultXmlNames {
	
	static final String ELEMENT_Ontology = "Ontology";
	static final String ELEMENT_LabelTypeHierarchies = "LabelTypeHierarchies";
	static final String ELEMENT_LabelType = "LabelType";
	static final String ELEMENT_Description = "Description";
	static final String ELEMENT_ActivityLabelSlots = "ActivityLabelSlots";
	static final String ELEMENT_DataObjectLabelSlots = "DataObjectLabelSlots";
	static final String ELEMENT_UserLabelSlots = "UserLabelSlots";
	static final String ELEMENT_LabelSlotGroup = "LabelSlotGroup";
	static final String ELEMENT_MigrationRules = "MigrationRules";
	static final String ELEMENT_SourceType = "SourceType";
	static final String ELEMENT_TargetType = "TargetType";
	
	static final String ATTR_name = "name";
	static final String ATTR_caption = "caption";
	static final String ATTR_slots = "slots";
	static final String ATTR_version = "version";
	static final String ATTR_id = "id";

}
