package org.primaresearch.clc.phd.repository.search.gui;

import java.awt.Color;
import java.awt.Font;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.ontology.label.LabelType;
import org.primaresearch.clc.phd.repository.search.LabelWorkflowFilter;
import org.primaresearch.clc.phd.repository.search.LabellableObjectFilter;

/**
 * Panel with check boxes for all label types of a workflow
 * 
 * @author clc
 *
 */
public class LabelWorkflowFilterPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private LabelWorkflowFilter filter;
	private Map<LabelType, JCheckBox> acticityFilterCheckboxes = new HashMap<LabelType, JCheckBox>();
	private Map<LabelType, JCheckBox> inputDataFilterCheckboxes = new HashMap<LabelType, JCheckBox>();
	private Map<LabelType, JCheckBox> outputDataFilterCheckboxes = new HashMap<LabelType, JCheckBox>();
	private Map<LabelType, JCheckBox> dataTableFilterCheckboxes = new HashMap<LabelType, JCheckBox>();

	public LabelWorkflowFilterPanel(LabelWorkflowFilter filter) {
		super();
		setBorder(new EmptyBorder(5, 5, 5, 5));
		//setSize(new Dimension(200, 300));
		setBackground(Color.WHITE);
		this.filter = filter;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		Font headingFont = new Font("Tahoma", Font.PLAIN, 17);
		JLabel lblWorkflowFeatures = new JLabel("Workflow Features");
		lblWorkflowFeatures.setForeground(new Color(70, 130, 180));
		lblWorkflowFeatures.setFont(headingFont);
		lblWorkflowFeatures.setBorder(new EmptyBorder(5, 0, 5, 0));
		add(lblWorkflowFeatures);
		
		//Activity labels
		Font subheadingFont = new Font("Tahoma", Font.BOLD, 13);
		Border subheadingBorder = new EmptyBorder(5, 0, 2, 0);
		LabellableObjectFilter activityFilter = filter.getRootActivityFilter();
		Collection<LabelGroup> activityRootLabels = activityFilter.getRootLabels();
		for (Iterator<LabelGroup> it = activityRootLabels.iterator(); it.hasNext(); ) {
			LabelGroup lg = it.next();
			JLabel label = new JLabel(lg.getType().getCaption());
			label.setFont(subheadingFont);
			label.setBorder(subheadingBorder);
			add(label);
			
			if (!addFilterCheckBoxes(activityFilter, lg.getType(), 0, acticityFilterCheckboxes)) {
				//Remove label because there were no child check boxes
				remove(label);
			}
		}
		
		JSeparator separator = new JSeparator();
		add(separator);
		
		JLabel lblInputData = new JLabel("Input Data");
		lblInputData.setForeground(new Color(70, 130, 180));
		lblInputData.setFont(headingFont);
		lblInputData.setBorder(new EmptyBorder(5, 0, 5, 0));
		add(lblInputData);
		
		//Input data  labels
		LabellableObjectFilter inputDataFilter = filter.getInputDataFilter();
		Collection<LabelGroup> inputDataRootLabels = inputDataFilter.getRootLabels();
		for (Iterator<LabelGroup> it = inputDataRootLabels.iterator(); it.hasNext(); ) {
			LabelGroup lg = it.next();
			JLabel label = new JLabel(lg.getType().getCaption());
			label.setFont(subheadingFont);
			label.setBorder(subheadingBorder);
			add(label);
			
			if (!addFilterCheckBoxes(inputDataFilter, lg.getType(), 0, inputDataFilterCheckboxes)) {
				//Remove label because there were no child check boxes
				remove(label);
			}
		}
	
		JSeparator separator_1 = new JSeparator();
		add(separator_1);
		
		JLabel lblOutputData = new JLabel("Output Data");
		lblOutputData.setForeground(new Color(70, 130, 180));
		lblOutputData.setFont(headingFont);
		lblOutputData.setBorder(new EmptyBorder(5, 0, 5, 0));
		add(lblOutputData);
		
		//Output data labels
		LabellableObjectFilter outputDataFilter = filter.getOutputDataFilter();
		Collection<LabelGroup> outputDataRootLabels = outputDataFilter.getRootLabels();
		for (Iterator<LabelGroup> it = outputDataRootLabels.iterator(); it.hasNext(); ) {
			LabelGroup lg = it.next();
			JLabel label = new JLabel(lg.getType().getCaption());
			label.setFont(subheadingFont);
			label.setBorder(subheadingBorder);
			add(label);
			
			if (!addFilterCheckBoxes(outputDataFilter, lg.getType(), 0, outputDataFilterCheckboxes)) {
				//Remove label because there were no child check boxes
				remove(label);
			}
		}

		JSeparator separator_2 = new JSeparator();
		add(separator_2);
		
		JLabel lblDataTables = new JLabel("Data Tables");
		lblDataTables.setForeground(new Color(70, 130, 180));
		lblDataTables.setFont(headingFont);
		lblDataTables.setBorder(new EmptyBorder(5, 0, 5, 0));
		add(lblDataTables);
		
		//Data table labels
		LabellableObjectFilter dataTableFilter = filter.getDataTableFilter();
		Collection<LabelGroup> dataTableRootLabels = dataTableFilter.getRootLabels();
		for (Iterator<LabelGroup> it = dataTableRootLabels.iterator(); it.hasNext(); ) {
			LabelGroup lg = it.next();
			JLabel label = new JLabel(lg.getType().getCaption());
			label.setFont(subheadingFont);
			label.setBorder(subheadingBorder);
			add(label);
			
			if (!addFilterCheckBoxes(dataTableFilter, lg.getType(), 0, dataTableFilterCheckboxes)) {
				//Remove label because there were no child check boxes
				remove(label);
			}
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateCounts();
				revalidate();
			}
		});
	}
	
	/**
	 * Recursively adds checkboxes for label types
	 * @param filter
	 * @param labelType
	 * @param indent
	 * @return <code>true</code> if at least one checkbox was added
	 */
	private boolean addFilterCheckBoxes(final LabellableObjectFilter filter, final LabelType labelType, int level,
									Map<LabelType, JCheckBox> saveToMap) {
		boolean ret = false;
		if (level > 0 && filter.isLabelTypeEnabled(labelType)) {
			final JCheckBox cb = new JCheckBox(labelType.getCaption());
			cb.setBackground(Color.WHITE);
			cb.setBorder(new EmptyBorder(3, level * 10, 3, 0));
			cb.setFont(new Font("Tahoma", Font.PLAIN, 12));
			cb.setSelected(filter.isLabelTypeSelected(labelType));
			add(cb);
			saveToMap.put(labelType, cb);
			ret = true;
			
			cb.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							filter.setLabelTypeSelected(labelType, cb.isSelected());
							updateCounts();
						}
					});
				}
			});
		}
		
		//Child types
		if (labelType.getChildren() != null && !labelType.getChildren().isEmpty()) {
			for (Iterator<LabelType> it = labelType.getChildren().iterator(); it.hasNext(); ) {
				
				boolean addedChildCb = addFilterCheckBoxes(filter, it.next(), level+1, saveToMap);
				ret = ret || addedChildCb;
			}
		}
		return ret;
	}
	
	private void updateCounts() {
		updateCounts(acticityFilterCheckboxes, filter.getRootActivityFilter());
		updateCounts(inputDataFilterCheckboxes, filter.getInputDataFilter());
		updateCounts(outputDataFilterCheckboxes, filter.getOutputDataFilter());
		updateCounts(dataTableFilterCheckboxes, filter.getDataTableFilter());

		//getParent().doLayout();
		//getParent().repaint();
	}
	
	private void updateCounts(Map<LabelType, JCheckBox> checkboxes, LabellableObjectFilter labelFilter) {
		for (Iterator<LabelType> it = checkboxes.keySet().iterator(); it.hasNext(); ) {
			LabelType labelType = it.next();
			final JCheckBox cb = checkboxes.get(labelType);
			int count = labelFilter.getObjectCount(labelType);
			if (count > 0)
				cb.setText(labelType.getCaption() + " (" + count + ")");
			else
				cb.setText(labelType.getCaption());
			//cb.invalidate();
			//cb.repaint();
			//cb.invalidate();
		}
	}
}
