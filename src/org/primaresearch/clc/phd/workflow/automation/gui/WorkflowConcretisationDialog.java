package org.primaresearch.clc.phd.workflow.automation.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.primaresearch.clc.phd.Pair;
import org.primaresearch.clc.phd.repository.WorkflowRepository;
import org.primaresearch.clc.phd.repository.io.LocalRepositoryIndex;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityIterator;
import org.primaresearch.clc.phd.workflow.automation.WorkflowConcretisationManager;
import org.primaresearch.clc.phd.workflow.automation.WorkflowConcretiser;

import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * Dialogue for making an abstract workflow concrete.
 * 
 * @author clc
 *
 */
public class WorkflowConcretisationDialog extends JDialog implements WorkflowConcretisationManager {

	private static final long serialVersionUID = 1L;

	private static volatile int lastSelectedFolderRepository = -1;

	private Workflow workflow;
	private LocalRepositoryIndex folderRepositoryIndex = LocalRepositoryIndex.getInstance();
	private JComboBox<String> repositoryComboBox;
	private JButton btnStart;
	private JTextPane textPane;
	private JRadioButton rdbtnAutomated;
	private JRadioButton rdbtnAssisted;
	private JSlider sliderStrictness;
	private JLabel labelStrictness;

	/**
	 * Constructor
	 * @param workflow
	 */
	public WorkflowConcretisationDialog(Workflow workflow) {
		super();
		this.workflow = workflow;
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setSize(1000,600);
		setTitle("Concretise Abstract Workflow");
		((JPanel)getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		
		JPanel toolbarPanel = new JPanel();
		getContentPane().add(toolbarPanel, BorderLayout.NORTH);
		toolbarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 10));
		
		JLabel lblActivitySource = new JLabel("Activity source");
		lblActivitySource.setFont(new Font("Tahoma", Font.PLAIN, 14));
		toolbarPanel.add(lblActivitySource);
		
		repositoryComboBox = new JComboBox<String>();
		repositoryComboBox.setFont(new Font("Tahoma", Font.PLAIN, 12));
		toolbarPanel.add(repositoryComboBox);
		
