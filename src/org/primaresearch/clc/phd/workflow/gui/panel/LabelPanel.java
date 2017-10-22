package org.primaresearch.clc.phd.workflow.gui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.primaresearch.clc.phd.ontology.label.Label;
import org.primaresearch.clc.phd.ontology.label.LabelType;
import org.primaresearch.clc.phd.workflow.gui.dialog.AddLabelDialog;

/**
 * Small panel with label root type (heading), label (button), and remove button.
 * This panel is used in the activity details panel (labels of the activity).
 * 
 * @author clc
 *
 */
public class LabelPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Label label;
	private LabelPanelListener panelListener;
	
	public LabelPanel(final Label label, LabelPanelListener listener) {
		this.label = label;
		this.panelListener = listener;
		
		setBackground(new Color(255, 222, 173));
		setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblLabeltype = new JLabel(label.getType().getRootType().getCaption());
		GridBagConstraints gbc_lblLabeltype = new GridBagConstraints();
		gbc_lblLabeltype.insets = new Insets(0, 0, 5, 5);
		gbc_lblLabeltype.gridx = 0;
		gbc_lblLabeltype.gridy = 0;
		add(lblLabeltype, gbc_lblLabeltype);
		
		JButton btnRemove = new JButton("X");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (panelListener != null)
					panelListener.removeLabelButtonClicked(LabelPanel.this);
			}
		});
		GridBagConstraints gbc_btnRemove = new GridBagConstraints();
		gbc_btnRemove.anchor = GridBagConstraints.EAST;
		gbc_btnRemove.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemove.gridx = 1;
		gbc_btnRemove.gridy = 0;
		add(btnRemove, gbc_btnRemove);
		
		final JButton btnLabel = new JButton(label.getType().getCaption());
		btnLabel.setToolTipText(label.getType().getId());
		btnLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (panelListener != null) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {

							//Show the 'Add label' dialogue but with reduced choices (only one root type)
							final AddLabelDialog dlg = new AddLabelDialog(label.getType().getRootType());
							
							final int width = 300;
							final int height = 300;
							dlg.setSize(width, height);
							int x = (int)(btnLabel.getLocationOnScreen().getX() + 5.0);
							int y = (int)(btnLabel.getLocationOnScreen().getY() + btnLabel.getHeight() + 10.0);
							Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
							if (x+width > screensize.width)
								x = screensize.width - width;
							if (y+height > screensize.height-60)
								y = screensize.height - height - 60;
							dlg.setLocation(x, y);
		
							dlg.setVisible(true);
							
							if (dlg.getSelectedLabelType() != null && !dlg.getSelectedLabelType().equals(label.getType())) {
								LabelType oldType = label.getType();
								//Set type
								label.setType(dlg.getSelectedLabelType());
								//Update button
								btnLabel.setText(label.getType().getCaption());
								btnLabel.setToolTipText(label.getType().getId());
								//Notify listener
								panelListener.labelTypeChanged(LabelPanel.this, oldType);
							}
						}
					});
				}
			}
		});
		GridBagConstraints gbc_btnLabel = new GridBagConstraints();
		gbc_btnLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnLabel.gridwidth = 2;
		gbc_btnLabel.insets = new Insets(0, 0, 0, 5);
		gbc_btnLabel.gridx = 0;
		gbc_btnLabel.gridy = 1;
		add(btnLabel, gbc_btnLabel);
	}
	
	public Label getLabel() {
		return label;
	}


	/**
	 * Listener for label panel events ('remove label' clicked, ...)
	 * 
	 * @author clc
	 *
	 */
	public static interface LabelPanelListener {
		/** Called when the 'Remove' button of a label panel has been clicked. */
		public void removeLabelButtonClicked(LabelPanel panel);
		/** Called when a new type for the label has been selected by the user. */
		public void labelTypeChanged(LabelPanel panel, LabelType oldType);
	}
}
