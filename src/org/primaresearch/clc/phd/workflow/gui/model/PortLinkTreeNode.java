package org.primaresearch.clc.phd.workflow.gui.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.DirectedGraphActivity;
import org.primaresearch.clc.phd.workflow.activity.DirectedGraphActivity.ActivityNode;
import org.primaresearch.clc.phd.workflow.activity.ForLoopActivity;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity.IfBranch;
import org.primaresearch.clc.phd.workflow.data.DataTable;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.DataTableColumn;
import org.primaresearch.clc.phd.workflow.data.port.IfElsePort;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.LoopPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;

/**
 * Tree node for data port link selection
 * 
 * @author clc
 *
 */
public class PortLinkTreeNode extends DefaultMutableTreeNode {
	
	private static final long serialVersionUID = 1L;

	private static final int TYPE_TREE_ROOT 						= 1;
	private static final int TYPE_DAG_SIBLINGS 						= 2;
	private static final int TYPE_OUTPUT_PORTS_OF_SOURCE_ACTIVITY 	= 3;
	private static final int TYPE_DAG_SIBLING_OUTPUT_PORT 			= 4;
	private static final int TYPE_PARENT_ACTIVITY_INPUT_PORTS 		= 5;
	private static final int TYPE_PARENT_ACTIVITY_INPUT_PORT 		= 6;
	private static final int TYPE_PARENT_LOOP_PORTS 				= 7;
	private static final int TYPE_LOOP_PORT 						= 8;
	private static final int TYPE_LOOP_ACTIVITY_INPUT_PORTS			= 9;
	private static final int TYPE_LOOP_ACTIVITY_INPUT_PORT			= 10;
	private static final int TYPE_DAG_CHILDREN 						= 11;
	private static final int TYPE_LOOP_ACTIVITY_CHILD				= 12;
	private static final int TYPE_LOOP_PORTS 						= 13;
	private static final int TYPE_IFELSE_CHILDREN 					= 14;
	private static final int TYPE_PORTS_VISIBLE_BY_PARENT			= 15;
	private static final int TYPE_DATA_TABLES 						= 16;
	private static final int TYPE_DATA_TABLE 						= 17;
	private static final int TYPE_DATA_TABLE_COLUMN					= 18;
	
	private int nodeType;
	
	private Activity sourceActivity = null;
	
	private DataTable sourceTable = null;
	
	private DataPort linkSource = null;

	/**
	 * Constructor for tree root node
	 * @param linkTarget
	 */
	public PortLinkTreeNode(Workflow workflow, DataPort linkTarget) {
		this(workflow, linkTarget, linkTarget.getActivity());
	}
	
	/**
	 * Constructor for root node of specific activity
	 * @param linkTarget
	 * @param Activity of target port
	 */
	public PortLinkTreeNode(Workflow workflow, DataPort linkTarget, Activity activity) {
		nodeType = activity == linkTarget.getActivity() ? TYPE_TREE_ROOT : TYPE_PORTS_VISIBLE_BY_PARENT;
		sourceActivity = activity;

		//Add children
		// For input ports (Note: LoopPort is also an InputPort)
		if (linkTarget instanceof IfElsePort) {
			if (activity != null) {
				add(new PortLinkTreeNode(workflow, activity, TYPE_PARENT_ACTIVITY_INPUT_PORTS));
			}
		}
		else if (linkTarget instanceof InputPort) {
			//Parent activity
			Activity parentActivity = activity.getParentActivity();
			
			if (parentActivity != null) {
			
				if (parentActivity instanceof DirectedGraphActivity) {
					add(new PortLinkTreeNode(workflow, linkTarget, parentActivity, TYPE_DAG_SIBLINGS));
					add(new PortLinkTreeNode(workflow, parentActivity, TYPE_PARENT_ACTIVITY_INPUT_PORTS));
				}
				else if (parentActivity instanceof ForLoopActivity) {
					add(new PortLinkTreeNode(workflow, parentActivity, TYPE_PARENT_ACTIVITY_INPUT_PORTS));
					add(new PortLinkTreeNode(workflow, parentActivity, TYPE_PARENT_LOOP_PORTS));
				}
				else if (parentActivity instanceof IfElseActivity) {
					add(new PortLinkTreeNode(workflow, parentActivity, TYPE_PARENT_ACTIVITY_INPUT_PORTS));
				}
				//Recursion
				add(new PortLinkTreeNode(workflow, linkTarget, parentActivity));
			}
			//Data tables
			if (workflow.getDataTables() != null && !workflow.getDataTables().isEmpty())
				add(new PortLinkTreeNode(workflow, linkTarget, activity, TYPE_DATA_TABLES));
		}
		// For output ports 
		else if (linkTarget instanceof OutputPort) {
			if (activity instanceof DirectedGraphActivity) {
				add(new PortLinkTreeNode(workflow, linkTarget, activity, TYPE_DAG_CHILDREN));
			}			
			else if (activity instanceof ForLoopActivity) {
				add(new PortLinkTreeNode(workflow, linkTarget, activity, TYPE_LOOP_ACTIVITY_CHILD));
				add(new PortLinkTreeNode(workflow, activity, TYPE_LOOP_PORTS));
			}			
			else if (activity instanceof IfElseActivity) {
				add(new PortLinkTreeNode(workflow, linkTarget, activity, TYPE_IFELSE_CHILDREN));
				//add(new PortLinkTreeNode(activity, TYPE_ACTIVITY_INPUT_PORTS));
			}
			if (workflow.getDataTables() != null && !workflow.getDataTables().isEmpty())
				add(new PortLinkTreeNode(workflow, linkTarget, activity, TYPE_DATA_TABLES));
		}
		
		// For loop ports only
		if (linkTarget instanceof LoopPort) {
			//Input ports of loop activity
			add(new PortLinkTreeNode(workflow, activity, TYPE_LOOP_ACTIVITY_INPUT_PORTS));
		}
	}
	
