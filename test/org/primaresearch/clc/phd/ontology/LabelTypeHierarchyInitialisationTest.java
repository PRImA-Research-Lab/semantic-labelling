package org.primaresearch.clc.phd.ontology;

import static org.junit.Assert.*;

import org.junit.Test;

public class LabelTypeHierarchyInitialisationTest {

	@Test
	public void testGetInstance() {
		assertNotNull(Ontology.getInstance());
	}

	@Test
	public void testGetRootType() {
		assertNotNull(Ontology.getInstance().getRootType(Ontology.DefaultRootType.PROCESSING_TYPE));
	}

}
