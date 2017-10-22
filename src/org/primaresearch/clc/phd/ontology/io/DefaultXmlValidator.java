package org.primaresearch.clc.phd.ontology.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

/**
 * Wrapper for XML schema validator
 * 
 * @author clc
 *
 */
public class DefaultXmlValidator {

	URL schemaSource;
	Schema schema = null;
	
	public DefaultXmlValidator(URL schemaSource) {
		this.schemaSource = schemaSource;
	}

	/**
	 * Returns the source file of the schema for this validator
	 * @return Schema location
	 */
	public URL getSchemaSource() {
		return schemaSource; 
	}
	
	/**
	 * Returns the schema object that can be used for validating XML (e.g. DOM or SAX). 
	 */
	public Schema getSchema() {
		if (schema == null) {
    		//SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
    		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			try {
				URL schemaSource = getSchemaSource();
				InputStream inputStream = schemaSource.openStream();
				Source src = new StreamSource(inputStream);
				schema = schemaFactory.newSchema(src);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		return schema;
	}
	

}
