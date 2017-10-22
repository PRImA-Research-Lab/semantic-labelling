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

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.validation.WorkflowValidationResult;
import org.primaresearch.clc.phd.workflow.validation.modules.ActivtyValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.WorkflowObjectValidationModule;

/**
 * Panel with text edit field to update a text attribute of an activity or a workflow
 * 
 * @author clc
 *
 */
public class MissingAttributePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JTextArea textArea;
	private WorkflowValidationResult validationResult;
	
	public MissingAttributePanel(WorkflowValidationResult validationResult) {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(5, 5));
		
		this.validationResult = validationResult;
		
		JLabel lblCaption = new JLabel(getCaption(validationResult));
		add(lblCaption, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		//FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		add(panel, BorderLayout.SOUTH);
		
		JButton btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						apply();
					}
				});
			}
		});
		panel.add(btnApply);
		
		textArea = new JTextArea();
		add(textArea, BorderLayout.CENTER);
	}
	
	private String getCaption(WorkflowValidationResult validationResult) {
		if (ActivtyValidationModule.TYPE_MISSING_NAME.equals(validationResult.getType()))
			return "Enter activity name:";
		if (ActivtyValidationModule.TYPE_MISSING_DESCRIPTION.equals(validationResult.getType()))
			return "Enter activity description:";
		if (WorkflowObjectValidationModule.TYPE_MISSING_NAME.equals(validationResult.getType()))
			return "Enter workflow name:";
		if (WorkflowObjectValidationModule.TYPE_MISSING_DESCRIPTION.equals(validationResult.getType()))
			return "Enter workflow description:";
		return "Not suported";
	}
	
	private void apply() {
		if (ActivtyValidationModule.TYPE_MISSING_NAME.equals(validationResult.getType())) {
			Activity act = (Activity)validationResult.getRelatedObject();
			if (act != null)
				act.setCaption(textArea.getText());
		}
		else if (ActivtyValidationModule.TYPE_MISSING_DESCRIPTION.equals(validationResult.getType())) {
			Activity act = (Activity)validationResult.getRelatedObject();
			if (act != null)
				act.setDescription(textArea.getText());
		}
		else if (WorkflowObjectValidationModule.TYPE_MISSING_NAME.equals(validationResult.getType())) {
			Workflow w = (Workflow)validationResult.getRelatedObject();
			if (w != null)
				w.setName(textArea.getText());
		}
		else if (WorkflowObjectValidationModule.TYPE_MISSING_DESCRIPTION.equals(validationResult.getType())) {
			Workflow w = (Workflow)validationResult.getRelatedObject();
			if (w != null)
				w.setDescription(0, textArea.getText());
		}
		
	}

}
