package org.primaresearch.clc.phd.repository.search.matching.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.primaresearch.clc.phd.repository.search.matching.ActivityMatchingResultView;
import org.primaresearch.clc.phd.repository.search.matching.MatchValue;
import org.primaresearch.clc.phd.workflow.activity.Activity;

/**
 * Grid view with scrolling, showing all items of a matching result.
 * 
 * @author clc
 *
 */
public class ActivityMatchingResultGrid extends JScrollPane implements ActivityMatchingResultView {

	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	private Set<ActivityMatchingResultItemListener> listeners = new HashSet<ActivityMatchingResultItemListener>();

	public ActivityMatchingResultGrid() {
		
		this.getVerticalScrollBar().setUnitIncrement(16);
		
		mainPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) mainPanel.getLayout();
		flowLayout.setVgap(8);
		flowLayout.setHgap(8);
		flowLayout.setAlignment(FlowLayout.LEFT);
		mainPanel.setBackground(Color.WHITE);
		setViewportView(mainPanel);
		
		//mainPanel.setPreferredSize(new Dimension(700, 500));
	}
	
	@Override
	public void update(List<MatchValue<Activity>> matchingResult) {
		
		Collections.sort(matchingResult);
		
		mainPanel.removeAll();
		
		for (Iterator<MatchValue<Activity>> it = matchingResult.iterator(); it.hasNext(); ) {
			ActivityMatchingResultItemPanel resultItemPanel = new ActivityMatchingResultItemPanel(it.next());
			mainPanel.add(resultItemPanel);
			for (Iterator<ActivityMatchingResultItemListener> itL = listeners.iterator(); itL.hasNext(); )
				resultItemPanel.addListener(itL.next());
		}
		
		mainPanel.setPreferredSize(new Dimension(700, Math.max(500,matchingResult.size() * 56)));
		
		//mainPanel.doLayout();
		mainPanel.revalidate();
		mainPanel.repaint();
		
		SwingUtilities.invokeLater(new Runnable() {
			   public void run() { 
				   ActivityMatchingResultGrid.this.getVerticalScrollBar().setValue(0);
				   //ActivityMatchingResultGrid.this.scrollRectToVisible(new Rectangle(0, 1, 1, 1));
			   }
			});
	}
	
	public void addListener(ActivityMatchingResultItemListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(ActivityMatchingResultItemListener listener) {
		listeners.remove(listener);
	}
	
	@Override
	public JComponent getComponent() {
		return this;
	}

	@Override
	public void clear() {
		SwingUtilities.invokeLater(new Runnable() {
			   public void run() { 
					mainPanel.removeAll();
			   }
			});
	}
	
}
