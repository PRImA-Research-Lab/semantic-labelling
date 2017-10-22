package org.primaresearch.clc.phd.workflow.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.primaresearch.clc.phd.repository.ChildActivityRepository;
import org.primaresearch.clc.phd.repository.WorkflowRepository;
import org.primaresearch.clc.phd.repository.io.LocalRepositoryIndex;
import org.primaresearch.clc.phd.repository.search.Filter;
import org.primaresearch.clc.phd.repository.search.FilterChangeListener;
import org.primaresearch.clc.phd.repository.search.LabelWorkflowFilter;
import org.primaresearch.clc.phd.repository.search.TextSearchWorkflowFilter;
import org.primaresearch.clc.phd.repository.search.gui.LabelWorkflowFilterPanel;
import org.primaresearch.clc.phd.repository.search.gui.WorkflowSearchTool;
import org.primaresearch.clc.phd.repository.search.gui.WorkflowSearchResultView.WorkflowSearchResultItemListener;
import org.primaresearch.clc.phd.repository.search.matching.ActivityDataTypeMatcher;
import org.primaresearch.clc.phd.repository.search.matching.ActivityLabelMatcher;
import org.primaresearch.clc.phd.repository.search.matching.ActivityMatchingResultView;
import org.primaresearch.clc.phd.repository.search.matching.ActivityMatchingResultView.ActivityMatchingResultItemListener;
import org.primaresearch.clc.phd.repository.search.matching.CompositeActivityMatcher;
import org.primaresearch.clc.phd.repository.search.matching.MatchValue;
import org.primaresearch.clc.phd.repository.search.matching.Matcher;
import org.primaresearch.clc.phd.repository.search.matching.gui.ActivityMatchingResultGrid;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;

import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Dialogue to find matching activities from a repository to replace a selected workflow activity or add a child activity.
 * 
 * @author clc
 *
 */
public class FindMatchingActivityDialog extends JDialog implements FilterChangeListener, ActivityMatchingResultItemListener, ActionListener {

	private static final long serialVersionUID = 1L;
	
	private static volatile int lastSelectedFolderRepository = 0;
	
	private Workflow workflow;
	private Activity referenceActivity;
	private Activity resultActivity = null;
	private LocalRepositoryIndex folderRepositoryIndex = LocalRepositoryIndex.getInstance();
	private JComboBox<String> repositoryComboBox;
	private ActivityMatchingResultView resultGrid = new ActivityMatchingResultGrid();
	private JCheckBox chckbxMatchLabels;
	private JCheckBox chckbxMatchDataTypes;
	private LabelWorkflowFilterPanel labelFilterPanel;
	private LabelWorkflowFilter labelFilter;
	private TextSearchWorkflowFilter textSearchFilter = new TextSearchWorkflowFilter();
	private WorkflowRepository repository = null;
	private JScrollPane filterScrollPane;
	private int matchingType;
	private JLabel lblSearch;
	private JTextField textFieldSearch;
	private WorkflowSearchTool workflowSearchDialog = null;
	
