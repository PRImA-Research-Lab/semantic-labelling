package org.primaresearch.clc.phd.workflow.validation.gui.panel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.automation.gui.WorkflowConcretisationDialog;
import org.primaresearch.clc.phd.workflow.data.DataConversionHelper;
import org.primaresearch.clc.phd.workflow.data.LoopHelper;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.gui.WorkflowEditor;
import org.primaresearch.clc.phd.workflow.gui.panel.DataPortPanel;
import org.primaresearch.clc.phd.workflow.validation.WorkflowValidationResult;
import org.primaresearch.clc.phd.workflow.validation.modules.DataCardinalityMatchValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.DatatypeMatchValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.UnconnectedDataPortsValidationModule;

/**
 * Validation result panel with data port editing feature.
 * 
 * @author clc
 *
 */
public class DataPortCorrectionPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private DataPort dataPort;
	
	public DataPortCorrectionPanel(final Workflow workflow, final WorkflowEditor workflowEditor, WorkflowValidationResult validationResult, IdGenerator idRegister,
			boolean showDataTablePanel, boolean showConversionPanel, boolean showAddLoopPanel) {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(5, 5));
		
		dataPort = (DataPort)validationResult.getRelatedObject();
		
		JLabel lblCaption = new JLabel(getCaption(validationResult));
		add(lblCaption, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		//FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		add(panel, BorderLayout.SOUTH);
		
		JPanel centrePanel = new JPanel();
		add(centrePanel, BorderLayout.CENTER);
		centrePanel.setLayout(new GridLayout(1, 2, 0, 5));
		
		//Button to add data table
		if (showDataTablePanel) {
			JPanel panel_1 = new JPanel();
			centrePanel.add(panel_1);
			
			JButton btnAddDataTable = new JButton("Add Data Table");
			panel_1.add(btnAddDataTable);
		}
		
		//Button to add data conversion step
		if (showConversionPanel) {
			JPanel panel_1 = new JPanel();
			centrePanel.add(panel_1);
			
			JButton btnAddConverter = new JButton("Add Converter");
			panel_1.add(btnAddConverter);
			btnAddConverter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							DataConversionHelper helper = new DataConversionHelper(workflow, workflowEditor);
							helper.addConversionStep(dataPort);
							
							//Invoke concretisation dialogue
							WorkflowConcretisationDialog dlg = new WorkflowConcretisationDialog(workflow);
							dlg.setVisible(true);
							
							//Refresh tree
							workflowEditor.refreshTree();
							
							//TODO Refresh?
					    }
					});
				}
			});
		}
		
		//Button to add loop activity
		if (showAddLoopPanel) {
			JPanel panel_1 = new JPanel();
			centrePanel.add(panel_1);
			
			JButton btnAddLoop = new JButton("Add Loop Activity");
			panel_1.add(btnAddLoop);
			btnAddLoop.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							LoopHelper helper = new LoopHelper(workflow, workflowEditor);
							helper.addLoopActivity(dataPort);
							
							//Refresh tree
							workflowEditor.refreshTree();
							
							//TODO Refresh?
					    }
					});
				}
			});
		}
		
		DataPortPanel portPanel = new DataPortPanel(workflow, dataPort, dataPort.getActivity(), null, idRegister);
		centrePanel.add(portPanel);
	}
	
	private String getCaption(WorkflowValidationResult validationResult) {
		if (UnconnectedDataPortsValidationModule.TYPE_UNCONNECTED_INPUT_PORT.equals(validationResult.getType()))
			return "Select an input port source (create a data table for external data if necessary):";
		if (UnconnectedDataPortsValidationModule.TYPE_UNCONNECTED_OUTPUT_PORT.equals(validationResult.getType()))
			return "Select an output port forwarding target:";
		if (DatatypeMatchValidationModule.TYPE_INPUT_PORT_DATA_TYPE_MISMATCH.equals(validationResult.getType()))
			return "Check data type of input port and it's source:";
		if (DatatypeMatchValidationModule.TYPE_OUTPUT_PORT_DATA_TYPE_MISMATCH.equals(validationResult.getType()))
			return "Check data type of output port and it's target:";
		if (DataCardinalityMatchValidationModule.TYPE_INPUT_PORT_DATA_CARDINALITY_MISMATCH.equals(validationResult.getType()))
			return "Check data cardinality of input port and it's source:";
		if (DataCardinalityMatchValidationModule.TYPE_OUTPUT_PORT_DATA_CARDINALITY_MISMATCH.equals(validationResult.getType()))
			return "Check data cardinality of output port and it's forwarded port:";
		return "Not suported";
	}

}
