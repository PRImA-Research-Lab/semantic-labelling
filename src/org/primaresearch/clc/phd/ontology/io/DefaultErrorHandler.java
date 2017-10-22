package org.primaresearch.clc.phd.ontology.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DefaultErrorHandler implements ErrorHandler {

	List<XmlValidationError> errors = new ArrayList<XmlValidationError>();
	List<XmlValidationError> warnings = new ArrayList<XmlValidationError>();

	@Override
	public void error(SAXParseException exc) throws SAXException {
		errors.add(new XmlValidationError(exc.getMessage(), "Line "+exc.getLineNumber()+", Column: "+exc.getColumnNumber()));
	}

	@Override
	public void fatalError(SAXParseException exc) throws SAXException {
		errors.add(new XmlValidationError(exc.getMessage(), "Line "+exc.getLineNumber()+", Column: "+exc.getColumnNumber()));
	}

	@Override
	public void warning(SAXParseException exc) throws SAXException {
		warnings.add(new XmlValidationError(exc.getMessage(), "Line "+exc.getLineNumber()+", Column: "+exc.getColumnNumber()));
	}
	
	/**
	 * Checks if there were errors
	 * @return <code>true</code> if errors were registered
	 */
	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	/**
	 * Checks if there were warnings
	 * @return <code>true</code> if warnings were registered
	 */
	public boolean hasWarnings() {
		return !warnings.isEmpty();
	}
	
	/**
	 * Returns all registered errors
	 * @return List of error objects
	 */
	public List<XmlValidationError> getErrors() {
		return errors;
	}

	/**
	 * Returns all registered warnings
	 * @return List of warning objects
	 */
	public List<XmlValidationError> getWarnings() {
		return warnings;
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		
		for (Iterator<XmlValidationError> it = errors.iterator(); it.hasNext(); ) {
			ret.append(it.next().getMessage());
			ret.append("\n");
		}
		
		for (Iterator<XmlValidationError> it = warnings.iterator(); it.hasNext(); ) {
			ret.append(it.next().getMessage());
			ret.append("\n");
		}	
		return ret.toString();
	}

	
	/**
	 * XML error
	 * 
	 * @author clc
	 *
	 */
	public static class XmlValidationError {

		private String message;
		private String location;
		
		public XmlValidationError(String message, String location) {
			this.message = message;
			this.location = location;
		}

		public String getMessage() {
			return message;
		}
		
		/**
		 * Returns the location of the error (usually line and column).
		 * @return Location text 
		 */
		public String getLocation() {
			return location;
		}
	}

}
