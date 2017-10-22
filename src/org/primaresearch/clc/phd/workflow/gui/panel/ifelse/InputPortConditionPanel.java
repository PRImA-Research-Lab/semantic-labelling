package org.primaresearch.clc.phd.workflow.gui.panel.ifelse;

import javax.swing.JPanel;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.ifelse.InputPortCondition;
import org.primaresearch.clc.phd.workflow.gui.panel.DataPortPanel;

import javax.swing.JLabel;

/**
 * Details panel for if-else conditions that evaluate and input port.
 * 
 * @author clc
 *
 */
public class InputPortConditionPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private InputPortCondition condition;
	
	public InputPortConditionPanel(Workflow workflow, InputPortCondition condition) {
		super();
		this.condition = condition;
		
		JLabel lblDataPort = new JLabel("Data port:");
		add(lblDataPort);
		
		DataPortPanel dataPortPanel = new DataPortPanel(workflow, condition.getInputPort(), condition.getInputPort().getActivity(), null, null);
		add(dataPortPanel);
	}

}
