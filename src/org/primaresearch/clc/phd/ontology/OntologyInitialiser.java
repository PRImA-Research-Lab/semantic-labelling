package org.primaresearch.clc.phd.ontology;


/**
 * Interface for classes that initialise an ontology with label type hierarchies (taxonomies).
 * 
 * @author clc
 *
 */
public interface OntologyInitialiser {

	/**
	 * Initialises the given ontology (label types and slots).
	 */
	public void init(Ontology ontology);
}
