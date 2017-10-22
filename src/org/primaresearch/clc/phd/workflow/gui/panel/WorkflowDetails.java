package org.primaresearch.clc.phd.workflow.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.primaresearch.clc.phd.workflow.DescriptionWithLabels;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.automation.gui.WorkflowConcretisationDialog;
import org.primaresearch.clc.phd.workflow.gui.dialog.DescriptionLabelDialog;
import org.primaresearch.clc.phd.workflow.gui.model.WorkflowRootNode;
import org.primaresearch.clc.phd.workflow.gui.model.WorkflowTreeModel;

/**
 * Panel with fields for workflow attributes such as name, author and version. Also offers an entry point for workflow concretisation.
 * 
 * @author clc
 *
 */
public class WorkflowDetails extends DetailsPanel implements DocumentListener {

	private static final long serialVersionUID = 1L;
	private Workflow workflow;
	private JTextField workflowName;
	private JTextField workflowVersion;
	private JTextField workflowAuthor;
	private JTextArea workflowDescription;
	private WorkflowTreeModel workflowTreeModel;
	private TreeNode selectedTreeNode;
	private JCheckBox chckbxAbstract;
	private JButton btnConcretise;
	private volatile boolean workflowDetailsChangeListenerActive = true;
	private DescriptionWithLabels workfDescriptionWithLabels = null;

