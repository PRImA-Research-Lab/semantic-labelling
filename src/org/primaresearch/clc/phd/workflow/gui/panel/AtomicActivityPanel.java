package org.primaresearch.clc.phd.workflow.gui.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.TreeNode;

import org.primaresearch.clc.phd.workflow.activity.AtomicActivity;
import org.primaresearch.clc.phd.workflow.gui.model.ActivityTreeNode;
import org.primaresearch.clc.phd.workflow.gui.model.WorkflowTreeModel;

/**
 * Extension for basic activity panel. Specialised for atomic activities.
 * 
 * @author clc
 *
 */
public class AtomicActivityPanel extends DetailsPanel implements DocumentListener {
	private static final long serialVersionUID = 1L;
	private volatile boolean activityDetailsChangeListenerActive = true;
	private AtomicActivity activity;
	
	private JTextField methodName;
	private JTextField methodVersion;
	private JCheckBox chckbxAbstractActivity;
	
	public AtomicActivityPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		chckbxAbstractActivity = new JCheckBox("Abstract activity");
		GridBagConstraints gbc_chckbxAbstractActivity = new GridBagConstraints();
		gbc_chckbxAbstractActivity.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxAbstractActivity.gridx = 0;
		gbc_chckbxAbstractActivity.gridy = 0;
		add(chckbxAbstractActivity, gbc_chckbxAbstractActivity);
		chckbxAbstractActivity.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateActivityFromControls();
			}
		});
		
		JLabel lblMethodName = new JLabel("Method name");
		GridBagConstraints gbc_lblMethodName = new GridBagConstraints();
		gbc_lblMethodName.insets = new Insets(0, 0, 5, 5);
		gbc_lblMethodName.anchor = GridBagConstraints.EAST;
		gbc_lblMethodName.gridx = 0;
		gbc_lblMethodName.gridy = 1;
		add(lblMethodName, gbc_lblMethodName);
		
		methodName = new JTextField();
		GridBagConstraints gbc_methodName = new GridBagConstraints();
		gbc_methodName.insets = new Insets(0, 0, 5, 0);
		gbc_methodName.fill = GridBagConstraints.HORIZONTAL;
		gbc_methodName.gridx = 1;
		gbc_methodName.gridy = 1;
		add(methodName, gbc_methodName);
		methodName.setColumns(10);
		methodName.getDocument().addDocumentListener(this);

		JLabel lblMethodVersion = new JLabel("Method version");
		GridBagConstraints gbc_lblMethodVersion = new GridBagConstraints();
		gbc_lblMethodVersion.anchor = GridBagConstraints.EAST;
		gbc_lblMethodVersion.insets = new Insets(0, 0, 0, 5);
		gbc_lblMethodVersion.gridx = 0;
		gbc_lblMethodVersion.gridy = 2;
		add(lblMethodVersion, gbc_lblMethodVersion);
		
		methodVersion = new JTextField();
		GridBagConstraints gbc_methodVersion = new GridBagConstraints();
		gbc_methodVersion.fill = GridBagConstraints.HORIZONTAL;
		gbc_methodVersion.gridx = 1;
		gbc_methodVersion.gridy = 2;
		add(methodVersion, gbc_methodVersion);
		methodVersion.setColumns(10);
		methodVersion.getDocument().addDocumentListener(this);
	}


	@Override
	public void refresh(TreeNode selectedNode,
			WorkflowTreeModel workflowTreeModel) {
		activity = null;
		if (selectedNode != null && selectedNode instanceof ActivityTreeNode) {
			activity = (AtomicActivity)((ActivityTreeNode)selectedNode).getActivity();
			
			refreshControls();
		}	
	}

	private void refreshControls() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					activityDetailsChangeListenerActive = false;
					if (activity != null) {
						methodName.setText(activity.getMethodName());
						methodVersion.setText(activity.getMethodVersion());
						chckbxAbstractActivity.setSelected(activity.isAbstract());
					} 
				} catch (Exception exc) {
					exc.printStackTrace();
				}
				activityDetailsChangeListenerActive = true;
			}
		});
	}
	
	//DocumentChangeListener
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		updateActivityFromControls();
	}

	//DocumentChangeListener
	@Override
	public void insertUpdate(DocumentEvent arg0) {
		updateActivityFromControls();
	}

	//DocumentChangeListener
	@Override
	public void removeUpdate(DocumentEvent arg0) {
		updateActivityFromControls();
	}
	
	private void updateActivityFromControls() {
		if (activityDetailsChangeListenerActive) {
			if (activity != null) {
				activity.setMethodName(methodName.getText());
				activity.setMethodVersion(methodVersion.getText());
				activity.setAbstract(chckbxAbstractActivity.isSelected());
			}		
		}
	}
}
