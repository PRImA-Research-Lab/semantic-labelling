package org.primaresearch.clc.phd.workflow.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.ListenableGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.primaresearch.clc.phd.ontology.label.Labels;
import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;


/**
 * Activity containing a directed acyclic graph (DAG) of child activities.
 * 
 * @author clc
 *
 */
public class DirectedGraphActivity extends BaseActivity implements HasChildActivities {
	
	private List<ActivityNode> nodes = new ArrayList<ActivityNode>();
	private ArrayList<DirectedGraphActivityListener> listeners = new ArrayList<DirectedGraphActivityListener>();
	
	/**
	 * Directed graph
	 * See: http://jgrapht.org/
	 */
	ListenableGraph<ActivityNode,DefaultEdge> graph = new ListenableDirectedGraph<ActivityNode,DefaultEdge>( DefaultEdge.class );
	

	/**
	 * Constructor
	 */
	DirectedGraphActivity(Activity parentActivity, String id, IdGenerator idRegister, Labels allowedLabels) {
		super(parentActivity, id, idRegister,  ActivityType.DIRECTED_GRAPH_ACTIVITY, allowedLabels);
	}
	
	/**
	 * Copy constructor
	 * @param other
	 */
	public DirectedGraphActivity(DirectedGraphActivity other) {
		super(other);
		for (Iterator<ActivityNode> it = other.nodes.iterator(); it.hasNext(); )
			addNode(it.next().clone(graph));
		
		//Clone graph edges
		//TODO
	}
	
	public Activity clone() {
		return new DirectedGraphActivity(this);
	}
	
	public void addListener(DirectedGraphActivityListener listener) {
		listeners.add(listener);
	}

