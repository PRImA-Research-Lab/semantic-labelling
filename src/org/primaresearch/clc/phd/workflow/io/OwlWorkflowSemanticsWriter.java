package org.primaresearch.clc.phd.workflow.io;

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

import org.primaresearch.clc.phd.ontology.io.DefaultErrorHandler;
import org.primaresearch.clc.phd.ontology.io.DefaultXmlValidator;
import org.primaresearch.clc.phd.ontology.io.OwlXmlNames;
import org.primaresearch.clc.phd.ontology.label.HasLabels;
import org.primaresearch.clc.phd.ontology.label.Label;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.ontology.label.LabelType;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityIterator;
import org.primaresearch.clc.phd.workflow.activity.ForLoopActivity;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.LoopPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * XML writer for semantic information of workflows.
 * Uses OWL XML format.
 * 
 * @author clc
 *
 */
public class OwlWorkflowSemanticsWriter {

	private Document doc = null;
	private String namespace = "http://www.w3.org/2002/07/owl#";
	private DefaultErrorHandler lastErrors;
	private DefaultXmlValidator validator;
	private final String ontologyIRI = "http://org.primaresearch.clc.phd.ontology.workflow";
	//private final String ontologyIRI = "http://org.primaresearch.clc.phd.ontology";
	private final String labelTypeOntologyUrl = "http://www.primaresearch.org/tmp/Christian/PhD/Ontology/OwlLabelTypes.xml";
	
