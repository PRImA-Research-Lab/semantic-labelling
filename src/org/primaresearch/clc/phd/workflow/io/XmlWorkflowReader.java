package org.primaresearch.clc.phd.workflow.io;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.primaresearch.clc.phd.Pair;
import org.primaresearch.clc.phd.ontology.Ontology;
import org.primaresearch.clc.phd.ontology.label.Label;
import org.primaresearch.clc.phd.ontology.label.LabelGroup.TooManyLabelsInGroupException;
import org.primaresearch.clc.phd.ontology.label.LabelType;
import org.primaresearch.clc.phd.ontology.migration.MigrationEngine;
import org.primaresearch.clc.phd.workflow.DescriptionWithLabels;
import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.WorkflowImpl;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityFactory;
import org.primaresearch.clc.phd.workflow.activity.ActivityType;
import org.primaresearch.clc.phd.workflow.activity.AtomicActivity;
import org.primaresearch.clc.phd.workflow.activity.DirectedGraphActivity;
import org.primaresearch.clc.phd.workflow.activity.DirectedGraphActivity.ActivityNode;
import org.primaresearch.clc.phd.workflow.activity.ifelse.CombinedCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.ComparisonCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.ComparisonCondition.ComparisonConditionOperator;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity.IfBranch;
import org.primaresearch.clc.phd.workflow.activity.ifelse.InputPortCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.NotCondition;
import org.primaresearch.clc.phd.workflow.activity.ForLoopActivity;
import org.primaresearch.clc.phd.workflow.data.DataCollection;
import org.primaresearch.clc.phd.workflow.data.DataObject;
import org.primaresearch.clc.phd.workflow.data.DataObjectFactory;
import org.primaresearch.clc.phd.workflow.data.DataTable;
import org.primaresearch.clc.phd.workflow.data.DefaultDataTable;
import org.primaresearch.clc.phd.workflow.data.SingleDataObject;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.DataTableColumn;
import org.primaresearch.clc.phd.workflow.data.port.IfElsePort;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.LoopPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Reader for workflow XML files (SAX)
 * 
 * @author clc
 *
 */
public class XmlWorkflowReader {

	private List<String> migrationMessages = null;
	
	public XmlWorkflowReader() {
	}
	
	public Workflow read(String filePath) {
	    try {
	    	migrationMessages = null;
	    	
	    	// Obtain a new instance of a SAXParserFactory.
	    	SAXParserFactory factory = SAXParserFactory.newInstance();
	    	// Specifies that the parser produced by this code will provide support for XML namespaces.
	    	factory.setNamespaceAware(true);
    		factory.setValidating(false);
	    	
    		SAXParser typeLabelParser = factory.newSAXParser();
    		WorkflowSaxHandler handler = new WorkflowSaxHandler();
    		
    		InputStream inputStream = new FileInputStream(filePath);
    		
    		typeLabelParser.parse(inputStream, handler);
    		
    		inputStream.close();
    		
    		migrationMessages = handler.getMigrationEngine().getMessages();
    		
    		return handler.getWorkflow();
	    } catch (Throwable t) {
	    	t.printStackTrace();
	    }
	    return null;
	}
	
	/**	
	 * Migration messages of last call of <code>read()</code> (which labels were changed and which removed during ontology migration)
	 */
	public List<String> getMigrationMessages() {
		return migrationMessages;
	}



