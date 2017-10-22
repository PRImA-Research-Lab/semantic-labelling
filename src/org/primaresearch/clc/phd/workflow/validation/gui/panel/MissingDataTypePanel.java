package org.primaresearch.clc.phd.workflow.validation.gui.panel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;
import org.primaresearch.clc.phd.workflow.gui.dialog.DataTypeDialog;
import org.primaresearch.clc.phd.workflow.validation.WorkflowValidationResult;
import org.primaresearch.clc.phd.workflow.validation.modules.MissingDatatypesValidationModule;

/**
 * Panel with data type selection control a data port of an activity
 * 
 * @author clc
 *
 */
public class MissingDataTypePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private DataPort dataPort;
	private JTextArea dataTypes;
	
	public MissingDataTypePanel(WorkflowValidationResult validationResult) {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(5, 5));
		
		dataPort = (DataPort)validationResult.getRelatedObject();
		
		JLabel lblCaption = new JLabel(getCaption(validationResult));
		add(lblCaption, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		//FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		add(panel, BorderLayout.SOUTH);
		
		JButton btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				apply();
			}
		});
		panel.add(btnApply);
		
		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		dataTypes = new JTextArea();
		panel_1.add(dataTypes, BorderLayout.CENTER);
		
		JButton button = new JButton("+");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showDefaultDataTypeSelection();
			}
		});
		panel_1.add(button, BorderLayout.EAST);
	}
	
	private String getCaption(WorkflowValidationResult validationResult) {
		if (MissingDatatypesValidationModule.TYPE_MISSING_INPUT_PORT_TYPE.equals(validationResult.getType()))
			return "Select an input port data type:";
		if (MissingDatatypesValidationModule.TYPE_MISSING_OUTPUT_PORT_TYPE.equals(validationResult.getType()))
			return "Select an output port data type:";
		return "Not suported";
	}
	
	private void showDefaultDataTypeSelection() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
	
				DataTypeDialog dlg = new DataTypeDialog();
				dlg.setModal(true);
				dlg.setVisible(true);
				
				if ((dlg.getSelectedType() != null) && (dlg.getSelectedType().length() > 0)) {
					if (dataPort instanceof OutputPort) //Replace
						dataTypes.setText(dlg.getSelectedType());
					else { //Add
						if (!dataTypes.getText().isEmpty())
							dataTypes.append("\n");
						dataTypes.append(dlg.getSelectedType());
					}
				}
			}
		});
	}
	
	private void apply() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Output port
				if (dataPort instanceof OutputPort) {
					((OutputPort)dataPort).setType(dataTypes.getText());
				}
				//Input port
				if (dataPort instanceof InputPort) {
					String text = dataTypes.getText();
					((InputPort)dataPort).getAllowedTypes().clear();
					try {
						for (int i=0; i<dataTypes.getLineCount(); i++) {
							String type = text.substring(dataTypes.getLineStartOffset(i), dataTypes.getLineEndOffset(i));
							((InputPort)dataPort).addAllowedType(type);
						}
					} catch(Exception exc) {
						exc.printStackTrace();
					}
				}
			}
		});
	}

}