	public WorkflowDetails(final Workflow workflow, final WorkflowTreeModel workflowTreeModel) {
		super();
		this.workflowTreeModel = workflowTreeModel;
		setBorder(new EmptyBorder(5, 5, 5, 5));
		this.workflow = workflow;
		this.workfDescriptionWithLabels = workflow.getDescription(0);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[] {0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0};
		setLayout(gridBagLayout);
		
		JLabel lblWorkflowProperties = new JLabel("Workflow Properties");
		GridBagConstraints gbc_lblWorkflowProperties = new GridBagConstraints();
		gbc_lblWorkflowProperties.gridwidth = 2;
		gbc_lblWorkflowProperties.insets = new Insets(0, 0, 5, 0);
		gbc_lblWorkflowProperties.gridx = 0;
		gbc_lblWorkflowProperties.gridy = 0;
		add(lblWorkflowProperties, gbc_lblWorkflowProperties);
		
		JLabel lblName = new JLabel("Name");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.anchor = GridBagConstraints.EAST;
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 1;
		add(lblName, gbc_lblName);
		
		workflowName = new JTextField();
		GridBagConstraints gbc_workflowName = new GridBagConstraints();
		gbc_workflowName.insets = new Insets(0, 0, 5, 0);
		gbc_workflowName.fill = GridBagConstraints.HORIZONTAL;
		gbc_workflowName.gridx = 1;
		gbc_workflowName.gridy = 1;
		add(workflowName, gbc_workflowName);
		workflowName.setColumns(10);
		workflowName.getDocument().addDocumentListener(this);
		
		JLabel lblVersion = new JLabel("Version");
		GridBagConstraints gbc_lblVersion = new GridBagConstraints();
		gbc_lblVersion.anchor = GridBagConstraints.EAST;
		gbc_lblVersion.insets = new Insets(0, 0, 5, 5);
		gbc_lblVersion.gridx = 0;
		gbc_lblVersion.gridy = 2;
		add(lblVersion, gbc_lblVersion);
		
		workflowVersion = new JTextField();
		GridBagConstraints gbc_workflowVersion = new GridBagConstraints();
		gbc_workflowVersion.insets = new Insets(0, 0, 5, 0);
		gbc_workflowVersion.fill = GridBagConstraints.HORIZONTAL;
		gbc_workflowVersion.gridx = 1;
		gbc_workflowVersion.gridy = 2;
		add(workflowVersion, gbc_workflowVersion);
		workflowVersion.setColumns(10);
		workflowVersion.getDocument().addDocumentListener(this);

		JLabel lblAuthor = new JLabel("Author");
		GridBagConstraints gbc_lblAuthor = new GridBagConstraints();
		gbc_lblAuthor.anchor = GridBagConstraints.EAST;
		gbc_lblAuthor.insets = new Insets(0, 0, 5, 5);
		gbc_lblAuthor.gridx = 0;
		gbc_lblAuthor.gridy = 3;
		add(lblAuthor, gbc_lblAuthor);
		
		workflowAuthor = new JTextField();
		GridBagConstraints gbc_workflowAuthor = new GridBagConstraints();
		gbc_workflowAuthor.insets = new Insets(0, 0, 5, 0);
		gbc_workflowAuthor.fill = GridBagConstraints.HORIZONTAL;
		gbc_workflowAuthor.gridx = 1;
		gbc_workflowAuthor.gridy = 3;
		add(workflowAuthor, gbc_workflowAuthor);
		workflowAuthor.setColumns(10);
		workflowAuthor.getDocument().addDocumentListener(this);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 4;
		add(panel, gbc_panel);
		panel.setLayout(new BorderLayout(0, 5));
		
		workflowDescription = new JTextArea();
		workflowDescription.setWrapStyleWord(true);
		workflowDescription.setLineWrap(true);
		workflowDescription.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panel.add(workflowDescription, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.add(panel_1, BorderLayout.SOUTH);
		
		chckbxAbstract = new JCheckBox("");
		chckbxAbstract.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chckbxAbstract.setSelected(workflow.getRootActivity() != null && workflow.getRootActivity().isAbstract());
			}
		});
		panel_1.add(chckbxAbstract);
		
		JLabel lblAbstractWorkflow = new JLabel("Abstract workflow");
		panel_1.add(lblAbstractWorkflow);
		
		btnConcretise = new JButton("Concretise");
		btnConcretise.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						WorkflowConcretisationDialog dlg = new WorkflowConcretisationDialog(workflow);
						dlg.setVisible(true);
						
						//Refresh tree
						// Root node
						WorkflowRootNode root = new WorkflowRootNode(workflow);
						workflowTreeModel.setRoot(root);
						
						// Add child nodes (recursive)
						root.addChildNodes(workflowTreeModel);
					}
				});
			}
		});
		panel_1.add(btnConcretise);
		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2, BorderLayout.NORTH);
		panel_2.setLayout(new GridLayout(1, 2, 0, 0));
		
		JLabel lblDescription = new JLabel("Description (1 of 1)");
		panel_2.add(lblDescription);
		
		JPanel panel_3 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_3.getLayout();
		flowLayout_1.setAlignment(FlowLayout.RIGHT);
		panel_2.add(panel_3);
		
		JButton btnDescriptionLabels = new JButton("Labels...");
		btnDescriptionLabels.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						DescriptionLabelDialog dlg = new DescriptionLabelDialog(workfDescriptionWithLabels);
						dlg.setModal(true);
						dlg.setVisible(true);
					}
				});				
			}
		});
		panel_3.add(btnDescriptionLabels);
		
		JButton btnAddDescription = new JButton("Add");
		btnAddDescription.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//TODO
			}
		});
		panel_3.add(btnAddDescription);
		
		JButton btnPrevDescription = new JButton("Prev");
		btnPrevDescription.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//TODO
			}
		});
		panel_3.add(btnPrevDescription);
		
		JButton btnNextDescription = new JButton("Next");
		btnNextDescription.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//TODO
			}
		});
		panel_3.add(btnNextDescription);
		workflowDescription.getDocument().addDocumentListener(this);
	}

	@Override
	public void refresh(TreeNode selectedNode, WorkflowTreeModel workflowTreeModel) {
		if (selectedNode == null || !(selectedNode instanceof WorkflowRootNode))
			return;

		selectedTreeNode = selectedNode;
		
		workflow = ((WorkflowRootNode)selectedNode).getWorkflow();
		
		refreshControls();
	}

	private void refreshControls() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					workflowDetailsChangeListenerActive = false;
					if (workflow != null) {
						workflowName.setText(workflow.getName());
						workflowVersion.setText(workflow.getVersion());
						workflowAuthor.setText(workflow.getAuthor());
						workflowDescription.setText(workflow.getDescription(0).getText());
						chckbxAbstract.setSelected(workflow.getRootActivity() != null && workflow.getRootActivity().isAbstract());
						btnConcretise.setEnabled(workflow.getRootActivity() != null && workflow.getRootActivity().isAbstract());
					} 
				} catch (Exception exc) {
					exc.printStackTrace();
				}
				workflowDetailsChangeListenerActive = true;
			}
		});
	}

	//DocumentChangeListener
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		updateWorkflowFromControls();
	}

	//DocumentChangeListener
	@Override
	public void insertUpdate(DocumentEvent arg0) {
		updateWorkflowFromControls();
	}

	//DocumentChangeListener
	@Override
	public void removeUpdate(DocumentEvent arg0) {
		updateWorkflowFromControls();
	}
	
	private void updateWorkflowFromControls() {
		if (workflowDetailsChangeListenerActive) {
			if (workflow != null) {
				if (!workflow.getName().equals(workflowName.getText())) {
					workflow.setName(workflowName.getText());
				}
				workflow.setVersion(workflowVersion.getText());
				workflow.setAuthor(workflowAuthor.getText());
				workflow.setDescription(0, workflowDescription.getText());
			}		
			if (selectedTreeNode != null)
				workflowTreeModel.nodeChanged((DefaultMutableTreeNode)selectedTreeNode);
		}
	}
}