		btnStart = new JButton("Start");
		btnStart.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnStart.setEnabled(false);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						runConcretisation();
					}
				});
			}
		});
		toolbarPanel.add(btnStart);
		
		JPanel panel_1 = new JPanel();
		toolbarPanel.add(panel_1);
		
		rdbtnAutomated = new JRadioButton("Automated");
		rdbtnAutomated.setSelected(true);
		panel_1.add(rdbtnAutomated);
		
		rdbtnAssisted = new JRadioButton("Assisted");
		panel_1.add(rdbtnAssisted);
		
		ButtonGroup group = new ButtonGroup();
	    group.add(rdbtnAutomated);
	    group.add(rdbtnAssisted);
	    
	    JLabel lblStrictness = new JLabel("Strictness:");
	    toolbarPanel.add(lblStrictness);
	    
	    sliderStrictness = new JSlider();
	    sliderStrictness.setSnapToTicks(true);
	    sliderStrictness.setMinorTickSpacing(5);
	    sliderStrictness.setPreferredSize(new Dimension(100, 25));
	    sliderStrictness.setValue(90);
	    toolbarPanel.add(sliderStrictness);
	    
	    labelStrictness = new JLabel(sliderStrictness.getValue() + "%");
	    toolbarPanel.add(labelStrictness);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		
	    sliderStrictness.addChangeListener(new ChangeListener() {
	    	public void stateChanged(ChangeEvent arg0) {
	    		labelStrictness.setText(sliderStrictness.getValue() + "%");
	    	}
	    });
	    sliderStrictness.setMinimum(5);

		JButton btnDone = new JButton("Done");
		btnDone.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnDone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		panel.add(btnDone);
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setFont(new Font("Tahoma", Font.PLAIN, 13));
		textPane.setBorder(new LineBorder(Color.GRAY));
		getContentPane().add(textPane, BorderLayout.CENTER);
		//Fill repository combobox
		for (int i=0; i<folderRepositoryIndex.getSize(); i++) {
			repositoryComboBox.addItem(folderRepositoryIndex.getFolder(i));
		}
		repositoryComboBox.addItem("Combined Repository");
		
		repositoryComboBox.addItem("[Select repository]");
		if (lastSelectedFolderRepository >= 0)
			repositoryComboBox.setSelectedIndex(lastSelectedFolderRepository);
		else 
			repositoryComboBox.setSelectedIndex(repositoryComboBox.getItemCount()-1);
		repositoryComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnStart.setEnabled(repositoryComboBox.getSelectedIndex() < repositoryComboBox.getItemCount()-1);
			}
		});

	}
	
	private void runConcretisation() {
	
		//Create concretiser
		WorkflowConcretiser concretiser = new WorkflowConcretiser(rdbtnAutomated.isSelected(), this);
		concretiser.setStrictness((double)sliderStrictness.getValue());
		
		//Run
		Collection<Pair<Activity, Activity>> result;
		result = concretiser.concretise(workflow);
		
		//Show result
		StringBuilder text = new StringBuilder();
		for (Iterator<Pair<Activity, Activity>> it = result.iterator(); it.hasNext();) {
			Pair<Activity, Activity> repl = it.next();
			if (text.length() > 0)
				text.append("\n");
			text.append("'");
			text.append(repl.left.getCaption());
			text.append("'  replaced with  '");
			text.append(repl.right.getCaption());
			text.append("'");
		}
		text.append("\n\n");
		if (workflow.getRootActivity().isAbstract())
			text.append("Not all activities could be made concrete. The workflow is still abstract.");
		else
			text.append("Successful. The workflow is now executable.");
		textPane.setText(text.toString());
	}

	@Override
	public Map<Activity, Workflow> getAvailableActivities() {
		int i = repositoryComboBox.getSelectedIndex();
		if (i >= folderRepositoryIndex.getSize() + 1) //(+1 for combined repository)
			return null;
		WorkflowRepository activitySource = folderRepositoryIndex.getListModel().get(i);

		Collection<Workflow> workflows = activitySource.getWorkflows();
		
		//Collect root activities
		Map<Activity, Workflow> activities = new TreeMap<Activity, Workflow>();
		for (Iterator<Workflow> it = workflows.iterator(); it.hasNext(); ) {
			Workflow actWorkflow = it.next();
			if (actWorkflow != null && actWorkflow.getRootActivity() != null) {
				Activity act = actWorkflow.getRootActivity();
				
				//Avoid using abstract activities that are already part of the workflow
				boolean canUse = true;
				for (ActivityIterator itAct = workflow.getActivities(); itAct.hasNext(); ) {
					Activity localAct = itAct.next();
					
					if (localAct.isAbstract() && localAct.getCaption().equals(act.getCaption())) {
						canUse = false;
						break;
					}
				}
				if (canUse)
					activities.put(act, actWorkflow);
			}
		}
		return activities;
	}
	
	@Override
	public List<Pair<Activity, Workflow>> refineMatch(List<Pair<Activity, Workflow>> bestMatchingActivities, String message) {

		//Create temporary workflows for the activities
		Collection<Workflow> workflows = new LinkedList<Workflow>();
		for (Iterator<Pair<Activity, Workflow>> it = bestMatchingActivities.iterator(); it.hasNext(); ) {
			workflows.add(it.next().right);
		}
		
		//Open dialogue
		WorkflowConcretisationInteractionDialog dlg = new WorkflowConcretisationInteractionDialog(workflows, message);
		
		dlg.setVisible(true);
		
		Collection<Workflow> selectedWorkflows = dlg.getSelectedWorkflows();
		
		if (selectedWorkflows == null || selectedWorkflows.isEmpty())
			return null;
		
		List<Pair<Activity, Workflow>> selectedActivities = new LinkedList<Pair<Activity, Workflow>>();
		for (Iterator<Workflow> it = selectedWorkflows.iterator(); it.hasNext(); ) {
			Workflow w = it.next();
			for (Iterator<Pair<Activity, Workflow>> itAct = bestMatchingActivities.iterator(); itAct.hasNext();) {
				Pair<Activity, Workflow> p = itAct.next();
				if (p.right == w) {
					selectedActivities.add(new Pair<Activity, Workflow>(p.left, w));
					break;
				}
			}
		}
		
		return selectedActivities; 
	}

	@Override
	public void onActivityReplaced(Activity oldActivity, Activity replacement) {
		StringBuilder text = new StringBuilder();
		text.append(textPane.getText());

		text.append("\n");
		text.append("'");
		text.append(oldActivity.getCaption());
		text.append("'  replaced with  '");
		text.append(replacement.getCaption());
		text.append("'");
		text.append("\n");
		
		textPane.setText(text.toString());
	}

}
