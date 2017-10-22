package org.primaresearch.clc.phd.workflow.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.gui.model.PortLinkTreeNode;

import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

/**
 * Dialog for selecting a source of an input port or a 'forwarded from' for output ports
 * @author clc
 *
 */
public class SelectPortLinkDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private DataPort linkTarget;
	
	private DataPort linkSource = null;

	public SelectPortLinkDialog(final Workflow workflow, DataPort linkTarget) {
		this.linkTarget = linkTarget;
		
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Data Port Link");
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 5));
		
		JPanel buttonPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		final JButton btnOk = new JButton("OK");
		btnOk.setEnabled(false);
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispatchEvent(new WindowEvent(SelectPortLinkDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		buttonPanel.add(btnOk);
		
		JTree tree = new JTree();
		tree.setBorder(new LineBorder(Color.LIGHT_GRAY));
		mainPanel.add(tree, BorderLayout.CENTER);
		tree.setModel(new DefaultTreeModel(new PortLinkTreeNode(workflow, this.linkTarget)));
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				PortLinkTreeNode selectedNode = (PortLinkTreeNode)e.getPath().getLastPathComponent();
				linkSource = selectedNode.getLinkSource();
				btnOk.setEnabled(linkSource != null);
			}
		});
		//Expand all
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
		
		JLabel lblNewLabel = new JLabel("Select a link source");
		mainPanel.add(lblNewLabel, BorderLayout.NORTH);
		

	}

	public DataPort getLinkSource() {
		return linkSource;
	}

}
