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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.primaresearch.clc.phd.repository.search.matching.Matcher;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityType;

/**
 * Dialogue for creating a new workflow activity. Shows a list of available activity types.
 * 
 * @author clc
 *
 */
public class CreateActivityDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JButton btnSearchActivity;
	private JList<ActivityType> activityTypeList;
	private ActivityType selectedActivityType = null;
	private Activity selectedActivity = null;

	/**
	 * Constructor
	 */
	public CreateActivityDialog(Frame parent, final Workflow workflow, final Activity parentActivity) {
		super(parent, "New Activity", true);
		setTitle("Add Activity");
		setResizable(false);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblSelectActivityType = new JLabel("Click on activity type to create new activity");
		lblSelectActivityType.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel.add(lblSelectActivityType, BorderLayout.NORTH);
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel.add(panel_1, BorderLayout.SOUTH);
		
		btnSearchActivity = new JButton("Search repository for activity");
		btnSearchActivity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						FindMatchingActivityDialog dlg = new FindMatchingActivityDialog(workflow, parentActivity, Matcher.MATCHING_FOR_ADDING_CHILD);
						dlg.setModal(true);
						dlg.setVisible(true);
						
						selectedActivity = dlg.getResultActivity();
						if (selectedActivity != null) {
							setVisible(false);
							dispatchEvent(new WindowEvent(CreateActivityDialog.this, WindowEvent.WINDOW_CLOSING));
						}
					}
				});
			}
		});
		//btnCreateActivity.setEnabled(false);
		panel_1.add(btnSearchActivity);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispatchEvent(new WindowEvent(CreateActivityDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		panel_1.add(btnCancel);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EmptyBorder(5, 0, 5, 0));
		panel.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		activityTypeList = new JList<ActivityType>();
		activityTypeList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				selectedActivityType = activityTypeList.getSelectedValue();
				setVisible(false);
				dispatchEvent(new WindowEvent(CreateActivityDialog.this, WindowEvent.WINDOW_CLOSING));
				//btnCreateActivity.setEnabled(activityTypeList.getSelectedValue() != null);
			}
		});
		activityTypeList.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panel_2.add(activityTypeList);
		
		activityTypeList.setModel(new ActivityTypeListModel());
	}
	
	
	/**
	 * Returns the activity type the user selected (if opted for creating a new activity)
	 * @return Activity type or null (invalid selection)
	 */
	public ActivityType getSelectedActivityType() {
		return selectedActivityType;
	}

	/**
	 * Returns the activity the user selected (if opted for using an existing activity from a repository)
	 * @return Activity type or null (invalid selection)
	 */
	public Activity getSelectedActivity() {
		return selectedActivity;
	}

	/**
	 * List model for activity types.
	 * 
	 * @author clc
	 *
	 */
	private static class ActivityTypeListModel extends DefaultListModel<ActivityType> {

		private static final long serialVersionUID = 1L;

		public ActivityTypeListModel() {
			addElement(ActivityType.ATOMIC_ACTIVITY);
			addElement(ActivityType.DIRECTED_GRAPH_ACTIVITY);
			addElement(ActivityType.FOR_LOOP_ACTIVITY);
			addElement(ActivityType.IF_ELSE_ACTIVITY);
		}
		
	}

}
