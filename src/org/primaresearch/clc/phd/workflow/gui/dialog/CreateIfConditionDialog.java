package org.primaresearch.clc.phd.workflow.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.primaresearch.clc.phd.workflow.activity.ifelse.IfCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity.IfBranch;

/**
 * Dialogue for creating a new if-else condition. Shows a list of available condition types.
 * 
 * @author clc
 *
 */
public class CreateIfConditionDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JList<IfConditionType> conditionTypeList;
	private IfConditionType selectedConditionType = null;

	/**
	 * Constructor
	 */
	public CreateIfConditionDialog(Frame parent, final IfBranch ifBranch, final IfCondition parentCondition) {
		super(parent, "New Condition", true);
		setTitle("Add Condition");
		setResizable(false);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblSelectActivityType = new JLabel("Click on condition type to create new condition");
		lblSelectActivityType.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel.add(lblSelectActivityType, BorderLayout.NORTH);
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel.add(panel_1, BorderLayout.SOUTH);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispatchEvent(new WindowEvent(CreateIfConditionDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		panel_1.add(btnCancel);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EmptyBorder(5, 0, 5, 0));
		panel.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		conditionTypeList = new JList<IfConditionType>();
		conditionTypeList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				selectedConditionType = conditionTypeList.getSelectedValue();
				setVisible(false);
				dispatchEvent(new WindowEvent(CreateIfConditionDialog.this, WindowEvent.WINDOW_CLOSING));
				//btnCreateActivity.setEnabled(activityTypeList.getSelectedValue() != null);
			}
		});
		conditionTypeList.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panel_2.add(conditionTypeList);
		
		conditionTypeList.setModel(new ConditionTypeListModel());
	}
	
	
	/**
	 * Returns the condition type the user selected
	 * @return Condition type or null (invalid selection)
	 */
	public IfConditionType getSelectedConditionType() {
		return selectedConditionType;
	}

	/**
	 * List model for condition types.
	 * 
	 * @author clc
	 *
	 */
	private static class ConditionTypeListModel extends DefaultListModel<IfConditionType> {

		private static final long serialVersionUID = 1L;

		public ConditionTypeListModel() {
			addElement(IfConditionType.NOT);
			addElement(IfConditionType.AND);
			addElement(IfConditionType.OR);
			addElement(IfConditionType.INPUT);
			addElement(IfConditionType.COMPARISON);
		}
		
	}
	
	public static class IfConditionType {
		public static IfConditionType NOT = new IfConditionType("NOT");
		public static IfConditionType AND = new IfConditionType("AND");
		public static IfConditionType OR = new IfConditionType("OR");
		public static IfConditionType INPUT = new IfConditionType("Input Port");
		public static IfConditionType COMPARISON = new IfConditionType("Comparison");
		
		private String id;
		
		private IfConditionType(String id) {
			this.id = id;
		}
		
		public final String toString() {
			return id;
		}
	}

}
