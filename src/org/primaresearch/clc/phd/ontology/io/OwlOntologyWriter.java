package org.primaresearch.clc.phd.ontology.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Validator;

import org.primaresearch.clc.phd.ontology.Ontology;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.ontology.label.LabelType;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * XML writer for ontology label types.
 * Uses OWL XML format.
 * 
 * @author clc
 *
 */
public class OwlOntologyWriter implements OntologyWriter {

	private Document doc = null;
	private String namespace = "http://www.w3.org/2002/07/owl#";
	private DefaultErrorHandler lastErrors;
	private DefaultXmlValidator validator;
	private final String ontologyIRI = "http://org.primaresearch.clc.phd.ontology";

	@Override
	public void writeToFile(File file, Ontology ontology) throws Exception {
		if(!file.getAbsolutePath().toLowerCase().endsWith(".xml")){
		    file = new File(file + ".xml");
		}
		
		try
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setValidating(false);
			docFactory.setNamespaceAware(true);

	        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			DOMImplementation domImpl = docBuilder.getDOMImplementation();

			doc = domImpl.createDocument(namespace, OwlXmlNames.ELEMENT_Ontology, null);
			
			
			//Schema location
			Element root = doc.getDocumentElement();
			//String schemaLocation = "http://www.w3.org/ns/owl2-xml http://www.w3.org/2009/09/owl2-xml.xsd";
			//root.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:schemaLocation", schemaLocation);
			
			lastErrors = new DefaultErrorHandler();

			//Ontology IRI
			root.setAttributeNS(null, OwlXmlNames.ATTR_ontologyIRI, ontologyIRI);
			
			//Fixed annotation properties (attributes)
			// Name
			declareAnnotationProperty(root, "#labelCaption");
			declareAnnotationProperty(root, "#labelDescription");

			//Fixed classes
			writeOwlClass(root, "#LabelType");
			writeOwlClass(root, "#Activity");
			writeOwlClass(root, "#DataObject");
			
			//Classes for label types
			writeOwlLabelTypeClasses(root, ontology.getRootTypes());
			
			//Class relations (label type hierarchy)
			writeClassRelations(root, null, ontology.getRootTypes());
			
			//Label slots
			declareLabelSlotObjectProperties(root, ontology.getActivityLabelSlots());
			declareLabelSlotObjectProperties(root, ontology.getDataObjectLabelSlots());

			writeLabelSlotObjectPropertyDomains(root, "#Activity", ontology.getActivityLabelSlots());
			writeLabelSlotObjectPropertyDomains(root, "#DataObject", ontology.getDataObjectLabelSlots());
			
			writeLabelSlotObjectPropertyRanges(root, ontology.getActivityLabelSlots());
			writeLabelSlotObjectPropertyRanges(root, ontology.getDataObjectLabelSlots());

			
	        //Validation errors?
			if (validator == null)
				validator = new DefaultXmlValidator(new URL("http://www.w3.org/2009/09/owl2-xml.xsd"));
        	Validator domVal = validator.getSchema().newValidator();
        	domVal.setErrorHandler(lastErrors);

        	try {
				domVal.validate(new DOMSource(doc));
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

	        if (lastErrors.hasErrors()) {
	        	throw new Exception(lastErrors.toString());
	        }

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
	 * Adds the corresponding OWL classes for the given label types and subtypes (recursive)
	 * 
	 * @param parent Parent XML element
	 * @param labelTypes Collection of label types to add
	 */
	private void writeOwlLabelTypeClasses(Element parent, Collection<LabelType> labelTypes) {
		
		if (labelTypes == null)
			return;
		
		for (Iterator<LabelType> it = labelTypes.iterator(); it.hasNext(); ) {
			LabelType labelType = it.next();
			
			writeOwlClass(parent, "#"+labelType.getId());
			
			addAnnotation(parent, "#labelCaption", "#"+labelType.getId(), labelType.getCaption());
			addAnnotation(parent, "#labelDescription", "#"+labelType.getId(), labelType.getDescription());
			
			//Recursion
			Collection<LabelType> childTypes = labelType.getChildren();
			if (childTypes != null && !childTypes.isEmpty()) {
				writeOwlLabelTypeClasses(parent, childTypes);
			}
		}
	}
	
	/**
	 * Adds an OWL class to the parent element (wrapped in 'Declaration' element)
	 * @param parent Parent XML node
	 * @param iri Internationalized resource identifier of the class
	 */
	private Element writeOwlClass(Element parent, String iri) {
		//  'Declaration'
		Element owlDeclarationNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_Declaration);
		parent.appendChild(owlDeclarationNode);
		
		//  'Class'
		Element owlClassNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_Class);
		owlDeclarationNode.appendChild(owlClassNode);
		
		// IRI (Internationalized resource identifier)
		owlClassNode.setAttributeNS(null, OwlXmlNames.ATTR_IRI, iri);
		
		return owlClassNode;
	}
	
