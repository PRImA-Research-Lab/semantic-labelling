package org.primaresearch.clc.phd.workflow.gui.panel;

import javax.swing.tree.TreeNode;

import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.ForLoopActivity;
import org.primaresearch.clc.phd.workflow.gui.model.ActivityTreeNode;
import org.primaresearch.clc.phd.workflow.gui.model.WorkflowTreeModel;

import java.awt.GridBagLayout;

import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;

import java.awt.Insets;

import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.FlowLayout;

/**
 * Extension for basic activity panel. Specialised for 'for loop' activities.
 * 
 * @author clc
 *
 */
public class ForLoopActivityPanel extends DetailsPanel {
	
	private JPanel loopPortsPanel;
	private IdGenerator idRegister;
	private Workflow workflow;
	
	public ForLoopActivityPanel(Workflow workflow, IdGenerator idRegister) {
		this.idRegister = idRegister;
		this.workflow = workflow;
		
		setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblLoopPorts = new JLabel("Loop ports");
		GridBagConstraints gbc_lblLoopPorts = new GridBagConstraints();
		gbc_lblLoopPorts.anchor = GridBagConstraints.WEST;
		gbc_lblLoopPorts.insets = new Insets(0, 0, 5, 0);
		gbc_lblLoopPorts.gridx = 0;
		gbc_lblLoopPorts.gridy = 0;
		add(lblLoopPorts, gbc_lblLoopPorts);
		
		loopPortsPanel = new JPanel();
		loopPortsPanel.setBackground(new Color(255, 255, 255));
		FlowLayout flowLayout = (FlowLayout) loopPortsPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		loopPortsPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		GridBagConstraints gbc_loopPortsPanel = new GridBagConstraints();
		gbc_loopPortsPanel.gridwidth = 2;
		gbc_loopPortsPanel.fill = GridBagConstraints.BOTH;
		gbc_loopPortsPanel.gridx = 0;
		gbc_loopPortsPanel.gridy = 1;
		add(loopPortsPanel, gbc_loopPortsPanel);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void refresh(final TreeNode selectedNode,
			WorkflowTreeModel workflowTreeModel) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (selectedNode instanceof ActivityTreeNode) {
					ForLoopActivity activity = (ForLoopActivity)((ActivityTreeNode)selectedNode).getActivity();
				
					loopPortsPanel.removeAll();
					loopPortsPanel.add(new DataPortPanel(workflow, activity.getLoopStart(), activity, null, idRegister));
					loopPortsPanel.add(new DataPortPanel(workflow, activity.getLoopPosition(), activity, null, idRegister));
					loopPortsPanel.add(new DataPortPanel(workflow, activity.getLoopEnd(), activity, null, idRegister));
					loopPortsPanel.add(new DataPortPanel(workflow, activity.getLoopStep(), activity, null, idRegister));
				}
			}
		});
	}

}
