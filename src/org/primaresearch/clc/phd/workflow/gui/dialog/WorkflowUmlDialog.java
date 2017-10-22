package org.primaresearch.clc.phd.workflow.gui.dialog;

import java.awt.BorderLayout;

import javax.swing.JDialog;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.gui.panel.WorkflowUmlView;

/**
 * Experimental dialog with UML view of workflow
 * @author clc
 *
 */
public class WorkflowUmlDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	public WorkflowUmlDialog(Workflow workflow) {
		super();
		setSize(1024, 768);
		setTitle("Workflow UML View");
		
		this.getContentPane().add(new WorkflowUmlView(workflow), BorderLayout.CENTER);
	}

}
