package org.primaresearch.clc.phd.ontology.io;

import java.io.File;

import org.primaresearch.clc.phd.ontology.Ontology;

/**
 * Interface for writers that save an ontology to a file.
 * 
 * @author clc
 *
 */
public interface OntologyWriter {

	/**
	 * Saves the given label type hierarchies (taxonomies) to the specified XML file
	 */
	public void writeToFile(File file, Ontology ontology) throws Exception;
}