	/**
	 * Constructor for link source group node
	 * @param linkTarget
	 */
	public PortLinkTreeNode(Workflow workflow, DataPort linkTarget, Activity activity, int nodeType) {
		this.nodeType = nodeType;
		
		//Add children
		if (linkTarget instanceof InputPort) {
			if (nodeType == TYPE_DAG_SIBLINGS) {
				DirectedGraphActivity dag = (DirectedGraphActivity)activity;
				Collection<ActivityNode> dagNodes = dag.getGraphNodes();
				for (Iterator<ActivityNode> it=dagNodes.iterator(); it.hasNext(); ) {
					Activity a = it.next().getActivity();
					if (a.getOutputPorts() != null && !a.getOutputPorts().isEmpty())
						add(new PortLinkTreeNode(workflow, a, TYPE_OUTPUT_PORTS_OF_SOURCE_ACTIVITY));
				}
			}
		}
		else if (linkTarget instanceof OutputPort) {
			if (nodeType == TYPE_DAG_CHILDREN) {
				DirectedGraphActivity dag = (DirectedGraphActivity)activity;
				Collection<ActivityNode> dagNodes = dag.getGraphNodes();
				for (Iterator<ActivityNode> it=dagNodes.iterator(); it.hasNext(); ) {
					Activity a = it.next().getActivity();
					if (a.getOutputPorts() != null && !a.getOutputPorts().isEmpty())
						add(new PortLinkTreeNode(workflow, a, TYPE_OUTPUT_PORTS_OF_SOURCE_ACTIVITY));
				}
			}
			else if (nodeType == TYPE_LOOP_ACTIVITY_CHILD) {
				ForLoopActivity loop = (ForLoopActivity)activity;
				Activity a = loop.getChildActivity();
				if (a != null) {
					if (a.getOutputPorts() != null && !a.getOutputPorts().isEmpty())
						add(new PortLinkTreeNode(workflow, a, TYPE_OUTPUT_PORTS_OF_SOURCE_ACTIVITY));
				}
			}
			else if (nodeType == TYPE_IFELSE_CHILDREN) {
				IfElseActivity ifElse = (IfElseActivity)activity;
				List<IfBranch> branches = ifElse.getBranches();
				for (Iterator<IfBranch> it=branches.iterator(); it.hasNext(); ) {
					Activity a = it.next().getActivity();
					if (a.getOutputPorts() != null && !a.getOutputPorts().isEmpty())
						add(new PortLinkTreeNode(workflow, a, TYPE_OUTPUT_PORTS_OF_SOURCE_ACTIVITY));
				}
			}
		}
		//Data tables
		if (nodeType == TYPE_DATA_TABLES) {
			for (Iterator<DataTable> it = workflow.getDataTables().iterator(); it.hasNext(); )
				add(new PortLinkTreeNode(workflow, it.next(), TYPE_DATA_TABLE));
		}
	}
	
	/**
	 * Constructor for source table root
	 */
	public PortLinkTreeNode(Workflow workflow, DataTable linkSourceTable, int nodeType) {
		this.nodeType = nodeType;
		sourceTable = linkSourceTable;
		
		//Add children
		if (nodeType == TYPE_DATA_TABLE) {
			List<DataTableColumn> columns = linkSourceTable.getColumns();
			if (columns != null) {
				for (Iterator<DataTableColumn> it = columns.iterator(); it.hasNext(); )
					add(new PortLinkTreeNode(it.next(), TYPE_DATA_TABLE_COLUMN));
			}
		}
	}
	
