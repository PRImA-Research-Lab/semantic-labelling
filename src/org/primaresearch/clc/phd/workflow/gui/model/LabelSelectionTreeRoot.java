package org.primaresearch.clc.phd.workflow.gui.model;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.primaresearch.clc.phd.ontology.gui.model.LabelTypeTreeNode;
import org.primaresearch.clc.phd.ontology.label.LabelType;

/**
 * Root node for label type selection tree
 * 
 * @author clc
 *
 */
public class LabelSelectionTreeRoot extends DefaultMutableTreeNode implements
		TreeNode {

	private static final long serialVersionUID = 1L;

	public LabelSelectionTreeRoot(Collection<LabelType> allowedLabels) {
		addChildNodes(allowedLabels);
	}
	
	private void addChildNodes(Collection<LabelType> allowedLabels) {
		for (Iterator<LabelType> it = allowedLabels.iterator(); it.hasNext(); ) {
			LabelTypeTreeNode node = new LabelTypeTreeNode(it.next());
			add(node);
		}
	}
	
	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public TreeNode getParent() {
		return null;
	}

	@Override
	public String toString() {
		return "Ontology";
	}
}
