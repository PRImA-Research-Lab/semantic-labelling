package org.primaresearch.clc.phd.workflow.validation.gui.panel;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.primaresearch.clc.phd.workflow.validation.WorkflowValidationResult;
import org.primaresearch.clc.phd.workflow.validation.modules.ActivtyValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.CycleDetectionValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.LabelMatchValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.MissingChildActivitiesValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.WorkflowObjectValidationModule;

/**
 * Simple text panel that shows possible user actions to resolve a validation problem
 * @author clc
 *
 */
public class ProblemResolutionDescriptionPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JTextArea textArea;
	
	public ProblemResolutionDescriptionPanel(WorkflowValidationResult validationResult) {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(5, 5));
		
		JLabel lblCaption = new JLabel("Follow the steps below to resolve the problem:");
		add(lblCaption, BorderLayout.NORTH);
		
		textArea = new JTextArea(getDescription(validationResult));
		textArea.setEditable(false);
		add(textArea, BorderLayout.CENTER);
	}
	
	
	private String getDescription(WorkflowValidationResult validationResult) {
		if (ActivtyValidationModule.TYPE_MISSING_PORTS.equals(validationResult.getType()))
			return "- Go to the workflow editor\n- Add at least one input or output data port";
		if (CycleDetectionValidationModule.TYPE_CYCLE_IN_GRAPH_ACTICITY.equals(validationResult.getType()))
			return "- Go to the workflow editor\n- Remove links from the child activities that form a cycle";
		if (LabelMatchValidationModule.TYPE_INPUT_PORT_LABEL_MISMATCH.equals(validationResult.getType()))
			return "- Go to the workflow editor\n- Check the 'Source' link of the input port";
		if (MissingChildActivitiesValidationModule.TYPE_MISSING_FOR_LOOP_CHILD.equals(validationResult.getType())
			|| MissingChildActivitiesValidationModule.TYPE_MISSING_GRAPH_CHILD.equals(validationResult.getType())
			|| MissingChildActivitiesValidationModule.TYPE_MISSING_CHILD.equals(validationResult.getType())
			|| WorkflowObjectValidationModule.TYPE_MISSING_ROOT_ACTIVITY.equals(validationResult.getType())
			)
			return "- Go to the workflow editor\n- Click 'Add Activity' to insert a new child activity";
		if (WorkflowObjectValidationModule.TYPE_ABSTRACT_WORKFLOW.equals(validationResult.getType()))
			return "- Go to the workflow editor\n- Replace all abstract activities or use 'Concretise'";
		return "Not suported";
	}

}
