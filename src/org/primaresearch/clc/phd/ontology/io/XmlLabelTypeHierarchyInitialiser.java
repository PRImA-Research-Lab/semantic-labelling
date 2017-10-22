package org.primaresearch.clc.phd.ontology.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.primaresearch.clc.phd.ontology.OntologyInitialiser;
import org.primaresearch.clc.phd.ontology.Ontology;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.ontology.label.LabelType;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Loads the label type hierarchies (taxonomies) for an ontology from an XML file
 * 
 * @author clc
 *
 */
public class XmlLabelTypeHierarchyInitialiser implements
		OntologyInitialiser {

	public static final String defaultXmlResourcePath = "/org/primaresearch/clc/phd/ontology/res/DefaultLabelTypes.xml";
	public static final String defaultXmlFilePath = "H:\\Users\\clc\\Dropbox\\PhD\\Experiments\\Ontology\\DefaultLabelTypes_3.xml";
	private String xmlResourcePath;
	private String xmlFilePath;
	
	public XmlLabelTypeHierarchyInitialiser() {
		xmlResourcePath = defaultXmlResourcePath;
		xmlFilePath = defaultXmlFilePath;
	}
	
	/**
	 * Sets the resource path to the XML resource that contains label type hierarchies.
	 * The resource file is used if the external file (specified by setXmlFilePath) is not found.
	 * Use this method to override the default path.  
	 * @param path Path to XML resource (e.g. '/org/primaresearch/DefaultLabelTypes.xml')
	 */
	public void setXmlResourcePath(String path) {
		this.xmlResourcePath = path;
	}
	
	/**
	 * Sets the file path to the XML resource that contains label type hierarchies.
	 * Use this method to override the default path.  
	 * @param path Path to XML resource (e.g. '/org/primaresearch/DefaultLabelTypes.xml')
	 */
	public void setXmlFilePath(String path) {
		this.xmlFilePath = path;
	}
	
	@Override
	public void init(Ontology ontology) {
	    try {
	    	// Obtain a new instance of a SAXParserFactory.
	    	SAXParserFactory factory = SAXParserFactory.newInstance();
	    	// Specifies that the parser produced by this code will provide support for XML namespaces.
	    	factory.setNamespaceAware(true);
    		factory.setValidating(false);
	    	
    		SAXParser typeLabelParser = factory.newSAXParser();
    		OntologySaxHandler handler = new OntologySaxHandler(ontology.getAllMigrationRules());
    		
    		InputStream inputStream = null;
    		
    		//Try external file first
    		File extFile = new File(xmlFilePath);
    		if (extFile.exists()) {
    			inputStream = new FileInputStream(extFile);
    			ontology.setSourceFile(extFile);
    		}
    		else { //Use internal resource
    			System.out.println("Warning: Enternal ontology XML file not found: "+xmlFilePath);
    			System.out.println("         Using internal resource: "+xmlResourcePath);
    			inputStream = getClass().getResource(xmlResourcePath).openStream();
    			//Get the file object for the internal resource
    			ontology.setSourceFile(new File(getClass().getResource(XmlLabelTypeHierarchyInitialiser.defaultXmlResourcePath).getFile()));
    		}
    		
    		typeLabelParser.parse(inputStream, handler);
    		
    		//Ontology version
    		ontology.setVersion(handler.getVersion());
    		
    		//Add type labels
    		Collection<LabelType> types = handler.getLabelTypes();
    		for (Iterator<LabelType> it = types.iterator(); it.hasNext(); ) {
    			ontology.addTypeAndSubtypes(it.next());
    		}
    		
    		//Add slots
    		ontology.setActivityLabelSlots(handler.getActivityLabelSlots());
    		ontology.setDataObjectLabelSlots(handler.getDataObjectLabelSlots());
    		ontology.setUserLabelSlots(handler.getUserLabelSlots());

	    } catch (Throwable t) {
	    	t.printStackTrace();
	    }
	}

	/**
	 * SAX handler implementation to parse label type hierarchies.
	 * 
	 * @author clc
	 */
	private static class OntologySaxHandler extends DefaultHandler {
		private List<LabelType> labelTypes = new LinkedList<LabelType>();
		private StringBuffer currentDescription = null;
		private Deque<LabelType> labelTypeStack = new LinkedList<LabelType>();
		private List<LabelGroup> activityLabelSlots = new ArrayList<LabelGroup>();
		private List<LabelGroup> dataObjectLabelSlots = new ArrayList<LabelGroup>();
		private List<LabelGroup> userLabelSlots = new ArrayList<LabelGroup>();
		private List<LabelGroup> currentSlotList = null;
		private int version = 1;
		private Map<Integer, Map<String, Set<String>>> migrationRules;
		private Integer activeMigrationOntologyVersion = null;
		private String activeMigrationRuleSourceLabelTypeId = null;

		public OntologySaxHandler(Map<Integer, Map<String, Set<String>>> migrationRules) {
			this.migrationRules = migrationRules;
			
			//Delete all rules
			this.migrationRules.clear();
		}
		
		public Collection<LabelType> getLabelTypes() {
			return labelTypes;
		}
		
		public List<LabelGroup> getActivityLabelSlots() {
			return activityLabelSlots;
		}

		public List<LabelGroup> getDataObjectLabelSlots() {
			return dataObjectLabelSlots;
		}

		public List<LabelGroup> getUserLabelSlots() {
			return userLabelSlots;
		}

		public int getVersion() {
			return version;
		}

		/**
		 * Receive notification of the start of an element.
		 * @param namespaceURI - The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
		 * @param localName - The local name (without prefix), or the empty string if Namespace processing is not being performed.
		 * @param qName - The qualified name (with prefix), or the empty string if qualified names are not available.
		 * @param atts - The attributes attached to the element. If there are no attributes, it shall be an empty Attributes object.
		 * @throws SAXException - Any SAX exception, possibly wrapping another exception.
		 */
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
		      throws SAXException {
			
		    if (DefaultXmlNames.ELEMENT_Ontology.equals(localName)){
				//Version number
				int i;
				if ((i = atts.getIndex(DefaultXmlNames.ATTR_version)) >= 0) {
					version = Integer.parseInt(atts.getValue(i));
				}		    } 
		    else if (DefaultXmlNames.ELEMENT_LabelType.equals(localName)){
		    	handleLabelTypeElement(atts);
		    } 
		    else if (DefaultXmlNames.ELEMENT_Description.equals(localName)) {
		    	currentDescription = new StringBuffer();
		    }
		    else if (DefaultXmlNames.ELEMENT_ActivityLabelSlots.equals(localName)) {
		    	currentSlotList = activityLabelSlots;
		    }
		    else if (DefaultXmlNames.ELEMENT_DataObjectLabelSlots.equals(localName)) {
		    	currentSlotList = dataObjectLabelSlots;
		    }
		    else if (DefaultXmlNames.ELEMENT_UserLabelSlots.equals(localName)) {
		    	currentSlotList = userLabelSlots;
		    }
		    else if (DefaultXmlNames.ELEMENT_LabelSlotGroup.equals(localName)) {
		    	handleLabelSlotGroup(atts);
		    }
		    else if (DefaultXmlNames.ELEMENT_MigrationRules.equals(localName)) {
				//Ontology version number
				int i;
				if ((i = atts.getIndex(DefaultXmlNames.ATTR_version)) >= 0) {
					activeMigrationOntologyVersion = new Integer(atts.getValue(i));
				}		     
		    }
		    else if (DefaultXmlNames.ELEMENT_SourceType.equals(localName)) {
		    	handleMigrationRuleSource(atts);
		    }
		    else if (DefaultXmlNames.ELEMENT_TargetType.equals(localName)) {
		    	handleMigrationRuleTarget(atts);
		    }
		}
		
		/**
		 * Receive notification of the end of an element.
		 * 
		 * @param namespaceURI - The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
		 * @param localName - The local name (without prefix), or the empty string if Namespace processing is not being performed.
		 * @param qName - The qualified name (with prefix), or the empty string if qualified names are not available.
		 * @throws SAXException - Any SAX exception, possibly wrapping another exception.
		 */
		public void endElement(String namespaceURI, String localName, String qName)
		      throws SAXException {
			
		    if (DefaultXmlNames.ELEMENT_LabelType.equals(localName)){
		    	labelTypeStack.removeLast();
		    }
		    else if (DefaultXmlNames.ELEMENT_Description.equals(localName)) {
				if (!labelTypeStack.isEmpty())
					labelTypeStack.getLast().setDescription(currentDescription.toString());
				currentDescription = null;
		    }
		    else if (DefaultXmlNames.ELEMENT_ActivityLabelSlots.equals(localName)) {
		    	currentSlotList = null;
		    }
		    else if (DefaultXmlNames.ELEMENT_DataObjectLabelSlots.equals(localName)) {
		    	currentSlotList = null;
		    }

		}
		
		/**
		 * Receive notification of character data inside an element.
		 * @param ch - The characters.
		 * @param start - The start position in the character array.
		 * @param length - The number of characters to use from the character array.
		 * @throws SAXException - Any SAX exception, possibly wrapping another exception.
		 */
		public void characters(char[] ch, int start, int length)
		      throws SAXException {

			String strValue = new String(ch, start, length);
			
			//Text might be parsed bit by bit, so we have to accumulate until a closing tag is found.
			if (currentDescription != null) {
				currentDescription.append(strValue);
			}
		}
		
		/**
		 * Reads the attributes of the Page element. 
		 */
		private void handleLabelTypeElement(Attributes atts) {
			
			//Attributes
			String name = "";
			String caption = "";
			int i;
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_name)) >= 0) {
				name = atts.getValue(i);
			}
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_caption)) >= 0) {
				caption = atts.getValue(i);
			}
			
			//Parent?
			LabelType parent = null;
			if (!labelTypeStack.isEmpty())
				parent = labelTypeStack.getLast();
			
			//Create label type object
			LabelType labelType = new LabelType(name, caption, parent);
			labelTypeStack.add(labelType);
			
			//Add to parent
			if (parent != null)
				parent.addChildType(labelType);
			else //Root type -> Add to result list
				labelTypes.add(labelType);
		}
		
		private void handleLabelSlotGroup(Attributes atts) {
			//Attributes
			String name = "";
			int slots = 1;
			int i;
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_name)) >= 0) {
				name = atts.getValue(i);
			}
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_slots)) >= 0) {
				slots = Integer.parseInt(atts.getValue(i));
			}
			
			//Add
			LabelType labelType = null;
			// Find type
			for (Iterator<LabelType> it = labelTypes.iterator(); it.hasNext(); ) {
				LabelType t = it.next();
				if (t.getName().equals(name)) {
					labelType = t;
					break;
				}
			}
			LabelGroup grp = new LabelGroup(labelType, slots);
			currentSlotList.add(grp);
		}
		
		/**
		 * Parses the source element of a migration rule (the target label types are nested elements)
		 * @param atts
		 */
		private void handleMigrationRuleSource(Attributes atts) {
			if (activeMigrationOntologyVersion == null)
				return; //Should not happen
			
			//Attributes
			String sourceLabelTypeId = "";
			int i;
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_id)) >= 0) {
				sourceLabelTypeId = atts.getValue(i);
			}
			
			//Add rule
			// Get or create map for current rules
			Map<String,Set<String>> rulesForActiveVersion = migrationRules.get(activeMigrationOntologyVersion);
			if (rulesForActiveVersion == null) {
				rulesForActiveVersion = new HashMap<String, Set<String>>();
				migrationRules.put(activeMigrationOntologyVersion, rulesForActiveVersion);
			}
			
			// Add empty rule (target types will be  parsed later)
			if (!rulesForActiveVersion.containsKey(sourceLabelTypeId))
				rulesForActiveVersion.put(sourceLabelTypeId, new HashSet<String>());
			
			activeMigrationRuleSourceLabelTypeId = sourceLabelTypeId;
		}
		
		/**
		 * Parses the target element of a migration rule (the target label types are nested in source element)
		 * @param atts
		 */
		private void handleMigrationRuleTarget(Attributes atts) {
			if (activeMigrationOntologyVersion == null || activeMigrationRuleSourceLabelTypeId == null)
				return; //Should not happen
			
			//Attributes
			String targetLabelTypeId = "";
			int i;
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_id)) >= 0) {
				targetLabelTypeId = atts.getValue(i);
			}
			
			//Add target label type
			// Get map for current rules
			Map<String,Set<String>> rulesForActiveVersion = migrationRules.get(activeMigrationOntologyVersion);
			if (rulesForActiveVersion == null)
				return; //Should not happen
			
			// Get current rule targets
			Set<String> targetIds = rulesForActiveVersion.get(activeMigrationRuleSourceLabelTypeId);
			if (targetIds == null) {
				targetIds = new HashSet<String>();
				rulesForActiveVersion.put(activeMigrationRuleSourceLabelTypeId, new HashSet<String>());
			}
			
			// Add
			if (!targetIds.contains(targetLabelTypeId))
				targetIds.add(targetLabelTypeId);
		}
	}
	
}
