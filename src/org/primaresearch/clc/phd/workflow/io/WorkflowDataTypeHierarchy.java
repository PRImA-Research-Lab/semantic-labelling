package org.primaresearch.clc.phd.workflow.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.primaresearch.clc.phd.workflow.data.WorkflowDataType;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Singleton containing all predefined data types that can be used in activities 
 */
public class WorkflowDataTypeHierarchy {

	private static WorkflowDataTypeHierarchy instance;
	
	private static final String hierarchyXmlFilePath = "H:\\Users\\clc\\Dropbox\\PhD\\Experiments\\Ontology\\WorkflowDataTypes.xml";
	
	private WorkflowDataType root = null;
	
	
	private WorkflowDataTypeHierarchy() {
		load();
	}
	
	public static WorkflowDataTypeHierarchy getInstance() {
		if (instance == null) {
			instance = new WorkflowDataTypeHierarchy();
		}
		return instance;
	}
	
	public void load() {
	    try {
	    	// Obtain a new instance of a SAXParserFactory.
	    	SAXParserFactory factory = SAXParserFactory.newInstance();
	    	// Specifies that the parser produced by this code will provide support for XML namespaces.
	    	factory.setNamespaceAware(true);
    		factory.setValidating(false);
	    	
    		SAXParser typeParser = factory.newSAXParser();
    		DataTypeSaxHandler handler = new DataTypeSaxHandler();
    		
    		InputStream inputStream = null;
    		
    		//Try external file first
    		File extFile = new File(hierarchyXmlFilePath);
    		if (extFile.exists()) {
    			inputStream = new FileInputStream(extFile);
    		}
    		
    		typeParser.parse(inputStream, handler);
    		
    		//Add root type
    		root = handler.getRootType();
    		
	    } catch (Throwable t) {
	    	t.printStackTrace();
	    }
	}
	
	public WorkflowDataType getRoot() {
		return root;
	}



	/**
	 * SAX handler implementation to parse data type hierarchies.
	 * 
	 * @author clc
	 */
	private static class DataTypeSaxHandler extends DefaultHandler {
		private WorkflowDataType rootType = null;
		private Stack<WorkflowDataType> parents = new Stack<WorkflowDataType>();

		public WorkflowDataType getRootType() {
			return rootType;
		}
		

		/**
		 * Receive notification of the start of an element.
		 * @param namespaceURI - The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
		 * @param localName - The local name (without prefix), or the empty string if Namespace processing is not being performed.
		 * @param qName - The qualified name (with prefix), or the empty string if qualified names are not available.
		 * @param atts - The attributes attached to the element. If there are no attributes, it shall be an empty Attributes object.
		 * @throws SAXException - Any SAX exception, possibly wrapping another exception.
		 */
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
		      throws SAXException {
			
		    if ("DataType".equals(localName)){
		    	String name = atts.getValue("name");
		    	String caption = atts.getValue("caption");
		    	int i;
		    	boolean primitive = false;
		    	if ((i=atts.getIndex("primitive")) >= 0)
		    		primitive = atts.getValue(i).equals("true");
		    	
		    	WorkflowDataType parent = parents.isEmpty() ? null : parents.peek();
		    	
		    	WorkflowDataType dataType = new WorkflowDataType(caption, name, primitive, parent);
		    	
		    	if (parent != null) {
		    		parent.addChildType(dataType);
		    	}
		    	
		    	parents.push(dataType);
		    	
		    	if (rootType == null)
		    		rootType = dataType;
		    } 
		}
		
		/**
		 * Receive notification of the end of an element.
		 * 
		 * @param namespaceURI - The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
		 * @param localName - The local name (without prefix), or the empty string if Namespace processing is not being performed.
		 * @param qName - The qualified name (with prefix), or the empty string if qualified names are not available.
		 * @throws SAXException - Any SAX exception, possibly wrapping another exception.
		 */
		public void endElement(String namespaceURI, String localName, String qName)
		      throws SAXException {
			
		    if ("DataType".equals(localName)) {
		    	parents.pop();
		    } 

		}
		
		/**
		 * Receive notification of character data inside an element.
		 * @param ch - The characters.
		 * @param start - The start position in the character array.
		 * @param length - The number of characters to use from the character array.
		 * @throws SAXException - Any SAX exception, possibly wrapping another exception.
		 */
		public void characters(char[] ch, int start, int length)
		      throws SAXException {
		}
		

	}
}
