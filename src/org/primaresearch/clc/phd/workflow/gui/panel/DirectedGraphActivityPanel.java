package org.primaresearch.clc.phd.workflow.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultFormatter;
import javax.swing.tree.TreeNode;

import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgraph.graph.DefaultEdge;
import org.primaresearch.clc.phd.workflow.activity.DirectedGraphActivity;
import org.primaresearch.clc.phd.workflow.activity.DirectedGraphActivity.ActivityNode;
import org.primaresearch.clc.phd.workflow.gui.model.DirectedGraphActivityNode;
import org.primaresearch.clc.phd.workflow.gui.model.WorkflowTreeModel;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import java.awt.Font;

/**
 * Extension for basic activity panel. Specialised for 'directed acyclic graph' activities.
 * 
 * @author clc
 *
 */
public class DirectedGraphActivityPanel extends DetailsPanel implements GraphSelectionListener {
	private static final long serialVersionUID = 1L;
	private JGraphModelAdapter<ActivityNode,DefaultEdge> jgraphAdapter = null;
	private JGraph jgraph = null;
	private DirectedGraphActivity activity = null;
	private JButton btnLinkNodes;
	private JButton btnDelete;
	private JCheckBox chckbxOptional;
	private JSpinner minSuccessors;
	private JLabel lblMinSuccessors;
	private JSeparator separator;
	private JSeparator separator_1;
	private JLabel lblDirectedAcyclicGraph;
	private JSeparator separator_2;
	
	/**
	 * Constructor
	 */
	public DirectedGraphActivityPanel() {
		setBorder(new LineBorder(Color.DARK_GRAY));
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panel, BorderLayout.NORTH);
		
