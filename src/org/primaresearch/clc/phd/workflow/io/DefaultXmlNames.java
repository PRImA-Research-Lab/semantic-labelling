package org.primaresearch.clc.phd.workflow.io;

public interface DefaultXmlNames {

	public static final String ELEMENT_WORKFLOW 				= "Workflow";
	public static final String ELEMENT_DESCRIPTION				= "Description";
	public static final String ELEMENT_DESCRIPTION_WITH_LABELS	= "DescriptionWithLabels";
	public static final String ELEMENT_ATOMIC_ACTIVITY 			= "AtomicActivity";
	public static final String ELEMENT_FOR_LOOP_ACTIVITY 		= "ForLoopActivity";
	public static final String ELEMENT_DIRECTED_GRAPH_ACTIVITY 	= "DirectedGraphActivity";
	public static final String ELEMENT_IFELSE_ACTIVITY 			= "IfElseActivity";
	public static final String ELEMENT_LOOP_PORT 				= "LoopPort";
	public static final String ELEMENT_INPUT_PORT 				= "InputPort";
	public static final String ELEMENT_OUTPUT_PORT 				= "OutputPort";
	public static final String ELEMENT_LOOP_START_PORT 			= "LoopStartPort";
	public static final String ELEMENT_LOOP_POSITION_PORT 		= "LoopPositionPort";
	public static final String ELEMENT_LOOP_END_PORT 			= "LoopEndPort";
	public static final String ELEMENT_LOOP_STEP_PORT 			= "LoopStepPort";
	public static final String ELEMENT_LABEL 					= "Label";
	public static final String ELEMENT_COMMENTS 				= "Comments";
	public static final String ELEMENT_SINGLE_DATA_OBJECT		= "SingleDataObject";
	public static final String ELEMENT_DATA_COLLECTION			= "DataCollection";
	public static final String ELEMENT_VERTEX					= "Vertex";
	public static final String ELEMENT_PREDECESSOR				= "Predecessor";
	public static final String ELEMENT_SOURCE					= "Source";
	public static final String ELEMENT_BRANCH		 			= "Branch";
	public static final String ELEMENT_NOT_CONDITION 			= "NotCondition";
	public static final String ELEMENT_COMBINED_CONDITION 		= "CombinedCondition";
	public static final String ELEMENT_INPUT_PORT_CONDITION 	= "InputPortCondition";
	public static final String ELEMENT_COMPARISON_CONDITION 	= "ComparisonCondition";
	public static final String ELEMENT_IFELSE_PORT 				= "IfElsePort";
	public static final String ELEMENT_DATA_TABLE 				= "DataTable";
	public static final String ELEMENT_TABLE_COLUMN 			= "TableColumn";
	public static final String ELEMENT_TEXT 					= "Text";

	public static final String ATTR_NAME 		= "name";
	public static final String ATTR_AUTHOR 		= "author";
	public static final String ATTR_VERSION 	= "version";
	public static final String ATTR_ID 			= "id";
	public static final String ATTR_CAPTION 	= "caption";
	public static final String ATTR_LOCAL_NAME 	= "localName";
	public static final String ATTR_ABSTRACT 	= "abstract";
	public static final String ATTR_METHOD_NAME = "methodName";
	public static final String ATTR_METHOD_VERSION = "methodVersion";
	public static final String ATTR_TYPE 		= "type";
	public static final String ATTR_ALLOWED_TYPES = "allowedTypes";
	public static final String ATTR_VALUE 		= "value";
	public static final String ATTR_SOURCE 		= "source";
	public static final String ATTR_POSITION_SOURCE 	= "positionSource";
	public static final String ATTR_OPTIONAL 	= "optional";
	public static final String ATTR_MIN_SUCCESSORS 	= "minSuccessors";
	public static final String ATTR_X 			= "x";
	public static final String ATTR_Y 			= "y";
	public static final String ATTR_WIDTH 		= "width";
	public static final String ATTR_HEIGHT 		= "height";
	public static final String ATTR_ONTOLOGY_VERSION = "ontologyVersion";
	public static final String ATTR_OP 			= "op";
}
