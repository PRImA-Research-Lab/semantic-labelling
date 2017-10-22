package org.primaresearch.clc.phd.ontology.io;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.primaresearch.clc.phd.ontology.Ontology;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.ontology.label.LabelType;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML writer for ontology label types.
 * Uses a custom XML format.
 * 
 * @author clc
 *
 */
public class SimpleOntologyWriter implements OntologyWriter {

	private Document doc = null;
	
	/**
	 * Saves the given label type hierarchies (taxonomies) to the specified XML file
	 */
	@Override
	public void writeToFile(File file, Ontology ontology) throws Exception {
		if(!file.getAbsolutePath().toLowerCase().endsWith(".xml")){
		    file = new File(file + ".xml");
		}
		
		Collection<LabelType> labelTypes = ontology.getRootTypes();
		
		try
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			doc = docBuilder.newDocument();

			//Root
			Element rootElement = doc.createElement(DefaultXmlNames.ELEMENT_Ontology);
			doc.appendChild(rootElement);
			
			//Version number
			Attr attr = doc.createAttribute(DefaultXmlNames.ATTR_version);
			attr.setValue(""+ontology.getVersion());
			rootElement.setAttributeNode(attr);

			
			//Label type hierarchies
			Element taxonomiesRoot = doc.createElement(DefaultXmlNames.ELEMENT_LabelTypeHierarchies);
			rootElement.appendChild(taxonomiesRoot);

			for (Iterator<LabelType> it = labelTypes.iterator(); it.hasNext(); ) {
				addChildType(taxonomiesRoot, it.next());
			}
			
			//Label slots
			// Activity
			Element slotsRoot = doc.createElement(DefaultXmlNames.ELEMENT_ActivityLabelSlots);
			rootElement.appendChild(slotsRoot);
			
			List<LabelGroup> slots = ontology.getActivityLabelSlots();
			
			for (Iterator<LabelGroup> it = slots.iterator(); it.hasNext(); ) {
				addSlots(slotsRoot, it.next());
			}
			
			// Data object
			slotsRoot = doc.createElement(DefaultXmlNames.ELEMENT_DataObjectLabelSlots);
			rootElement.appendChild(slotsRoot);
			
			slots = ontology.getDataObjectLabelSlots();
			
			for (Iterator<LabelGroup> it = slots.iterator(); it.hasNext(); ) {
				addSlots(slotsRoot, it.next());
			}
			
			// User
			slotsRoot = doc.createElement(DefaultXmlNames.ELEMENT_UserLabelSlots);
			rootElement.appendChild(slotsRoot);
			
			slots = ontology.getUserLabelSlots();
			
			for (Iterator<LabelGroup> it = slots.iterator(); it.hasNext(); ) {
				addSlots(slotsRoot, it.next());
			}
			
			//Migration rules
			writeAllMigrationRules(rootElement, ontology);

			//Write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 			//For nicer formatting
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			DOMSource source = new DOMSource(doc);

			StreamResult result =  new StreamResult(file);
			transformer.transform(source, result);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Recursively adds the given label type and its children to the XML DOM.
	 * 
	 * @param parentElement Element to which to append the new child
	 * @param childType Label type to append
	 */
	private void addChildType(Element parentElement, LabelType childType) {

		Element labelTypeElement = doc.createElement(DefaultXmlNames.ELEMENT_LabelType);
		parentElement.appendChild(labelTypeElement);
		
		Attr attr = doc.createAttribute(DefaultXmlNames.ATTR_name);
		attr.setValue(childType.getName());
		labelTypeElement.setAttributeNode(attr);
		
		attr = doc.createAttribute(DefaultXmlNames.ATTR_caption);
		attr.setValue(childType.getCaption());
		labelTypeElement.setAttributeNode(attr);
		
		if (childType.getDescription() != null && !childType.getDescription().isEmpty()) {
			Element descriptionElement = doc.createElement(DefaultXmlNames.ELEMENT_Description);
			labelTypeElement.appendChild(descriptionElement);
			descriptionElement.appendChild(doc.createTextNode(childType.getDescription()));
		}
		
		//Recursion for children
		Set<LabelType> children = childType.getChildren();
		if (children != null) {
			for (Iterator<LabelType> it = children.iterator(); it.hasNext(); ) {
				addChildType(labelTypeElement, it.next());
			}
		}
	}
	
	private void addSlots(Element slotsRoot, LabelGroup slotGroup) {
		Element slotGroupNode = doc.createElement(DefaultXmlNames.ELEMENT_LabelSlotGroup);
		slotsRoot.appendChild(slotGroupNode);
		
		Attr attr = doc.createAttribute(DefaultXmlNames.ATTR_name);
		attr.setValue(slotGroup.getType().getName());
		slotGroupNode.setAttributeNode(attr);
		
		attr = doc.createAttribute(DefaultXmlNames.ATTR_slots);
		attr.setValue(""+slotGroup.getMaxLabels());
		slotGroupNode.setAttributeNode(attr);
	}
	
	/**
	 * Adds the ontology migration rules
	 * @param rootElement
	 * @param ontology
	 */
	private void writeAllMigrationRules(Element rootElement, Ontology ontology) {
		
		Map<Integer,Map<String,Set<String>>> rules = ontology.getAllMigrationRules();
		if (rules == null || rules.isEmpty())
			return;
		
		//Iterate over supported ontology versions
		for (Iterator<Integer> it = rules.keySet().iterator(); it.hasNext(); ) {
			Integer version = it.next();
			writeMigrationRules(rootElement, version, rules.get(version));
		}
	}
	
	/**
	 * Adds the ontology migration rules for the given version
	 * @param rulesRootNode
	 * @param ontologyVersion
	 * @param rules
	 */
	private void writeMigrationRules(Element rootElement, Integer ontologyVersion, Map<String,Set<String>> rules) {

		//Root
		Element rulesRootNode = doc.createElement(DefaultXmlNames.ELEMENT_MigrationRules);
		rootElement.appendChild(rulesRootNode);
		
		//Version number
		Attr attr = doc.createAttribute(DefaultXmlNames.ATTR_version);
		attr.setValue(ontologyVersion.toString());
		rulesRootNode.setAttributeNode(attr);
		
		//Rules
		for (Iterator<String> it = rules.keySet().iterator(); it.hasNext(); ) {
			String sourceLabelTypeId = it.next();
			writeMigrationRules(rulesRootNode, sourceLabelTypeId, rules.get(sourceLabelTypeId));
		}
	}

	/**
	 * Adds the rules for a single source label type ID (one to many relation)
	 * @param rulesRootNode
	 * @param sourceLabelTypeId
	 * @param targetIds
	 */
	private void writeMigrationRules(Element rulesRootNode, String sourceLabelTypeId, Set<String> targetIds) {
		//Source node
		Element sourceNode = doc.createElement(DefaultXmlNames.ELEMENT_SourceType);
		rulesRootNode.appendChild(sourceNode);
		
		// Source ID
		Attr attr = doc.createAttribute(DefaultXmlNames.ATTR_id);
		attr.setValue(sourceLabelTypeId);
		sourceNode.setAttributeNode(attr);
		
		//Target IDs
		for (Iterator<String> it = targetIds.iterator(); it.hasNext(); ) {
			String tagetLabelTypeId = it.next();
			
			Element targetNode = doc.createElement(DefaultXmlNames.ELEMENT_TargetType);
			sourceNode.appendChild(targetNode);

			// Target ID
			attr = doc.createAttribute(DefaultXmlNames.ATTR_id);
			attr.setValue(tagetLabelTypeId);
			targetNode.setAttributeNode(attr);
		}
	}
}
