package org.primaresearch.clc.phd.workflow.io;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.primaresearch.clc.phd.ontology.label.Label;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.workflow.DescriptionWithLabels;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityType;
import org.primaresearch.clc.phd.workflow.activity.AtomicActivity;
import org.primaresearch.clc.phd.workflow.activity.DirectedGraphActivity;
import org.primaresearch.clc.phd.workflow.activity.DirectedGraphActivity.ActivityNode;
import org.primaresearch.clc.phd.workflow.activity.ForLoopActivity;
import org.primaresearch.clc.phd.workflow.activity.ifelse.CombinedCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.ComparisonCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity;
import org.primaresearch.clc.phd.workflow.activity.ifelse.InputPortCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity.IfBranch;
import org.primaresearch.clc.phd.workflow.activity.ifelse.NotCondition;
import org.primaresearch.clc.phd.workflow.data.DataCollection;
import org.primaresearch.clc.phd.workflow.data.DataObject;
import org.primaresearch.clc.phd.workflow.data.DataTable;
import org.primaresearch.clc.phd.workflow.data.SingleDataObject;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.DataTableColumn;
import org.primaresearch.clc.phd.workflow.data.port.IfElsePort;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.LoopPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML writer for workflows (DOM).
 * 
 * @author clc
 *
 */
public class XmlWorkflowWriter {
	
	private Document doc = null;

