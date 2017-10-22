package org.primaresearch.clc.phd.workflow.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.primaresearch.clc.phd.ontology.migration.MigrationMessagesDialog;
import org.primaresearch.clc.phd.repository.search.matching.Matcher;
import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.WorkflowImpl;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityFactory;
import org.primaresearch.clc.phd.workflow.activity.AtomicActivity;
import org.primaresearch.clc.phd.workflow.activity.DirectedGraphActivity;
import org.primaresearch.clc.phd.workflow.activity.ForLoopActivity;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity;
import org.primaresearch.clc.phd.workflow.data.DataTable;
import org.primaresearch.clc.phd.workflow.data.DefaultDataTable;
import org.primaresearch.clc.phd.workflow.gui.dialog.CreateActivityDialog;
import org.primaresearch.clc.phd.workflow.gui.dialog.FindMatchingActivityDialog;
import org.primaresearch.clc.phd.workflow.gui.dialog.WorkflowUmlDialog;
import org.primaresearch.clc.phd.workflow.gui.model.ActivityTreeNode;
import org.primaresearch.clc.phd.workflow.gui.model.DataTableTreeNode;
import org.primaresearch.clc.phd.workflow.gui.model.WorkflowRootNode;
import org.primaresearch.clc.phd.workflow.gui.model.WorkflowTreeModel;
import org.primaresearch.clc.phd.workflow.gui.model.WorkflowTreeNode;
import org.primaresearch.clc.phd.workflow.gui.panel.ActivityDetails;
import org.primaresearch.clc.phd.workflow.gui.panel.AtomicActivityPanel;
import org.primaresearch.clc.phd.workflow.gui.panel.DataTableDetails;
import org.primaresearch.clc.phd.workflow.gui.panel.DetailsPanel;
import org.primaresearch.clc.phd.workflow.gui.panel.DirectedGraphActivityPanel;
import org.primaresearch.clc.phd.workflow.gui.panel.ForLoopActivityPanel;
import org.primaresearch.clc.phd.workflow.gui.panel.WelcomePanel;
import org.primaresearch.clc.phd.workflow.gui.panel.WorkflowDetails;
import org.primaresearch.clc.phd.workflow.gui.panel.ifelse.IfElseActivityPanel;
import org.primaresearch.clc.phd.workflow.io.OwlWorkflowSemanticsWriter;
import org.primaresearch.clc.phd.workflow.io.XmlWorkflowReader;
import org.primaresearch.clc.phd.workflow.io.XmlWorkflowWriter;
import org.primaresearch.clc.phd.workflow.validation.gui.WorkflowValidationDialog;

/**
 * Main class for graphical workflow editor (Swing user interface)
 * 
 * @author clc
 *
 */
public class WorkflowEditor extends JFrame implements TreeModelListener {

	private static final long serialVersionUID = 1L;

	private Workflow workflow;
	private RecentDocuments recentDocs = new RecentDocuments(".xml");
	private String filePath = null;
	private String currentDirectory = null;
	private JPanel contentPanel;
	private JSplitPane splitPane;
	private JTree workflowTree;
	private WorkflowTreeModel workflowTreeModel;
	private Map<Class<? extends Activity>, DetailsPanel> activityDetailsPanels = new HashMap<Class<? extends Activity>, DetailsPanel>();
	private WorkflowDetails workflowDetails;
	private DataTableDetails dataTableDetails;
	private WelcomePanel welcomeDetailsPanel;
	private JButton btnNewActivity;
	private JButton btnRemoveWorkflowItem;
	private JButton btnReplaceActivity;
	private JButton btnAddDataTable;
	
	private ActivityFactory activityFactory;
	private WorkflowValidationDialog validationDialog = null;

	/**
	 * Main function
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					WorkflowEditor frame = new WorkflowEditor();
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
	public WorkflowEditor() {
		this(new WorkflowImpl("[New workflow]"), null, true);
	}
	
	/**
	 * Constructor
	 */
	public WorkflowEditor(boolean showWelcomePanel) {
		this(new WorkflowImpl("[New workflow]"), null, showWelcomePanel);
	}
	
