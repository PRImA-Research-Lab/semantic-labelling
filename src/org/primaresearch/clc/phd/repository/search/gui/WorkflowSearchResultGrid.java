package org.primaresearch.clc.phd.repository.search.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.primaresearch.clc.phd.workflow.Workflow;

/**
 * Grid view for workflow search result items. Scrollable.
 * @author clc
 *
 */
public class WorkflowSearchResultGrid extends JScrollPane implements WorkflowSearchResultView {

	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	private Set<WorkflowSearchResultItemListener> listeners = new HashSet<WorkflowSearchResultItemListener>();

	public WorkflowSearchResultGrid() {
		
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
	public void update(final Collection<Workflow> workflows) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mainPanel.removeAll();
				
				for (Iterator<Workflow> it = workflows.iterator(); it.hasNext(); ) {
					WorkflowSearchResultItemPanel itemPanel = new WorkflowSearchResultItemPanel(it.next()); 
					mainPanel.add(itemPanel);
					
					for (Iterator<WorkflowSearchResultItemListener> itL = listeners.iterator(); itL.hasNext(); )
						itemPanel.addListener(itL.next());
				}
				
				mainPanel.setPreferredSize(new Dimension(700, Math.max(500, workflows.size() * 56)));
				
				//mainPanel.doLayout();
				mainPanel.revalidate();
				mainPanel.repaint();
			}
		});
			
	}

	@Override
	public JComponent getComponent() {
		return this;
	}
	
	public void addListener(WorkflowSearchResultItemListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(WorkflowSearchResultItemListener listener) {
		listeners.remove(listener);
	}
}
