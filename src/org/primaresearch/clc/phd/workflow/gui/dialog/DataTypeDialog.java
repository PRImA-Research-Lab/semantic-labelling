package org.primaresearch.clc.phd.workflow.gui.dialog;

import javax.swing.JDialog;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JButton;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.primaresearch.clc.phd.workflow.data.WorkflowDataType;
import org.primaresearch.clc.phd.workflow.io.WorkflowDataTypeHierarchy;

/**
 * Dialogue for data type selection from a tree with all available data types.
 * 
 * @author clc
 *
 */
public class DataTypeDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private String selectedType = null;
	private JTree tree;

	public DataTypeDialog() {
		setTitle("Default Types");
		setSize(300,800);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TreePath path = tree.getSelectionPath();
				if (path != null && path.getPathCount() > 0) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
					if (node.getUserObject() != null) {
						selectedType = ((WorkflowDataType)node.getUserObject()).getId();
					}
				}
				setVisible(false);
				dispatchEvent(new WindowEvent(DataTypeDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		panel.add(btnOk);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		tree = new JTree();
		tree.setBorder(new EmptyBorder(5, 5, 5, 5));
		scrollPane.setViewportView(tree);
		tree.setModel(initTree());
		
		//Expand all
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}

		//this.pack();
	}
	
	private DefaultTreeModel initTree() {
		
		WorkflowDataTypeHierarchy hierarchy = WorkflowDataTypeHierarchy.getInstance();
		
		WorkflowDataType rootType = hierarchy.getRoot();
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootType);
		
		addChildren(rootType, root);
		
		/*DefaultMutableTreeNode root = new DefaultMutableTreeNode(new WorkflowDataType("Data types", null));
		
		//Basic types
		DefaultMutableTreeNode basicTypes = new DefaultMutableTreeNode(new WorkflowDataType("Primitive types", null));
		root.add(basicTypes);
		
		basicTypes.add(new DefaultMutableTreeNode(new WorkflowDataType("Integer", "int")));
		basicTypes.add(new DefaultMutableTreeNode(new WorkflowDataType("Decimal (float/double)", "decimal")));
		basicTypes.add(new DefaultMutableTreeNode(new WorkflowDataType("String/Text", "string")));
		
		//Complex types
		DefaultMutableTreeNode complexTypes = new DefaultMutableTreeNode(new WorkflowDataType("Complex types", null));
		root.add(complexTypes);
		
		// File
		DefaultMutableTreeNode file = new DefaultMutableTreeNode(new WorkflowDataType("File", "file"));
		complexTypes.add(file);

		//  Image
		DefaultMutableTreeNode image = new DefaultMutableTreeNode(new WorkflowDataType("Image", "file.image"));
		file.add(image);

		image.add(new DefaultMutableTreeNode(new WorkflowDataType("TIFF", "file.image.tiff")));
		image.add(new DefaultMutableTreeNode(new WorkflowDataType("PNG", "file.image.png")));
		image.add(new DefaultMutableTreeNode(new WorkflowDataType("JPG", "file.image.jpg")));
		
		//  Text
		DefaultMutableTreeNode txt = new DefaultMutableTreeNode(new WorkflowDataType("Text", "file.text"));
		file.add(txt);

		txt.add(new DefaultMutableTreeNode(new WorkflowDataType("TXT", "file.text.txt")));

		// Page Content
		DefaultMutableTreeNode pageContent = new DefaultMutableTreeNode(new WorkflowDataType("Page Content", "file.pagecontent"));
		file.add(pageContent);

		//  PAGE XML
		DefaultMutableTreeNode pageXml = new DefaultMutableTreeNode(new WorkflowDataType("PAGE XML", "file.pagecontent.PAGEXML"));
		pageContent.add(pageXml);

		pageXml.add(new DefaultMutableTreeNode(new WorkflowDataType("Version 2013-07-15", "file.pagecontent.PAGEXML.2013_07_15")));
		 */
		
		return new DefaultTreeModel(root);
	}
	
	private void addChildren(WorkflowDataType dataType, DefaultMutableTreeNode treeNode) {
		if (dataType == null || treeNode == null)
			return;
		List<WorkflowDataType> childTypes = dataType.getChildren();
		for (Iterator<WorkflowDataType> it = childTypes.iterator(); it.hasNext(); ) {
			WorkflowDataType childType = it.next();
			
			DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(childType);
			treeNode.add(childTreeNode);
			
			//Recursion
			addChildren(childType, childTreeNode);
		}
	}
		
	public String getSelectedType() {
		return selectedType;
	}

}
