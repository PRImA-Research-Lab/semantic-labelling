package org.primaresearch.clc.phd.workflow.gui.panel.ifelse;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.ifelse.CombinedCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.ComparisonCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity.IfBranch;
import org.primaresearch.clc.phd.workflow.activity.ifelse.InputPortCondition;
import org.primaresearch.clc.phd.workflow.activity.ifelse.NotCondition;
import org.primaresearch.clc.phd.workflow.data.DataObjectFactory;
import org.primaresearch.clc.phd.workflow.gui.dialog.CreateIfConditionDialog;
import org.primaresearch.clc.phd.workflow.gui.dialog.CreateIfConditionDialog.IfConditionType;
import org.primaresearch.clc.phd.workflow.gui.model.ActivityTreeNode;
import org.primaresearch.clc.phd.workflow.gui.model.IfElseActivityNode;
import org.primaresearch.clc.phd.workflow.gui.model.WorkflowTreeModel;
import org.primaresearch.clc.phd.workflow.gui.model.ifelse.ConditionsRootNode;
import org.primaresearch.clc.phd.workflow.gui.model.ifelse.IfElseConditionTreeNode;
import org.primaresearch.clc.phd.workflow.gui.model.ifelse.IfElseConditionsTreeModel;
import org.primaresearch.clc.phd.workflow.gui.panel.DetailsPanel;

/**
 * Extension for basic activity panel. Specialised for 'if-else' activities.
 * 
 * @author clc
 *
 */
public class IfElseActivityPanel extends DetailsPanel {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private IdGenerator idRegister;
	private Workflow workflow;
	private JList<IfBranch> branchList;
	private JTree conditionsTree;
	private IfElseConditionsTreeModel conditionsTreeModel;
	private IfElseActivity activity = null;
	private JFrame parentFrame;
	private JButton btnAddCondition;
	private JButton btnRemoveCondition;
	private DataObjectFactory dataObjectFactory;
	private JPanel conditionDetailsParentPanel;
	private JPanel conditionDetailsPanel;

	public IfElseActivityPanel(Workflow workflow, JFrame parentFrame, IdGenerator idRegister) {
		this.idRegister = idRegister;
		this.parentFrame = parentFrame;
		this.workflow = workflow;
		dataObjectFactory = new DataObjectFactory(idRegister);
		
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new GridLayout(1, 3, 5, 0));
		
		JPanel branchesPanel = new JPanel();
		add(branchesPanel);
		branchesPanel.setLayout(new BorderLayout(0, 5));
		
		JLabel lblBranches = new JLabel("Branches");
		branchesPanel.add(lblBranches, BorderLayout.NORTH);
		
