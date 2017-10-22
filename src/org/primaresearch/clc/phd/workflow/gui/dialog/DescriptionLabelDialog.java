package org.primaresearch.clc.phd.workflow.gui.dialog;

import javax.swing.JDialog;

import org.primaresearch.clc.phd.workflow.DescriptionWithLabels;
import org.primaresearch.clc.phd.workflow.gui.panel.LabelListPanel;

import javax.swing.JLabel;

import java.awt.BorderLayout;

/**
 * Dialog to manage labels for description objects (that can have labels)
 * 
 * @author clc
 *
 */
public class DescriptionLabelDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * @param description
	 */
	public DescriptionLabelDialog(final DescriptionWithLabels description) {
		super();
		setSize(600, 300);
		setTitle("Description Labels");
		
		JLabel lblLabels = new JLabel("Labels:");
		getContentPane().add(lblLabels, BorderLayout.NORTH);
		
		getContentPane().add(new LabelListPanel(description), BorderLayout.CENTER);
	}

}
