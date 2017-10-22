package org.primaresearch.clc.phd.workflow.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.gui.dialog.DataPortDialog;
import org.primaresearch.clc.phd.workflow.gui.model.DataPortAlignment;

/**
 * Panel to allow the user to align data ports.
 * 
 * @author clc
 *
 */
public class DataPortAlignmentPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private DataPortAlignment<?> portAlignemntModel;
	private JPanel referencePortColumn = null;
	private JPanel portsToBeAlignedColumn = null;
	private Workflow workflow;
	
	private Color referenceBackground;
	private Color toBeAlignedBackground;
	private boolean referencePortsOnLeftSide;

	public DataPortAlignmentPanel(Workflow workflow, DataPortAlignment<?> portAlignemntModel, 
									Color referenceBackground, Color toBeAlignedBackground,
									boolean referencePortsOnLeftSide) {
		super(new GridLayout(0, 2));
		
		this.portAlignemntModel = portAlignemntModel;
		this.referenceBackground = referenceBackground;
		this.toBeAlignedBackground = toBeAlignedBackground;
		this.referencePortsOnLeftSide = referencePortsOnLeftSide;
		this.workflow = workflow;
		
		refresh();
	}
	
	public void refresh() {
		
		if (referencePortsOnLeftSide) {
			addReferencePorts();
			addPortsToBeAligned();
		} else {
			addPortsToBeAligned();
			addReferencePorts();
		}
		revalidate();
	}
	
	private void addReferencePorts() {
		//Reference 
		if (referencePortColumn == null) {
			referencePortColumn = new JPanel(new FlowLayout());
			referencePortColumn.setBackground(referenceBackground);
			referencePortColumn.setPreferredSize(new Dimension(125, 500));
			add(referencePortColumn);
		} else {
			referencePortColumn.removeAll();
		}
		
		for (Iterator<? extends DataPort> it = portAlignemntModel.getReferencePorts().iterator(); it.hasNext(); ) {
			DataPort port = it.next();
			referencePortColumn.add(new DataPortAlignmentTile(workflow, this, port, portAlignemntModel.getReferenceActivity(), 
																referenceBackground, Color.WHITE, portAlignemntModel));
		}
	}
	
	private void addPortsToBeAligned() {
		//To be aligned
		if (portsToBeAlignedColumn == null) {
			portsToBeAlignedColumn = new JPanel(new FlowLayout());
			portsToBeAlignedColumn.setBackground(toBeAlignedBackground);
			portsToBeAlignedColumn.setPreferredSize(new Dimension(125, 500));
			add(portsToBeAlignedColumn);
		} else {
			portsToBeAlignedColumn.removeAll();
		}
		
		int i=0;
		for (Iterator<? extends DataPort> it = portAlignemntModel.getPortsToBeAligned().iterator(); it.hasNext(); ) {
			DataPort port = it.next();
			double matchScore = portAlignemntModel.matchPorts(i);
			portsToBeAlignedColumn.add(new DataPortAlignmentTile(workflow, this, port, portAlignemntModel.getActivityOfPortsToBeAligned(), 
																	toBeAlignedBackground, getMatchColor(matchScore), portAlignemntModel));
			i++;
		}
	}
	
	/**
	 * Returns a colour representing the given match score (red to yellow to green)
	 * @param matchScore 0..100 (or negative for invalid)
	 * @return
	 */
	private Color getMatchColor(double matchScore) {
		if (matchScore < 0.0)
			return new Color(255, 217, 217);
		
		matchScore /= 100.0;
		
		if (matchScore < 0.5) {
			//Red to yellow
			return new Color(255, (int)(510.0 * matchScore), 0);
		}
		
		//Yellow to green
		double x = (matchScore - 0.5) * 2.0;
		return new Color(255 - (int)(255.0 * x), 255, 0);
	}
	
	
	/**
	 * Small panel for one data port. Contains button to open data port details dialogue and up/down buttons
	 * to align the ports of the current activity with the ports of the replacement activity.
	 * @author clc
	 *
	 */
	private static class DataPortAlignmentTile extends JPanel {

		private static final long serialVersionUID = 1L;
		private DataPort dataPort;
		private Activity activity;
		private JButton editDataObject;
		private Workflow workflow;

		public DataPortAlignmentTile(Workflow workflow, final DataPortAlignmentPanel parent, final DataPort dataPort, Activity activity, 
									Color backgroundColor, Color foregroundColor,
									final DataPortAlignment<? extends DataPort> portAlignemntModel) {
			super();
			this.dataPort = dataPort;
			this.activity = activity;
			this.workflow = workflow;
			
			setBorder(new EmptyBorder(5, 5, 5, 5));
			setBackground(backgroundColor);
			setLayout(new BorderLayout(5, 0));
			
			JPanel upDownPanel = new JPanel(new FlowLayout());
			upDownPanel.setBackground(foregroundColor);
			JButton btnUp = new JButton("↑"/*"Up"*/);
			btnUp.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							portAlignemntModel.movePortUp(dataPort);
							parent.refresh();
						}
					});
				}
			});
			if (dataPort == null)
				btnUp.setEnabled(false);
			upDownPanel.add(btnUp);

			JButton btnDown = new JButton("↓"/*"Down"*/);
			btnDown.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							portAlignemntModel.movePortDown(dataPort);
							parent.refresh();
						}
					});
				}
			});
			upDownPanel.add(btnDown);
			if (dataPort == null)
				btnDown.setEnabled(false);

			add(upDownPanel, BorderLayout.SOUTH);
			
			editDataObject = new JButton(dataPort != null && dataPort.getDataObject() != null ? dataPort.getDataObject().getCaption() : "[]");
			editDataObject.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openEditDialog();
				}
			});
			editDataObject.setToolTipText(composeLabelTypesToolTip(dataPort));
			add(editDataObject, BorderLayout.CENTER);
			if (dataPort == null)
				editDataObject.setEnabled(false);
		}
		
		public void openEditDialog() {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					DataPortDialog dlg = new DataPortDialog(workflow, dataPort, activity, null);
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

	}
}