		JButton btnSaveLayout = new JButton("Save layout");
		btnSaveLayout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveGraphLayout();
			}
		});
		
		lblDirectedAcyclicGraph = new JLabel("Directed Acyclic Graph");
		lblDirectedAcyclicGraph.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel.add(lblDirectedAcyclicGraph);
		
		separator_2 = new JSeparator();
		separator_2.setOrientation(SwingConstants.VERTICAL);
		separator_2.setPreferredSize(new Dimension(2,23));
		panel.add(separator_2);
		panel.add(btnSaveLayout);
		
		btnLinkNodes = new JButton("Link nodes");
		btnLinkNodes.setEnabled(false);
		btnLinkNodes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createGraphEdgeBetweenSelectedNodes();
			}
		});
		
		separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		panel.add(separator);
		panel.add(btnLinkNodes);
		
		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteSelectedGraphObjects();
			}
		});
		btnDelete.setEnabled(false);
		panel.add(btnDelete);
		
		chckbxOptional = new JCheckBox("Optional");
		chckbxOptional.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ActivityNode selectedNode = null;
				if (jgraph.getSelectionCount() == 1 && jgraph.getSelectionCell() instanceof DefaultGraphCell)
					selectedNode = (ActivityNode)((DefaultGraphCell)jgraph.getSelectionCell()).getUserObject();
				if (selectedNode != null) 
					selectedNode.setOptional(chckbxOptional.isSelected());
			}
		});
		
		separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		separator_1.setPreferredSize(new Dimension(2,23));
		panel.add(separator_1);
		chckbxOptional.setEnabled(false);
		panel.add(chckbxOptional);
		
		lblMinSuccessors = new JLabel("Min. successors");
		panel.add(lblMinSuccessors);
		
		minSuccessors = new JSpinner();
		minSuccessors.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				ActivityNode selectedNode = null;
				if (jgraph.getSelectionCount() == 1 && jgraph.getSelectionCell() instanceof DefaultGraphCell)
					selectedNode = (ActivityNode)((DefaultGraphCell)jgraph.getSelectionCell()).getUserObject();
				if (selectedNode != null) 
					selectedNode.setMinSuccessors((Integer)minSuccessors.getValue());
			}
		});
		minSuccessors.setEnabled(false);
		minSuccessors.setPreferredSize(new Dimension(50,23));
		panel.add(minSuccessors);
	    JComponent comp = minSuccessors.getEditor();
	    JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
	    DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
	    formatter.setCommitsOnValidEdit(true);
	}


	@Override
	public void refresh(final TreeNode selectedNode, WorkflowTreeModel workflowTreeModel) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (jgraph != null)
					remove(jgraph);
				
				activity = (DirectedGraphActivity) ((DirectedGraphActivityNode)selectedNode).getActivity();
				
			    //Create a visualisation using JGraph, via an adapter
				jgraphAdapter = new JGraphModelAdapter<ActivityNode,DefaultEdge>(activity.getGraph());
		
			    jgraph = new JGraph(jgraphAdapter);
			    
			    jgraph.addGraphSelectionListener(DirectedGraphActivityPanel.this);
			    
			    //Position all nodes
			    for (Iterator<ActivityNode> it = activity.getGraphNodes().iterator(); it.hasNext(); ) {
			    	ActivityNode n = it.next();
			    	positionVertexAt(n, n.getPosX(), n.getPosY(), n.getWidth(), n.getHeight());
			    }
		
			    DirectedGraphActivityPanel.this.add(jgraph, BorderLayout.CENTER);
			}
		});
	}
	
	/**
	 * Moves the given graph node to the specified position
	 */
    private void positionVertexAt( Object vertex, int x, int y, int w, int h ) {
        DefaultGraphCell cell = jgraphAdapter.getVertexCell( vertex );
        Map<?, ?> attr = cell.getAttributes(  );
        //Rectangle2D      b    = GraphConstants.getBounds( attr );

        GraphConstants.setBounds( attr, new Rectangle2D.Double( x, y, w, h ) );

        Map<DefaultGraphCell, Map<?, ?>> cellAttr = new HashMap<DefaultGraphCell, Map<?, ?>>();
        cellAttr.put(cell, attr);
        jgraphAdapter.edit( cellAttr, null, null, null );
    }

    /**
     * Copies the current graphical node positions and sizes to the 
     * respective activity graph nodes.
     */
    private void saveGraphLayout() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    //Position all nodes
			    for (Iterator<ActivityNode> it = activity.getGraphNodes().iterator(); it.hasNext(); ) {
			    	ActivityNode n = it.next();
			    	saveNodePosition(n);
			    }
			}
		});
	}
    
    /**
     * Copies position and size of the graphical node that corresponds to
     * the given activity graph node.
     * @param node
     */
    private void saveNodePosition(ActivityNode node) {
        DefaultGraphCell cell = jgraphAdapter.getVertexCell( node );
        Map<?, ?> attr = cell.getAttributes(  );
        Rectangle2D b = GraphConstants.getBounds( attr );
        node.setPosX((int)b.getX());
        node.setPosY((int)b.getY());
        node.setWidth((int)b.getWidth());
        node.setHeight((int)b.getHeight());
    }


	@Override
	public void valueChanged(GraphSelectionEvent e) {
		ActivityNode selectedNode = null;
		if (jgraph.getSelectionCount() == 1 && jgraph.getSelectionCell() instanceof DefaultGraphCell) {
			Object obj = ((DefaultGraphCell)jgraph.getSelectionCell()).getUserObject();
			if (obj instanceof ActivityNode)
				selectedNode = (ActivityNode)obj;
		}
		
		//Enable / disable toolbar controls
		btnLinkNodes.setEnabled(jgraph.getSelectionCount() == 2);
		btnDelete.setEnabled(jgraph.getSelectionCount() >= 1);
		chckbxOptional.setEnabled(selectedNode != null);
		minSuccessors.setEnabled(selectedNode != null);
		
		//Update values
		if (selectedNode != null) {
			chckbxOptional.setSelected(selectedNode.isOptional());
			minSuccessors.setValue(selectedNode.getMinSuccessors());
		}
	}
	
	/**
	 * Creates a graph edge between the two selected nodes. The
	 * edge direction is from the node which was selected first
	 * to the node which was selected second.
	 */
	private void createGraphEdgeBetweenSelectedNodes() {
		
		//Two nodes selected
		Object selectionCells[] = jgraph.getSelectionCells();
		
		//Get nodes
		DefaultGraphCell cell = (DefaultGraphCell)selectionCells[0];
		ActivityNode n1 = (ActivityNode)cell.getUserObject();
		cell = (DefaultGraphCell)selectionCells[1];
		ActivityNode n2 = (ActivityNode)cell.getUserObject();
		
		//Create edge
		activity.getGraph().addEdge(n1, n2);
		
		//Set predecessor
		n2.addPredecessor(n1);
	}
	
	/**
	 * Deletes all selected nodes and edges
	 */
	private void deleteSelectedGraphObjects() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Object selectionCells[] = jgraph.getSelectionCells();
				
				for (Object cell : selectionCells) {
					//Edge
					if (cell instanceof DefaultEdge) {
						DefaultEdge edge = (DefaultEdge)cell;
						DefaultPort p1 = (DefaultPort)edge.getSource();
						DefaultPort p2 = (DefaultPort)edge.getTarget();
						Object userObj1 = ((DefaultGraphCell)p1.getParent()).getUserObject();
						Object userObj2 = ((DefaultGraphCell)p2.getParent()).getUserObject();
						ActivityNode n1 = (ActivityNode)userObj1;
						ActivityNode n2 = (ActivityNode)userObj2;
						//ActivityNode n2 = (ActivityNode)((DefaultPort)edge.getTarget()).getUserObject();
						//if (activity.getGraph().containsEdge(edge)) {
							//ActivityNode n1 = activity.getGraph().getEdgeSource(edge);
							//ActivityNode n2 = activity.getGraph().getEdgeTarget(edge);
							n2.removePredecessor(n1);
						//}
					}
					//Node
					else if (cell instanceof DefaultGraphCell) {
						Object userObj = ((DefaultGraphCell)cell).getUserObject();
						if (userObj instanceof ActivityNode) {
							activity.removeNode((ActivityNode)userObj);
						}
					}
				}
			}
		});
	}

}