		branchList = new JList<IfBranch>(new DefaultListModel<IfBranch>());
		branchList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if (branchList.getSelectedIndex() >= 0) {
					refreshConditionsPanel();
				}
			}
		});
		branchesPanel.add(branchList, BorderLayout.CENTER);
		
		JPanel branchButtonPanel = new JPanel();
		branchesPanel.add(branchButtonPanel, BorderLayout.SOUTH);
		
		JButton btnAddBranch = new JButton("Add");
		btnAddBranch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (activity != null)
							activity.requestNewBranch();
					}
				});	
			}
		});
		branchButtonPanel.add(btnAddBranch);
		
		JButton btnRemoveBranch = new JButton("Remove");
		branchButtonPanel.add(btnRemoveBranch);
		
		JPanel conditionsPanel = new JPanel();
		add(conditionsPanel);
		conditionsPanel.setLayout(new BorderLayout(0, 5));
		
		JLabel lblConditions = new JLabel("Conditions");
		conditionsPanel.add(lblConditions, BorderLayout.NORTH);
		
		conditionsTree = new JTree();
		conditionsPanel.add(conditionsTree, BorderLayout.CENTER);
		
		JPanel conditionsButtonPanel = new JPanel();
		conditionsPanel.add(conditionsButtonPanel, BorderLayout.SOUTH);
		
		btnAddCondition = new JButton("Add");
		btnAddCondition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						addCondition();
					}
				});					
			}
		});
		conditionsButtonPanel.add(btnAddCondition);
		
		btnRemoveCondition = new JButton("Remove");
		conditionsButtonPanel.add(btnRemoveCondition);
		
		conditionDetailsParentPanel = new JPanel();
		add(conditionDetailsParentPanel);
		conditionDetailsParentPanel.setLayout(new BorderLayout(0, 5));
		
		JLabel lblDetails = new JLabel("Details");
		conditionDetailsParentPanel.add(lblDetails, BorderLayout.NORTH);
	}
	
	private void refreshConditionsPanel() {
		
		//Get selected branch
		IfBranch selBranch = branchList.getSelectedValue();
		if (selBranch == null)
			conditionsTree.setModel(null);
		else {
			ConditionsRootNode root = null;
			conditionsTree.setModel(conditionsTreeModel = new IfElseConditionsTreeModel(root = new ConditionsRootNode(selBranch)));
			conditionsTree.addTreeSelectionListener(new TreeSelectionListener() {
				@Override
				public void valueChanged(TreeSelectionEvent e) {
					IfElseConditionTreeNode selectedNode = (IfElseConditionTreeNode)e.getPath().getLastPathComponent();
					createConditionDetailsPanel(selectedNode);
					//TODO
					enableConditionControls(selectedNode);
					
				}
			});
			//conditionsTreeModel.addTreeModelListener(this);
			
			//Add child nodes (recursive)
			root.addChildNodes(conditionsTreeModel);

			//Select root
			conditionsTree.setSelectionRow(0);

			//Expand all
			for (int i = 0; i < conditionsTree.getRowCount(); i++) {
				conditionsTree.expandRow(i);
			}

		}
	}
	
	private void enableConditionControls(IfElseConditionTreeNode selectedNode) {
		btnAddCondition.setEnabled(selectedNode != null && selectedNode.canCreateChildCondition());
		btnRemoveCondition.setEnabled(selectedNode != null && selectedNode.isRemovable());
	}
	
	private void createConditionDetailsPanel(IfElseConditionTreeNode selectedNode) {
		
		//Remove old
		if (conditionDetailsPanel != null)
			conditionDetailsParentPanel.remove(conditionDetailsPanel);
		
		//Add new
		IfCondition condition = selectedNode.getCondition();
		conditionDetailsPanel = null;
		if (condition != null) {
			//NOT
			if (condition instanceof NotCondition)
				conditionDetailsParentPanel.add(conditionDetailsPanel = new NotConditionPanel(), BorderLayout.CENTER);
			//AND/OR
			else if (condition instanceof CombinedCondition)
				conditionDetailsParentPanel.add(conditionDetailsPanel = new CombinedConditionPanel((CombinedCondition)condition), BorderLayout.CENTER);
			//Input port
			else if (condition instanceof InputPortCondition)
				conditionDetailsParentPanel.add(conditionDetailsPanel = new InputPortConditionPanel(workflow, (InputPortCondition)condition), BorderLayout.CENTER);
			//Comparison port
			else if (condition instanceof ComparisonCondition)
				conditionDetailsParentPanel.add(conditionDetailsPanel = new ComparisonConditionPanel(workflow, (ComparisonCondition)condition), BorderLayout.CENTER);
			
			//if (conditionDetailsPanel != null)
			//	conditionDetailsPanel.invalidate();
		}		
		
		conditionDetailsParentPanel.repaint();
		conditionDetailsParentPanel.revalidate();
	}
	
	@Override
	public void refresh(final TreeNode selectedNode,
			WorkflowTreeModel workflowTreeModel) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (selectedNode instanceof IfElseActivityNode) {
					IfElseActivity activity = (IfElseActivity)((ActivityTreeNode)selectedNode).getActivity();
				
					IfElseActivityPanel.this.activity = activity;
					
					//Branches
					((DefaultListModel<IfBranch>)branchList.getModel()).removeAllElements();
					for (Iterator<IfBranch> it = activity.getBranches().iterator(); it.hasNext(); ) {
						IfBranch branch = it.next();
						((DefaultListModel<IfBranch>)branchList.getModel()).addElement(branch);
					}
					
					//TODO
				}
			}
		});	
	}
	
	/**
	 * Adds a new condition to the condition tree of the current if-else branch
	 */
	private void addCondition() {
		IfBranch selBranch = branchList.getSelectedValue();
		if (selBranch == null)
			return;
		
		IfCondition parentCondition = null;
		TreePath selPath = conditionsTree.getSelectionPath();
		if (selPath != null && selPath.getPathCount() > 0)
			parentCondition = ((IfElseConditionTreeNode)selPath.getLastPathComponent()).getCondition();
		
		CreateIfConditionDialog dlg = new CreateIfConditionDialog(parentFrame, selBranch, parentCondition);
		dlg.pack();
		dlg.setLocation((int)(btnAddCondition.getLocationOnScreen().getX() + 5.0),
						(int)(btnAddCondition.getLocationOnScreen().getY() + btnAddCondition.getHeight() + 10.0));
		dlg.setVisible(true);
		
		IfCondition newCondition = null;
		IfElseConditionTreeNode parentNode = (IfElseConditionTreeNode)conditionsTree.getLastSelectedPathComponent();
		
		IfConditionType conditionType = dlg.getSelectedConditionType();
		if (conditionType != null) { 
			if (conditionType == IfConditionType.NOT)
				newCondition = new NotCondition();
			else if (conditionType == IfConditionType.AND)
				newCondition = new CombinedCondition(true);
			else if (conditionType == IfConditionType.OR)
				newCondition = new CombinedCondition(false);
			else if (conditionType == IfConditionType.INPUT)
				newCondition = new InputPortCondition(dataObjectFactory.createIfElseConditionPort(activity));
			else if (conditionType == IfConditionType.COMPARISON)
				newCondition = new ComparisonCondition(dataObjectFactory.createIfElseComparisonPort(activity),
														ComparisonCondition.ComparisonConditionOperator.Equals,
														dataObjectFactory.createIfElseComparisonPort(activity));
		}
		
		if (newCondition != null) {
			parentNode.onChildConditionCreated(newCondition);
			IfElseConditionTreeNode newNode = conditionsTreeModel.createConditionTreeNode(parentNode, newCondition);
			
			if (newNode != null) {
				//Select the new item
		        TreeNode[] nodes = conditionsTreeModel.getPathToRoot(newNode);    
		        conditionsTree.setSelectionPath(new TreePath(nodes));    
			}
		}
	}

}
