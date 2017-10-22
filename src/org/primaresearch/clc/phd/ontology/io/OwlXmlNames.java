package org.primaresearch.clc.phd.ontology.io;

/**
 * Element and attribute names for OWL XML format.
 * 
 * @author clc
 *
 */
public interface OwlXmlNames {

	public final String ELEMENT_Ontology = "Ontology";
	public final String ELEMENT_Declaration = "Declaration";
	public final String ELEMENT_Class = "Class";
	public final String ELEMENT_SubClassOf = "SubClassOf";
	public final String ELEMENT_AnnotationProperty = "AnnotationProperty";
	public final String ELEMENT_AnnotationAssertion = "AnnotationAssertion";
	public final String ELEMENT_IRI = "IRI";
	public final String ELEMENT_Literal = "Literal";
	public final String ELEMENT_ObjectProperty = "ObjectProperty";
	public final String ELEMENT_ObjectPropertyDomain = "ObjectPropertyDomain";
	public final String ELEMENT_ObjectPropertyRange = "ObjectPropertyRange";
	public final String ELEMENT_ClassAssertion = "ClassAssertion";
	public final String ELEMENT_ObjectMaxCardinality = "ObjectMaxCardinality";
	public final String ELEMENT_Import = "Import";
	public final String ELEMENT_NamedIndividual = "NamedIndividual";
	public final String ELEMENT_ObjectPropertyAssertion = "ObjectPropertyAssertion";
	
	public final String ATTR_ontologyIRI = "ontologyIRI";
	public final String ATTR_IRI = "IRI";
	public final String ATTR_datatypeIRI = "datatypeIRI";
	public final String ATTR_property = "property";
	public final String ATTR_cardinality = "cardinality";
	public final String ATTR_URI = "URI";
}
