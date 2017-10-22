package org.primaresearch.clc.phd.ontology.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.primaresearch.clc.phd.ontology.Ontology;
import org.primaresearch.clc.phd.ontology.OtherOntology;
import org.primaresearch.clc.phd.ontology.gui.model.LabelTypeTreeModel;
import org.primaresearch.clc.phd.ontology.gui.model.LabelTypeTreeNode;
import org.primaresearch.clc.phd.ontology.gui.model.LabelTypesRootTreeNode;
import org.primaresearch.clc.phd.ontology.io.XmlLabelTypeHierarchyInitialiser;
import org.primaresearch.clc.phd.ontology.label.LabelType;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

/**
 * Dialog that allows the definition of rules for migration from an older ontology to the current one
 * 
 * @author clc
 *
 */
public class OntologyMigrationRulesDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTextField currentOntologyVersionTextField;
	private Ontology currentOntology;
	private Ontology previousOntology = null;
	
	private JTree currentLabelTypes;
	private LabelTypeTreeModel currentLabelTypeTreeModel;
	private JTree oldLabelTypes;
	private LabelTypeTreeModel oldLabelTypeTreeModel;
	private JSpinner oldOntologyVersionSpinner;
	private JList<String> migrationSourceLabelTypeList;
	private JList<String> migrationTargetLabelTypesList;

	/**
	 * Constructor
	 * @param parent
	 */
	public OntologyMigrationRulesDialog(JFrame parent) {
		super(parent, true);
		setTitle("Ontology Migration Rules");
		setSize(800,600);
		
		currentOntology = Ontology.getInstance();
		
		getContentPane().setLayout(new GridLayout(2, 2, 10, 10));
		
		JPanel oldLabelTypesPanel = new JPanel();
		getContentPane().add(oldLabelTypesPanel);
		oldLabelTypesPanel.setLayout(new BorderLayout(0, 5));
		
		JPanel oldLabelTypesTopPanel = new JPanel();
		oldLabelTypesPanel.add(oldLabelTypesTopPanel, BorderLayout.NORTH);
		
		JLabel lblPreviousOntology = new JLabel("Previous Ontology");
		oldLabelTypesTopPanel.add(lblPreviousOntology);
		
		oldOntologyVersionSpinner = new JSpinner();
		oldOntologyVersionSpinner.setEnabled(false);
		oldLabelTypesTopPanel.add(oldOntologyVersionSpinner);
		
		JButton openOldOntologyButton = new JButton("Open...");
		openOldOntologyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						openOldOntology();
					}
				});
			}
		});
		oldLabelTypesTopPanel.add(openOldOntologyButton);
		
		oldLabelTypes = new JTree();
		JScrollPane scrollPaneForLeftTree = new JScrollPane(oldLabelTypes);
		oldLabelTypesPanel.add(scrollPaneForLeftTree, BorderLayout.CENTER);
		
		JPanel currentLabelTypesPanel = new JPanel();
		getContentPane().add(currentLabelTypesPanel);
		currentLabelTypesPanel.setLayout(new BorderLayout(0, 5));
		
		JPanel currentLabelTypesTopPanel = new JPanel();
		currentLabelTypesPanel.add(currentLabelTypesTopPanel, BorderLayout.NORTH);
		
		JLabel lblCurrentOntology = new JLabel("Current Ontology");
		currentLabelTypesTopPanel.add(lblCurrentOntology);
		
		currentOntologyVersionTextField = new JTextField();
		currentOntologyVersionTextField.setEditable(false);
		currentOntologyVersionTextField.setText(""+currentOntology.getVersion());
		currentLabelTypesTopPanel.add(currentOntologyVersionTextField);
		currentOntologyVersionTextField.setColumns(3);
		
		currentLabelTypes = new JTree();
		currentLabelTypes.setModel(currentLabelTypeTreeModel = new LabelTypeTreeModel(new LabelTypesRootTreeNode(currentOntology)));
		JScrollPane scrollPaneForRightTree = new JScrollPane(currentLabelTypes);
		currentLabelTypesPanel.add(scrollPaneForRightTree, BorderLayout.CENTER);
		
		JPanel migrationSourceLabelPanel = new JPanel();
		getContentPane().add(migrationSourceLabelPanel);
		migrationSourceLabelPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel migrationSourceTopPanel = new JPanel();
		migrationSourceLabelPanel.add(migrationSourceTopPanel, BorderLayout.NORTH);
		
		JButton addMigrationSourceLabelTypeButton = new JButton("+");
		addMigrationSourceLabelTypeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addMigrationRuleSource();
			}
		});
		migrationSourceTopPanel.add(addMigrationSourceLabelTypeButton);
		
		JButton removeMigrationSourceLabelTypeButton = new JButton("-");
		removeMigrationSourceLabelTypeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				removeMigrationRuleSource();
			}
		});
		migrationSourceTopPanel.add(removeMigrationSourceLabelTypeButton);
		
		JPanel migrationSourceBottomPanel = new JPanel();
		migrationSourceLabelPanel.add(migrationSourceBottomPanel, BorderLayout.SOUTH);
		
		JLabel lblSourceLabelTypes = new JLabel("Source label types of migration rules");
		migrationSourceBottomPanel.add(lblSourceLabelTypes);
		
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		migrationSourceLabelTypeList = new JList<String>(listModel);
		migrationSourceLabelTypeList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				updateTargetLabelTypeListBox();
			}
		});
		JScrollPane scrollPaneForLeftListbox = new JScrollPane(migrationSourceLabelTypeList);
		migrationSourceLabelPanel.add(scrollPaneForLeftListbox, BorderLayout.CENTER);
		
		JPanel migrationTargetLabelsPanel = new JPanel();
		getContentPane().add(migrationTargetLabelsPanel);
		migrationTargetLabelsPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel migrationTargetTopPanel = new JPanel();
		migrationTargetLabelsPanel.add(migrationTargetTopPanel, BorderLayout.NORTH);
		
		JButton addMigrationTargetLabelTypeButton = new JButton("+");
		addMigrationTargetLabelTypeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addMigrationRuleTarget();
			}
		});
		migrationTargetTopPanel.add(addMigrationTargetLabelTypeButton);
		
		JButton removeMigrationTargetLabelTypeButton = new JButton("-");
		removeMigrationTargetLabelTypeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				removeMigrationRuleTarget();
			}
		});
		migrationTargetTopPanel.add(removeMigrationTargetLabelTypeButton);
		
		JPanel migrationTargetBottomPanel = new JPanel();
		migrationTargetLabelsPanel.add(migrationTargetBottomPanel, BorderLayout.SOUTH);
		
		JLabel lblTargetLabelTypes = new JLabel("Target label types of selected migration rule");
		migrationTargetBottomPanel.add(lblTargetLabelTypes);
		
		listModel = new DefaultListModel<String>();
		migrationTargetLabelTypesList = new JList<String>(listModel);
		JScrollPane scrollPaneForRightListbox = new JScrollPane(migrationTargetLabelTypesList);
		migrationTargetLabelsPanel.add(scrollPaneForRightListbox, BorderLayout.CENTER);
		
	}
	
	private void openOldOntology() {
		final JFileChooser fc = new JFileChooser();
		fc.setApproveButtonText("Open");
		fc.setApproveButtonToolTipText("Open selected ontology");
		fc.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));
		
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        	XmlLabelTypeHierarchyInitialiser reader = new XmlLabelTypeHierarchyInitialiser();
        	reader.setXmlFilePath(fc.getSelectedFile().getAbsolutePath());
        	
        	previousOntology = new OtherOntology(fc.getSelectedFile().getAbsolutePath());
        	reader.init(previousOntology);
        	
        	oldLabelTypes.setModel(oldLabelTypeTreeModel = new LabelTypeTreeModel(new LabelTypesRootTreeNode(previousOntology)));
        	
        	oldOntologyVersionSpinner.setValue(new Integer(previousOntology.getVersion()));
        	
        	updateSourceLabelTypeListBox();
        }
	}
	
	/**
	 * Adds a source label type to the left list box
	 */
	private void addMigrationRuleSource() {
		//Get selection from left tree
		LabelType sourceLabelType = ((LabelTypeTreeNode)oldLabelTypes.getSelectionPath().getLastPathComponent()).getLabelType();
		
		if (sourceLabelType == null)
			return; //Nothing selected
		
		Map<String,Set<String>> rules = currentOntology.getMigrationRules(new Integer(previousOntology.getVersion()), true);
		
		if (rules.containsKey(sourceLabelType.getId()))
			return; //Rule already exists
		
		//Add to ontology
		rules.put(sourceLabelType.getId(), new HashSet<String>());
		
		//Add to left list box
		((DefaultListModel<String>)migrationSourceLabelTypeList.getModel()).addElement(sourceLabelType.getId());
	}
	
	/**
	 * Removes a source label type from the left list box
	 */
	private void removeMigrationRuleSource() {
		//Get list box selection
		String sourceLabelTypeId = migrationSourceLabelTypeList.getSelectedValue();
		
		if (sourceLabelTypeId == null)
			return; //Nothing selected
		
		Map<String,Set<String>> rules = currentOntology.getMigrationRules(new Integer(previousOntology.getVersion()), true);
		
		//Remove from to ontology
		if (rules.containsKey(sourceLabelTypeId))
			rules.remove(sourceLabelTypeId);
		
		//Remove from left list box
		((DefaultListModel<String>)migrationSourceLabelTypeList.getModel()).removeElement(sourceLabelTypeId);
		
		updateTargetLabelTypeListBox();
	}
	
	/**
	 * Adds a target label type to the right list box
	 */
	private void addMigrationRuleTarget() {
		//Get selection from right tree
		LabelType targetLabelType = ((LabelTypeTreeNode)currentLabelTypes.getSelectionPath().getLastPathComponent()).getLabelType();
		
		if (targetLabelType == null)
			return; //Nothing selected
		
		//Get migration rule
		String sourceLabelTypeId = migrationSourceLabelTypeList.getSelectedValue();
		
		if (sourceLabelTypeId == null)
			return; //Nothing selected

		Map<String,Set<String>> rules = currentOntology.getMigrationRules(new Integer(previousOntology.getVersion()), true);
		
		Set<String> targetIds = rules.get(sourceLabelTypeId);
		if (targetIds == null)
			return; //should not happen
		
		if (targetIds.contains(targetLabelType.getId()))
			return; //Rule already exists
		
		//Add to ontology
		targetIds.add(targetLabelType.getId());
		
		//Add to right list box
		((DefaultListModel<String>)migrationTargetLabelTypesList.getModel()).addElement(targetLabelType.getId());
	}
	
	/**
	 * Removes a target label type from the right list box
	 */
	private void removeMigrationRuleTarget() {
		//Get list box selection
		String targetLabelTypeId = migrationTargetLabelTypesList.getSelectedValue();
		
		if (targetLabelTypeId == null)
			return; //Nothing selected
		
		//Get migration rule
		String sourceLabelTypeId = migrationSourceLabelTypeList.getSelectedValue();
		
		if (sourceLabelTypeId == null)
			return; //Nothing selected

		Map<String,Set<String>> rules = currentOntology.getMigrationRules(new Integer(previousOntology.getVersion()), true);
		
		Set<String> targetIds = rules.get(sourceLabelTypeId);
		if (targetIds == null)
			return; //should not happen
		
		//Remove from ontology
		targetIds.remove(targetLabelTypeId);
		
		//Remove from right list box
		((DefaultListModel<String>)migrationTargetLabelTypesList.getModel()).removeElement(targetLabelTypeId);
	}
	
	/**
	 * Refreshes the content of the right list box according to the current source label type selection
	 */
	private void updateTargetLabelTypeListBox() {
		//Remove all from right list box
		((DefaultListModel<String>)migrationTargetLabelTypesList.getModel()).removeAllElements();
		
		//Get migration rule
		String sourceLabelTypeId = migrationSourceLabelTypeList.getSelectedValue();
		
		if (sourceLabelTypeId == null)
			return; //Nothing selected

		Map<String,Set<String>> rules = currentOntology.getMigrationRules(new Integer(previousOntology.getVersion()), true);
		
		Set<String> targetIds = rules.get(sourceLabelTypeId);
		for (Iterator<String> it = targetIds.iterator(); it.hasNext(); )
			((DefaultListModel<String>)migrationTargetLabelTypesList.getModel()).addElement(it.next());
	}
	
	/**
	 * Refreshes the content of the left list box according to the version of the loaded old ontology
	 */
	private void updateSourceLabelTypeListBox() {
		//Remove all from right list box
		((DefaultListModel<String>)migrationTargetLabelTypesList.getModel()).removeAllElements();

		//Remove all from left list box
		((DefaultListModel<String>)migrationSourceLabelTypeList.getModel()).removeAllElements();
		
		//Get migration rules
		if (previousOntology == null)
			return;
		
		Map<String,Set<String>> rules = currentOntology.getMigrationRules(new Integer(previousOntology.getVersion()), true);

		if (rules == null || rules.isEmpty())
			return;
		
		//Add source label types
		for (Iterator<String> it = rules.keySet().iterator(); it.hasNext(); ) {
			String sourceLabelTypeId = it.next();
			
			((DefaultListModel<String>)migrationSourceLabelTypeList.getModel()).addElement(sourceLabelTypeId);
		}
	}
}