	/**
	 * SAX handler implementation to parse a workflow XML file.
	 * 
	 * @author clc
	 */
	private static class WorkflowSaxHandler extends DefaultHandler {
		private Workflow workflow = null;
		private MigrationEngine migrationEngine = new MigrationEngine();
		private StringBuffer currentTextContent = null;
		private Activity currentActivity = null;
		private DataPort currentPort = null;
		private Label currentLabel = null;
		private DataCollection currentDataCollection = null;
		private ActivityNode currentDirectGraphNode = null;
		private IfCondition currentCondition = null;
		private DataTable currentDataTable = null;
		private DescriptionWithLabels currentDescriptionWithLabels;
		private Stack<IfCondition> conditionStack = new Stack<IfCondition>();
		private ActivityFactory activityFactory;
		private DataObjectFactory dataObjectFactory;
		/** Map [data port ID, data port] */
		private Map<String, DataPort> dataPortRegister = new HashMap<String, DataPort>();
		/** Map [activity ID, directed graph node] */
		private Map<String, ActivityNode> graphNodeRegister = new HashMap<String, ActivityNode>();
		/** Map [data port, source attribute] */
		private List<Pair<DataPort, String>> dataPortSourcesToResolve = new ArrayList<Pair<DataPort, String>>();
		/** Map [data port, position source attribute] */
		private Map<DataPort, String> dataPortPositionSourcesToResolve = new HashMap<DataPort, String>();
		/** Map [graph node, list of predecessor activity IDs] */
		private Map<ActivityNode, List<String>> graphPredecessorsToResolve = new HashMap<ActivityNode, List<String>>();
		private IdGenerator idRegister;

		
		WorkflowSaxHandler() {
		}

		public Workflow getWorkflow() {
			return workflow;
		}
		
