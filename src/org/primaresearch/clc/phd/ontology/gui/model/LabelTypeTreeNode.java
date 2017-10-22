package org.primaresearch.clc.phd.ontology.gui.model;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.primaresearch.clc.phd.ontology.label.LabelType;

/**
 * Tree node implementation for ontology label types.
 * 
 * @author clc
 *
 */
public class LabelTypeTreeNode extends DefaultMutableTreeNode implements TreeNode {

	private static final long serialVersionUID = 1L;
	private LabelType labelType;


	/**
	 * Constructor
	 */
	public LabelTypeTreeNode(LabelType labelType) {
		this.labelType = labelType;
		addChildNodes();
	}

	/**
	 * Creates and adds nodes for all child label types.
	 */
	private void addChildNodes() {
		if (labelType != null) {
			Collection<LabelType> childTypes = labelType.getChildren();
			if (childTypes != null) {
				for (Iterator<LabelType> it = childTypes.iterator(); it.hasNext(); ) {
					LabelTypeTreeNode node = new LabelTypeTreeNode(it.next());
					add(node);
					//System.out.println(node.getLabelType().getName());
				}
			}
		}
	}
	
	public LabelType getLabelType() {
		return labelType;
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public String toString() {
		return labelType != null ? labelType.getCaption() : "unknown";
	}
	

}
