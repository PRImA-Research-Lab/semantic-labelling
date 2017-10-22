package org.primaresearch.clc.phd.workflow.validation.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.gui.WorkflowEditor;
import org.primaresearch.clc.phd.workflow.validation.WorkflowValidationResult;
import org.primaresearch.clc.phd.workflow.validation.WorkflowValidator;
import org.primaresearch.clc.phd.workflow.validation.gui.model.ValidationResultTreeItem;
import org.primaresearch.clc.phd.workflow.validation.gui.panel.DataPortCorrectionPanel;
import org.primaresearch.clc.phd.workflow.validation.gui.panel.MissingAttributePanel;
import org.primaresearch.clc.phd.workflow.validation.gui.panel.MissingDataTypePanel;
import org.primaresearch.clc.phd.workflow.validation.gui.panel.ProblemResolutionDescriptionPanel;
import org.primaresearch.clc.phd.workflow.validation.modules.ActivtyValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.CycleDetectionValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.DataCardinalityMatchValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.DatatypeMatchValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.LabelMatchValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.MissingChildActivitiesValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.MissingDatatypesValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.UnconnectedDataPortsValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.WorkflowObjectValidationModule;

import javax.swing.border.LineBorder;

/**
 * Validation of workflow and presentation of results.
 * 
 * @author clc
 *
 */