		public MigrationEngine getMigrationEngine() {
			return migrationEngine;
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
			
		    if (DefaultXmlNames.ELEMENT_WORKFLOW.equals(localName)){
		    	handleWorkflowElement(atts);
		    } 
		    else if (DefaultXmlNames.ELEMENT_DESCRIPTION.equals(localName)
		    		|| DefaultXmlNames.ELEMENT_COMMENTS.equals(localName)
	    			|| DefaultXmlNames.ELEMENT_TEXT.equals(localName))
		    	currentTextContent = new StringBuffer();
		    else if (DefaultXmlNames.ELEMENT_DESCRIPTION_WITH_LABELS.equals(localName)) {
		    	handleDescriptionWithLabels(atts);
		    }
		    else if (isActivityNode(localName)) {
		    	handleActivityElement(localName, atts);
		    }
		    else if (DefaultXmlNames.ELEMENT_INPUT_PORT.equals(localName)) {
		    	handleDataPort(dataObjectFactory.createInputPort(currentActivity), atts);
		    	currentActivity.getInputPorts().add((InputPort)currentPort);
		    }
		    else if (DefaultXmlNames.ELEMENT_IFELSE_PORT.equals(localName)) {
		    	if (currentCondition != null) {
		    		if (currentCondition instanceof InputPortCondition) {
		    			handleDataPort(dataObjectFactory.createIfElseConditionPort(currentActivity), atts);
		    			((InputPortCondition)currentCondition).setInputPort((IfElsePort)currentPort);
		    		}
		    		else if (currentCondition instanceof ComparisonCondition) {
		    			handleDataPort(dataObjectFactory.createIfElseComparisonPort(currentActivity), atts);
		    			ComparisonCondition condition = (ComparisonCondition)currentCondition;
		    			if (condition.getLeftOperand() == null)
		    				condition.setLeftOperand((IfElsePort)currentPort);
		    			else
		    				condition.setRightOperand((IfElsePort)currentPort);
		    		}
		    	}
		    }
		    else if (DefaultXmlNames.ELEMENT_TABLE_COLUMN.equals(localName)) {
		    	if (currentDataTable != null) {
	    			handleDataPort(dataObjectFactory.createDataTableColumn(), atts);
	    			currentDataTable.addColumn((DataTableColumn)currentPort);
	    		}
		    }
		    else if (DefaultXmlNames.ELEMENT_OUTPUT_PORT.equals(localName)) {
		    	handleDataPort(dataObjectFactory.createOutputPort(currentActivity), atts);
		    	currentActivity.getOutputPorts().add((OutputPort)currentPort);
		    }
		    else if (DefaultXmlNames.ELEMENT_LOOP_START_PORT.equals(localName)) {
		    	handleDataPort(dataObjectFactory.createLoopPort(currentActivity), atts);
		    	((ForLoopActivity)currentActivity).setLoopStart((LoopPort)currentPort);
		    }
		    else if (DefaultXmlNames.ELEMENT_LOOP_POSITION_PORT.equals(localName)) {
		    	handleDataPort(dataObjectFactory.createLoopPort(currentActivity), atts);
		    	((ForLoopActivity)currentActivity).setLoopPosition((LoopPort)currentPort);
		    }
		    else if (DefaultXmlNames.ELEMENT_LOOP_END_PORT.equals(localName)) {
		    	handleDataPort(dataObjectFactory.createLoopPort(currentActivity), atts);
		    	((ForLoopActivity)currentActivity).setLoopEnd((LoopPort)currentPort);
		    }
		    else if (DefaultXmlNames.ELEMENT_LOOP_STEP_PORT.equals(localName)) {
		    	handleDataPort(dataObjectFactory.createLoopPort(currentActivity), atts);
		    	((ForLoopActivity)currentActivity).setLoopStep((LoopPort)currentPort);
		    }
		    else if (DefaultXmlNames.ELEMENT_SOURCE.equals(localName)) {
		    	handleDataPortSource(atts);
		    }
		    else if (DefaultXmlNames.ELEMENT_LABEL.equals(localName)) {
		    	handleLabel(atts);
		    }
		    else if (DefaultXmlNames.ELEMENT_SINGLE_DATA_OBJECT.equals(localName)) {
		    	handleSingleDataObject(atts);
		    }
		    else if (DefaultXmlNames.ELEMENT_DATA_COLLECTION.equals(localName)) {
		    	handleDataCollection(atts);
		    }
		    else if (DefaultXmlNames.ELEMENT_VERTEX.equals(localName)) {
		    	handleDirectedGraphNode(atts);
		    }
		    else if (DefaultXmlNames.ELEMENT_PREDECESSOR.equals(localName)) {
		    	handleGraphNodePredecessor(atts);
		    }
		    else if (DefaultXmlNames.ELEMENT_BRANCH.equals(localName)) {
		    	handleIfelseBranchNode(atts);
		    }
		    else if (DefaultXmlNames.ELEMENT_NOT_CONDITION.equals(localName)
		    		|| DefaultXmlNames.ELEMENT_COMBINED_CONDITION.equals(localName)
		    		|| DefaultXmlNames.ELEMENT_INPUT_PORT_CONDITION.equals(localName)
		    		|| DefaultXmlNames.ELEMENT_COMPARISON_CONDITION.equals(localName)
		    		) {
		    	handleIfElseConditionNode(localName, atts);
		    }
		    else if (DefaultXmlNames.ELEMENT_DATA_TABLE.equals(localName)) {
		    	handleDataTableNode(atts);
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
			
		    if (DefaultXmlNames.ELEMENT_WORKFLOW.equals(localName)){
		    	resolveDataPortSources();
		    	resolveGraphNodePredecessors();
		    }
		    else if (DefaultXmlNames.ELEMENT_DESCRIPTION.equals(localName)) {
		    	//Check what is the target object
		    	if (currentPort != null)
		    		currentPort.getDataObject().setDescription(currentTextContent.toString());
		    	else if (currentDataTable != null)
		    		currentDataTable.setDescription(currentTextContent.toString());
		    	else if (currentActivity != null)
		    		currentActivity.setDescription(currentTextContent.toString());
		    	else
		    		workflow.setDescription(0, currentTextContent.toString()); //Backwards compatibility (now using DescriptionWithLabels)
				currentTextContent = null;
		    }
		    else if (DefaultXmlNames.ELEMENT_TEXT.equals(localName)) {
		    	if (currentDescriptionWithLabels != null)
		    		currentDescriptionWithLabels.setDescription(currentTextContent.toString());
		    	currentTextContent = null;
		    }
		    else if (DefaultXmlNames.ELEMENT_DESCRIPTION_WITH_LABELS.equals(localName)) {
		    	currentDescriptionWithLabels = null;
		    }
		    else if (DefaultXmlNames.ELEMENT_COMMENTS.equals(localName)) {
		    	if (currentLabel != null)
		    		currentLabel.setComments(currentTextContent.toString());
		    }
		    else if (isActivityNode(localName)) {
		    	currentActivity = currentActivity.getParentActivity();
		    }
		    else if (DefaultXmlNames.ELEMENT_INPUT_PORT.equals(localName)
		    		|| DefaultXmlNames.ELEMENT_OUTPUT_PORT.equals(localName)
		    		|| DefaultXmlNames.ELEMENT_LOOP_START_PORT.equals(localName)
		    		|| DefaultXmlNames.ELEMENT_LOOP_POSITION_PORT.equals(localName)
		    		|| DefaultXmlNames.ELEMENT_LOOP_END_PORT.equals(localName)
		    		|| DefaultXmlNames.ELEMENT_LOOP_STEP_PORT.equals(localName)
		    		) {
		    	currentPort = null;
		    }
		    else if (DefaultXmlNames.ELEMENT_LABEL.equals(localName)) {
		    	currentLabel = null;
		    }
		    else if (DefaultXmlNames.ELEMENT_DATA_COLLECTION.equals(localName)) {
		    	currentDataCollection = null;
		    }
		    else if (DefaultXmlNames.ELEMENT_VERTEX.equals(localName)) {
		    	currentDirectGraphNode = null;
		    }
		    else if (DefaultXmlNames.ELEMENT_BRANCH.equals(localName)) {
		    	currentCondition = null;
		    	conditionStack.clear();
		    }
		    else if (DefaultXmlNames.ELEMENT_NOT_CONDITION.equals(localName)
		    		|| DefaultXmlNames.ELEMENT_COMBINED_CONDITION.equals(localName)
		    		|| DefaultXmlNames.ELEMENT_INPUT_PORT_CONDITION.equals(localName)
		    		|| DefaultXmlNames.ELEMENT_COMPARISON_CONDITION.equals(localName)
		    		) {
		    	if (!conditionStack.isEmpty())
		    		currentCondition = conditionStack.pop();
		    	else
		    		currentCondition = null;
		    }
		    else if (DefaultXmlNames.ELEMENT_DATA_TABLE.equals(localName)) {
		    	currentDataTable = null;
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

			String strValue = new String(ch, start, length);
			
			//Text might be parsed bit by bit, so we have to accumulate until a closing tag is found.
			if (currentTextContent != null) {
				currentTextContent.append(strValue);
			}
		}
		
		/**
		 * A description element that has labels and text content
		 * @param atts
		 */
		private void handleDescriptionWithLabels(Attributes atts) {
			currentDescriptionWithLabels = workflow.addDescription("");
		}
		
		/**
		 * Reads the attributes of the Workflow element. 
		 */
		private void handleWorkflowElement(Attributes atts) {
			//Name
			String name = "Unknown";
			int i;
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_NAME)) >= 0) {
				name = atts.getValue(i);
			}

