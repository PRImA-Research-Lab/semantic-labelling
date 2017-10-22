package org.primaresearch.clc.phd.repository.search.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.primaresearch.clc.phd.repository.search.gui.WorkflowSearchResultView.WorkflowSearchResultItemListener;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;
import org.primaresearch.clc.phd.workflow.gui.WorkflowEditor;

/**
 * Panel for a single workflow search result item. The item is clickable and the respective action can be handled with a listener.
 * 
 * @author clc
 *
 */
public class WorkflowSearchResultItemPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = 1L;
	private Workflow workflow;
	private Set<WorkflowSearchResultItemListener> listeners = new HashSet<WorkflowSearchResultItemListener>();
	
	public WorkflowSearchResultItemPanel(Workflow workflow) {
		super();
		setSize(250, 100);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setPreferredSize(new Dimension(300, 100));
		setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, null));
		setBackground(Color.WHITE);
		this.workflow = workflow;
		setLayout(new BorderLayout(0, 5));
		this.addMouseListener(this);
		
		String tooltip = createTooltip(workflow);
		
		JLabel lblTitle = new JLabel(workflow.getName());
		lblTitle.setForeground(new Color(70, 130, 180));
		lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblTitle.setBorder(new EmptyBorder(5,5,0,5));
		add(lblTitle, BorderLayout.NORTH);
		lblTitle.setToolTipText(tooltip);
		lblTitle.addMouseListener(this);
		
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		textArea.setForeground(Color.DARK_GRAY);
		textArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
		textArea.setLineWrap(true);
		textArea.setBorder(new EmptyBorder(0,5,5,5));
		textArea.setText(workflow.getDescription(0).getText());
		add(textArea, BorderLayout.CENTER);
		textArea.setToolTipText(tooltip);
		textArea.addMouseListener(this);
	}

	public Workflow getWorkflow() {
		return workflow;
	}
	
	public void addListener(WorkflowSearchResultItemListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(WorkflowSearchResultItemListener listener) {
		listeners.remove(listener);
	}
	
	private void notifyListenersItemClicked() {
		for (Iterator<WorkflowSearchResultItemListener> it = listeners.iterator(); it.hasNext(); )
			it.next().workflowSearchResultItemClicked(workflow);
	}
	
	/**
	 * Opens the workflow of this panel
	 */
	private void openWorkflow() {
		WorkflowEditor frame = new WorkflowEditor(workflow, workflow.getLocation(), false);
		frame.setVisible(true);
	}
	
	private String createTooltip(Workflow workflow) {
		StringBuilder txt = new StringBuilder();
		
		Activity act = workflow.getRootActivity();
		
		if (act == null)
			return "<empty>";
		
		txt.append("<html>");
		txt.append("<h2>");
		txt.append(act.getCaption());
		txt.append("</h2>");
		txt.append("<table border=\"1\">");
		txt.append("<tr><th>Input</th><th>Output</th></tr>");
		
		for (int i=0; i<Math.max(act.getInputPorts().size(), act.getOutputPorts().size()); i++) {
			txt.append("<tr>");
			txt.append("<td>");
			if (i<act.getInputPorts().size()) {
				InputPort p = act.getInputPorts().get(i);
				txt.append(p.getDataObject().getCaption());
			}
			txt.append("</td>");
			txt.append("<td>");
			if (i<act.getOutputPorts().size()) {
				OutputPort p = act.getOutputPorts().get(i);
				txt.append(p.getDataObject().getCaption());
			}
			txt.append("</td>");
			txt.append("</tr>");
			if (workflow.getRootActivity() != null)	{
				txt.append("<tr>");
				txt.append("<td colspan=\"2\">");
				txt.append(workflow.getRootActivity().isAbstract() ? "abstract (not executable)" : "concrete (executable)");
				txt.append("</td>");
				txt.append("</tr>");
			}
		}
		
		txt.append("</table>");
		txt.append("</html>");
		
		return txt.toString();
	}
	
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
	public void mouseClicked(MouseEvent arg0) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (listeners.isEmpty())
					openWorkflow();
				else
					notifyListenersItemClicked();
			}
		});
	}
}
