package org.primaresearch.clc.phd.workflow.gui.panel.ifelse;

import javax.swing.JPanel;
import javax.swing.JLabel;

/**
 * Details panel for NOT if-else condition
 * @author clc
 *
 */
public class NotConditionPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public NotConditionPanel() {
		
		JLabel lblInvertsTheBoolean = new JLabel("Inverts the boolean value of the child condition");
		add(lblInvertsTheBoolean);
	}

}
