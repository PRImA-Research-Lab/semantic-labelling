package org.primaresearch.clc.phd.workflow.gui.panel.ifelse;

import javax.swing.JPanel;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.ifelse.ComparisonCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.ComparisonCondition.ComparisonConditionOperator;
import org.primaresearch.clc.phd.workflow.gui.panel.DataPortPanel;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Details panel for if-else conditions that compare two values using a specified operator (e.g. >=).
 * 
 * @author clc
 *
 */
public class ComparisonConditionPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private ComparisonCondition condition;
	
	public ComparisonConditionPanel(Workflow workflow, final ComparisonCondition condition) {
		super();
		this.condition = condition;
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblLeft = new JLabel("Left:");
		GridBagConstraints gbc_lblLeft = new GridBagConstraints();
		gbc_lblLeft.insets = new Insets(0, 0, 5, 5);
		gbc_lblLeft.gridx = 0;
		gbc_lblLeft.gridy = 0;
		add(lblLeft, gbc_lblLeft);
		
		DataPortPanel leftOperandPanel = new DataPortPanel(workflow, condition.getLeftOperand(), condition.getLeftOperand().getActivity(), null, null);
		GridBagConstraints gbc_lblLeftOp = new GridBagConstraints();
		gbc_lblLeftOp.insets = new Insets(0, 0, 5, 5);
		gbc_lblLeftOp.gridx = 1;
		gbc_lblLeftOp.gridy = 0;
		add(leftOperandPanel, gbc_lblLeftOp);
		
		JLabel lblOperation = new JLabel("Operation");
		GridBagConstraints gbc_lblOperation = new GridBagConstraints();
		gbc_lblOperation.anchor = GridBagConstraints.EAST;
		gbc_lblOperation.insets = new Insets(0, 0, 5, 5);
		gbc_lblOperation.gridx = 0;
		gbc_lblOperation.gridy = 1;
		add(lblOperation, gbc_lblOperation);
		
		final JComboBox<String> comboBoxOperator = new JComboBox<String>();
		GridBagConstraints gbc_comboBoxOperator = new GridBagConstraints();
		gbc_comboBoxOperator.insets = new Insets(0, 0, 5, 0);
		gbc_comboBoxOperator.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxOperator.gridx = 1;
		gbc_comboBoxOperator.gridy = 1;
		add(comboBoxOperator, gbc_comboBoxOperator);
		comboBoxOperator.addItem("=");
		comboBoxOperator.addItem("!=");
		comboBoxOperator.addItem("<");
		comboBoxOperator.addItem("<=");
		comboBoxOperator.addItem(">");
		comboBoxOperator.addItem(">=");
		if (condition.getOperator() != null) {
			if (condition.getOperator() == ComparisonConditionOperator.Equals)
				comboBoxOperator.setSelectedIndex(0);
			else if (condition.getOperator() == ComparisonConditionOperator.NotEquals)
				comboBoxOperator.setSelectedIndex(1);
			else if (condition.getOperator() == ComparisonConditionOperator.LessThan)
				comboBoxOperator.setSelectedIndex(2);
			else if (condition.getOperator() == ComparisonConditionOperator.LessOrEqual)
				comboBoxOperator.setSelectedIndex(3);
			else if (condition.getOperator() == ComparisonConditionOperator.GreaterThan)
				comboBoxOperator.setSelectedIndex(4);
			else if (condition.getOperator() == ComparisonConditionOperator.GreaterOrEqual)
				comboBoxOperator.setSelectedIndex(5);
			
		} else
			comboBoxOperator.setSelectedIndex(0);
		
		comboBoxOperator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String selOp = (String)comboBoxOperator.getSelectedItem();
				if ("=".equals(selOp))
					condition.setOperator(ComparisonConditionOperator.Equals);
				else if ("!=".equals(selOp))
					condition.setOperator(ComparisonConditionOperator.NotEquals);
				else if ("<".equals(selOp))
					condition.setOperator(ComparisonConditionOperator.LessThan);
				else if ("<=".equals(selOp))
					condition.setOperator(ComparisonConditionOperator.LessOrEqual);
				else if (">".equals(selOp))
					condition.setOperator(ComparisonConditionOperator.GreaterThan);
				else if (">=".equals(selOp))
					condition.setOperator(ComparisonConditionOperator.GreaterOrEqual);
			}
		});

		JLabel lblRight = new JLabel("Right:");
		GridBagConstraints gbc_lblRight = new GridBagConstraints();
		gbc_lblRight.insets = new Insets(0, 0, 0, 5);
		gbc_lblRight.gridx = 0;
		gbc_lblRight.gridy = 2;
		add(lblRight, gbc_lblRight);
		
		DataPortPanel rightOperandPanel = new DataPortPanel(workflow, condition.getRightOperand(), condition.getRightOperand().getActivity(), null, null);
		GridBagConstraints gbc_lblRightOp = new GridBagConstraints();
		gbc_lblRightOp.insets = new Insets(0, 0, 5, 5);
		gbc_lblRightOp.gridx = 1;
		gbc_lblRightOp.gridy = 2;
		add(rightOperandPanel, gbc_lblRightOp);

	}

}
