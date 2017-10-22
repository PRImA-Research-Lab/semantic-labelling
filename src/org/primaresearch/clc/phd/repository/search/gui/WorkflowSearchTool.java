package org.primaresearch.clc.phd.repository.search.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.net.URL;
import java.util.Collection;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.primaresearch.clc.phd.repository.WorkflowRepository;
import org.primaresearch.clc.phd.repository.search.Filter;
import org.primaresearch.clc.phd.repository.search.FilterChangeListener;
import org.primaresearch.clc.phd.repository.search.LabelWorkflowFilter;
import org.primaresearch.clc.phd.repository.search.TextSearchWorkflowFilter;
import org.primaresearch.clc.phd.repository.search.gui.WorkflowSearchResultView.WorkflowSearchResultItemListener;
import org.primaresearch.clc.phd.workflow.Workflow;

import javax.swing.JCheckBox;

import java.awt.FlowLayout;

import javax.swing.JTextField;
import javax.swing.JSeparator;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Dialogue to search workflow repository using label filters
 * 
 * @author clc
 *
 */
public class WorkflowSearchTool extends JDialog implements FilterChangeListener {

	private static final long serialVersionUID = 1L;
	private WorkflowRepository repository;
	private LabelWorkflowFilterPanel labelFilterPanel;
	private LabelWorkflowFilter labelFilter;
	private TextSearchWorkflowFilter textSearchFilter = new TextSearchWorkflowFilter();
	private WorkflowSearchResultView resultView;
	private JLabel lblSearchResultTitle;
	private JPanel controlPanel;
	private JCheckBox chckbxIncludeChildActivities;
	private JCheckBox chckbxShowConcreteWorkflows;
	private JTextField textFieldSearch;
	private JLabel lblSearch;
	private JSeparator separator;

	public static void main(String[] args) {
		/*EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					WorkflowSearchTool frame = new WorkflowSearchTool();
					frame.setVisible(true);
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		});*/
		JOptionPane.showMessageDialog(null, "Not implemented");
	}

	/**
	 * Constructor without listener (opens workflow on click)
	 */
	public WorkflowSearchTool(WorkflowRepository repository) {
		this(repository, null);
	}
	
	/**
	 * Constructor with listener
	 */
	public WorkflowSearchTool(WorkflowRepository repository, WorkflowSearchResultItemListener listener) {
		this.repository = repository;
		getContentPane().setBackground(Color.WHITE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //Close only this frame (EXIT_ON_CLOSE closes everything, also other frames)
		setTitle("Search Repository for Workflows ("+repository.toString()+")");
		setSize(1024, 768);
		
		URL iconURL = getClass().getResource("/org/primaresearch/clc/phd/repository/search/gui/res/search_icon.png");
		ImageIcon icon = new ImageIcon(iconURL);
		setIconImage(icon.getImage());

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.WEST);
		scrollPane.setPreferredSize(new Dimension(300, 300));
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		//scrollPane.setMinimumSize(new Dimension(200, 300));
		
		labelFilter = new LabelWorkflowFilter();
		labelFilter.init(repository.getWorkflows());
		labelFilter.addListener(this);
		labelFilterPanel = new LabelWorkflowFilterPanel(labelFilter);
		//labelFilterPanel.setPreferredSize(new Dimension(200, 300));
		//labelFilterPanel.setSize(new Dimension(200, 300));
		scrollPane.setViewportView(labelFilterPanel);
		
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
		if (listener != null)
			resultView.addListener(listener);
		panel.add(resultView.getComponent(), BorderLayout.CENTER);
		
		controlPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) controlPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		getContentPane().add(controlPanel, BorderLayout.NORTH);
		
		chckbxIncludeChildActivities = new JCheckBox("Include child activities");
		chckbxIncludeChildActivities.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				labelFilter.setIncludeChildActivities(chckbxIncludeChildActivities.isSelected());
				filterChanged(labelFilter);
			}
		});
		controlPanel.add(chckbxIncludeChildActivities);
		
		chckbxShowConcreteWorkflows = new JCheckBox("Show concrete workflows only");
		controlPanel.add(chckbxShowConcreteWorkflows);
		
		separator = new JSeparator();
		controlPanel.add(separator);
		
		lblSearch = new JLabel("Search");
		controlPanel.add(lblSearch);
		
		textFieldSearch = new JTextField();
		textFieldSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textSearchFilter.setSearchString(textFieldSearch.getText());
				filterChanged(labelFilter);
			}
		});
		controlPanel.add(textFieldSearch);
		textFieldSearch.setColumns(10);
		
		filterChanged(labelFilter);
	}

	@Override
	public void filterChanged(Filter filter) {
		Date start = new Date();
		if (filter == labelFilter) {
			//Label filter
			Collection<Workflow> filtered = labelFilter.filterWorkflows(repository.getWorkflows());
			
			//Text search filter
			filtered = textSearchFilter.filterWorkflows(filtered);
			
			resultView.update(filtered);
			
			//Search result title
			String title = "Search results - ";
			if (filtered.isEmpty())
				title += "No workflows found";
			else if (filtered.size() == 1)
				title += filtered.size() + " workflow found";
			else
				title += filtered.size() + " workflows found";
			lblSearchResultTitle.setText(title);
			
			/*EventQueue.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					WorkflowSearchTool.this.invalidate();
					WorkflowSearchTool.this.repaint();
				}
			});*/
		}
		Date end = new Date();
		long milliseconds = end.getTime() - start.getTime();
		System.out.println(milliseconds);
		
	}
}