	public void removeListener(DirectedGraphActivityListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Creates and adds a graph node for a new child activity
	 * @param activity Child activity
	 * @return The graph node
	 */
	public ActivityNode createNode(Activity activity) {
		ActivityNode node = new ActivityNode(activity);
		addNode(node);
		return node;
	}
	
	/**
	 * Adds the given graph node
	 * @param node
	 */
	public void addNode(ActivityNode node) {
		nodes.add(node);
		graph.addVertex(node);
		node.setGraph(graph);
	}
	
	/**
	 * Removes the given graph node
	 * @param node
	 */
	public void removeNode(ActivityNode node) {
		//Remove references to the given node
		for (Iterator<ActivityNode> it = nodes.iterator(); it.hasNext(); ) {
			ActivityNode n = it.next();
			n.getPredecessors().remove(n);
		}
		
		//Remove the node itself
		nodes.remove(node);
		graph.removeVertex(node);
		
		//Notify listeners
		notifyListenersNodeRemoved(node);
	}
	
	/**
	 * Removes the graph node containing the given activity
	 * @param activity
	 */
	public void removeNode(Activity activity) {
		for (int i=0; i<nodes.size(); i++) {
			ActivityNode n = nodes.get(i);
			if (n.getActivity() == activity) {
				removeNode(n);
				break;
			}
		}
	}
	
	/**
	 * Returns all nodes (vertices) of this graph.
	 * Note: Use addNode and removeNode functions to modify the collection.
	 * @return
	 */
	public Collection<ActivityNode> getGraphNodes() {
		return nodes;
	}
	
	@Override
	public boolean isAbstract() {
		//A compound activity is abstract if one or more child activities are abstract.
		for (Iterator<ActivityNode> it = nodes.iterator(); it.hasNext(); ) {
			if (it.next().getActivity().isAbstract())
				return true;
		}
		return false;
	}
	
	/**
	 * Returns the directed graph
	 */
	public ListenableGraph<ActivityNode, DefaultEdge> getGraph() {
		return graph;
	}
	
	private void notifyListenersNodeRemoved(ActivityNode removedNode) {
		for (Iterator<DirectedGraphActivityListener> it = listeners.iterator(); it.hasNext(); ) {
			it.next().onChildActivityRemovedFromDirectedGraph(removedNode);
		}
	}
	
	@Override
	public void replaceActivity(Activity old, Activity replacement) {
		if (nodes == null || nodes.isEmpty())
			return;
		
		for (Iterator<ActivityNode> it = nodes.iterator(); it.hasNext(); ) {
			ActivityNode node = it.next();
			if (node.getActivity() == old) {
				node.setActivity(replacement);
				break;
			}
			else if (node.getActivity() instanceof HasChildActivities){
				((HasChildActivities)node.getActivity()).replaceActivity(old, replacement);
			}
		}
	}
	
	@Override
	public Collection<DataPort> getSourcePortsForChildren() {
		Collection<DataPort> ret = new LinkedList<DataPort>();
		ret.addAll(getInputPorts());

		if (nodes != null) {
			for (Iterator<ActivityNode> it = nodes.iterator(); it.hasNext(); ) {
				ActivityNode node = it.next();
				if (node.getActivity() != null) 
					ret.addAll(node.getActivity().getOutputPorts());
			}
		}
		
		return ret;
	}

	@Override
	public Collection<DataPort> getTargetPortsForChildren() {
		Collection<DataPort> ret = new LinkedList<DataPort>();
		ret.addAll(getOutputPorts());
		
		if (nodes != null) {
			for (Iterator<ActivityNode> it = nodes.iterator(); it.hasNext(); ) {
				ActivityNode node = it.next();
				if (node.getActivity() != null) 
					ret.addAll(node.getActivity().getInputPorts());
			}
		}

		return ret;
	}

	public Collection<Activity> getChildActivities() {
		Collection<Activity> ret = new LinkedList<Activity>();
		if (nodes != null) {
			for (Iterator<ActivityNode> it = nodes.iterator(); it.hasNext(); ) {
				ActivityNode node = it.next();
				if (node.getActivity() != null) 
					ret.add(node.getActivity());
			}
		}
		return ret;
	}

	/**
	 * Checks if the graph has a cycle and is therefore invalid
	 */
	public boolean hasCycles() {
		if (nodes == null || nodes.size() <= 1)
			return false;
		
		//Reset 'visited' flag
		for (Iterator<ActivityNode> it = nodes.iterator(); it.hasNext(); )
			it.next().setVisited(false);
		
		ActivityNode startNode = findCycleDetectionStartNode();
		if (startNode == null)
			return true; //Cycle
		
		while (startNode != null) {
			if (hasCycle(startNode))
				return true;
			startNode = findCycleDetectionStartNode();
		}
		
		return false;
	}
	
	/**
	 * Tries to find a node that has only outgoing edges
	 * @return
	 */
	private ActivityNode findCycleDetectionStartNode() {
		for (Iterator<ActivityNode> it = nodes.iterator(); it.hasNext(); ) {
			ActivityNode node = it.next();
			if (!node.isVisited()) {
				if (node.getPredecessors() == null || node.getPredecessors().isEmpty()) //Only outgoing edges
					return node;
			}
		}
		return null;
	}
	
	/**
	 * Depth first search for cycles (recursive)
	 * @param node Current node
	 * @return <code>true</code> if cycle found
	 */
	private boolean hasCycle(ActivityNode node) {
		if (node.isVisited())
			return true; //Cycle
		
		node.setVisited(true);
		
		//Go to successors
		for (Iterator<ActivityNode> it = nodes.iterator(); it.hasNext(); ) {
			ActivityNode possibleSuccessor = it.next();
			if (possibleSuccessor.getPredecessors() != null) {
				if (possibleSuccessor.getPredecessors().contains(node)) {
					boolean cycle = hasCycle(possibleSuccessor);
					if (cycle)
						return true;
				}
			}
		}
		
		return false;
	}


	/**
	 * Graph node containing one activity.
	 * @author clc
	 *
	 */
	public static class ActivityNode {
		private Activity activity;
		private Collection<ActivityNode> predecessors = new ArrayList<ActivityNode>();
		private boolean optional = false;
		private int minSuccessors = -1;
		private int posX = 0;
		private int posY = 0;
		private int width = 50;
		private int height = 30;
		private ListenableGraph<ActivityNode,DefaultEdge> graph = null;
		private boolean visited = false;
		
		ActivityNode(Activity activity) {
			this.activity = activity;
		}
		
		public ActivityNode() {
		}
		
		/**
		 * Copy constructor
		 * @param other
		 */
		public ActivityNode(ActivityNode other, ListenableGraph<ActivityNode,DefaultEdge> graph) {
			this.activity = other.activity.clone();
			for (Iterator<ActivityNode> it = other.predecessors.iterator(); it.hasNext(); )
				predecessors.add(it.next().clone(graph));
			this.optional = other.optional;
			this.minSuccessors = other.minSuccessors;
			this.posX = other.posX;
			this.posY = other.posY;
			this.width = other.width;
			this.height = other.height;
			this.graph = graph;
		}
		
		public ActivityNode clone(ListenableGraph<ActivityNode,DefaultEdge> graph) {
			return new ActivityNode(this, graph);
		}
		
		public void addPredecessor(ActivityNode predecessor) {
			predecessors.add(predecessor);
			
			//Add graph edge
			graph.addEdge(predecessor, this);
		}
		
		public void removePredecessor(ActivityNode predecessor) {
			predecessors.remove(predecessor);
			
			//Remove graph edge
			graph.removeEdge(predecessor, this);
		}

		public Activity getActivity() {
			return activity;
		}

		public void setActivity(Activity a) {
			activity = a;
		}

		public Collection<ActivityNode> getPredecessors() {
			return predecessors;
		}

		/**
		 * Specifies if this activity is optional (can be used for optional parallel paths).
		 * Only abstract activities can be optional.
		 */
		public boolean isOptional() {
			return optional;
		}

		/**
		 * Specifies if this activity is optional (can be used for optional parallel paths).
		 * Note: Only abstract activities can be optional.
		 */
		public void setOptional(boolean optional) {
			if (activity == null || activity.isAbstract()) //Concrete activities cannot be optional
				this.optional = optional;
		}

		/**
		 * The minimum number of successor nodes. To be used in conjunction with 'optional' successor (abstract).
		 * Specifies how many direct successors (optional and non optional) are required.
		 * If there is an optional successor, it can be instantiated several times (depending on the MinSuccessors value)
		 * @return
		 */
		public int getMinSuccessors() {
			return minSuccessors;
		}

		/**
		 * Sets the minimum number of successor nodes. To be used in conjunction with 'optional' successor (abstract).
		 * Specifies how many direct successors (optional and non optional) are required.
		 * If there is an optional successor, it can be instantiated several times (depending on the MinSuccessors value)
		 * @return
		 */
		public void setMinSuccessors(int minSuccessors) {
			this.minSuccessors = minSuccessors;
		}

		public int getPosX() {
			return posX;
		}

		public void setPosX(int posX) {
			this.posX = posX;
		}

		public int getPosY() {
			return posY;
		}

		public void setPosY(int posY) {
			this.posY = posY;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		@Override
		public String toString() {
			return ""+activity.getCaption();
		}

		public void setGraph(ListenableGraph<ActivityNode, DefaultEdge> graph) {
			this.graph = graph;
		}

		public boolean isVisited() {
			return visited;
		}

		public void setVisited(boolean visited) {
			this.visited = visited;
		}
		
		
	}
	
	
	/**
	 * Listener interface for directed graph activity events
	 * 
	 * @author clc
	 *
	 */
	public static interface DirectedGraphActivityListener {
		/**
		 * Called when a graph node (with a child activity) has been removed from the directed graph
		 * @param removedNode
		 */
		public void onChildActivityRemovedFromDirectedGraph(ActivityNode removedNode);
	}




}