public class WorkflowValidationDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private Workflow workflow;
	private WorkflowEditor workflowEditor;
	private JTextArea details;
	private JTree tree;
	private JPanel correctionPanelContainer;
	

	public WorkflowValidationDialog(final WorkflowEditor workflowEditor, Workflow workflow) {
		super(workflowEditor, "Workflow Validation");
		setSize(600, 900);
		
		this.workflow = workflow;
		this.workflowEditor = workflowEditor;

		JPanel contentPane = new JPanel(new BorderLayout());
		setContentPane(contentPane);
		
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(Color.WHITE);
		
		//Details panel and buttons
		JPanel bottomPanel = new JPanel(new BorderLayout());
		contentPane.add(bottomPanel, BorderLayout.SOUTH);
		
		FlowLayout fl_buttonPanel = new FlowLayout();
		fl_buttonPanel.setAlignment(FlowLayout.RIGHT);
		JPanel buttonPanel = new JPanel(fl_buttonPanel);
		bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		JButton btnExpandAll = new JButton("Expand all");
		btnExpandAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < tree.getRowCount(); i++) {
					tree.expandRow(i);
				}
			}
		});
		buttonPanel.add(btnExpandAll);
		
		JButton validate = new JButton("Revalidate");
		buttonPanel.add(validate);
		validate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				validate();
			}
		});
		
		details = new JTextArea();
		details.setWrapStyleWord(true);
		details.setLineWrap(true);
		details.setFont(new Font("Tahoma", Font.PLAIN, 13));
		details.setPreferredSize(new Dimension(500, 100));
		bottomPanel.add(details, BorderLayout.NORTH);
		
		correctionPanelContainer = new JPanel();
		correctionPanelContainer.setBorder(new LineBorder(Color.GRAY));
		correctionPanelContainer.setPreferredSize(new Dimension(500, 100));
		bottomPanel.add(correctionPanelContainer, BorderLayout.CENTER);
		correctionPanelContainer.setLayout(new BorderLayout(0, 0));
		
		//Result tree
		tree = new JTree();
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				ValidationResultTreeItem selectedNode = (ValidationResultTreeItem)e.getPath().getLastPathComponent();
				onTreeItemSelected(selectedNode);
			}
		});
		tree.setCellRenderer(new ValidationTreeCellRenderer());
		
		JScrollPane scrollPane = new JScrollPane(tree);
		contentPane.add(scrollPane, BorderLayout.CENTER);
	}
	
	/**
	 * Validates the workflow and shows the result in tree form
	 */
	public void validateWorkflow() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				WorkflowValidator validator = new WorkflowValidator();
				WorkflowValidationResult res = validator.validate(workflow);
				tree.setModel(new DefaultTreeModel(new ValidationResultTreeItem(res)));
			}
		});
	}
	
	/**
	 * A validation result tree item was selected
	 * @param selectedNode
	 */
	private void onTreeItemSelected(final ValidationResultTreeItem selectedNode) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (selectedNode != null) {
					WorkflowValidationResult valResult = selectedNode.getValidationResult();
					details.setText(valResult.getDescription());
					
					//Correction panel
					correctionPanelContainer.removeAll();
					JPanel correctionPanel = createCorrectionPanel(valResult);
					if (correctionPanel != null) {
						correctionPanelContainer.add(correctionPanel, BorderLayout.CENTER);
						correctionPanel.revalidate();
					}
					
					if (valResult.getRelatedObject() != null)
						workflowEditor.selectWorkflowTreeItem(valResult.getRelatedObject());
				} else
					details.setText("");
			}
		});
	}
	
	/**
	 * Creates a panel with controls for correcting the problem uncovered by the validation
	 * @param valResult
	 * @return
	 */
	private JPanel createCorrectionPanel(WorkflowValidationResult valResult) {
		if (valResult == null)
			return null;
		
		//Missing attribute
		if (ActivtyValidationModule.TYPE_MISSING_NAME.equals(valResult.getType())
			|| ActivtyValidationModule.TYPE_MISSING_DESCRIPTION.equals(valResult.getType())
			|| WorkflowObjectValidationModule.TYPE_MISSING_NAME.equals(valResult.getType())
			|| WorkflowObjectValidationModule.TYPE_MISSING_DESCRIPTION.equals(valResult.getType())) {
			return new MissingAttributePanel(valResult);
		}

		//Textual description of solution
		if (ActivtyValidationModule.TYPE_MISSING_PORTS.equals(valResult.getType())
			|| CycleDetectionValidationModule.TYPE_CYCLE_IN_GRAPH_ACTICITY.equals(valResult.getType())
			|| LabelMatchValidationModule.TYPE_INPUT_PORT_LABEL_MISMATCH.equals(valResult.getType())
			|| MissingChildActivitiesValidationModule.TYPE_MISSING_FOR_LOOP_CHILD.equals(valResult.getType())
			|| MissingChildActivitiesValidationModule.TYPE_MISSING_GRAPH_CHILD.equals(valResult.getType())
			|| MissingChildActivitiesValidationModule.TYPE_MISSING_CHILD.equals(valResult.getType())
			|| WorkflowObjectValidationModule.TYPE_MISSING_ROOT_ACTIVITY.equals(valResult.getType())
			|| WorkflowObjectValidationModule.TYPE_ABSTRACT_WORKFLOW.equals(valResult.getType())
					) {
			return new ProblemResolutionDescriptionPanel(valResult);
		}
		
		//Missing data type
		if (MissingDatatypesValidationModule.TYPE_MISSING_INPUT_PORT_TYPE.equals(valResult.getType())
			|| MissingDatatypesValidationModule.TYPE_MISSING_INPUT_PORT_TYPE.equals(valResult.getType())
					) {
			return new MissingDataTypePanel(valResult);
		}
		
		//Unconnected data ports / type mismatch
		if (UnconnectedDataPortsValidationModule.TYPE_UNCONNECTED_INPUT_PORT.equals(valResult.getType())
			||	UnconnectedDataPortsValidationModule.TYPE_UNCONNECTED_OUTPUT_PORT.equals(valResult.getType())
			||	DatatypeMatchValidationModule.TYPE_INPUT_PORT_DATA_TYPE_MISMATCH.equals(valResult.getType())
			||	DatatypeMatchValidationModule.TYPE_OUTPUT_PORT_DATA_TYPE_MISMATCH.equals(valResult.getType())
			||	DataCardinalityMatchValidationModule.TYPE_INPUT_PORT_DATA_CARDINALITY_MISMATCH.equals(valResult.getType())
			||	DataCardinalityMatchValidationModule.TYPE_OUTPUT_PORT_DATA_CARDINALITY_MISMATCH.equals(valResult.getType())
				) {
			return new DataPortCorrectionPanel(workflow, workflowEditor, valResult, workflowEditor.getIdRegister(), 
					UnconnectedDataPortsValidationModule.TYPE_UNCONNECTED_INPUT_PORT.equals(valResult.getType()),
					DatatypeMatchValidationModule.TYPE_INPUT_PORT_DATA_TYPE_MISMATCH.equals(valResult.getType())
					||	DatatypeMatchValidationModule.TYPE_OUTPUT_PORT_DATA_TYPE_MISMATCH.equals(valResult.getType()),
					DataCardinalityMatchValidationModule.TYPE_INPUT_PORT_DATA_CARDINALITY_MISMATCH.equals(valResult.getType())
					||	DataCardinalityMatchValidationModule.TYPE_OUTPUT_PORT_DATA_CARDINALITY_MISMATCH.equals(valResult.getType()));
		}
		
			
		//TODO
		
		return null;
	}
}
