package org.primaresearch.clc.phd.workflow.gui.panel.ifelse;

import javax.swing.JPanel;

import org.primaresearch.clc.phd.workflow.activity.ifelse.CombinedCondition;
import javax.swing.JLabel;

/**
 * Details panel for if-else conditions that implement AND or OR
 * @author clc
 *
 */
public class CombinedConditionPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	public CombinedConditionPanel(CombinedCondition condition) {
		
		JLabel lblDetails = new JLabel(condition.usesAndOperator() ? 
										"Combines the child conditions using AND"
									:	"Combines the child conditions using OR");
		add(lblDetails);
		
	}

}