	/**
	 * Writes class relations for label types (recursive)
	 * @param parent Parent XML node
	 * @param parentLabelType Parent label type or <code>null</code>
	 * @param childLabelTypes Child label type
	 */
	private void writeClassRelations(Element parent, LabelType parentLabelType, Collection<LabelType> childLabelTypes) {
		if (childLabelTypes == null)
			return;
		
		for (Iterator<LabelType> it = childLabelTypes.iterator(); it.hasNext(); ) {
			LabelType childLabelType = it.next();
			
			writeSubClassRelation(parent, parentLabelType, childLabelType);
			
			//Recursion
			Collection<LabelType> grandChildTypes = childLabelType.getChildren();
			if (grandChildTypes != null && !grandChildTypes.isEmpty()) {
				writeClassRelations(parent, childLabelType, grandChildTypes);
			}
		}
	}
	
	/**
	 * Writes a 'subClassOf' relation for the given label types
	 */
	private void writeSubClassRelation(Element parent, LabelType parentLabelType, LabelType childLabelType) {
		//'SubClassOf'
		Element subClassOfNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_SubClassOf);
		parent.appendChild(subClassOfNode);
		
		//'Class' (child)
		Element owlClassNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_Class);
		subClassOfNode.appendChild(owlClassNode);
		
		// IRI (Internationalized resource identifier)
		owlClassNode.setAttributeNS(null, OwlXmlNames.ATTR_IRI, "#"+childLabelType.getId());
		
		//'Class' (parent)
		owlClassNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_Class);
		subClassOfNode.appendChild(owlClassNode);
		// IRI (Internationalized resource identifier)
		//  Root type? -> use 'LabelType' as parent
		if (parentLabelType == null)
			owlClassNode.setAttributeNS(null, OwlXmlNames.ATTR_IRI, "#LabelType");
		else 
			owlClassNode.setAttributeNS(null, OwlXmlNames.ATTR_IRI, "#"+parentLabelType.getId());
	}
	
	/**
	 * Writes an annotation property (attribute)
	 * @param parent Parent XML node
	 * @param iri
	 */
	private void declareAnnotationProperty(Element parent, String iri) {
		//  'Declaration'
		Element owlDeclarationNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_Declaration);
		parent.appendChild(owlDeclarationNode);
		
		//  'AnnotationProperty'
		Element owlAnnotationPropertyNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_AnnotationProperty);
		owlDeclarationNode.appendChild(owlAnnotationPropertyNode);
		
		// IRI (Internationalized resource identifier)
		owlAnnotationPropertyNode.setAttributeNS(null, OwlXmlNames.ATTR_IRI, iri);
	}
	
	/**
	 * Adds an annotation assertion which assigns an annotation property value to a class
	 * @param parent Parent XML node
	 * @param propertyIri IRI of annotation property to use
	 * @param targetIri IRI of target OWL class
	 * @param propertyValue Value of the property (the annotation)
	 */
	private void addAnnotation(Element parent, String propertyIri, String targetIri, String propertyValue) {
		if (propertyValue == null)
			return;
		
		//'AnnotationAssertion'
		Element annotationAssertionNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_AnnotationAssertion);
		parent.appendChild(annotationAssertionNode);
		
		// 'AnnotationProperty'
		Element annotationPropertyNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_AnnotationProperty);
		annotationAssertionNode.appendChild(annotationPropertyNode);
	
		annotationPropertyNode.setAttributeNS(null, OwlXmlNames.ATTR_IRI, propertyIri);

		// 'IRI'
		Element targetIriNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_IRI);
		annotationAssertionNode.appendChild(targetIriNode);
		
		Text textNode = doc.createTextNode(targetIri);
		targetIriNode.appendChild(textNode);

		// 'Literal'
		Element valueNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_Literal);
		annotationAssertionNode.appendChild(valueNode);
		
		valueNode.setAttributeNS(null, OwlXmlNames.ATTR_datatypeIRI, "&rdf;PlainLiteral");
		
		textNode = doc.createTextNode(propertyValue);
		valueNode.appendChild(textNode);
	}
	
	/**
	 * Writes declarations of the OWL object properties that represent the available label slots for activities and data objects.
	 * The IRI of a label slot group is set to '#has'+labelType.
	 * @param parent Parent XML node
	 * @param labelSlots Label slots for activities or data objects
	 */
	private void declareLabelSlotObjectProperties(Element parent, List<LabelGroup> labelSlots) {
		
		for (Iterator<LabelGroup> it = labelSlots.iterator(); it.hasNext(); ) {
			LabelGroup labelGroup = it.next();
			
			//  'Declaration'
			Element owlDeclarationNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_Declaration);
			parent.appendChild(owlDeclarationNode);
			
			//  'ObjectProperty'
			Element owlObjectPropertyNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_ObjectProperty);
			owlDeclarationNode.appendChild(owlObjectPropertyNode);
			
			// IRI (Internationalized resource identifier)
			owlObjectPropertyNode.setAttributeNS(null, OwlXmlNames.ATTR_IRI, "#has"+labelGroup.getType().getName());
		}		
	}
	
	/**
	 * Writes the OWL object property domains for label slots. The domain is the left side of the property relation 
	 * (e.g. 'Activity' in 'Activity hasLicense License')
	 * @param parent Parent XML node
	 * @param labelTargetIri Class IRI for Activity or DataObject
	 * @param labelSlots Label slots for activities or data objects
	 */
	private void writeLabelSlotObjectPropertyDomains(Element parent, String labelTargetIri, List<LabelGroup> labelSlots) {
		for (Iterator<LabelGroup> it = labelSlots.iterator(); it.hasNext(); ) {
			LabelGroup labelGroup = it.next();
			
			//'ObjectPropertyDomain'
			Element owlObjectPropertyDomainNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_ObjectPropertyDomain);
			parent.appendChild(owlObjectPropertyDomainNode);
			
			// 'ObjectProperty'
			Element owlObjectPropertyNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_ObjectProperty);
			owlObjectPropertyDomainNode.appendChild(owlObjectPropertyNode);
			
			//  Property IRI (Internationalized resource identifier)
			owlObjectPropertyNode.setAttributeNS(null, OwlXmlNames.ATTR_IRI, "#has"+labelGroup.getType().getName());
			
			//Label target class
			Element owlClassNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_Class);
			owlObjectPropertyDomainNode.appendChild(owlClassNode);
			
			// IRI (Internationalized resource identifier)
			owlClassNode.setAttributeNS(null, OwlXmlNames.ATTR_IRI, labelTargetIri);
		}		
	}
	
	/**
	 * Writes the OWL object property ranges for label slots. The range is the right side of the property relation 
	 * (e.g. 'License' in 'Activity hasLicense License')
	 * @param parent Parent XML node
	 * @param labelSlots Label slots for activities or data objects
	 */
	private void writeLabelSlotObjectPropertyRanges(Element parent, List<LabelGroup> labelSlots) {
		
		for (Iterator<LabelGroup> it = labelSlots.iterator(); it.hasNext(); ) {
			LabelGroup labelGroup = it.next();
			
			//'ObjectPropertyRange'
			Element owlObjectPropertyRangeNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_ObjectPropertyRange);
			parent.appendChild(owlObjectPropertyRangeNode);
			
			// 'ObjectProperty'
			Element owlObjectPropertyNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_ObjectProperty);
			owlObjectPropertyRangeNode.appendChild(owlObjectPropertyNode);
			
			//  Property IRI (Internationalized resource identifier)
			owlObjectPropertyNode.setAttributeNS(null, OwlXmlNames.ATTR_IRI, "#has"+labelGroup.getType().getName());
			
			// 'Class' (slot type)
			//Element owlClassNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_Class);
			//owlObjectPropertyRangeNode.appendChild(owlClassNode);
			
			//  IRI (Internationalized resource identifier)
			//owlClassNode.setAttributeNS(null, OwlXmlNames.ATTR_IRI, "#"+labelGroup.getType().getId());
			
			// ->'ObjectMaxCardinality'
			Element owlObjectMaxCardinalityNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_ObjectMaxCardinality);
			owlObjectPropertyRangeNode.appendChild(owlObjectMaxCardinalityNode);
			
			//    'cardinality'
			owlObjectMaxCardinalityNode.setAttributeNS(null, OwlXmlNames.ATTR_cardinality, ""+labelGroup.getMaxLabels());
			
			//    ->'ObjectProperty'
			owlObjectPropertyNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_ObjectProperty);
			owlObjectMaxCardinalityNode.appendChild(owlObjectPropertyNode);
			
			//       Property IRI (Internationalized resource identifier)
			owlObjectPropertyNode.setAttributeNS(null, OwlXmlNames.ATTR_IRI, "#has"+labelGroup.getType().getName());
			
			//    ->'Class' (slot holder)
			Element owlClassNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_Class);
			owlObjectMaxCardinalityNode.appendChild(owlClassNode);
			
			//  IRI (Internationalized resource identifier)
			owlClassNode.setAttributeNS(null, OwlXmlNames.ATTR_IRI, "#"+labelGroup.getType().getId());
		}		
	}



}
