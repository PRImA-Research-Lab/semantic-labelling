package org.primaresearch.clc.phd.workflow.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreeNode;

import org.primaresearch.clc.phd.workflow.gui.RecentDocuments;
import org.primaresearch.clc.phd.workflow.gui.WorkflowEditor;
import org.primaresearch.clc.phd.workflow.gui.model.WorkflowTreeModel;

/**
 * Placeholder panel for workflow component details that is displayed when the editor is opened.
 * 
 * @author clc
 *
 */
public class WelcomePanel extends DetailsPanel {
	private static final long serialVersionUID = 1L;

	public WelcomePanel(RecentDocuments recentDocs, final WorkflowEditor workflowEditor) {
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setLayout(new BorderLayout(0, 10));
		
		JLabel lblWorkflowEditor = new JLabel("Workflow Editor 1.0");
		lblWorkflowEditor.setFont(new Font("Tahoma", Font.PLAIN, 15));
		add(lblWorkflowEditor, BorderLayout.NORTH);
		
		JPanel recentDocsPanel = new JPanel();
		add(recentDocsPanel, BorderLayout.CENTER);
		GridBagLayout gbl_recentDocsPanel = new GridBagLayout();
		gbl_recentDocsPanel.columnWidths = new int[]{0};
		int [] rowHeights = new int[recentDocs.getRecentDocs().size()];
		for (int i=0; i<recentDocs.getRecentDocs().size(); i++)
			rowHeights[i] = 25;
		gbl_recentDocsPanel.rowHeights = rowHeights;
		gbl_recentDocsPanel.columnWeights = new double[]{Double.MIN_VALUE};
		double [] rowWeights = new double[recentDocs.getRecentDocs().size()];
		for (int i=0; i<recentDocs.getRecentDocs().size(); i++)
			rowWeights[i] = 1;
		gbl_recentDocsPanel.rowWeights = rowWeights;
		recentDocsPanel.setLayout(gbl_recentDocsPanel);
		
		int y=0;
		for (Iterator<String> it = recentDocs.getRecentDocs().iterator(); it.hasNext(); ) {
			String p = it.next();
			
			JLabel label = new JLabel(p);
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(0, 0, 5, 0);
			gbc.gridx = 0;
			gbc.gridy = y;

			recentDocsPanel.add(label, gbc);
			
			label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			label.setForeground(Color.blue);
			
			label.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent arg0) {
				}
				
				@Override
				public void mousePressed(MouseEvent arg0) {
				}
				
				@Override
				public void mouseExited(MouseEvent arg0) {
				}
				
				@Override
				public void mouseEntered(MouseEvent arg0) {
				}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					final String filePath = ((JLabel)e.getSource()).getText();
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							try {
								workflowEditor.loadWorkflow(filePath);
							} catch (Exception exc) {
								exc.printStackTrace();
							}
						}
					});					
				}
			});
			y++;
		}
	}

	@Override
	public void refresh(TreeNode selectedNode, WorkflowTreeModel workflowTreeModel) {
	}

}
