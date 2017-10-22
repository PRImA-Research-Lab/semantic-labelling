package org.primaresearch.clc.phd.workflow.gui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.primaresearch.clc.phd.ontology.gui.model.LabelTypeTreeNode;
import org.primaresearch.clc.phd.ontology.label.HasLabels;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.ontology.label.LabelType;
import org.primaresearch.clc.phd.workflow.gui.model.LabelSelectionTreeRoot;

/**
 * Dialogue to add a new label to an activity. Presents a tree with all available label types.
 * Note: Root types cannot be used for labels.
 * 
 * @author clc
 *
 */
public class AddLabelDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private LabelType selectedLabelType = null;

	public AddLabelDialog(HasLabels labelledObject) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Add Label");
		
		Collection<LabelGroup> labelGroups = labelledObject.getLabels();
		Collection<LabelType> freeLabelSlots = new LinkedList<LabelType>();
		for (Iterator<LabelGroup> it = labelGroups.iterator(); it.hasNext(); ) {
			LabelGroup labelGroup = it.next();
			if (labelGroup.canAddLabel())
				freeLabelSlots.add(labelGroup.getType());
		}
		init(freeLabelSlots);
	}
	
	public AddLabelDialog(LabelType labelType) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Choose Label");
		
		Collection<LabelType> labelSlots = new LinkedList<LabelType>();
		labelSlots.add(labelType);
		init(labelSlots);
	}
	
	private void init(Collection<LabelType> labelSlots) {
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel.add(panel_1, BorderLayout.SOUTH);
		
		final JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispatchEvent(new WindowEvent(AddLabelDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		btnOk.setEnabled(false);
		panel_1.add(btnOk);
		
		JTree tree = new JTree();
		tree.setModel(new DefaultTreeModel(new LabelSelectionTreeRoot(labelSlots)));
		panel.add(tree, BorderLayout.CENTER);
		tree.setRootVisible(true);
		tree.expandRow(0);
		
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				
				TreeNode selectedNode = (TreeNode)e.getPath().getLastPathComponent();
				if (selectedNode instanceof LabelTypeTreeNode && ((LabelTypeTreeNode)selectedNode).getLabelType().getParent() != null) {
					btnOk.setEnabled(true);
					selectedLabelType = ((LabelTypeTreeNode)selectedNode).getLabelType();
				} else {
					btnOk.setEnabled(false);
					selectedLabelType = null;
				}
			}
		});
	}

	public LabelType getSelectedLabelType() {
		return selectedLabelType;
	}
	
	
}
