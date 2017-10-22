package org.primaresearch.clc.phd.workflow.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;
import org.primaresearch.clc.phd.workflow.gui.model.DataPortAlignment;
import org.primaresearch.clc.phd.workflow.gui.panel.ActivityDetails;
import org.primaresearch.clc.phd.workflow.gui.panel.DataPortAlignmentPanel;

/**
 * Dialogue to choose what activity details to copy from a current activity
 * to a replacement activity.
 * 
 * @author clc
 *
 */
public class ReplaceActivityDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private Activity newActivity;
	private Activity outputActivity = null;
	
	private DataPortAlignment<InputPort> inputPortAlignment = null;
	private DataPortAlignment<OutputPort> outputPortAlignment = null;
	private DataPortAlignmentPanel inputPortTable;
	private DataPortAlignmentPanel outputPortTable;
	
	/**
	 * Constructor
	 * @param currentActivity
	 * @param newActivity
	 */
	public ReplaceActivityDialog(Workflow workflow, Activity currentActivity, Activity newActivity) {
		super();
		setModal(true);
		setTitle("Replace Activity");
		setSize(1024,768);
		this.newActivity = newActivity.clone();
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		JButton btnReplace = new JButton("Replace now");
		btnReplace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inputPortAlignment.copyDataToAlignedPorts(); //TODO Test
				outputPortAlignment.copyDataToAlignedPorts();
				outputActivity = ReplaceActivityDialog.this.newActivity;
				ReplaceActivityDialog.this.setVisible(false);
			}
		});
		
		JButton btnAutoalign = new JButton("Auto-align");
		btnAutoalign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						inputPortAlignment.autoAlign();
						outputPortAlignment.autoAlign();
						inputPortTable.refresh();
						outputPortTable.refresh();
					}
				});
			}
		});
		panel.add(btnAutoalign);
		
		JLabel label = new JLabel("   ");
		panel.add(label);
		panel.add(btnReplace);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ReplaceActivityDialog.this.setVisible(false);
			}
		});
		panel.add(btnCancel);
		
		JPanel currentActivityHeaderPanel = new JPanel();
		currentActivityHeaderPanel.setBackground(new Color(215, 215, 227));
		getContentPane().add(currentActivityHeaderPanel, BorderLayout.NORTH);

		
		Color replacementColor = new Color(210, 247, 196);
		
		//Input ports
		inputPortAlignment = new DataPortAlignment<InputPort>(workflow, currentActivity.getInputPorts(), currentActivity,
																newActivity.getInputPorts(), newActivity);
		inputPortTable = new DataPortAlignmentPanel(workflow, inputPortAlignment, currentActivityHeaderPanel.getBackground(),
															replacementColor, true);
		

		JScrollPane inputScrollPane = new JScrollPane(inputPortTable);
		inputScrollPane.setPreferredSize(new Dimension(250, 500));
		getContentPane().add(inputScrollPane, BorderLayout.WEST);
		
		//Output ports
		outputPortAlignment = new DataPortAlignment<OutputPort>(workflow, currentActivity.getOutputPorts(), currentActivity,
				newActivity.getOutputPorts(), newActivity);
		
		outputPortTable = new DataPortAlignmentPanel(workflow, outputPortAlignment, currentActivityHeaderPanel.getBackground(),
								replacementColor, false);
		
		JScrollPane outputScrollPane = new JScrollPane(outputPortTable);
		inputScrollPane.setPreferredSize(new Dimension(250, 500));
		getContentPane().add(outputScrollPane, BorderLayout.EAST);
		
		
		
		JLabel lblCurrentActivity = new JLabel("Current Activity");
		lblCurrentActivity.setFont(new Font("Tahoma", Font.PLAIN, 15));
		currentActivityHeaderPanel.add(lblCurrentActivity);
		
		JPanel newActivityPanel = new JPanel();
		getContentPane().add(newActivityPanel, BorderLayout.CENTER);
		newActivityPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel newActivityHeaderPanel = new JPanel();
		newActivityHeaderPanel.setBackground(replacementColor);
		newActivityPanel.add(newActivityHeaderPanel, BorderLayout.NORTH);
		
		JLabel lblReplacementActivity = new JLabel("Replacement Activity");
		lblReplacementActivity.setFont(new Font("Tahoma", Font.PLAIN, 15));
		newActivityHeaderPanel.add(lblReplacementActivity);
		
		//Activity details
		ActivityDetails newActivityDetails = new ActivityDetails(workflow, null, null); //TODO
		newActivityDetails.setBackground(replacementColor);
		newActivityPanel.add(newActivityDetails, BorderLayout.CENTER);
		newActivityDetails.refresh(newActivity);
	}
	
	/**
	 * Returns the replacement activity.
	 */
	public Activity getNewActivity() {
		return outputActivity;
	}




	
}
