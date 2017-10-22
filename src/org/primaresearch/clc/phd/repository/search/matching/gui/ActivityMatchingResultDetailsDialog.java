package org.primaresearch.clc.phd.repository.search.matching.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.primaresearch.clc.phd.repository.search.matching.MatchValue;
import org.primaresearch.clc.phd.repository.search.matching.gui.model.ActivityMatchValueTreeItem;
import org.primaresearch.clc.phd.workflow.activity.Activity;

/**
 * Dialogue with tree for all match values of an activity matching.
 * 
 * @author clc
 *
 */
public class ActivityMatchingResultDetailsDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	public ActivityMatchingResultDetailsDialog(MatchValue<Activity> rootMatchValue) {
		setTitle("Match Details");
		setModalityType(ModalityType.APPLICATION_MODAL);
		
		JTree tree = new JTree();
		getContentPane().add(tree, BorderLayout.CENTER);
		DefaultMutableTreeNode root = new ActivityMatchValueTreeItem(rootMatchValue);
		tree.setModel(new DefaultTreeModel(root));

		//Expand all
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
		
		getContentPane().setBackground(Color.WHITE);
		((JPanel)getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
	}

}
