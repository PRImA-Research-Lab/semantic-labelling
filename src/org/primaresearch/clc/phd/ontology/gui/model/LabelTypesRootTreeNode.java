package org.primaresearch.clc.phd.ontology.gui.model;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.primaresearch.clc.phd.ontology.Ontology;
import org.primaresearch.clc.phd.ontology.label.LabelType;

/**
 * Tree node implementation for the ontology tree root.
 * 
 * @author clc
 *
 */
public class LabelTypesRootTreeNode extends DefaultMutableTreeNode implements TreeNode {

	private static final long serialVersionUID = 1L;
	private Ontology ontology;
	

	public LabelTypesRootTreeNode(Ontology ontology) {
		this.ontology = ontology;
		addChildNodes();
	}
	
	/**
	 * Adds nodes for all root label types of the ontology
	 */
	private void addChildNodes() {
		Collection<LabelType> rootTypes = ontology.getRootTypes();
		for (Iterator<LabelType> it = rootTypes.iterator(); it.hasNext(); ) {
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
		return null; //No parent
	}

	@Override
	public String toString() {
		return "Label Taxonomies";
	}
	

}
