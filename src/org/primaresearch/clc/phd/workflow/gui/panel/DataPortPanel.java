package org.primaresearch.clc.phd.workflow.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.primaresearch.clc.phd.ontology.label.Label;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.IfElsePort;
import org.primaresearch.clc.phd.workflow.data.port.LoopPort;
import org.primaresearch.clc.phd.workflow.gui.dialog.DataPortDialog;


/**
 * Panel representing a single input or output data port of an activity.
 * 
 * @author clc
 *
 */
public class DataPortPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private DataPort dataPort;
	private Activity activity;
	private JButton editDataObject;
	private IdGenerator idRegister;
	private Workflow workflow;

	public DataPortPanel(Workflow workflow, final DataPort dataPort, Activity activity, final DataObjectPanelListener listener, IdGenerator idRegister) {
		super();
		this.dataPort = dataPort;
		this.activity = activity;
		this.idRegister = idRegister;
		this.workflow = workflow;
		
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setBackground(new Color(70, 130, 180));
		setLayout(new BorderLayout(5, 0));
		
		if (!(dataPort instanceof LoopPort) && !(dataPort instanceof IfElsePort)) {
			JButton removeDataObject = new JButton("X");
			removeDataObject.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (listener != null)
						listener.removeDataObjectButtonClicked(DataPortPanel.this);
				}
			});
			add(removeDataObject, BorderLayout.EAST);
		}
		
		editDataObject = new JButton(dataPort.getDataObject() != null ? dataPort.getDataObject().getCaption() : "[new port]");
		editDataObject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openEditDialog();
			}
		});
		editDataObject.setToolTipText(composeLabelTypesToolTip(dataPort));
		add(editDataObject, BorderLayout.CENTER);
	}
	
	public DataPort getDataPort() {
		return dataPort;
	}

	public void openEditDialog() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DataPortDialog dlg = new DataPortDialog(workflow, dataPort, activity, idRegister);
				dlg.setSize(500, 500);
				dlg.setLocation((int)(editDataObject.getLocationOnScreen().getX() + 5.0),
						(int)(editDataObject.getLocationOnScreen().getY() + editDataObject.getHeight() + 10.0));
				dlg.setVisible(true);
				
				editDataObject.setText(dataPort.getDataObject().getCaption());
				doLayout();
			}
		});
	}
	
	private String composeLabelTypesToolTip(DataPort dataPort) {
		if (dataPort == null)
			return "";
		StringBuilder toolTip = new StringBuilder();
		toolTip.append("<html>");
		Collection<LabelGroup> labelGroups = dataPort.getDataObject().getLabels();
		for (Iterator<LabelGroup> it = labelGroups.iterator(); it.hasNext(); ) {
			LabelGroup group = it.next();
			if (group.getLabels() == null || group.getLabels().isEmpty())
				continue;
			toolTip.append("<h3>");
			toolTip.append(group.getType().getCaption());
			toolTip.append("</h3>");
			for (Iterator<Label> itLabels = group.getLabels().iterator(); itLabels.hasNext();) {
				Label label = itLabels.next();
				toolTip.append(label.getType().getCaption());
				toolTip.append("<br>");
			}
		}
		toolTip.append("</html>");
		return toolTip.toString();
	}

	/**
	 * Listener interface for data object events (such as removal of a data object).
	 * @author clc
	 *
	 */
	public static interface DataObjectPanelListener {
		/** Called when the 'Remove' button of a label panel has been clicked. */
		public void removeDataObjectButtonClicked(DataPortPanel panel);
	}
}
