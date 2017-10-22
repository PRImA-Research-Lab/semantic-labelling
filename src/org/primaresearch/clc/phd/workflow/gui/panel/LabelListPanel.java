package org.primaresearch.clc.phd.workflow.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import org.primaresearch.clc.phd.ontology.label.HasLabels;
import org.primaresearch.clc.phd.ontology.label.Label;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.ontology.label.LabelGroup.TooManyLabelsInGroupException;
import org.primaresearch.clc.phd.ontology.label.LabelType;
import org.primaresearch.clc.phd.workflow.gui.dialog.AddLabelDialog;
import org.primaresearch.clc.phd.workflow.gui.panel.LabelPanel.LabelPanelListener;

/**
 * Container for label panels representing all labels of a workflow activity.
 * 
 * @author clc
 *
 */
public class LabelListPanel extends JPanel implements LabelPanelListener {

	private static final long serialVersionUID = 1L;
	private HasLabels labelledObject;
	private JPanel mainPanel;
	
	public LabelListPanel(HasLabels labelledObject) {
		this.labelledObject = labelledObject;
		setLayout(new BorderLayout(0, 0));
		
		mainPanel = new JPanel();
		mainPanel.setBackground(Color.WHITE);
		add(mainPanel, BorderLayout.CENTER);
	
		FlowLayout fl_labelPanel = (FlowLayout) mainPanel.getLayout();
		fl_labelPanel.setAlignment(FlowLayout.LEFT);
		mainPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		final JButton addLabel = new JButton("+");
		addLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						final AddLabelDialog dlg = new AddLabelDialog(LabelListPanel.this.labelledObject);
						final int width = 300;
						final int height = 300;
						dlg.setSize(width, height);
						int x = (int)(addLabel.getLocationOnScreen().getX() + 5.0);
						int y = (int)(addLabel.getLocationOnScreen().getY() + addLabel.getHeight() + 10.0);
						Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
						if (x+width > screensize.width)
							x = screensize.width - width;
						if (y+height > screensize.height - 60)
							y = screensize.height - height - 60;
						dlg.setLocation(x, y);
						dlg.setVisible(true);
						
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								try {
									//Add label
									if (dlg.getSelectedLabelType() != null) {
										//Add to activity
										Label newLabel = null;
										try {
											newLabel = LabelListPanel.this.labelledObject.addLabel(dlg.getSelectedLabelType());
										} catch (TooManyLabelsInGroupException e1) {
											e1.printStackTrace();
										}
										//Add to panel
										LabelPanel labelPanel = new LabelPanel(newLabel, LabelListPanel.this);
										mainPanel.add(labelPanel, mainPanel.getComponentCount()-1);
										mainPanel.repaint();
										mainPanel.revalidate();
										labelPanel.repaint();
										labelPanel.revalidate();
									}
								} catch (Exception exc) {
									exc.printStackTrace();
								}
							}
						});
					}
				});

			}
		});
		mainPanel.add(addLabel);
		mainPanel.repaint();
		mainPanel.revalidate();
	}
	
	public void refresh(final HasLabels labelledObject) {
		this.labelledObject = labelledObject;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				while (mainPanel.getComponentCount() > 1)
					mainPanel.remove(0);
				
				Collection<LabelGroup> groups = labelledObject.getLabels();
				for (Iterator<LabelGroup> it = groups.iterator(); it.hasNext(); ) {
					LabelGroup group = it.next();
					
					List<Label> labels = group.getLabels();
					
					for (Iterator<Label> itLabel = labels.iterator(); itLabel.hasNext(); ) {
						Label label = itLabel.next();
						//Add to panel
						LabelPanel labelPanel = new LabelPanel(label, LabelListPanel.this);
						mainPanel.add(labelPanel, mainPanel.getComponentCount()-1);
						labelPanel.repaint();
						labelPanel.revalidate();
					}
				}
				mainPanel.repaint();
				mainPanel.revalidate();
			}
		});
	}
	
	@Override
	public void removeLabelButtonClicked(final LabelPanel panel) {
		//Remove from activity
		labelledObject.removeLabel(panel.getLabel());
		
		//Remove from parent panel
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					mainPanel.remove(panel);
					mainPanel.repaint();
					mainPanel.revalidate();
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public void labelTypeChanged(LabelPanel panel, LabelType oldType) {
		panel.doLayout();
		mainPanel.doLayout();
		mainPanel.repaint();
		mainPanel.revalidate();
	}

	public HasLabels getLabelledObject() {
		return labelledObject;
	}

	public void setLabelledObject(HasLabels labelledObject) {
		this.labelledObject = labelledObject;
		refresh(labelledObject);
	}

}
