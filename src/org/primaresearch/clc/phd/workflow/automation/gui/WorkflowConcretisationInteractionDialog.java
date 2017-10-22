package org.primaresearch.clc.phd.workflow.automation.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.primaresearch.clc.phd.repository.search.Filter;
import org.primaresearch.clc.phd.repository.search.FilterChangeListener;
import org.primaresearch.clc.phd.repository.search.LabelWorkflowFilter;
import org.primaresearch.clc.phd.repository.search.gui.LabelWorkflowFilterPanel;
import org.primaresearch.clc.phd.repository.search.gui.WorkflowSearchResultGrid;
import org.primaresearch.clc.phd.repository.search.gui.WorkflowSearchResultView;
import org.primaresearch.clc.phd.repository.search.gui.WorkflowSearchResultView.WorkflowSearchResultItemListener;
import org.primaresearch.clc.phd.workflow.Workflow;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Dialogue for user interaction during workflow concretisation
 * 
 * @author clc
 *
 */
public class WorkflowConcretisationInteractionDialog extends JDialog implements FilterChangeListener, WorkflowSearchResultItemListener {
	
	private static final long serialVersionUID = 1L;
	private Collection<Workflow> workflows;
	private Collection<Workflow> selectedWorkflows = new LinkedList<Workflow>();
	private LabelWorkflowFilter labelFilter;
	private WorkflowSearchResultView resultView;
	private JLabel lblSearchResultTitle;

	public WorkflowConcretisationInteractionDialog(Collection<Workflow> workflows, String messageToUser) {
		super();
		this.workflows = workflows;
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setSize(1024, 768);
		setTitle("Interaction Required");
		((JPanel)getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		
		JLabel lblMessage = new JLabel(messageToUser);
		lblMessage.setBorder(new EmptyBorder(5, 5, 5, 5));
		lblMessage.setFont(new Font("Tahoma", Font.PLAIN, 14));
		getContentPane().add(lblMessage, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(300, 300));
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		getContentPane().add(scrollPane, BorderLayout.WEST);
		
		labelFilter = new LabelWorkflowFilter();
		labelFilter.init(workflows);
		labelFilter.addListener(this);
		LabelWorkflowFilterPanel labelWorkflowFilterPanel = new LabelWorkflowFilterPanel(labelFilter);
		scrollPane.setViewportView(labelWorkflowFilterPanel);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		lblSearchResultTitle = new JLabel("Title");
		lblSearchResultTitle.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblSearchResultTitle.setBorder(new EmptyBorder(5, 5, 5, 5));
		lblSearchResultTitle.setBackground(Color.WHITE);
		panel.add(lblSearchResultTitle, BorderLayout.NORTH);
		
		resultView = new WorkflowSearchResultGrid();
		panel.add(resultView.getComponent(), BorderLayout.CENTER);
		resultView.addListener(this);
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		JButton btnContinue = new JButton("Continue and auto-select");
		btnContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						setVisible(false);
					}
				});
			}
		});
		
		JButton btnDoNotReplace = new JButton("Do not replace");
		btnDoNotReplace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						selectedWorkflows.clear();
						setVisible(false);
					}
				});
			}
		});
		panel_1.add(btnDoNotReplace);
		panel_1.add(btnContinue);
		
		
		filterChanged(labelFilter);
	}
	
	public Collection<Workflow> getSelectedWorkflows() {
		return selectedWorkflows;
	}

	@Override
	public void filterChanged(Filter filter) {
		if (filter == labelFilter) {
			selectedWorkflows = labelFilter.filterWorkflows(workflows);
			
			resultView.update(selectedWorkflows);
			
			//Search result title
			String title = "Search results - ";
			if (selectedWorkflows.isEmpty())
				title += "No workflows found";
			else if (selectedWorkflows.size() == 1)
				title += selectedWorkflows.size() + " workflow found";
			else
				title += selectedWorkflows.size() + " workflows found";
			lblSearchResultTitle.setText(title);
			
		}
	}
	
	@Override
	public void workflowSearchResultItemClicked(final Workflow workflow) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				selectedWorkflows.clear();
				selectedWorkflows.add(workflow);
				setVisible(false);
			}
		});
	}
}