			//Create workflow
			workflow = new WorkflowImpl(name);
			idRegister = workflow.getIdRegister();
			activityFactory = new ActivityFactory(idRegister);
			dataObjectFactory = new DataObjectFactory(idRegister);
			
			//Other attributes
			// Version
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_VERSION)) >= 0) {
				workflow.setVersion(atts.getValue(i));
			}
			// Author
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_AUTHOR)) >= 0) {
				workflow.setAuthor(atts.getValue(i));
			}
			//Ontology version
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_ONTOLOGY_VERSION)) >= 0) {
				workflow.setOntologyVersion(Integer.parseInt(atts.getValue(i)));
			}
		}
		
		/**
		 * Checks if the given node name belongs an activity element.
		 */
		private boolean isActivityNode(String name) {
			return 		name.equals(DefaultXmlNames.ELEMENT_ATOMIC_ACTIVITY)
					||	name.equals(DefaultXmlNames.ELEMENT_FOR_LOOP_ACTIVITY)
					||	name.equals(DefaultXmlNames.ELEMENT_DIRECTED_GRAPH_ACTIVITY)
					||	name.equals(DefaultXmlNames.ELEMENT_IFELSE_ACTIVITY);
		}
		
		/**
		 * Reads the base attributes of an activity element. 
		 */
		private void handleActivityElement(String nodeName, Attributes atts) {
			//Type
			ActivityType type = getActivityNode(nodeName);
			
			//Id
			String id = "Unknown";
			int i;
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_ID)) >= 0) {
				id = atts.getValue(i);
			}
			
			//Create activity
			currentActivity = activityFactory.createActivity(currentActivity, type, id);
			
			//Base attributes
			// Caption
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_CAPTION)) >= 0) {
				currentActivity.setCaption(atts.getValue(i));
			}
			// Local name
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_LOCAL_NAME)) >= 0) {
				currentActivity.setLocalName(atts.getValue(i));
			}
			
			//Add to parent
			Activity parentActivity = currentActivity.getParentActivity();
			// Root activity?
			if (parentActivity == null)
				workflow.setRootActivity(currentActivity);
			// Parent is for-loop
			else if (parentActivity.getType().equals(ActivityType.FOR_LOOP_ACTIVITY))
				((ForLoopActivity)parentActivity).setChildActivity(currentActivity);
			// Parent is Directed acyclic graph
			else if (parentActivity.getType().equals(ActivityType.DIRECTED_GRAPH_ACTIVITY)) {
				currentDirectGraphNode.setActivity(currentActivity);
				if (graphNodeRegister.containsKey(id))
					System.err.println("Graph node ID conflict: "+id);
				graphNodeRegister.put(id, currentDirectGraphNode);
			}
			// Parent is if-else activity
			else if (parentActivity.getType().equals(ActivityType.IF_ELSE_ACTIVITY)) {
				//Get last branch
				IfBranch branch = ((IfElseActivity)parentActivity).getBranches().get(((IfElseActivity)parentActivity).getBranches().size()-1);
				branch.setActivity(currentActivity);
			}
			
			//Specialised handlers
			if (currentActivity.getType().equals(ActivityType.ATOMIC_ACTIVITY))
				handleAtomicActivity(atts);
			else if (currentActivity.getType().equals(ActivityType.FOR_LOOP_ACTIVITY))
				handleForLoopActivity(atts);
			else if (currentActivity.getType().equals(ActivityType.DIRECTED_GRAPH_ACTIVITY))
				handleDirectedGraphActivity(atts);
			else if (currentActivity.getType().equals(ActivityType.IF_ELSE_ACTIVITY))
				handleIfElseActivity(atts);
		}
		
		/**
		 * Reads specialised attributes of an atomic activity
		 * @param atts
		 */
		private void handleAtomicActivity(Attributes atts) {
			AtomicActivity activity = (AtomicActivity)currentActivity;
			
			//Abstract
			int i;
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_ABSTRACT)) >= 0) {
				activity.setAbstract(Boolean.parseBoolean(atts.getValue(i)));
			}
			//Method name
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_METHOD_NAME)) >= 0) {
				activity.setMethodName(atts.getValue(i));
			}			
			//Method version
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_METHOD_VERSION)) >= 0) {
				activity.setMethodVersion(atts.getValue(i));
			}			
		}
		
		/**
		 * Reads specialised attributes of a for loop activity
		 * @param atts
		 */
		private void handleForLoopActivity(Attributes atts) {
			//Nothing to do (for now)
		}
		
		/**
		 * Reads specialised attributes of a directed graph activity
		 * @param atts
		 */
		private void handleDirectedGraphActivity(Attributes atts) {
			//Nothing to do (for now). The graph nodes have their own handler method.
		}

		/**
		 * Reads specialised attributes of if-else an activity
		 * @param atts
		 */
		private void handleIfElseActivity(Attributes atts) {
			//Nothing to do (for now). The if-else branches have their own handler method.
		}

		/**
		 * Checks if the given node name belongs an activity element.
		 */
		private ActivityType getActivityNode(String nodeName) {
			if (nodeName.equals(DefaultXmlNames.ELEMENT_ATOMIC_ACTIVITY))
				return ActivityType.ATOMIC_ACTIVITY;
			if (nodeName.equals(DefaultXmlNames.ELEMENT_FOR_LOOP_ACTIVITY))
				return ActivityType.FOR_LOOP_ACTIVITY;
			if (nodeName.equals(DefaultXmlNames.ELEMENT_DIRECTED_GRAPH_ACTIVITY))
				return ActivityType.DIRECTED_GRAPH_ACTIVITY;
			if (nodeName.equals(DefaultXmlNames.ELEMENT_IFELSE_ACTIVITY))
				return ActivityType.IF_ELSE_ACTIVITY;
			throw new IllegalArgumentException("Activity type not supported: "+nodeName);
		}
		
		/**
		 * Reads the base attributes of a data port element. 
		 */
		private void handleDataPort(DataPort port, Attributes atts) {
	    	currentPort = port;
			
			//Id
			int i;
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_ID)) >= 0) {
				port.setId(atts.getValue(i));
			}
			
			//Source
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_SOURCE)) >= 0) {
				dataPortSourcesToResolve.add(new Pair<DataPort,String>(port, atts.getValue(i)));
			}
			
			//Position source
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_POSITION_SOURCE)) >= 0) {
				dataPortPositionSourcesToResolve.put(port, atts.getValue(i));
			}
		
			//Add to port register
			if (dataPortRegister.containsKey(port.getId()))
				System.err.println("Data port ID conflict: "+port.getId());
	    	dataPortRegister.put(port.getId(), port);
	    	
	    	//Type(s)
	    	if (port instanceof OutputPort) {
				if ((i = atts.getIndex(DefaultXmlNames.ATTR_TYPE)) >= 0) {
					((OutputPort)port).setType(atts.getValue(i));
				}
	    	}
	    	if (port instanceof InputPort) {
				if ((i = atts.getIndex(DefaultXmlNames.ATTR_ALLOWED_TYPES)) >= 0) {
					String types = atts.getValue(i);
					// |-separated list
					String split[] = types.split(Pattern.quote("|"));
					for (int t=0; t<split.length; t++) {
						String dataType = split[t].trim();
						if (!dataType.isEmpty())
							((InputPort)port).addAllowedType(dataType);
					}
				}
	    	}
	    }
		
		/**
		 * Reads a data port source (CC 30/08/2016: Changed from attribute to child elements)
		 */
		private void handleDataPortSource(Attributes atts) {
			int i;
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_ID)) >= 0) {
				dataPortSourcesToResolve.add(new Pair<DataPort,String>(currentPort, atts.getValue(i)));
			}			
		}
		
		/**
		 * Reads the attributes of a type label element. 
		 */
		private void handleLabel(Attributes atts) {
			//Type
			String typeId = null;
			int i;
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_TYPE)) >= 0) {
				typeId = atts.getValue(i);
			}
			
			List<LabelType> types = getLabelTypes(typeId, currentPort == null);
			
			if (types == null || types.isEmpty())
				return;
			
			//There could be multiple types in case we migrated from an older ontology version (one-to-many relation)
			for (Iterator<LabelType> it = types.iterator(); it.hasNext(); ) {
				LabelType type = it.next();
				
				if (type == null)
					continue;
				
				try {
					if (currentPort != null)
						currentLabel = currentPort.getDataObject().addLabel(type);
					else if (currentActivity != null)
						currentLabel = currentActivity.addLabel(type);
				} catch (TooManyLabelsInGroupException e) {
					e.printStackTrace();
				}
			}
		}
		
		private List<LabelType> getLabelTypes(String typeId, boolean labelForActivity) {
			//Migrate or return directly
			if (workflow.getOntologyVersion() < Ontology.getInstance().getVersion()) { //From old ontology version
				//Migrate
				return migrationEngine.getMigratedLabelTypes(typeId, workflow.getOntologyVersion(), labelForActivity);
			}

			//Labels from latest ontology
			List<LabelType> ret = new LinkedList<LabelType>();
			ret.add(Ontology.getInstance().getLabelType(typeId));
			return ret;
		}
		
		private void handleDataObject(DataObject obj, Attributes atts) {
			int i;
			
			// Caption
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_CAPTION)) >= 0) {
				obj.setCaption(atts.getValue(i));
			}

		}
		/**
		 * Reads the attributes of a single data object. 
		 */
		private void handleSingleDataObject(Attributes atts) {
			if (currentPort == null)
				return;
			
			SingleDataObject obj = dataObjectFactory.createSingleDataObject();
			
			handleDataObject(obj, atts);
			
			//Value
			int i;
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_VALUE)) >= 0) {
				obj.setValue(atts.getValue(i));
			}
			
			//Add to data collection or port?
			if (currentDataCollection != null)
				currentDataCollection.addDataItem(obj);
			else
				currentPort.setDataObject(obj);
		}
		
		/**
		 * Reads the attributes of a single data object. 
		 */
		private void handleDataCollection(Attributes atts) {
			if (currentPort == null)
				return;
			
			currentDataCollection = dataObjectFactory.createDataCollection();
			currentPort.setDataObject(currentDataCollection);
			
			handleDataObject(currentDataCollection, atts);
		}
		
		/**
		 * Finds and sets the actual data port objects for the attributes 'source' and 'positionSource'
		 * which represent IDs of data ports.
		 * (Has to be done at the very end of loading the XML.)
		 */
		private void resolveDataPortSources() {
			//Sources
			for (Iterator<Pair<DataPort, String>> it = dataPortSourcesToResolve.iterator(); it.hasNext(); ) {
				Pair<DataPort, String> pair = it.next();
				DataPort target = pair.left;
				String sourceId = pair.right;
				//Find source port
				DataPort sourcePort = dataPortRegister.get(sourceId);
				if (sourcePort != null) {
					if (target instanceof InputPort)
						((InputPort)target).setSource(sourcePort);
					else if (target instanceof OutputPort)
						((OutputPort)target).addForwardedPort((OutputPort)sourcePort);
				}
				else
					; //TODO
			}
			
			//Position sources
			for (Iterator<DataPort> it = dataPortPositionSourcesToResolve.keySet().iterator(); it.hasNext(); ) {
				DataPort target = it.next();
				String sourceId = dataPortPositionSourcesToResolve.get(target);
				//Find position source port
				DataPort positionSourcePort = dataPortRegister.get(sourceId);
				if (positionSourcePort != null)
					target.setCollectionPositionProvider(positionSourcePort);
				else
					; //TODO
			}
		}
		
		/**
		 * Handles a graph node of a directed acyclic graph activity
		 */
		private void handleDirectedGraphNode(Attributes atts) {
			currentDirectGraphNode = new ActivityNode();
			((DirectedGraphActivity)currentActivity).addNode(currentDirectGraphNode);
			
			//Attributes
			// Optional
			int i;
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_OPTIONAL)) >= 0) {
				currentDirectGraphNode.setOptional(Boolean.parseBoolean(atts.getValue(i)));
			}
			// Min successors
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_MIN_SUCCESSORS)) >= 0) {
				currentDirectGraphNode.setMinSuccessors(Integer.parseInt(atts.getValue(i)));
			}
			// X
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_X)) >= 0) {
				currentDirectGraphNode.setPosX(Integer.parseInt(atts.getValue(i)));
			}
			// Y
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_Y)) >= 0) {
				currentDirectGraphNode.setPosY(Integer.parseInt(atts.getValue(i)));
			}
			// Width
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_WIDTH)) >= 0) {
				currentDirectGraphNode.setWidth(Integer.parseInt(atts.getValue(i)));
			}
			// Height
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_HEIGHT)) >= 0) {
				currentDirectGraphNode.setHeight(Integer.parseInt(atts.getValue(i)));
			}
		}
		
		/**
		 * Adds the predecessor ID to a temporary data structure to resolve the
		 * corresponding activities/graph nodes later.
		 */
		private void handleGraphNodePredecessor(Attributes atts) {
			int i;
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_ID)) >= 0) {
				String id = atts.getValue(i);
				
				//Get/create list
				List<String> idList = graphPredecessorsToResolve.get(currentDirectGraphNode);
				if (idList == null) {
					idList = new ArrayList<String>();
					graphPredecessorsToResolve.put(currentDirectGraphNode, idList);
				}
				//Add to list
				idList.add(id);
			}
		}
		
		/**
		 * Resolves the graph nodes by ID and adds the actual objects
		 */
		private void resolveGraphNodePredecessors() {
			for (Iterator<ActivityNode> it = graphPredecessorsToResolve.keySet().iterator(); it.hasNext(); ) {
				ActivityNode graphNode = it.next();
				List<String> predecessorIds = graphPredecessorsToResolve.get(graphNode);
				if (predecessorIds != null) {
					for (Iterator<String> itPre = predecessorIds.iterator(); itPre.hasNext(); ) {
						String id = itPre.next();
						//Get graph for ID
						ActivityNode predecessor = graphNodeRegister.get(id);
						if (predecessor != null)
							graphNode.addPredecessor(predecessor);
					}
				}
			}
		}
		
		private void handleIfelseBranchNode(Attributes atts) {
			if (currentActivity != null && currentActivity instanceof IfElseActivity) {
				((IfElseActivity)currentActivity).createBranch(null);
			}
		}
		
		private void handleIfElseConditionNode(String localName, Attributes atts) {
			if (currentActivity != null && currentActivity instanceof IfElseActivity) {
				int i;
				
				//Create condition
				IfCondition newCondition = null;
				// NOT
				if (DefaultXmlNames.ELEMENT_NOT_CONDITION.equals(localName))
					newCondition = new NotCondition();
				// AND/OR
				else if (DefaultXmlNames.ELEMENT_COMBINED_CONDITION.equals(localName)) {
					String op = "AND";
					if ((i = atts.getIndex(DefaultXmlNames.ATTR_OP)) >= 0) 
						op = atts.getValue(i);
					newCondition = new CombinedCondition("AND".equals(op));
				}
				// Input port
				else if (DefaultXmlNames.ELEMENT_INPUT_PORT_CONDITION.equals(localName))
					newCondition = new InputPortCondition();
				// Comparison
				else if (DefaultXmlNames.ELEMENT_COMPARISON_CONDITION.equals(localName)) {
					newCondition = new ComparisonCondition();
					String op = ComparisonConditionOperator.Equals.toString();
					if ((i = atts.getIndex(DefaultXmlNames.ATTR_OP)) >= 0) 
						op = atts.getValue(i);
					((ComparisonCondition)newCondition).setOperator(ComparisonConditionOperator.getById(op));
				}
				
				IfCondition parentCondition = conditionStack.isEmpty() ? null : conditionStack.peek();
				currentCondition = newCondition;
				conditionStack.push(currentCondition);
				
				//Get last branch
				IfBranch branch = ((IfElseActivity)currentActivity).getBranches().get(((IfElseActivity)currentActivity).getBranches().size()-1);
				
				//Add condition
				if (parentCondition == null) //First condition
					branch.setCondition(newCondition);
				else {
					if (parentCondition instanceof NotCondition)
						((NotCondition)parentCondition).setChildCondition(newCondition);
					else if (parentCondition instanceof CombinedCondition)
						((CombinedCondition)parentCondition).addChildCondition(newCondition);
				}
				
			}
		}
		
		/**
		 * Reads a data table element
		 * @param atts
		 */
		private void handleDataTableNode(Attributes atts) {
			currentDataTable = new DefaultDataTable(idRegister);
			workflow.getDataTables().add(currentDataTable);
			
			//Id
			int i;
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_ID)) >= 0) {
				currentDataTable.setId(atts.getValue(i));
			}

			// Caption
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_CAPTION)) >= 0) {
				currentDataTable.setCaption(atts.getValue(i));
			}
		}
		
	}

	


}