	/**
	 * Save semantic information of the given workflow to the specified XML file using OWL XML format 
	 * @param workflow
	 * @param filePath
	 * @throws Exception
	 */
	public void write(Workflow workflow, String filePath) throws Exception {
		if(!filePath.toLowerCase().endsWith(".xml")){
			filePath += ".xml";
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
			
			//Import
			Element owlImportsNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_Import);
			root.appendChild(owlImportsNode);
			// URL
			Text textNode = doc.createTextNode(labelTypeOntologyUrl);
			owlImportsNode.appendChild(textNode);
			
			//Fixed properties
			// Name
			//declareAnnotationProperty(root, "#labelCaption");
			
			//Fixed classes
			//writeOwlClass(root, "#LabelType");
			
			//Named individuals for activities as well as their data objects and labels
			for (ActivityIterator it = workflow.getActivities(); it.hasNext(); )
				declareNamedIndividuals(root, it.next());

			//Class assertions for individuals that correspond to activities as well as their data objects and labels
			for (ActivityIterator it = workflow.getActivities(); it.hasNext(); )
				writeClassAssertions(root, it.next());
			
			//Object property assertions for all labels of the activities and their data objects
			for (ActivityIterator it = workflow.getActivities(); it.hasNext(); )
				writeObjectPropertyAssertions(root, it.next());
			

			//Classes for label types
			/*writeOwlLabelTypeClasses(root, ontology.getRootTypes());
			
			//Class relations (label type hierarchy)
			writeClassRelations(root, null, ontology.getRootTypes());
			
			//Label slots
			declareLabelSlotObjectProperties(root, ontology.getActivityLabelSlots());
			declareLabelSlotObjectProperties(root, ontology.getDataObjectLabelSlots());

			writeLabelSlotObjectPropertyDomains(root, "#Activity", ontology.getActivityLabelSlots());
			writeLabelSlotObjectPropertyDomains(root, "#DataObject", ontology.getDataObjectLabelSlots());
			
			writeLabelSlotObjectPropertyRanges(root, ontology.getActivityLabelSlots());
			writeLabelSlotObjectPropertyRanges(root, ontology.getDataObjectLabelSlots());
			*/
			
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

			StreamResult result =  new StreamResult(new File(filePath));
			transformer.transform(source, result);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
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
	 * Adds OWL named individuals for an activity and its data objects to the parent element (wrapped in 'Declaration' element)
	 * @param parent Parent XML node
	 * @param activity Activity
	 */
	private void declareNamedIndividuals(Element parent, Activity activity) {
		if (activity == null)
			return;
		
		//Activity individual
		declareNamedIndividual(parent, "#Activity_"+activity.getId());
		
		//Data objects
		// Input Port
		if (activity.getInputPorts() != null) {
			for (Iterator<InputPort> it = activity.getInputPorts().iterator(); it.hasNext(); ) {
				declareNamedIndividuals(parent, it.next());
			}
		}
		// Output Port
		if (activity.getOutputPorts() != null) {
			for (Iterator<OutputPort> it = activity.getOutputPorts().iterator(); it.hasNext(); ) {
				declareNamedIndividuals(parent, it.next());
			}
		}
		//Loop ports
		if (activity instanceof ForLoopActivity) {
			ForLoopActivity forLoop = (ForLoopActivity)activity;
			if (forLoop.getLoopStart() != null && forLoop.getLoopStart().getDataObject() != null) {
				declareNamedIndividuals(parent, forLoop.getLoopStart());
			}
			if (forLoop.getLoopPosition() != null && forLoop.getLoopPosition().getDataObject() != null) {
				declareNamedIndividuals(parent, forLoop.getLoopPosition());
			}
			if (forLoop.getLoopEnd() != null && forLoop.getLoopEnd().getDataObject() != null) {
				declareNamedIndividuals(parent, forLoop.getLoopEnd());
			}
			if (forLoop.getLoopStep() != null && forLoop.getLoopStep().getDataObject() != null) {
				declareNamedIndividuals(parent, forLoop.getLoopStep());
			}
		}
		
		//Labels
		declareNamedIndividuals(parent, "#Activity_"+activity.getId(), activity);
	}
	
	/**
	 * Adds OWL named individuals for a data port (data object and labels) to the parent element (wrapped in 'Declaration' element)
	 * @param parent Parent XML node
	 * @param activity Activity
	 */
	private void declareNamedIndividuals(Element parent, DataPort port) {
		if (port == null || port.getDataObject() == null)
			return;
		
		String iriBase = "";
		if (port instanceof InputPort)
			iriBase = "#InputPortDataObject_";			
		else if (port instanceof OutputPort)
			iriBase = "#OutputPortDataObject_";			
		else if (port instanceof LoopPort)
			iriBase = "#LoopPortDataObject_";	
		
		iriBase += port.getId();
		
		//DataObject
		declareNamedIndividual(parent, iriBase);
		
		//Labels
		declareNamedIndividuals(parent, iriBase, port.getDataObject());
	}
	
	/**
	 * Adds OWL named individuals for all labels of an object to the parent element (wrapped in 'Declaration' element)
	 * @param parent Parent XML node
	 * @param activity Activity
	 */
	private void declareNamedIndividuals(Element parent, String iriBase, HasLabels labellableObject) {
		if (labellableObject == null || labellableObject.getLabels() == null)
			return;
		
		for (Iterator<LabelGroup> it = labellableObject.getLabels().iterator(); it.hasNext(); ) {
			LabelGroup labelGroup = it.next();
			List<Label> labels = labelGroup.getLabels();
			if (labels == null)
				continue;
			for (Iterator<Label> itLabel = labels.iterator(); itLabel.hasNext(); ) {
				Label label = itLabel.next();
				if (label == null)
					continue;
				
				declareNamedIndividual(parent, iriBase+"_Label_"+label.getType().getId());
			}
		}
	}
	
	/**
	 * Adds an OWL named individual to the parent element (wrapped in 'Declaration' element)
	 * @param parent Parent XML node
	 * @param iri Internationalized resource identifier of the individual
	 */
	private void declareNamedIndividual(Element parent, String iri) {
		// 'Declaration'
		Element owlDeclarationNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_Declaration);
		parent.appendChild(owlDeclarationNode);
		
		//  'NamedIndividual'
		Element owlNamedIndividualNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_NamedIndividual);
		owlDeclarationNode.appendChild(owlNamedIndividualNode);
		