	/**
	 * Constructor for source activity root
	 */
	public PortLinkTreeNode(Workflow workflow, Activity linkSourceActivity, int nodeType) {
		this.nodeType = nodeType;
		sourceActivity = linkSourceActivity;
		
		//Add children
		if (nodeType == TYPE_OUTPUT_PORTS_OF_SOURCE_ACTIVITY) {
			List<OutputPort> outputPorts =  linkSourceActivity.getOutputPorts();
			if (outputPorts != null) {
				for (Iterator<OutputPort> it = outputPorts.iterator(); it.hasNext(); )
					add(new PortLinkTreeNode(it.next(), TYPE_DAG_SIBLING_OUTPUT_PORT));
			}
			
		}
		else if (nodeType == TYPE_PARENT_ACTIVITY_INPUT_PORTS) {
			List<InputPort> inputPorts =  linkSourceActivity.getInputPorts();
			if (inputPorts != null) {
				for (Iterator<InputPort> it = inputPorts.iterator(); it.hasNext(); )
					add(new PortLinkTreeNode(it.next(), TYPE_PARENT_ACTIVITY_INPUT_PORT));
			}
		}	
		else if (nodeType == TYPE_PARENT_LOOP_PORTS || nodeType == TYPE_LOOP_PORTS) {
			if (linkSourceActivity instanceof ForLoopActivity) {
				ForLoopActivity loop = (ForLoopActivity)linkSourceActivity;
				add(new PortLinkTreeNode(loop.getLoopStart(), TYPE_LOOP_PORT));
				add(new PortLinkTreeNode(loop.getLoopPosition(), TYPE_LOOP_PORT));
				add(new PortLinkTreeNode(loop.getLoopEnd(), TYPE_LOOP_PORT));
				add(new PortLinkTreeNode(loop.getLoopStep(), TYPE_LOOP_PORT));
			}
			
		}	
		else if (nodeType == TYPE_LOOP_ACTIVITY_INPUT_PORTS) {
			List<InputPort> inputPorts =  linkSourceActivity.getInputPorts();
			if (inputPorts != null) {
				for (Iterator<InputPort> it = inputPorts.iterator(); it.hasNext(); )
					add(new PortLinkTreeNode(it.next(), TYPE_LOOP_ACTIVITY_INPUT_PORT));
			}
		}	
		/*else if (nodeType == TYPE_ACTIVITY_INPUT_PORTS) {
			List<InputPort> inputPorts =  linkSourceActivity.getInputPorts();
			if (inputPorts != null) {
				for (Iterator<InputPort> it = inputPorts.iterator(); it.hasNext(); )
					add(new PortLinkTreeNode(it.next(), TYPE_ACTIVITY_INPUT_PORT));
			}
		}*/	
	}
	
	/**
	 * Constructor for link source port
	 * @param linkTarget
	 */
	public PortLinkTreeNode(DataPort linkSource, int nodeType) {
		this.nodeType = nodeType;
		this.linkSource = linkSource;
	}
	
	//Node caption
	@Override
	public String toString() {
		if (nodeType == TYPE_TREE_ROOT)
			return "Link sources";
		else if (nodeType == TYPE_DAG_SIBLINGS)
			return "Siblings of directed graph activity";
		else if (nodeType == TYPE_OUTPUT_PORTS_OF_SOURCE_ACTIVITY)
			return "Activity '"+sourceActivity.getCaption()+"'";
		else if (nodeType == TYPE_DAG_SIBLING_OUTPUT_PORT)
			return "Port '"+linkSource.getDataObject().getCaption()+"'";
		else if (nodeType == TYPE_PARENT_ACTIVITY_INPUT_PORTS)
			return "Input ports of parent activity ('"+sourceActivity.getCaption()+"')";
		else if (nodeType == TYPE_PARENT_ACTIVITY_INPUT_PORT)
			return "Input port '"+linkSource.getDataObject().getCaption()+"'";
		else if (nodeType == TYPE_PARENT_LOOP_PORTS)
			return "Loop ports of parent activity";
		else if (nodeType == TYPE_LOOP_PORT)
			return "Loop port '"+linkSource.getDataObject().getCaption()+"'";
		else if (nodeType == TYPE_LOOP_ACTIVITY_INPUT_PORTS)
			return "Input ports of loop activity";
		else if (nodeType == TYPE_LOOP_ACTIVITY_INPUT_PORT)
			return "Input port '"+linkSource.getDataObject().getCaption()+"'";
		else if (nodeType == TYPE_DAG_CHILDREN)
			return "Child activities of directed graph";
		else if (nodeType == TYPE_LOOP_ACTIVITY_CHILD)
			return "Child activity of loop";
		else if (nodeType == TYPE_LOOP_PORTS)
			return "Loop ports";
		else if (nodeType == TYPE_IFELSE_CHILDREN)
			return "Child activities of if-else branches";
		else if (nodeType == TYPE_PORTS_VISIBLE_BY_PARENT)
			return "Activities visible by parent ('"+sourceActivity.getCaption()+"')";
		else if (nodeType == TYPE_DATA_TABLES)
			return "Data tables of workflow";
		else if (nodeType == TYPE_DATA_TABLE)
			return "Data table '"+sourceTable.getCaption()+"'";
		else if (nodeType == TYPE_DATA_TABLE_COLUMN)
			return "Table column '"+linkSource.getDataObject().getCaption()+"'";
		return "Unknown";
	}

	public DataPort getLinkSource() {
		return linkSource;
	}
	
	
}
