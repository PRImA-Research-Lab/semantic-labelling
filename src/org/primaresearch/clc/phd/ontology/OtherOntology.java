package org.primaresearch.clc.phd.ontology;

import java.io.File;

/**
 * Wrapper for ontology class to allow the instantiation of multiple ontologies (the base class is a singleton)
 * @author clc
 *
 */
public class OtherOntology extends Ontology {

	public OtherOntology(String sourceFilePath) {
		super(new File(sourceFilePath));
	}
}