	/**
	 * Writes the given workflow to the specified file
	 */
	public void write(Workflow workflow, String filePath) {
		if (!filePath.endsWith(".xml"))
			filePath += ".xml";
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    //factory.setNamespaceAware(true);
	    //factory.setValidating(true); 

	    try {
	      File f = new File(filePath);
	      DocumentBuilder builder = factory.newDocumentBuilder();
	      doc = builder.newDocument();
	      
	      writeWorkflow(workflow);
	  
	      // Use a Transformer for output
	      TransformerFactory tFactory = TransformerFactory.newInstance();
	      Transformer transformer = tFactory.newTransformer();

	      DOMSource source = new DOMSource(doc);
	      StreamResult result = new StreamResult(f);
	      result.setSystemId(java.net.URLDecoder.decode(result.getSystemId(), "UTF-8")); //Fix for paths with spaces
	      transformer.transform(source, result); 
	      
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	/**
	 * Adds the root note to the document including all children
	 */
	private void writeWorkflow(Workflow workflow) {
		Element workflowNode = doc.createElement(DefaultXmlNames.ELEMENT_WORKFLOW);
		
		//Name
		workflowNode.setAttribute(DefaultXmlNames.ATTR_NAME, workflow.getName());
		//Version
		addAttrIfNotEmpty(workflowNode, DefaultXmlNames.ATTR_VERSION, workflow.getVersion());
		//Author
		addAttrIfNotEmpty(workflowNode, DefaultXmlNames.ATTR_AUTHOR, workflow.getAuthor());
		//Ontology version
		addAttrIfNotEmpty(workflowNode, DefaultXmlNames.ATTR_ONTOLOGY_VERSION, ""+workflow.getOntologyVersion());
		//Description(s)
		for (int i=0; i< workflow.getDescriptionCount(); i++)
			addDescription(workflowNode, workflow.getDescription(i));
		
		doc.appendChild(workflowNode);
		
		//Data tables
		if (workflow.getDataTables() != null) {
			for (Iterator<DataTable> it = workflow.getDataTables().iterator(); it.hasNext(); ) {
				writeDataTable(it.next(), workflowNode);
			}
		}
		
		//Activities
		writeActivity(workflow.getRootActivity(), workflowNode);
	}
	
	/**
	 * Adds a description element with semantic labels
	 * @param parentNode
	 * @param description
	 */
	private void addDescription(Element parentNode, DescriptionWithLabels description) {
		
		Element descriptionNode = doc.createElement(DefaultXmlNames.ELEMENT_DESCRIPTION_WITH_LABELS);
		
		//Labels
		Collection<LabelGroup> labelGroups = description.getLabels();
		for (Iterator<LabelGroup> it = labelGroups.iterator(); it.hasNext(); ) {
			LabelGroup grp = it.next();
			for (Iterator<Label> labelIt = grp.getLabels().iterator(); labelIt.hasNext(); ) {
				Label label = labelIt.next();
				addLabel(label, descriptionNode);
			}
		}		
		
		addTextNode(descriptionNode, DefaultXmlNames.ELEMENT_TEXT, description.getText());
	}
	
	/**
	 * Adds an activity node to the given parent element
	 * @param activity
	 * @param parentNode
	 */
	private void writeActivity(Activity activity, Element parentNode) {
		if (activity == null || parentNode == null)
			return;
		
		Element activityNode = doc.createElement(getActivityNodeName(activity));
		
		//Base attributes
		// Id
		activityNode.setAttribute(DefaultXmlNames.ATTR_ID, activity.getId());
		// Caption
		activityNode.setAttribute(DefaultXmlNames.ATTR_CAPTION, activity.getCaption());
		// Description
		addTextNode(activityNode, DefaultXmlNames.ELEMENT_DESCRIPTION, activity.getDescription());
		// Local name
		addAttrIfNotEmpty(activityNode, DefaultXmlNames.ATTR_LOCAL_NAME, activity.getLocalName());
		
		//Labels
		Collection<LabelGroup> labelGroups = activity.getLabels();
		for (Iterator<LabelGroup> it = labelGroups.iterator(); it.hasNext(); ) {
			LabelGroup grp = it.next();
			for (Iterator<Label> labelIt = grp.getLabels().iterator(); labelIt.hasNext(); ) {
				Label label = labelIt.next();
				addLabel(label, activityNode);
			}
		}		
		
		//Input ports
		List<InputPort> inputPorts = activity.getInputPorts();
		for (Iterator<InputPort> it = inputPorts.iterator(); it.hasNext(); ) {
			writeDataPort(it.next(), activityNode);
		}
	
		//Output ports
		List<OutputPort> outputPorts = activity.getOutputPorts();
		for (Iterator<OutputPort> it = outputPorts.iterator(); it.hasNext(); ) {
			writeDataPort(it.next(), activityNode);
		}
		
		//Activity type specific stuff
		if (activity.getType().equals(ActivityType.ATOMIC_ACTIVITY))
			writeAtomicActivityData((AtomicActivity)activity, activityNode);
		else if (activity.getType().equals(ActivityType.FOR_LOOP_ACTIVITY))
			writeForLoopActivityData((ForLoopActivity)activity, activityNode);
		else if (activity.getType().equals(ActivityType.DIRECTED_GRAPH_ACTIVITY))
			writeDirectedGraphActivityData((DirectedGraphActivity)activity, activityNode);
		else if (activity.getType().equals(ActivityType.IF_ELSE_ACTIVITY))
			writeIfElseActivityData((IfElseActivity)activity, activityNode);
		
		//Add to parent
		parentNode.appendChild(activityNode);
	}
	
	/**
	 * Adds a data table element to the workflow element
	 * @param dataTable
	 * @param workflowNode
	 */
	private void writeDataTable(DataTable dataTable, Element workflowNode) {
		if (dataTable == null || workflowNode == null)
			return;
		
		Element dataTableNode = doc.createElement(DefaultXmlNames.ELEMENT_DATA_TABLE);
		
		//Attributes
		// Id
		dataTableNode.setAttribute(DefaultXmlNames.ATTR_ID, dataTable.getId());
		// Caption
		dataTableNode.setAttribute(DefaultXmlNames.ATTR_CAPTION, dataTable.getCaption());
		// Description
		addTextNode(dataTableNode, DefaultXmlNames.ELEMENT_DESCRIPTION, dataTable.getDescription());

		//Columns
		List<DataTableColumn> columns = dataTable.getColumns();
		if (columns != null) {
			for (Iterator<DataTableColumn> it = columns.iterator(); it.hasNext(); ) {
				writeDataPort(it.next(), dataTableNode);
			}
		}

		//Add to parent
		workflowNode.appendChild(dataTableNode);
	}

	/**
	 * Writes data that is specific to atomic activities
	 * @param activity
	 * @param activityNode
	 */
	private void writeAtomicActivityData(AtomicActivity activity, Element activityNode) {
		//Attributes
		// Is Abstract
		activityNode.setAttribute(DefaultXmlNames.ATTR_ABSTRACT, ""+activity.isAbstract());
		// Method Name
		addAttrIfNotEmpty(activityNode, DefaultXmlNames.ATTR_METHOD_NAME, activity.getMethodName());
		// Method Version
		addAttrIfNotEmpty(activityNode, DefaultXmlNames.ATTR_METHOD_VERSION, activity.getMethodVersion());
	}

	/**
	 * Writes data that is specific to for loop activities
	 * @param activity
	 * @param activityNode
	 */
	private void writeForLoopActivityData(ForLoopActivity activity, Element activityNode) {
		//Loop ports
		writeDataPort(activity.getLoopStart(), activityNode, DefaultXmlNames.ELEMENT_LOOP_START_PORT);
		writeDataPort(activity.getLoopPosition(), activityNode, DefaultXmlNames.ELEMENT_LOOP_POSITION_PORT);
		writeDataPort(activity.getLoopEnd(), activityNode, DefaultXmlNames.ELEMENT_LOOP_END_PORT);
		writeDataPort(activity.getLoopStep(), activityNode, DefaultXmlNames.ELEMENT_LOOP_STEP_PORT);
		
		//Child activity
		writeActivity(activity.getChildActivity(), activityNode);
	}
	
	/**
	 * Writes data that is specific to directed acyclic graph activities
	 * @param activity
	 * @param activityNode
	 */
	private void writeDirectedGraphActivityData(DirectedGraphActivity activity, Element activityNode) {
		
		//Child activities and corresponding graph nodes
		for (Iterator<ActivityNode> it = activity.getGraphNodes().iterator(); it.hasNext(); ) {
			ActivityNode n = it.next();
			Element vertexNode = doc.createElement(DefaultXmlNames.ELEMENT_VERTEX);
			
			//Attributes
			// Optional
			vertexNode.setAttribute(DefaultXmlNames.ATTR_OPTIONAL, ""+n.isOptional());
			// Min successors
			vertexNode.setAttribute(DefaultXmlNames.ATTR_MIN_SUCCESSORS, ""+n.getMinSuccessors());
			// Pos X
			vertexNode.setAttribute(DefaultXmlNames.ATTR_X, ""+n.getPosX());
			// Pos Y
			vertexNode.setAttribute(DefaultXmlNames.ATTR_Y, ""+n.getPosY());
			// Width
			vertexNode.setAttribute(DefaultXmlNames.ATTR_WIDTH, ""+n.getWidth());
			// Height
			vertexNode.setAttribute(DefaultXmlNames.ATTR_HEIGHT, ""+n.getHeight());
			
			//Activity
			writeActivity(n.getActivity(), vertexNode);
			
			//Predecessors
			for (Iterator<ActivityNode> itPre = n.getPredecessors().iterator(); itPre.hasNext(); ) {
				ActivityNode nPre = itPre.next();
				Element preNode = doc.createElement(DefaultXmlNames.ELEMENT_PREDECESSOR);
				
				//Id
				preNode.setAttribute(DefaultXmlNames.ATTR_ID, nPre.getActivity().getId());
				
				//Add to parent
				vertexNode.appendChild(preNode);
			}
			
			//Add to parent
			activityNode.appendChild(vertexNode);
		}
	}
	
	/**
	 * Writes data that is specific to if-else activities
	 * @param activity
	 * @param activityNode
	 */
	private void writeIfElseActivityData(IfElseActivity activity, Element activityNode) {
		List<IfBranch> branches = activity.getBranches();
		if (branches == null)
			return;
		
		for (Iterator<IfBranch> it = branches.iterator(); it.hasNext(); ) {

			IfBranch branch = it.next();
			Element branchNode = doc.createElement(DefaultXmlNames.ELEMENT_BRANCH);
			
			//Child activity
			writeActivity(branch.getActivity(), branchNode);
			
			//Condition
			writeIfElseCondition(branch.getCondition(), branchNode);

			activityNode.appendChild(branchNode);
		}
	}
	
	/**
	 * Writes an if-else condition and it's children. Delegates to specialised methods.
	 */
	private void writeIfElseCondition(IfCondition condition, Element parentNode) {
		if (condition == null)
			return;
		
		//NOT
		if (condition instanceof NotCondition)
			writeNotCondition((NotCondition)condition, parentNode);
		//AND / OR
		else if (condition instanceof CombinedCondition)
			writeCombinedCondition((CombinedCondition)condition, parentNode);
		//Input port
		else if (condition instanceof InputPortCondition)
			writeInputPortCondition((InputPortCondition)condition, parentNode);
		//Comparison condition
		else if (condition instanceof ComparisonCondition)
			writeComparisonCondition((ComparisonCondition)condition, parentNode);
	}

	/**
	 * Writes a NOT if-else condition
	 */
	private void writeNotCondition(NotCondition condition, Element parentNode) {
		Element conditionNode = doc.createElement(DefaultXmlNames.ELEMENT_NOT_CONDITION);
		
		//Child condition
		writeIfElseCondition(condition.getChildCondition(), conditionNode);
		
		parentNode.appendChild(conditionNode);
	}
	
	/**
	 * Writes an AND or OR if-else condition
	 */
	private void writeCombinedCondition(CombinedCondition condition, Element parentNode) {
		Element conditionNode = doc.createElement(DefaultXmlNames.ELEMENT_COMBINED_CONDITION);
		
		//Operator
		conditionNode.setAttribute(DefaultXmlNames.ATTR_OP, condition.usesAndOperator() ? "AND" : "OR");
		
		//Child conditions
		if (condition.getChildConditions() != null) {
			for (Iterator<IfCondition> it=condition.getChildConditions().iterator(); it.hasNext();)
				writeIfElseCondition(it.next(), conditionNode);
		}
		
		parentNode.appendChild(conditionNode);
	}
	
	/**
	 * Writes an if-else condition that uses an input port
	 */
	private void writeInputPortCondition(InputPortCondition condition, Element parentNode) {
		Element conditionNode = doc.createElement(DefaultXmlNames.ELEMENT_INPUT_PORT_CONDITION);
		
		//Port
		if (condition.getInputPort() != null) {
			writeDataPort(condition.getInputPort(), conditionNode);
		}
		
		parentNode.appendChild(conditionNode);
	}
	
	/**
	 * Writes an if-else condition that performs a comparison operation
	 */
	private void writeComparisonCondition(ComparisonCondition condition, Element parentNode) {
		Element conditionNode = doc.createElement(DefaultXmlNames.ELEMENT_COMPARISON_CONDITION);
		
		//Operator
		conditionNode.setAttribute(DefaultXmlNames.ATTR_OP, condition.getOperator().toString());
		
		//Ports
		if (condition.getLeftOperand() != null) 
			writeDataPort(condition.getLeftOperand(), conditionNode);
		if (condition.getRightOperand() != null) 
			writeDataPort(condition.getRightOperand(), conditionNode);		
		
		parentNode.appendChild(conditionNode);
	}
	
	/**
	 * Adds a data port node to the given parent element
	 * @param port
	 * @param parentNode
	 */
	private Element writeDataPort(DataPort port, Element parentNode) {
		return writeDataPort(port, parentNode, getDataPortNodeName(port));
	}
	
	/**
	 * Adds a data port node to the given parent element
	 * @param port
	 * @param parentNode
	 * @param elementName
	 */
	private Element writeDataPort(DataPort port, Element parentNode, String elementName) {
		if (port == null || parentNode == null)
			return null;
		
		Element portNode = doc.createElement(elementName);
		
		//Base attributes
		// Id
		portNode.setAttribute(DefaultXmlNames.ATTR_ID, port.getId());
		
		//Data collection position provider
		DataPort collPosProvider = port.getCollectionPositionProvider();
		if (collPosProvider != null) {
			portNode.setAttribute(DefaultXmlNames.ATTR_POSITION_SOURCE, collPosProvider.getId());
		}
		
		//Input port specific stuff
		if (port instanceof InputPort) {
			InputPort inputPort = (InputPort)port;
			DataPort source = inputPort.getSource();
			if (source != null) {
				portNode.setAttribute(DefaultXmlNames.ATTR_SOURCE, source.getId());
			}
			
			//Allowed types
			String allowedTypes = "";
			// Concatenate (separated by | )
			for (Iterator<String> it = inputPort.getAllowedTypes().iterator(); it.hasNext(); ) {
				if (!allowedTypes.isEmpty())
					allowedTypes += "|";
				allowedTypes += it.next().trim();
			}
			portNode.setAttribute(DefaultXmlNames.ATTR_ALLOWED_TYPES, allowedTypes);

		}
		
		//Output port specific stuff
		if (port instanceof OutputPort) {
			OutputPort outputPort = (OutputPort)port;
			// Type
			portNode.setAttribute(DefaultXmlNames.ATTR_TYPE, outputPort.getType());

			//Forwarded ports (CC 30/08/16: changed from attribute to child node)
			if (outputPort.getForwardedPorts() != null) {
				for (Iterator<OutputPort> it = outputPort.getForwardedPorts().iterator(); it.hasNext(); ) {
					OutputPort forwaredPort = it.next();
					Element sourceNode = doc.createElement(DefaultXmlNames.ELEMENT_SOURCE);
					sourceNode.setAttribute(DefaultXmlNames.ATTR_ID, forwaredPort.getId());
					portNode.appendChild(sourceNode);
				}
			}			
		}		
		
		//Data object
		writeDataObject(port.getDataObject(), portNode);
		
		//Add to parent
		parentNode.appendChild(portNode);
		
		return portNode;
	}
	
	/**
	 * Adds data object data to the given node
	 * @param dataObj
	 * @param node
	 */
	private void writeDataObject(DataObject dataObj, Element parentNode) {
		
		Element objNode = doc.createElement(dataObj instanceof DataCollection 
												? DefaultXmlNames.ELEMENT_DATA_COLLECTION
												: DefaultXmlNames.ELEMENT_SINGLE_DATA_OBJECT);
		//Base attributes
		// Caption
		objNode.setAttribute(DefaultXmlNames.ATTR_CAPTION, dataObj.getCaption());
		// Description
		addTextNode(objNode, DefaultXmlNames.ELEMENT_DESCRIPTION, dataObj.getDescription());

		//Labels
		Collection<LabelGroup> labelGroups = dataObj.getLabels();
		for (Iterator<LabelGroup> it = labelGroups.iterator(); it.hasNext(); ) {
			LabelGroup grp = it.next();
			for (Iterator<Label> labelIt = grp.getLabels().iterator(); labelIt.hasNext(); ) {
				Label label = labelIt.next();
				addLabel(label, objNode);
			}
		}
		
		//Collection specific stuff
		if (dataObj instanceof DataCollection) {
			writeDataCollectionData((DataCollection)dataObj, objNode);
		}
		//Single data object specific stuff
		else if (dataObj instanceof SingleDataObject) {
			writeSingleDataObjectData((SingleDataObject)dataObj, objNode);
		}
		
		//Add to parent
		parentNode.appendChild(objNode);

	}
	
	private void writeDataCollectionData(DataCollection coll, Element node) {
		//Child data objects
		for (int i=0; i<coll.getSize(); i++) {
			writeDataObject(coll.getDataItem(i), node);
		}
	}

	private void writeSingleDataObjectData(SingleDataObject obj, Element node) {
		//Value
		addAttrIfNotEmpty(node, DefaultXmlNames.ATTR_VALUE, obj.getValue());
	}

	/**
	 * Adds a label element to the given node
	 * @param label
	 * @param node
	 */
	private void addLabel(Label label, Element parentNode) {
		Element labelNode = doc.createElement(DefaultXmlNames.ELEMENT_LABEL);
		
		//Type
		labelNode.setAttribute(DefaultXmlNames.ATTR_TYPE, label.getType().getId());
		//Comments
		addTextNode(labelNode, DefaultXmlNames.ELEMENT_COMMENTS, label.getComments());
		
		//Add to parent
		parentNode.appendChild(labelNode);
	}
	
	/**
	 * Returns the correct XML element name for the given activity 
	 */
	private String getActivityNodeName(Activity activity) {
		if (activity.getType().equals(ActivityType.ATOMIC_ACTIVITY))
			return DefaultXmlNames.ELEMENT_ATOMIC_ACTIVITY;
		else if (activity.getType().equals(ActivityType.FOR_LOOP_ACTIVITY))
			return DefaultXmlNames.ELEMENT_FOR_LOOP_ACTIVITY;
		else if (activity.getType().equals(ActivityType.DIRECTED_GRAPH_ACTIVITY))
			return DefaultXmlNames.ELEMENT_DIRECTED_GRAPH_ACTIVITY;
		else if (activity.getType().equals(ActivityType.IF_ELSE_ACTIVITY))
			return DefaultXmlNames.ELEMENT_IFELSE_ACTIVITY;
		throw new IllegalArgumentException("Activity type not supported: "+activity.getType());
	}
	
	/**
	 * Returns the correct XML element name for the given data port
	 */
	private String getDataPortNodeName(DataPort port) {
		if (port instanceof LoopPort)
			return DefaultXmlNames.ELEMENT_LOOP_PORT;
		else if (port instanceof IfElsePort)
			return DefaultXmlNames.ELEMENT_IFELSE_PORT;
		else if (port instanceof InputPort)
			return DefaultXmlNames.ELEMENT_INPUT_PORT;
		else if (port instanceof DataTableColumn)
			return DefaultXmlNames.ELEMENT_TABLE_COLUMN;
		else if (port instanceof OutputPort)
			return DefaultXmlNames.ELEMENT_OUTPUT_PORT;
		throw new IllegalArgumentException("Data port type not supported: "+port.getClass().getName());
	}
	
	/**
	 * Helper method to add a text attribute
	 * @param el Parent element
	 * @param name Attribute name
	 * @param value Attribute value
	 */
	private void addAttrIfNotEmpty(Element el, String name, String value) {
		if (value != null && !value.isEmpty())
			el.setAttribute(name, value);
	}
	
	/**
	 * Helper method to add a child text element (only if value not empty)
	 * @param el Parent element
	 * @param name Node name
	 * @param value Text
	 */
	private void addTextNode(Element el, String name, String value) {
		if (value == null || value.isEmpty())
			return;
		
		Element textNode = doc.createElement(name);
		textNode.setTextContent(value);
		el.appendChild(textNode);
	}
}
