package org.primaresearch.clc.phd.repository.search.matching.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.primaresearch.clc.phd.repository.search.matching.ActivityMatchingResultView.ActivityMatchingResultItemListener;
import org.primaresearch.clc.phd.repository.search.matching.MatchValue;
import org.primaresearch.clc.phd.workflow.activity.Activity;

/**
 * A clickable panel for a single matching result item.
 * @author clc
 *
 */
public class ActivityMatchingResultItemPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = 1L;
	private MatchValue<Activity> matchValue;
	private static DecimalFormat formatter = new DecimalFormat("#0.0");
	private Set<ActivityMatchingResultItemListener> listeners = new HashSet<ActivityMatchingResultItemListener>();
	
	public ActivityMatchingResultItemPanel(final MatchValue<Activity> matchValue) {
		super();
		setSize(250, 100);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setPreferredSize(new Dimension(300, 100));
		setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, null));
		setBackground(Color.WHITE);
		this.matchValue = matchValue;
		Activity activity = matchValue.getObject();
		setLayout(new BorderLayout(0, 5));
		this.addMouseListener(this);
		
		String tooltip = createTooltip(activity);
		
		JLabel lblTitle = new JLabel(activity.getCaption());
		lblTitle.setForeground(new Color(70, 130, 180));
		lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblTitle.setBorder(new EmptyBorder(5,5,0,5));
		add(lblTitle, BorderLayout.NORTH);
		lblTitle.setToolTipText(tooltip);
		lblTitle.addMouseListener(this);
		
		JPanel textPanel = new JPanel(new BorderLayout());
		add(textPanel, BorderLayout.CENTER);
		
		JButton moreInfo = new JButton("...");
		textPanel.add(moreInfo, BorderLayout.EAST);
		moreInfo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						ActivityMatchingResultDetailsDialog dlg = new ActivityMatchingResultDetailsDialog(matchValue);
						dlg.pack();
						dlg.setVisible(true);
					}
				});
			}
		});
		
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		textArea.setForeground(Color.DARK_GRAY);
		textArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
		textArea.setLineWrap(true);
		textArea.setBorder(new EmptyBorder(0,5,5,5));
		textArea.setText("" + formatter.format(matchValue.getMatchScore()) + "%");
		textPanel.add(textArea, BorderLayout.CENTER);
		textArea.setToolTipText(tooltip);
		textArea.addMouseListener(this);
	}

	public Activity getActivity() {
		return matchValue.getObject();
	}
	
	public void addListener(ActivityMatchingResultItemListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(ActivityMatchingResultItemListener listener) {
		listeners.remove(listener);
	}
	
	private void notifyListenersItemClicked() {
		for (Iterator<ActivityMatchingResultItemListener> it = listeners.iterator(); it.hasNext(); )
			it.next().activityMatchingResultItemClicked(matchValue);
	}
	
	/**
	 * Opens the workflow of this panel
	 */
	//private void openWorkflow() {
	//	WorkflowEditor frame = new WorkflowEditor(workflow, workflow.getLocation(), false);
	//	frame.setVisible(true);
	//}
	
	private String createTooltip(Activity activity) {
		StringBuilder txt = new StringBuilder();
		
		if (activity == null)
			return "<empty>";
		
		txt.append("<html>");
		txt.append("<h2>");
		txt.append(activity.getCaption());
		txt.append("</h2>");
		
		txt.append("<table border=\"1\">");
		txt.append("<tr><th>Match Details</th></tr>");
		
		txt.append("<tr>");
		txt.append("<td>");
		txt.append(matchValue.getMatchDescription().replaceAll("\n", "<br/>"));
		txt.append("</td>");
		txt.append("</tr>");
		
		txt.append("</table>");
		
		/*txt.append("<table border=\"1\">");
		txt.append("<tr><th>Input</th><th>Output</th></tr>");
		
		for (int i=0; i<Math.max(activity.getInputPorts().size(), activity.getOutputPorts().size()); i++) {
			txt.append("<tr>");
			txt.append("<td>");
			if (i<activity.getInputPorts().size()) {
				InputPort p = activity.getInputPorts().get(i);
				txt.append(p.getDataObject().getCaption());
			}
			txt.append("</td>");
			txt.append("<td>");
			if (i<activity.getOutputPorts().size()) {
				OutputPort p = activity.getOutputPorts().get(i);
				txt.append(p.getDataObject().getCaption());
			}
			txt.append("</td>");
			txt.append("</tr>");
		}
		
		txt.append("</table>");*/
		
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
				notifyListenersItemClicked();		
			}
		});
	}
	

}