	/**
	 * Constructor
	 * @param workflow Initial workflow
	 */
	public WorkflowEditor(final Workflow workflow, String filePath, boolean showWelcomePanel) {
		super();
		this.workflow = workflow;
		this.filePath = filePath;
		this.activityFactory = new ActivityFactory(workflow.getIdRegister());
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //Close only this frame (EXIT_ON_CLOSE closes everything, also other frames)
		setTitle("Workflow Editor");
		setSize(1280, 960);
		
		URL iconURL = getClass().getResource("/org/primaresearch/clc/phd/workflow/gui/res/workflow_icon.png");
		ImageIcon icon = new ImageIcon(iconURL);
		setIconImage(icon.getImage());

		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);
		contentPanel.setLayout(new BorderLayout(0, 5));

		JPanel buttonPanel = new JPanel();
		contentPanel.add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		JButton btnSave = new JButton("Save as...");
		btnSave.setToolTipText("Save to XML");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveWorkflowAs();
			}
		});
		
		JButton btnSave_1 = new JButton("Save");
		btnSave_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (WorkflowEditor.this.filePath != null) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							saveWorkflowAs(WorkflowEditor.this.filePath);
						}
					});
				}
				else
					saveWorkflowAs();
			}
		});
		
		JButton btnValidate = new JButton("Validate...");
		btnValidate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (validationDialog == null) {
							validationDialog = new WorkflowValidationDialog(WorkflowEditor.this, workflow);
						}
						validationDialog.setVisible(true);
						validationDialog.validateWorkflow();
					}
				});
				
	
			}
		});
		buttonPanel.add(btnValidate);
		
		JSeparator separator = new JSeparator();
		buttonPanel.add(separator);
		buttonPanel.add(btnSave_1);
		buttonPanel.add(btnSave);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		
		JButton btnSaveSemanticInformation = new JButton("Save semantic information...");
		btnSaveSemanticInformation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveSemanticInformationAs();
			}
		});
		btnSaveSemanticInformation.setToolTipText("Save semantic information of workflow as OWL XML file");
		buttonPanel.add(btnSaveSemanticInformation);
		buttonPanel.add(btnClose);
		
		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.3);
		contentPanel.add(splitPane, BorderLayout.CENTER);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		contentPanel.add(toolBar, BorderLayout.NORTH);
		
		btnNewActivity = new JButton("Add Activity");
		btnNewActivity.setToolTipText("Create new child activity");
		btnNewActivity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAddActivity();
			}
		});
		toolBar.add(btnNewActivity);
		
		btnRemoveWorkflowItem = new JButton("Remove Item");
		btnRemoveWorkflowItem.setToolTipText("Remove selected activity");
		btnRemoveWorkflowItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRemoveActivity();
			}
		});
		
		btnReplaceActivity = new JButton("Replace Activity...");
		btnReplaceActivity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onReplaceActivity();
			}
		});
		toolBar.add(btnReplaceActivity);
		toolBar.add(btnRemoveWorkflowItem);
		
		JButton btnExpandAll = new JButton("Expand All");
		btnExpandAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < workflowTree.getRowCount(); i++) {
					workflowTree.expandRow(i);
				}
			}
		});
		
		btnAddDataTable = new JButton("Add New Data Table");
		btnAddDataTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onAddDataTable();
			}
		});
		toolBar.add(btnAddDataTable);
		
		JButton btnImportDataTable = new JButton("Import Data Table...");
		btnImportDataTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//TODO
			}
		});
		toolBar.add(btnImportDataTable);
		toolBar.add(btnExpandAll);
		
		JButton btnUmlView = new JButton("UML View");
		btnUmlView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						WorkflowUmlDialog dlg = new WorkflowUmlDialog(workflow);
						dlg.setModal(true);
						dlg.setVisible(true);
				    }
				});
			}
		});
		toolBar.add(btnUmlView);
		
		workflowTree = new JTree();
		workflowTree.setBorder(new EmptyBorder(5, 5, 5, 5));
		workflowTree.setModel(workflowTreeModel = new WorkflowTreeModel(new WorkflowRootNode(workflow)));
		workflowTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				WorkflowTreeNode selectedNode = (WorkflowTreeNode)e.getPath().getLastPathComponent();
				selectDetailsPanel(selectedNode);
				enableControls(selectedNode);
			}
		});
		workflowTree.setMinimumSize(new Dimension(200, 400));
		splitPane.setLeftComponent(workflowTree);
		workflowTreeModel.addTreeModelListener(this);
		
		initTree();
		if (workflow == null)
			enableControls(null);
		initDetailsPanels();
		selectDetailsPanel(showWelcomePanel ? null : (TreeNode)workflowTree.getModel().getRoot());
	}
	
	/**
	 * Creates details panels that correspond to different tree nodes.
	 */
	private void initDetailsPanels() {
		welcomeDetailsPanel = new WelcomePanel(recentDocs, this);
		workflowDetails = new WorkflowDetails(workflow, workflowTreeModel);
		
		//Atomic activity
		ActivityDetails activityDetails = new ActivityDetails(workflow, new AtomicActivityPanel(), workflow.getIdRegister());
		activityDetailsPanels.put(AtomicActivity.class, activityDetails);
		
		//For loop activity
		activityDetails = new ActivityDetails(workflow, new ForLoopActivityPanel(workflow, workflow.getIdRegister()), workflow.getIdRegister());
		activityDetailsPanels.put(ForLoopActivity.class, activityDetails);

		//Directed graph activity
		activityDetails = new ActivityDetails(workflow, new DirectedGraphActivityPanel(), workflow.getIdRegister());
		activityDetailsPanels.put(DirectedGraphActivity.class, activityDetails);
		
		//If-else activity
		activityDetails = new ActivityDetails(workflow, new IfElseActivityPanel(workflow, this, workflow.getIdRegister()), workflow.getIdRegister());
		activityDetailsPanels.put(IfElseActivity.class, activityDetails);
		
		//Data table
		dataTableDetails = new DataTableDetails(workflow, null, workflow.getIdRegister());
	}
	
	/**
	 * Closes and destroys the window.
	 */
	private void close() {
		this.dispose();
	}
	
	/**
	 * Sets the corresponding details panel for the currently selected tree node.
	 */
	private void selectDetailsPanel(TreeNode selectedNode) {
		//Remove old panel
		//if (splitPane.getRightComponent() != null)
		//	splitPane.remove(splitPane.getRightComponent());
				
		//Add new
		DetailsPanel detailsPanel = null;
		if (selectedNode == null)
			detailsPanel = welcomeDetailsPanel;
		else if (selectedNode instanceof WorkflowRootNode)
			detailsPanel = workflowDetails;
		else if (selectedNode instanceof DataTableTreeNode)
			detailsPanel = dataTableDetails;
		else if (selectedNode instanceof ActivityTreeNode) { //Activity
			detailsPanel = activityDetailsPanels.get(((ActivityTreeNode)selectedNode).getActivity().getClass());
		}
			
		//Refresh
		if (detailsPanel != null) {
			splitPane.setRightComponent(detailsPanel);
			detailsPanel.refresh(selectedNode, workflowTreeModel);
		}
	}
	
	/**
	 * Enables / disables the relevant controls for the selected tree node.
	 */
	private void enableControls(WorkflowTreeNode selectedNode) {
		btnNewActivity.setEnabled(selectedNode != null && selectedNode.canCreateChildActivity());
		btnReplaceActivity.setEnabled(selectedNode != null && selectedNode instanceof ActivityTreeNode);
		btnRemoveWorkflowItem.setEnabled(selectedNode != null && selectedNode.isRemovable());
		btnAddDataTable.setEnabled(selectedNode != null && selectedNode.canCreateChildDataTable());
	}
	
	/**
	 * Saves the current workflow to an XML file
	 */
	private void saveWorkflowAs() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				String lastSaveFolder = recentDocs.getLastSaveFolder();
				
				final JFileChooser fc = new JFileChooser();
				fc.setApproveButtonText("Save");
				fc.setApproveButtonToolTipText("Save to selected file");
				fc.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));
				
				if (lastSaveFolder != null || currentDirectory != null)
					fc.setCurrentDirectory(new File(currentDirectory != null ? currentDirectory : lastSaveFolder));
		         
		        if (fc.showOpenDialog(WorkflowEditor.this) == JFileChooser.APPROVE_OPTION) {
		        	saveWorkflowAs(fc.getSelectedFile().getAbsolutePath());
		        }
		    }
		});
    }
	
	/**
	 * Saves the current workflow to an XML file
	 */
	private void saveWorkflowAs(String filePath) {
		
        try {
        	XmlWorkflowWriter writer = new XmlWorkflowWriter();
        	writer.write(workflow, filePath);
        	JOptionPane.showMessageDialog(null, "File has been saved.\n"+filePath);
        	
        	recentDocs.register(filePath);
        	this.filePath = filePath;
        } catch (Exception exc) {
        	exc.printStackTrace();
        	JOptionPane.showMessageDialog(null, "Error on saving.\n"+filePath);
        }
	}
	
	/**
	 * Saves the semantic information of the current workflow to an OWL XML file.
	 */
	private void saveSemanticInformationAs() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				String lastSaveFolder = recentDocs.getLastSaveFolder();
				
				final JFileChooser fc = new JFileChooser();
				fc.setApproveButtonText("Save");
				fc.setApproveButtonToolTipText("Save to selected file");
				fc.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));
				
				if (lastSaveFolder != null || currentDirectory != null)
					fc.setCurrentDirectory(new File(currentDirectory != null ? currentDirectory : lastSaveFolder));
		         
		        if (fc.showOpenDialog(WorkflowEditor.this) == JFileChooser.APPROVE_OPTION) {
		        	saveSemanticInformationAs(fc.getSelectedFile().getAbsolutePath());
		        }
		    }
		});
	}
	
	/**
	 * Saves the semantic information of the current workflow to the specified OWL XML file.
	 */
	private void saveSemanticInformationAs(String filePath) {
		
        try {
        	OwlWorkflowSemanticsWriter writer = new OwlWorkflowSemanticsWriter();
        	writer.write(workflow, filePath);
        	JOptionPane.showMessageDialog(null, "File has been saved.\n"+filePath);
        } catch (Exception exc) {
        	exc.printStackTrace();
        	JOptionPane.showMessageDialog(null, "Error on saving.\n"+filePath);
        }
	}
	
	/**
	 * Loads a workflow from the specified XML file
	 * @param filePath
	 */
	public void loadWorkflow(String filePath) {
		XmlWorkflowReader reader = new XmlWorkflowReader();
		workflow = reader.read(filePath);
		this.filePath = filePath;
		initTree();
		
		List<String> migrationMessages = reader.getMigrationMessages();
		if (migrationMessages != null && !migrationMessages.isEmpty()) {
			MigrationMessagesDialog dlg = new MigrationMessagesDialog(migrationMessages);
			dlg.setVisible(true);
		}
	}
	
	/**
	 * Initialises the workflow tree for a new workflow
	 */
	private void initTree() {
		//Root node
		WorkflowRootNode root;
		workflowTree.setModel(workflowTreeModel = new WorkflowTreeModel(root = new WorkflowRootNode(workflow)));
		workflowTreeModel.addTreeModelListener(this);
		
		//Add child nodes (recursive)
		root.addChildNodes(workflowTreeModel);

		//Select root
		workflowTree.setSelectionRow(0);

		//Expand all
		for (int i = 0; i < workflowTree.getRowCount(); i++) {
			workflowTree.expandRow(i);
		}
	}

	/**
	 * Adds a new activity (child activity or root activity)
	 */
	private void onAddActivity() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Activity parentActivity = null;
					WorkflowTreeNode node = (WorkflowTreeNode)workflowTree.getLastSelectedPathComponent();
					if (node instanceof ActivityTreeNode) {
						ActivityTreeNode activityNode = (ActivityTreeNode)node;
						parentActivity = activityNode.getActivity();
					}
					
					CreateActivityDialog dlg = new CreateActivityDialog(WorkflowEditor.this, workflow, parentActivity);
					dlg.pack();
					dlg.setLocation((int)(btnNewActivity.getLocationOnScreen().getX() + 5.0),
									(int)(btnNewActivity.getLocationOnScreen().getY() + btnNewActivity.getHeight() + 10.0));
					dlg.setVisible(true);
					
					Activity newActivity = null;
					WorkflowTreeNode parentNode = (WorkflowTreeNode)workflowTree.getLastSelectedPathComponent();
					
					if (dlg.getSelectedActivityType() != null) 
						newActivity = activityFactory.createActivity(parentActivity, dlg.getSelectedActivityType(), "[New activity]");
					else
						newActivity = dlg.getSelectedActivity();
					
					if (newActivity != null) {
						parentNode.onChildActivityCreated(newActivity);
						WorkflowTreeNode newNode = workflowTreeModel.createActivityTreeNode(parentNode, newActivity);
						
						if (newNode != null) {
							//Select the new item
					        TreeNode[] nodes = workflowTreeModel.getPathToRoot(newNode);    
					        workflowTree.setSelectionPath(new TreePath(nodes));    
						}
					}
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Adds a new data table
	 */
	private void onAddDataTable() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					
					DataTable newTable = new DefaultDataTable(workflow.getIdRegister());
					workflow.getDataTables().add(newTable);
					WorkflowTreeNode parentNode = (WorkflowTreeNode)workflowTree.getModel().getRoot();
					
					//parentNode.onChildActivityCreated(newActivity);
					WorkflowTreeNode newNode = workflowTreeModel.createDataTableTreeNode(parentNode, newTable);
						
					if (newNode != null) {
						//Select the new item
					    TreeNode[] nodes = workflowTreeModel.getPathToRoot(newNode);    
					    workflowTree.setSelectionPath(new TreePath(nodes));    
					}
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Deletes the currently selected activity
	 */
	private void onRemoveActivity() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				WorkflowTreeNode activityNode = (WorkflowTreeNode)workflowTree.getLastSelectedPathComponent();
				if (activityNode instanceof ActivityTreeNode) {
					Activity activity = ((ActivityTreeNode)activityNode).getActivity();
					
					//Root activity
					if (activity.getParentActivity() == null) {
						workflow.setRootActivity(null);
					}
					//Child activity
					else {
						Activity parentActivity = activity.getParentActivity();
						if (parentActivity instanceof ForLoopActivity)
							((ForLoopActivity)parentActivity).setChildActivity(null);
						else if (parentActivity instanceof DirectedGraphActivity)
							((DirectedGraphActivity)parentActivity).removeNode(activity);
						else
							throw new UnsupportedOperationException("Delete child activity operation not supported for: "+parentActivity.getClass().getName());
					}
					
					TreeNode parentNode = activityNode.getParent();
					
					//Remove from tree
					workflowTreeModel.removeNodeFromParent(activityNode);
					
					//Select parent
			        TreeNode[] nodes = workflowTreeModel.getPathToRoot(parentNode);    
			        workflowTree.setSelectionPath(new TreePath(nodes));    
				}		
			}
		});
	}
	
	/**
	 * Opens dialogue to find matching activities for replacing the currently selected activity.
	 */
	private void onReplaceActivity() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				WorkflowTreeNode node = (WorkflowTreeNode)workflowTree.getLastSelectedPathComponent();
				if (node instanceof ActivityTreeNode) {
					ActivityTreeNode activityNode = (ActivityTreeNode)node;
					Activity activity = activityNode.getActivity();
					
					FindMatchingActivityDialog dlg = new FindMatchingActivityDialog(workflow, activity, Matcher.MATCHING_FOR_REPLACING);
					dlg.setModal(true);
					dlg.setVisible(true);
					
					Activity replacement = dlg.getResultActivity();
					if (replacement != null) {
						WorkflowEditor.this.workflow.replaceActivity(activity, replacement);
						
						//Update tree
						activityNode.setActivity(replacement);	
						// Update children
						// TODO
					}
				}		
			}
		});
	}
	
	public ActivityFactory getActivityFactory() {
		return activityFactory;
	}

	public void setCurrentDirectory(String currentDirectory) {
		this.currentDirectory = currentDirectory;
	}

	@Override
	public void treeNodesChanged(TreeModelEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void treeNodesInserted(TreeModelEvent e) {
		//workflowTree.setSelectionPath(e.getTreePath());  
		
	}


	@Override
	public void treeNodesRemoved(TreeModelEvent e) {
	}


	@Override
	public void treeStructureChanged(TreeModelEvent e) {
		// TODO Auto-generated method stub
		
	}
		
	public IdGenerator getIdRegister() {
		if (workflow == null)
			return null;
		return workflow.getIdRegister();
	}

	public void selectWorkflowTreeItem(Object objectOfTreeItem) {
		if (objectOfTreeItem instanceof Workflow)
			workflowTree.setSelectionRow(0);
		else if (objectOfTreeItem instanceof Activity) {
			//Find node
			ActivityTreeNode node = workflowTreeModel.findActivityNode((Activity)objectOfTreeItem);
			if (node != null) {
				//Select the new item
		        TreeNode[] nodes = workflowTreeModel.getPathToRoot(node);    
		        workflowTree.setSelectionPath(new TreePath(nodes));    
			}
		}
	}
	
	public void refreshTree() {
		// Root node
		WorkflowRootNode root = new WorkflowRootNode(workflow);
		workflowTreeModel.setRoot(root);
		
		// Add child nodes (recursive)
		root.addChildNodes(workflowTreeModel);
	}
}