	/**
	 * Constructor
	 * @param referenceActivity
	 * @param matchingType Use Matcher.MATCHING_FOR_REPLACING to find activities similar to the reference activity. Use Matcher.MATCHING_FOR_ADDING_CHILD to find suitable child activities.
	 */
	public FindMatchingActivityDialog(Workflow workflow, Activity referenceActivity, int matchingType) {
		super();
		this.referenceActivity = referenceActivity;
		this.matchingType = matchingType;
		this.workflow = workflow;
		
		if (matchingType == Matcher.MATCHING_FOR_REPLACING)
			setTitle("Replace with Matching Activity");
		else 
			setTitle("Add Activity From Repository");
		
		setSize(1200,800);
		
		JPanel toolbarPanel = new JPanel();
		getContentPane().add(toolbarPanel, BorderLayout.NORTH);
		toolbarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		repositoryComboBox = new JComboBox<String>();
		toolbarPanel.add(repositoryComboBox);
		
		chckbxMatchLabels = new JCheckBox("Match labels");
		chckbxMatchLabels.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				match();
			}
		});
		chckbxMatchLabels.setSelected(true);
		toolbarPanel.add(chckbxMatchLabels);
		
		chckbxMatchDataTypes = new JCheckBox("Match data types");
		chckbxMatchDataTypes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				match();
			}
		});
		chckbxMatchDataTypes.setSelected(true);
		toolbarPanel.add(chckbxMatchDataTypes);
		
		filterScrollPane = new JScrollPane();
		getContentPane().add(filterScrollPane, BorderLayout.WEST);
		filterScrollPane.setPreferredSize(new Dimension(300, 300));
		filterScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		//scrollPane.setMinimumSize(new Dimension(200, 300));
		

		//Fill repository combobox
		for (int i=0; i<folderRepositoryIndex.getSize(); i++) {
			repositoryComboBox.addItem(folderRepositoryIndex.getFolder(i));
		}
		repositoryComboBox.addItem("Combined Repository");
		repositoryComboBox.addItem("Activities of a selected workflow...");
		repositoryComboBox.addItem("[Select repository]");
		repositoryComboBox.addActionListener(this);
		
		resultGrid.addListener(this);
		getContentPane().add(resultGrid.getComponent(), BorderLayout.CENTER);

		lblSearch = new JLabel("Search:");
		toolbarPanel.add(lblSearch);
		
		textFieldSearch = new JTextField();
		textFieldSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textSearchFilter.setSearchString(textFieldSearch.getText());
				match();
			}
		});
		toolbarPanel.add(textFieldSearch);
		textFieldSearch.setColumns(10);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				repositoryComboBox.setSelectedIndex(lastSelectedFolderRepository);
			}
		});
	}
	
	//Get selected repository
	private WorkflowRepository getRepository() {
		
		int i = repositoryComboBox.getSelectedIndex();
		
		if (i < folderRepositoryIndex.getSize() + 1) { //+1 for combined repository
		
			repository = folderRepositoryIndex.getListModel().get(i);
			return repository;
		}
		if (i == folderRepositoryIndex.getSize() + 1) {
			
			//Select workflow
			workflowSearchDialog = new WorkflowSearchTool(repository, new WorkflowSearchResultItemListener() {
				@Override
				public void workflowSearchResultItemClicked(Workflow workflow) {
					repository = new ChildActivityRepository(workflow);
					workflowSearchDialog.setVisible(false);
				}
			});
			workflowSearchDialog.setModal(true);
			workflowSearchDialog.setVisible(true);
			return repository;
		}
		repository = null;
		return null;
	}

	
	/**
	 * Run the matching
	 */
	private void match() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//getRepository();		
				
				if (repository == null)
					return;
				
				//Label filter
				Collection<Workflow> filtered = labelFilter.filterWorkflows(repository.getWorkflows());
				
				//Text search filter
				filtered = textSearchFilter.filterWorkflows(filtered);
		
				//Collect root activities
				Collection<Activity> activities = new LinkedList<Activity>();
				for (Iterator<Workflow> it = filtered.iterator(); it.hasNext(); ) {
					Workflow workflow = it.next();
					if (workflow != null && workflow.getRootActivity() != null)
						activities.add(workflow.getRootActivity());
				}
				
				List<MatchValue<Activity>> result = null;
				if (referenceActivity != null) {
					//Matching
					// Select matcher
					Matcher<Activity> matcher = null;
					if (chckbxMatchLabels.isSelected() && chckbxMatchDataTypes.isSelected()) {
						CompositeActivityMatcher compMatcher = new CompositeActivityMatcher();
						compMatcher.addSubMatcher(new ActivityLabelMatcher(referenceActivity, matchingType));
						compMatcher.addSubMatcher(new ActivityDataTypeMatcher(referenceActivity, matchingType));
						matcher = compMatcher;
					}
					else if (chckbxMatchLabels.isSelected())
						matcher = new ActivityLabelMatcher(referenceActivity, matchingType);
					else if (chckbxMatchDataTypes.isSelected())
						matcher = new ActivityDataTypeMatcher(referenceActivity, matchingType);
					else
						resultGrid.clear();
					// Run matching
					result = matcher.match(activities);
				}
				//No reference activity (add all activities with 100% to result)
				else {
					result = new LinkedList<MatchValue<Activity>>();
					for (Iterator<Activity> it = activities.iterator(); it.hasNext(); ) {
						result.add(new SimpleMatchValue(it.next()));
					}
				}
				
				//Display result
				resultGrid.update(result);
			}
		});
	}

	@Override
	public void filterChanged(Filter filter) {
		match();		
	}

	@Override
	public void activityMatchingResultItemClicked(final MatchValue<Activity> matchValue) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				//Looking for replacement?
				if (matchingType == Matcher.MATCHING_FOR_REPLACING) {
					ReplaceActivityDialog dlg = new ReplaceActivityDialog(workflow, referenceActivity, matchValue.getObject());
					dlg.setVisible(true);
					
					Activity replacement = dlg.getNewActivity();
					if (replacement != null) {
						FindMatchingActivityDialog.this.resultActivity = replacement;
						setVisible(false);
					}
				}
				//Looking for new child activity
				else {
					FindMatchingActivityDialog.this.resultActivity = matchValue.getObject().clone();
					setVisible(false);
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				WorkflowRepository rep = getRepository();
				if (rep == null)
					return;
				
				//Save selection index
				lastSelectedFolderRepository = repositoryComboBox.getSelectedIndex();
				
				labelFilter = new LabelWorkflowFilter();
				labelFilter.init(rep.getWorkflows());
				labelFilter.addListener(FindMatchingActivityDialog.this);
				
				labelFilterPanel = new LabelWorkflowFilterPanel(labelFilter);
				filterScrollPane.setViewportView(labelFilterPanel);
		
				match();
			}
		});
	}
	
	/**
	 * Returns the replacement activity.
	 * @return Activity or <code>null</code>
	 */
	public Activity getResultActivity() {
		return resultActivity;
	}
	
	
	/**
	 * Most basic activity match value that has a fixed match score of 100%.
	 * 
	 * @author clc
	 *
	 */
	private static class SimpleMatchValue implements MatchValue<Activity> {

		private Activity activity;
		
		public SimpleMatchValue(Activity activity) {
			this.activity = activity;
		}
		
		@Override
		public int compareTo(MatchValue<Activity> arg0) {
			return 0;
		}

		@Override
		public double getMatchScore() {
			return 100.0;
		}

		@Override
		public String getMatchDescription() {
			return "";
		}

		@Override
		public List<MatchValue<?>> getSubValues() {
			return null;
		}

		@Override
		public Activity getObject() {
			return activity;
		}

		@Override
		public String getCaption() {
			return "";
		}

		@Override
		public int getMatchWeight() {
			return 1;
		}
		
	}
}
