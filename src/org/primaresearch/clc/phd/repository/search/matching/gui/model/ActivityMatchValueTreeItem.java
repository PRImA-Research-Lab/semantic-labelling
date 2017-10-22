package org.primaresearch.clc.phd.repository.search.matching.gui.model;

import java.text.DecimalFormat;
import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;

import org.primaresearch.clc.phd.repository.search.matching.MatchValue;

/**
 * DefaultMutableTreeNode extension for result items of an activity matching.
 * 
 * @author clc
 *
 */
public class ActivityMatchValueTreeItem extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;
	private MatchValue<?> matchValue;
	private static DecimalFormat formatter = new DecimalFormat("#0.0");
	
	public ActivityMatchValueTreeItem(MatchValue<?> matchValue) {
		this.matchValue = matchValue;
		
		if (!matchValue.getSubValues().isEmpty()) {
			for (Iterator<MatchValue<?>> it = matchValue.getSubValues().iterator(); it.hasNext(); ) {
				add(new ActivityMatchValueTreeItem(it.next()));
			}
		}
	}
	
	@Override
	public String toString() {
		String str = matchValue.getCaption();
		return (str != null && !str.isEmpty() ? str : "[]") + " - " + formatter.format(matchValue.getMatchScore())+"%, weight "+matchValue.getMatchWeight();
	}
}