		// IRI (Internationalized resource identifier)
		owlNamedIndividualNode.setAttributeNS(null, OwlXmlNames.ATTR_IRI, iri);
	}
	
	/**
	 * Adds an OWL class assertion for an activity and its data objects to the parent element 
	 * @param parent Parent XML node
	 * @param activity Activity
	 */
	private void writeClassAssertions(Element parent, Activity activity) {
		if (activity == null)
			return;
		
		//Activity individual
		writeClassAssertion(parent, labelTypeOntologyUrl+"#Activity", "#Activity_"+activity.getId());
		
		//Data objects
		// Input Port
		if (activity.getInputPorts() != null) {
			for (Iterator<InputPort> it = activity.getInputPorts().iterator(); it.hasNext(); ) {
				writeClassAssertions(parent, it.next());
			}
		}
		// Output Port
		if (activity.getOutputPorts() != null) {
			for (Iterator<OutputPort> it = activity.getOutputPorts().iterator(); it.hasNext(); ) {
				writeClassAssertions(parent, it.next());
			}
		}
		//Loop ports
		if (activity instanceof ForLoopActivity) {
			ForLoopActivity forLoop = (ForLoopActivity)activity;
			if (forLoop.getLoopStart() != null && forLoop.getLoopStart().getDataObject() != null) {
				writeClassAssertions(parent, forLoop.getLoopStart());
			}
			if (forLoop.getLoopPosition() != null && forLoop.getLoopPosition().getDataObject() != null) {
				writeClassAssertions(parent, forLoop.getLoopPosition());
			}
			if (forLoop.getLoopEnd() != null && forLoop.getLoopEnd().getDataObject() != null) {
				writeClassAssertions(parent, forLoop.getLoopEnd());
			}
			if (forLoop.getLoopStep() != null && forLoop.getLoopStep().getDataObject() != null) {
				writeClassAssertions(parent, forLoop.getLoopStep());
			}
		}
		
		//Labels
		writeClassAssertions(parent, "#Activity_"+activity.getId(), activity);		
	}
	
	/**
	 * Adds OWL class assertions for a data port (data object and labels) to the parent element 
	 * @param parent Parent XML node
	 * @param activity Activity
	 */
	private void writeClassAssertions(Element parent, DataPort port) {
		if (port == null || port.getDataObject() == null)
			return;
		
		String iriBase = "";
		if (port instanceof InputPort)
			iriBase = "#InputPortDataObject_";			
		else if (port instanceof OutputPort)
			iriBase = "#OutputPortDataObject_";			
		else if (port instanceof LoopPort)
			iriBase = "#LoopPortDataObject_";		
		
		iriBase += port.getId();
		
		//DataObject
		writeClassAssertion(parent, labelTypeOntologyUrl+"#DataObject", iriBase);
		
		//Labels
		writeClassAssertions(parent, iriBase, port.getDataObject());
	}
	
	/**
	 * Adds OWL class assertions for all labels of an object to the parent element
	 * @param parent Parent XML node
	 * @param activity Activity
	 */
	private void writeClassAssertions(Element parent, String iriBase, HasLabels labellableObject) {
		if (labellableObject == null || labellableObject.getLabels() == null)
			return;
		
		for (Iterator<LabelGroup> it = labellableObject.getLabels().iterator(); it.hasNext(); ) {
			LabelGroup labelGroup = it.next();
			List<Label> labels = labelGroup.getLabels();
			if (labels == null)
				continue;
			for (Iterator<Label> itLabel = labels.iterator(); itLabel.hasNext(); ) {
				Label label = itLabel.next();
				if (label == null)
					continue;
				
				writeClassAssertion(parent, labelTypeOntologyUrl+"#"+label.getType().getId(), iriBase+"_Label_"+label.getType().getId());
			}
		}
	}
	
	/**
	 * Adds an OWL class assertion to the parent element 
	 * @param parent Parent XML node
	 * @param iri Internationalized resource identifier of the individual
	 */
	private void writeClassAssertion(Element parent, String classIri, String namedIndividualIri) {
		// 'ClassAssertion'
		Element owlClassAssertionNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_ClassAssertion);
		parent.appendChild(owlClassAssertionNode);
		
		//  'Class'
		Element owlClassNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_Class);
		owlClassAssertionNode.appendChild(owlClassNode);
		
		// IRI (Internationalized resource identifier)
		owlClassNode.setAttributeNS(null, OwlXmlNames.ATTR_IRI, classIri);

		//  'NamedIndividual'
		Element owlNamedIndividualNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_NamedIndividual);
		owlClassAssertionNode.appendChild(owlNamedIndividualNode);
		
		// IRI (Internationalized resource identifier)
		owlNamedIndividualNode.setAttributeNS(null, OwlXmlNames.ATTR_IRI, namedIndividualIri);
	}
	
	/**
	 * Adds an OWL object property assertion for an activity and its data objects to the parent element 
	 * @param parent Parent XML node
	 * @param activity Activity
	 */
	private void writeObjectPropertyAssertions(Element parent, Activity activity) {
		if (activity == null)
			return;
		
		//Data objects
		// Input Port
		if (activity.getInputPorts() != null) {
			for (Iterator<InputPort> it = activity.getInputPorts().iterator(); it.hasNext(); ) {
				writeObjectPropertyAssertions(parent, it.next());
			}
		}
		// Output Port
		if (activity.getOutputPorts() != null) {
			for (Iterator<OutputPort> it = activity.getOutputPorts().iterator(); it.hasNext(); ) {
				writeObjectPropertyAssertions(parent, it.next());
			}
		}
		//Loop ports
		if (activity instanceof ForLoopActivity) {
			ForLoopActivity forLoop = (ForLoopActivity)activity;
			if (forLoop.getLoopStart() != null && forLoop.getLoopStart().getDataObject() != null) {
				writeObjectPropertyAssertions(parent, forLoop.getLoopStart());
			}
			if (forLoop.getLoopPosition() != null && forLoop.getLoopPosition().getDataObject() != null) {
				writeObjectPropertyAssertions(parent, forLoop.getLoopPosition());
			}
			if (forLoop.getLoopEnd() != null && forLoop.getLoopEnd().getDataObject() != null) {
				writeObjectPropertyAssertions(parent, forLoop.getLoopEnd());
			}
			if (forLoop.getLoopStep() != null && forLoop.getLoopStep().getDataObject() != null) {
				writeObjectPropertyAssertions(parent, forLoop.getLoopStep());
			}
		}
		
		//Labels
		writeObjectPropertyAssertions(parent, "#Activity_"+activity.getId(), activity, "#Activity_"+activity.getId());		
	}
	
	/**
	 * Adds OWL object property assertions for a data port (data object and labels) to the parent element 
	 * @param parent Parent XML node
	 * @param activity Activity
	 */
	private void writeObjectPropertyAssertions(Element parent, DataPort port) {
		if (port == null || port.getDataObject() == null)
			return;
		
		String iriBase = "";
		if (port instanceof InputPort)
			iriBase = "#InputPortDataObject_";			
		else if (port instanceof OutputPort)
			iriBase = "#OutputPortDataObject_";			
		else if (port instanceof LoopPort)
			iriBase = "#LoopPortDataObject_";	
		iriBase += port.getId();
		
		//Labels
		writeObjectPropertyAssertions(parent, iriBase, port.getDataObject(), iriBase);
	}
	
	/**
	 * Adds OWL object property assertions for all labels of an object to the parent element
	 * @param parent Parent XML node
	 * @param activity Activity
	 */
	private void writeObjectPropertyAssertions(Element parent, String iriBase, HasLabels labellableObject, String namedIndividual1Iri) {
		if (labellableObject == null || labellableObject.getLabels() == null)
			return;
		
		for (Iterator<LabelGroup> it = labellableObject.getLabels().iterator(); it.hasNext(); ) {
			LabelGroup labelGroup = it.next();
			List<Label> labels = labelGroup.getLabels();
			if (labels == null)
				continue;
			for (Iterator<Label> itLabel = labels.iterator(); itLabel.hasNext(); ) {
				Label label = itLabel.next();
				if (label == null)
					continue;
				
				writeObjectPropertyAssertion(parent, "#has"+label.getType().getRootType().getId(), namedIndividual1Iri, iriBase+"_Label_"+label.getType().getId());
			}
		}
	}
	
	/**
	 * Adds an OWL object property assertion to the parent element 
	 * @param parent Parent XML node
	 * @param propertyIri
	 * @param namedIndividual1Iri
	 * @param namedIndividual2Iri
	 */
	private void writeObjectPropertyAssertion(Element parent, String propertyIri, String namedIndividual1Iri, String namedIndividual2Iri) {
		// 'ObjectPropertyAssertion'
		Element owlPropertyAssertionNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_ObjectPropertyAssertion);
		parent.appendChild(owlPropertyAssertionNode);
		
		//  'ObjectProperty'
		Element owlObjectPropertyNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_ObjectProperty);
		owlPropertyAssertionNode.appendChild(owlObjectPropertyNode);
		
		//     IRI (Internationalized resource identifier)
		owlObjectPropertyNode.setAttributeNS(null, OwlXmlNames.ATTR_IRI, propertyIri);

		//  'NamedIndividual' 1
		Element owlNamedIndividualNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_NamedIndividual);
		owlPropertyAssertionNode.appendChild(owlNamedIndividualNode);
		
		//     IRI (Internationalized resource identifier)
		owlNamedIndividualNode.setAttributeNS(null, OwlXmlNames.ATTR_IRI, namedIndividual1Iri);
		
		//  'NamedIndividual' 2
		owlNamedIndividualNode = doc.createElementNS(namespace, OwlXmlNames.ELEMENT_NamedIndividual);
		owlPropertyAssertionNode.appendChild(owlNamedIndividualNode);
		
		//     IRI (Internationalized resource identifier)
		owlNamedIndividualNode.setAttributeNS(null, OwlXmlNames.ATTR_IRI, namedIndividual2Iri);
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
