package org.primaresearch.clc.phd.repository.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.primaresearch.clc.phd.repository.FileFolderWorkflowRepository;
import org.primaresearch.clc.phd.repository.WorkflowRepository;
import org.primaresearch.clc.phd.repository.io.LocalRepositoryIndex;
import org.primaresearch.clc.phd.repository.io.WorkflowRepositoryLabelExporter;
import org.primaresearch.clc.phd.repository.search.gui.WorkflowSearchTool;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.gui.WorkflowEditor;
import org.primaresearch.clc.phd.workflow.gui.model.WorkflowRootNode;
import org.primaresearch.clc.phd.workflow.gui.model.WorkflowTreeModel;

/**
 * Manager for workflow repositories.<br>
 * <ul>
 *  <li>Show registered repositories</li>
 *  <li>Add/remove repositories</li>
 *  <li>Show all workflows of a repository</li>
 *  <li>Add, open, edit, remove workflows</li>
 * </ul>
 * @author clc
 *
 */
public class RepositoryHub extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private LocalRepositoryIndex folderRepositoryIndex = LocalRepositoryIndex.getInstance();
	
	private JList<WorkflowRepository> repositoryList;
	private JList<Workflow> workflowList;
	private JButton btnDeleteWorkflow;
	private JButton btnCreateWorkflow;
	private JButton btnEditWorkflow;
	private JButton btnSearch;
	private JButton btnlabels;
	private JTree workflowTree;
	private WorkflowTreeModel workflowTreeModel;
	
	private WorkflowRepository currentRepository = null;

	/**
	 * Entry point function
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					RepositoryHub frame = new RepositoryHub();
					frame.setVisible(true);
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		});
	}

	/**
	 * Constructor
	 */
	public RepositoryHub() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Repository Hub");
		setSize(1024, 768);
		
		URL iconURL = getClass().getResource("/org/primaresearch/clc/phd/repository/gui/res/repository_icon.png");
		ImageIcon icon = new ImageIcon(iconURL);
		setIconImage(icon.getImage());
		
		JSplitPane mainSplitPane = new JSplitPane();
		mainSplitPane.setResizeWeight(0.5);
		mainSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		getContentPane().add(mainSplitPane, BorderLayout.CENTER);
		
		JPanel workflowRepositoriesPanel = new JPanel();
		mainSplitPane.setLeftComponent(workflowRepositoriesPanel);
		workflowRepositoriesPanel.setLayout(new BorderLayout(0, 0));
		
		JSplitPane WorkflowSplitPane = new JSplitPane();
		WorkflowSplitPane.setResizeWeight(0.5);
		workflowRepositoriesPanel.add(WorkflowSplitPane, BorderLayout.CENTER);
		
		JPanel repositoryListPanel = new JPanel();
		WorkflowSplitPane.setLeftComponent(repositoryListPanel);
		repositoryListPanel.setLayout(new BorderLayout(0, 5));
		
		JToolBar toolBar = new JToolBar();
		//BoxLayout boxLayout = new BoxLayout(toolBar, BoxLayout.X_AXIS);
		//toolBar.setLayout(boxLayout);
		repositoryListPanel.add(toolBar, BorderLayout.NORTH);
		
		JButton btnAddFolderRepository = new JButton(" Add Folder Repository ");
		btnAddFolderRepository.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addFileFolderRepository();
			}
		});
		btnAddFolderRepository.setAlignmentY(0.75f);
		
		JLabel lblRepositories = new JLabel("Repositories    ");
		lblRepositories.setFont(new Font("Tahoma", Font.PLAIN, 17));
		toolBar.add(lblRepositories);
		toolBar.add(btnAddFolderRepository);
		lblRepositories.setAlignmentY(0.9f);
		
		btnSearch = new JButton(" Search... ");
		btnSearch.setAlignmentY(0.75f);
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openSearch();
			}
		});
		btnSearch.setEnabled(false);
		toolBar.add(btnSearch);
		
		btnlabels = new JButton("Labels...");
		btnlabels.setEnabled(false);
		btnlabels.setAlignmentY(0.75f);
		toolBar.add(btnlabels);
		btnlabels.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportLabelTable();
			}
		});
		
		repositoryList = new JList<WorkflowRepository>();
		repositoryList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				onRepositorySelectionChange();
			}
		});
		repositoryList.setBorder(new LineBorder(Color.LIGHT_GRAY));
		repositoryList.setFont(new Font("Tahoma", Font.PLAIN, 12));
		repositoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		repositoryListPanel.add(repositoryList, BorderLayout.CENTER);
		repositoryList.setModel(folderRepositoryIndex.getListModel());
		
		JPanel workflowListPanel = new JPanel();
		WorkflowSplitPane.setRightComponent(workflowListPanel);
		workflowListPanel.setLayout(new BorderLayout(0, 5));
		
		workflowList = new JList<Workflow>();
		workflowList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				onWorkflowSelectionChange();
			}
		});
		workflowList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2)
					openWorkflow();
			}
		});
		workflowList.setBorder(new LineBorder(Color.LIGHT_GRAY));
		workflowList.setModel(new DefaultListModel<Workflow>());
		
		JScrollPane listScrollPane = new JScrollPane(workflowList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		workflowListPanel.add(listScrollPane, BorderLayout.CENTER);
		listScrollPane.getVerticalScrollBar().setUnitIncrement(16);

		JToolBar toolBar_1 = new JToolBar();
		workflowListPanel.add(toolBar_1, BorderLayout.NORTH);
		
		JLabel lblWorkflows = new JLabel("Workflows    ");
		lblWorkflows.setAlignmentY(0.9f);
		lblWorkflows.setFont(new Font("Tahoma", Font.PLAIN, 17));
		toolBar_1.add(lblWorkflows);
		
		btnDeleteWorkflow = new JButton(" Delete Workflow ");
		btnDeleteWorkflow.setAlignmentY(0.75f);
		btnDeleteWorkflow.setEnabled(false);
		btnDeleteWorkflow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onDeleteWorkflow();
			}
		});
		
		btnCreateWorkflow = new JButton(" Create Workflow ");
		btnCreateWorkflow.setAlignmentY(0.75f);
		btnCreateWorkflow.setEnabled(false);
		btnCreateWorkflow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCreateWorkflow();
			}
		});
		toolBar_1.add(btnCreateWorkflow);
		
		btnEditWorkflow = new JButton(" Edit ");
		btnEditWorkflow.setAlignmentY(0.75f);
		btnEditWorkflow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openWorkflow();
			}
		});
		btnEditWorkflow.setEnabled(false);
		toolBar_1.add(btnEditWorkflow);
		toolBar_1.add(btnDeleteWorkflow);
		
		JButton btnRefresh = new JButton(" Refresh ");
		btnRefresh.setAlignmentY(0.75f);
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshWorkflowList();
			}
		});
		toolBar_1.add(btnRefresh);
		
		JPanel previewPanel = new JPanel();
		mainSplitPane.setRightComponent(previewPanel);
		previewPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		previewPanel.add(panel, BorderLayout.NORTH);
		
		JLabel lblPreview = new JLabel("Preview");
		lblPreview.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel.add(lblPreview);
		
		workflowTree = new JTree();
		workflowTree.setBorder(new EmptyBorder(5, 5, 5, 5));
		workflowTree.setModel(null);
		previewPanel.add(workflowTree, BorderLayout.CENTER);
	}
	
	/**
	 * Initialises the workflow tree for a new workflow
	 */
	private void initWorkflowTree(Workflow workflow) {
		//Root node
		WorkflowRootNode root = null;
		if (workflow == null)
			workflowTreeModel = null;
		else
			workflowTreeModel = new WorkflowTreeModel(root = new WorkflowRootNode(workflow));
		
		workflowTree.setModel(workflowTreeModel);
		
		//Add child nodes (recursive)
		if (root != null) {
			root.addChildNodes(workflowTreeModel);
	
			//Expand all
			for (int i = 0; i < workflowTree.getRowCount(); i++) {
				workflowTree.expandRow(i);
			}
		}
	}
	
	/**
	 * Adds a link to a folder repository (a disk folder with XML workflow files)
	 */
	private void addFileFolderRepository() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final JFileChooser fc = new JFileChooser();
				fc.setApproveButtonText("Select");
				fc.setApproveButtonToolTipText("Select folder");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		 
		        if (fc.showOpenDialog(RepositoryHub.this) == JFileChooser.APPROVE_OPTION) {
		        	//Create and add to index and list control
		        	FileFolderWorkflowRepository repository = new FileFolderWorkflowRepository(fc.getSelectedFile().getAbsolutePath());
		        	folderRepositoryIndex.addFolder(repository.getRepositoryFolder());
		        	DefaultListModel<WorkflowRepository> model = ((DefaultListModel<WorkflowRepository>)repositoryList.getModel());
		        	model.add(model.getSize(), repository);
		        }
			}
		});
	}
	
	/**
	 * Handles selection events of the workflow repository list control
	 */
	private void onRepositorySelectionChange() {
		currentRepository = repositoryList.getSelectedValue();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

			DefaultListModel<Workflow> model = (DefaultListModel<Workflow>)workflowList.getModel();
			model.clear();
			
			//Add workflows to list
			if (currentRepository != null) {
				for (int i=0; i<currentRepository.getWorkflowCount(); i++) {
					model.add(i, currentRepository.getWorkflow(i));
					//System.out.println(currentRepository.getWorkflow(i).getName());
				}
			}
			
			//Enable/disable repository list buttons
			btnSearch.setEnabled(currentRepository != null);
			
			btnlabels.setEnabled(currentRepository != null);
			
			//Enable/disable workflow list buttons
			btnDeleteWorkflow.setEnabled(false);
			btnEditWorkflow.setEnabled(false);
			btnCreateWorkflow.setEnabled(currentRepository != null && currentRepository instanceof FileFolderWorkflowRepository);
			}
		});
	}
	
	/**
	 * Handles selection events of the workflow list control
	 */
	private void onWorkflowSelectionChange() {
		Workflow wf = workflowList.getSelectedValue();
		
		//Enable/disable 'Edit' and 'Delete workflow' button
		btnDeleteWorkflow.setEnabled(wf != null);
		btnEditWorkflow.setEnabled(wf != null);
		
		//Preview
		initWorkflowTree(wf);
	}
	
	/**
	 * Opens the currently selected workflow
	 */
	private void openWorkflow() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			Workflow wf = workflowList.getSelectedValue();
			if (wf != null) {
				int index = workflowList.getSelectedIndex();
				String filePath = null;
				if (currentRepository instanceof FileFolderWorkflowRepository) {
					filePath = ((FileFolderWorkflowRepository)currentRepository).getWorkflowFilePath(index);
				}
				WorkflowEditor frame = new WorkflowEditor(wf, filePath, false);
				frame.setVisible(true);
			}
			}
		});
	}
	
	/**
	 * Deletes the currently selected workflow
	 */
	private void onDeleteWorkflow() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Workflow wf = workflowList.getSelectedValue();
				if (wf != null) {
					if (JOptionPane.showConfirmDialog(RepositoryHub.this, "Delete workflow '"+wf+"'?", "Delete", JOptionPane.YES_NO_OPTION) 
							== JOptionPane.YES_OPTION) {
						//Delete
						currentRepository.deleteWorkflow(workflowList.getSelectedIndex());
						
						//Remove from list
						DefaultListModel<Workflow> model = (DefaultListModel<Workflow>)workflowList.getModel();
						model.removeElement(wf);
					}
				}		
			}
		});
	}
	
	private void onCreateWorkflow() {
		if (currentRepository == null || !(currentRepository instanceof FileFolderWorkflowRepository))
			return;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String rootFolder = ((FileFolderWorkflowRepository)currentRepository).getRepositoryFolder();
				
				//Open workflow editor
				WorkflowEditor frame = new WorkflowEditor(false);
				frame.setCurrentDirectory(rootFolder);
				frame.setVisible(true);
			}
		});
	}
	
	private void refreshWorkflowList() {
		if (currentRepository == null)
			return;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				currentRepository.refresh();
				
				//Refresh workflow list
				onRepositorySelectionChange();
			}
		});
	}
	
	/**
	 * Open search tool
	 */
	private void openSearch() {
		if (currentRepository == null)
			return;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				WorkflowSearchTool tool = new WorkflowSearchTool(currentRepository);
				tool.setVisible(true);
			}
		});
	}
	
	/**
	 * Export all used labels as CSV table
	 */
	private void exportLabelTable() {
		if (currentRepository == null)
			return;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Choose file path
				final JFileChooser fc = new JFileChooser();
				fc.setApproveButtonText("Save");
				fc.setApproveButtonToolTipText("Save to selected file");
				fc.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
		         
		        if (fc.showOpenDialog(RepositoryHub.this) == JFileChooser.APPROVE_OPTION) {
			        //Export
			        WorkflowRepositoryLabelExporter exporter = new WorkflowRepositoryLabelExporter();
			        exporter.exportLabels(currentRepository, fc.getSelectedFile().getAbsolutePath());
		        }
			}
		});
	}
}
