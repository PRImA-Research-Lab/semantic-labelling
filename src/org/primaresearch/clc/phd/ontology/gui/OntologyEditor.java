package org.primaresearch.clc.phd.ontology.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.primaresearch.clc.phd.ontology.Ontology;
import org.primaresearch.clc.phd.ontology.gui.model.LabelTypeTreeModel;
import org.primaresearch.clc.phd.ontology.gui.model.LabelTypeTreeNode;
import org.primaresearch.clc.phd.ontology.gui.model.LabelTypesRootTreeNode;
import org.primaresearch.clc.phd.ontology.io.OntologyWriter;
import org.primaresearch.clc.phd.ontology.io.OwlOntologyWriter;
import org.primaresearch.clc.phd.ontology.io.SimpleOntologyWriter;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.ontology.label.LabelType;

/**
 * Graphical editor for label type hierarchies and label slots of activities/data objects.
 * 
 * @author clc
 *
 */
public class OntologyEditor extends JFrame implements DocumentListener {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPanel;
	private JPanel labelTypeDetailsPanel;
	private JTextField typeName;
	private JTextField typeCaption;
	private JTextPane typeDescription;
	private JButton btnAddSlot;
	private JButton btnRemoveSlot;
	private JSpinner spinnerLabelSlots;
	private JSpinner spinnerVersion;
	
	private JTree labelTypeTree;
	private LabelTypeTreeModel labelTypeTreeModel;
	private LabelType selectedLabelType = null;
	private JTree labelSlotTree;
	private DefaultTreeModel labelSlotTreeModel;
	private DefaultMutableTreeNode activityNode;
	private DefaultMutableTreeNode dataObjectNode;
	private DefaultMutableTreeNode userNode;
	private JButton btnSaveAsDefault;
	
	private JRadioButton rdbtnCustom;
	private JRadioButton rdbtnOwl;

	/** Used to disable the change listener when the text is being changed from within the class */
	private volatile boolean labelTypeDetailsChangeListenerActive = true;
	private JTextField typeId;

	/**
	 * Main
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					OntologyEditor frame = new OntologyEditor();
					frame.setVisible(true);
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		});
	}

	/**
	 * Constructor
	 */
	public OntologyEditor() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Ontology Editor");
		setSize(1280, 960);
		
		URL iconURL = getClass().getResource("/org/primaresearch/clc/phd/ontology/gui/res/editor_icon.png");
		ImageIcon icon = new ImageIcon(iconURL);
		setIconImage(icon.getImage());

		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel buttonPanel = new JPanel();
		contentPanel.add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		JButton btnSave = new JButton("Save as...");
		btnSave.setToolTipText("Save to XML");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveOntologyAs();
			}
		});
		
		btnSaveAsDefault = new JButton("Save as default");
		btnSaveAsDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveOntologyAsDefault();
			}
		});
		
		JPanel versioningPanel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) versioningPanel.getLayout();
		flowLayout_1.setHgap(15);
		versioningPanel.setBorder(new EmptyBorder(0, 0, 0, 200));
		buttonPanel.add(versioningPanel);
		
		JLabel lblOntologyVersion = new JLabel("Ontology version");
		versioningPanel.add(lblOntologyVersion);
		
		spinnerVersion = new JSpinner();
		versioningPanel.add(spinnerVersion);
		spinnerVersion.setValue(new Integer(Ontology.getInstance().getVersion()));
		spinnerVersion.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Ontology.getInstance().setVersion((Integer)spinnerVersion.getValue());
			}
		});
				
		JButton btnOntologyMigrationRules = new JButton("Ontology migration rules");
		btnOntologyMigrationRules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						final OntologyMigrationRulesDialog dlg = new OntologyMigrationRulesDialog(OntologyEditor.this);
						dlg.setVisible(true);
					}
				});
			}
		});
		versioningPanel.add(btnOntologyMigrationRules);
		
		JLabel lblXmlFormat = new JLabel("XML format:");
		buttonPanel.add(lblXmlFormat);
		
		rdbtnCustom = new JRadioButton("Custom");
		rdbtnCustom.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				btnSaveAsDefault.setEnabled(rdbtnCustom.isSelected());
			}
		});
		rdbtnCustom.setSelected(true);
		buttonPanel.add(rdbtnCustom);
		
		rdbtnOwl = new JRadioButton("OWL");
		rdbtnOwl.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				btnSaveAsDefault.setEnabled(!rdbtnOwl.isSelected());
			}
		});
		buttonPanel.add(rdbtnOwl);
		btnSaveAsDefault.setToolTipText(Ontology.getInstance().getSourceFile().getAbsolutePath());
		buttonPanel.add(btnSaveAsDefault);
		buttonPanel.add(btnSave);
		
		ButtonGroup xmlFormatButtonGroup = new ButtonGroup();
		xmlFormatButtonGroup.add(rdbtnCustom);
		xmlFormatButtonGroup.add(rdbtnOwl);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		
		JButton btnCsv = new JButton("CSV");
		btnCsv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				typeDescription.setText(createCsvTable());
				typeDescription.setEnabled(true);
			}
		});
		buttonPanel.add(btnCsv);
		buttonPanel.add(btnClose);
				
		JPanel centerPanel = new JPanel();
		contentPanel.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new GridLayout(2, 0, 0, 10));
		
		JPanel labelTypesPanel = new JPanel();
		centerPanel.add(labelTypesPanel);
		labelTypesPanel.setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		labelTypesPanel.add(splitPane, BorderLayout.CENTER);
		splitPane.setResizeWeight(0.3);
		
		labelTypeTree = new JTree();
		labelTypeTree.setBorder(new EmptyBorder(5, 5, 5, 5));
		labelTypeTree.setModel(labelTypeTreeModel = new LabelTypeTreeModel(new LabelTypesRootTreeNode(Ontology.getInstance())));
		labelTypeTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				Object selectedNode = e.getPath().getLastPathComponent();
				if (selectedNode != null && selectedNode instanceof LabelTypeTreeNode) {
					selectedLabelType = ((LabelTypeTreeNode)selectedNode).getLabelType();
				} else {
					selectedLabelType = null;
				}
				refreshDetailsSection();
			}
		});
		
		JScrollPane scrollPaneForTree = new JScrollPane(labelTypeTree);
				
		splitPane.setLeftComponent(scrollPaneForTree);
		
		labelTypeDetailsPanel = new JPanel();
		labelTypeDetailsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		splitPane.setRightComponent(labelTypeDetailsPanel);
		GridBagLayout gbl_labelTypeDetailsPanel = new GridBagLayout();
		gbl_labelTypeDetailsPanel.columnWidths = new int[]{0, 0, 0};
		gbl_labelTypeDetailsPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_labelTypeDetailsPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_labelTypeDetailsPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		labelTypeDetailsPanel.setLayout(gbl_labelTypeDetailsPanel);
		
		JLabel lblName = new JLabel("Name");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.EAST;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		labelTypeDetailsPanel.add(lblName, gbc_lblName);
		
		typeName = new JTextField();
		GridBagConstraints gbc_typeName = new GridBagConstraints();
		gbc_typeName.insets = new Insets(0, 0, 5, 0);
		gbc_typeName.fill = GridBagConstraints.HORIZONTAL;
		gbc_typeName.gridx = 1;
		gbc_typeName.gridy = 0;
		labelTypeDetailsPanel.add(typeName, gbc_typeName);
		typeName.setColumns(10);
		typeName.getDocument().addDocumentListener(this);
		
		JLabel lblCaption = new JLabel("Caption");
		GridBagConstraints gbc_lblCaption = new GridBagConstraints();
		gbc_lblCaption.anchor = GridBagConstraints.EAST;
		gbc_lblCaption.insets = new Insets(0, 0, 5, 5);
		gbc_lblCaption.gridx = 0;
		gbc_lblCaption.gridy = 1;
		labelTypeDetailsPanel.add(lblCaption, gbc_lblCaption);
		
		typeCaption = new JTextField();
		GridBagConstraints gbc_typeCaption = new GridBagConstraints();
		gbc_typeCaption.insets = new Insets(0, 0, 5, 0);
		gbc_typeCaption.fill = GridBagConstraints.HORIZONTAL;
		gbc_typeCaption.gridx = 1;
		gbc_typeCaption.gridy = 1;
		labelTypeDetailsPanel.add(typeCaption, gbc_typeCaption);
		typeCaption.setColumns(10);
		typeCaption.getDocument().addDocumentListener(this);
		
		JLabel lblId = new JLabel("Id");
		GridBagConstraints gbc_lblId = new GridBagConstraints();
		gbc_lblId.anchor = GridBagConstraints.EAST;
		gbc_lblId.insets = new Insets(0, 0, 5, 5);
		gbc_lblId.gridx = 0;
		gbc_lblId.gridy = 2;
		labelTypeDetailsPanel.add(lblId, gbc_lblId);
						
		typeId = new JTextField();
		typeId.setEditable(false);
		GridBagConstraints gbc_typeId = new GridBagConstraints();
		gbc_typeId.insets = new Insets(0, 0, 5, 0);
		gbc_typeId.fill = GridBagConstraints.HORIZONTAL;
		gbc_typeId.gridx = 1;
		gbc_typeId.gridy = 2;
		labelTypeDetailsPanel.add(typeId, gbc_typeId);
		typeId.setColumns(10);
		
		JLabel lblDescription = new JLabel("Description");
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
		gbc_lblDescription.anchor = GridBagConstraints.WEST;
		gbc_lblDescription.insets = new Insets(0, 0, 5, 0);
		gbc_lblDescription.gridx = 1;
		gbc_lblDescription.gridy = 4;
		labelTypeDetailsPanel.add(lblDescription, gbc_lblDescription);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 5;
		labelTypeDetailsPanel.add(panel, gbc_panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		typeDescription = new JTextPane();
		panel.add(typeDescription);
		
		JToolBar labelTypesToolbar = new JToolBar();
		labelTypesPanel.add(labelTypesToolbar, BorderLayout.NORTH);
		
		JButton btnNewType = new JButton("New Type");
		btnNewType.setToolTipText("Create new label type");
		btnNewType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNewLabelType();
			}
		});
		
		JLabel lblLabelTypes = new JLabel("Label Types   ");
		lblLabelTypes.setFont(new Font("Tahoma", Font.PLAIN, 15));
		labelTypesToolbar.add(lblLabelTypes);
		labelTypesToolbar.add(btnNewType);
		
		JButton btnRemoveType = new JButton("Remove Type");
		btnRemoveType.setToolTipText("Remove selected label type");
		btnRemoveType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSelectedLabelType();
			}
		});
		labelTypesToolbar.add(btnRemoveType);
		
		JButton btnExpandAll = new JButton("Expand All");
		btnExpandAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < labelTypeTree.getRowCount(); i++) {
			         labelTypeTree.expandRow(i);
				}
			}
		});
		labelTypesToolbar.add(btnExpandAll);
		
		JPanel labelSlotsPanel = new JPanel();
		centerPanel.add(labelSlotsPanel);
		labelSlotsPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel labelSlotTreePanel = new JPanel();
		labelSlotTreePanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		labelSlotsPanel.add(labelSlotTreePanel, BorderLayout.CENTER);
		labelSlotTreePanel.setLayout(new BorderLayout(0, 0));
		
		labelSlotTree = new JTree();
		labelSlotTree.setBorder(new EmptyBorder(5, 5, 5, 5));
		labelSlotTreePanel.add(labelSlotTree, BorderLayout.CENTER);
		labelSlotTree.setModel(initLabelSlotTree());
		labelSlotTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
				if (selectedNode != null) {
					boolean isSlotGroupNode = (selectedNode.getUserObject() instanceof LabelGroup);
					boolean isLabellableObjectNode = !isSlotGroupNode && selectedNode != labelSlotTree.getModel().getRoot();
					
					btnAddSlot.setEnabled(isLabellableObjectNode);
					btnRemoveSlot.setEnabled(isSlotGroupNode);
					spinnerLabelSlots.setEnabled(isSlotGroupNode);
					
					if (isSlotGroupNode) {
						LabelGroup grp = (LabelGroup)selectedNode.getUserObject();
						spinnerLabelSlots.setValue(grp.getMaxLabels());
					}
					
				}
			}
		});
		
		JToolBar labelSlotsToolbar = new JToolBar();
		labelSlotsPanel.add(labelSlotsToolbar, BorderLayout.NORTH);
		
		JLabel lblLabelSlots = new JLabel("Label Slots   ");
		lblLabelSlots.setFont(new Font("Tahoma", Font.PLAIN, 15));
		labelSlotsToolbar.add(lblLabelSlots);
		
		btnAddSlot = new JButton("Add Slot Group");
		btnAddSlot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)labelSlotTree.getSelectionPath().getLastPathComponent();
				addSlotGroup(selectedNode);
			}
		});
		btnAddSlot.setEnabled(false);
		labelSlotsToolbar.add(btnAddSlot);
		
		btnRemoveSlot = new JButton("Remove Slot Group");
		btnRemoveSlot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)labelSlotTree.getSelectionPath().getLastPathComponent();
				removeSlotGroup(selectedNode);
			}
		});
		btnRemoveSlot.setEnabled(false);
		labelSlotsToolbar.add(btnRemoveSlot);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EmptyBorder(0, 10, 0, 0));
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		labelSlotsToolbar.add(panel_1);
		
		JLabel lblNumerOfSlots = new JLabel("Number of slots in group: ");
		panel_1.add(lblNumerOfSlots);
		
		spinnerLabelSlots = new JSpinner();
		spinnerLabelSlots.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)labelSlotTree.getSelectionPath().getLastPathComponent();
				updateSlotCount(selectedNode);
			}
		});
		spinnerLabelSlots.setEnabled(false);
		panel_1.add(spinnerLabelSlots);
		spinnerLabelSlots.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		typeDescription.getDocument().addDocumentListener(this);
		
		initDataBindings();
		refreshDetailsSection();
	}
	
	/**
	 * Refreshes the text fields of the label type details using the currently selected label type.
	 */
	private void refreshDetailsSection() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					labelTypeDetailsChangeListenerActive = false;
					if (selectedLabelType != null) {
						typeName.setEnabled(true);
						typeName.setText(selectedLabelType.getName());
						typeCaption.setEnabled(true);
						typeCaption.setText(selectedLabelType.getCaption());
						typeId.setText(selectedLabelType.getId());
						typeDescription.setEnabled(true);
						typeDescription.setText(selectedLabelType.getDescription());
					} else {
						typeName.setEnabled(false);
						typeName.setText("");
						typeCaption.setEnabled(false);
						typeCaption.setText("");
						typeId.setText("");
						typeDescription.setEnabled(false);
						typeDescription.setText("");
					}
				} catch (Exception exc) {
					exc.printStackTrace();
				}
				labelTypeDetailsChangeListenerActive = true;
			}
		});

	}
	
	/**
	 * Updates the currently selected label type using the content of the text fields of the details section.
	 */
	private void updateSelectedLabelType() {
		if (labelTypeDetailsChangeListenerActive) {
			if (selectedLabelType != null) {
				if (!selectedLabelType.getName().equals(typeName.getText())) {
					String oldId = selectedLabelType.getId();
					selectedLabelType.setName(typeName.getText());
					Ontology.getInstance().onLabelTypeRenamed(selectedLabelType, oldId);
					typeId.setText(selectedLabelType.getId());
				}
				selectedLabelType.setCaption(typeCaption.getText());
				selectedLabelType.setDescription(typeDescription.getText());
			}		
			Object selectedNode = labelTypeTree.getLastSelectedPathComponent();
			if (selectedNode != null)
				labelTypeTreeModel.nodeChanged((DefaultMutableTreeNode)selectedNode);
		}
	}
	
	/**
	 * Closes and destroys the window.
	 */
	private void close() {
		this.dispose();
	}

	/**
	 * Returns the currently selected label type.
	 * @return A label type or null.
	 */
	public LabelType getSelectedLabelType() {
		return selectedLabelType;
	}

	public void setSelectedLabelType(LabelType selectedLabelType) {
		this.selectedLabelType = selectedLabelType;
	}
	
	protected void initDataBindings() {
	}
	
	//DocumentChangeListener
	@Override
	public void removeUpdate(DocumentEvent e) {
		updateSelectedLabelType();
	}
	
	//DocumentChangeListener
	@Override
	public void insertUpdate(DocumentEvent e) {
		updateSelectedLabelType();
	}
	
	//DocumentChangeListener
	@Override
	public void changedUpdate(DocumentEvent e) {
		updateSelectedLabelType();
	}
	
	/**
	 * Creates a new label type and adds it as child to the currently selected type, or,
	 * if no type is selected, adds it as a root type to the ontology.
	 */
	private void createNewLabelType() {
		LabelTypeTreeNode newNode = null;
		if (selectedLabelType == null) {
			//New root type
			LabelType newType = new LabelType("[new type]", "[New Type]", null);
			Ontology.getInstance().addRootType(newType);
			newNode = new LabelTypeTreeNode(newType);
			((LabelTypesRootTreeNode)labelTypeTreeModel.getRoot()).add(newNode);
			labelTypeTreeModel.nodeStructureChanged((DefaultMutableTreeNode)labelTypeTreeModel.getRoot());
		}
		else {
			//New child type
			Object selectedNode = labelTypeTree.getLastSelectedPathComponent();
			if (selectedNode != null && selectedNode instanceof LabelTypeTreeNode) {
				selectedLabelType = ((LabelTypeTreeNode)selectedNode).getLabelType();
			
				LabelType newType = new LabelType("[new type]", "[New Type]", selectedLabelType);
				selectedLabelType.addChildType(newType);
				newNode = new LabelTypeTreeNode(newType);
				((LabelTypeTreeNode)selectedNode).add(newNode);
				labelTypeTreeModel.nodeStructureChanged((DefaultMutableTreeNode)selectedNode);
				labelTypeTree.expandPath(labelTypeTree.getSelectionPath());
			}
		}
		
		if (newNode != null) {
			//Select the new item
	        TreeNode[] nodes = labelTypeTreeModel.getPathToRoot(newNode);    
	        //TreePath path = new TreePath(nodes);    
	        //System.out.println(path.toString());    // Able to get the exact node here    
	        labelTypeTree.setSelectionPath(new TreePath(nodes));    
	        
	        //Focus name field
	        typeName.requestFocus();
		}
	}
	
	/**
	 * Removes the selected label type from the ontology and from the tree control.
	 */
	private void removeSelectedLabelType() {
		if (selectedLabelType != null) {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)labelTypeTree.getLastSelectedPathComponent();
			if (selectedLabelType.getParent() == null) {
				//Root label type
				Ontology.getInstance().removeTypeAndSubtypes(selectedLabelType, true);
				((DefaultMutableTreeNode)labelTypeTreeModel.getRoot()).remove(selectedNode);
				labelTypeTreeModel.nodeStructureChanged((DefaultMutableTreeNode)labelTypeTreeModel.getRoot());
			}
			else { //Child label type
				Ontology.getInstance().removeTypeAndSubtypes(selectedLabelType, true);
				TreeNode parent = selectedNode.getParent();
				((DefaultMutableTreeNode)parent).remove(selectedNode);
				labelTypeTreeModel.nodeStructureChanged((DefaultMutableTreeNode)parent);
			}
			selectedLabelType = null;
		}
	}
	
	/**
	 * Saves the ontology (label type hierarchies) at the default location.
	 */
	private void saveOntologyAsDefault() {
		//File f = new File(getClass().getResource(XmlLabelTypeHierarchyInitialiser.defaultXmlResourcePath).getFile());
		File f = Ontology.getInstance().getSourceFile();
		saveOntologyAs(f);
	}

	/**
	 * Saves the ontology (label type hierarchies) to a selected XML file.
	 */
	private void saveOntologyAs() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final JFileChooser fc = new JFileChooser();
				fc.setApproveButtonText("Save");
				fc.setApproveButtonToolTipText("Save to selected file");
				fc.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));
		         
		        if (fc.showOpenDialog(OntologyEditor.this) == JFileChooser.APPROVE_OPTION) {
		        	saveOntologyAs(fc.getSelectedFile());
		        }
			}
		});
    }
	
	/**
	 * Saves the ontology (label type hierarchies) to the specified XML file.
	 */
	private void saveOntologyAs(final File f) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
		        try {
		        	applyLabelSlots();
		        	
		        	OntologyWriter writer;
		        	if (rdbtnCustom.isSelected())
		        	   	writer = new SimpleOntologyWriter();
		        	else
		        		writer = new OwlOntologyWriter();
		        	
		        	writer.writeToFile(f, Ontology.getInstance());
		        	JOptionPane.showMessageDialog(null, "File has been saved.\n"+f.getAbsolutePath());
		        } catch (Exception exc) {
		        	exc.printStackTrace();
		        	JOptionPane.showMessageDialog(null, "Error on saving.");
		        }
			}
		});
	}
	
	/**
	 * Builds up the tree with allowed label slots for activities and data objects.
	 * @return Tree model
	 */
	private TreeModel initLabelSlotTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Labellable objects");
		
		//Activity labels
		activityNode = new DefaultMutableTreeNode("Activity");
		root.add(activityNode);
		
		List<LabelGroup> slotGroups = Ontology.getInstance().getActivityLabelSlots();
		for (Iterator<LabelGroup> it = slotGroups.iterator(); it.hasNext(); ) {
			activityNode.add(new DefaultMutableTreeNode(it.next()));
		}
		
		//Data object labels
		dataObjectNode = new DefaultMutableTreeNode("Data Object");
		root.add(dataObjectNode);
		
		slotGroups = Ontology.getInstance().getDataObjectLabelSlots();
		for (Iterator<LabelGroup> it = slotGroups.iterator(); it.hasNext(); ) {
			dataObjectNode.add(new DefaultMutableTreeNode(it.next()));
		}
		
		//User labels
		userNode = new DefaultMutableTreeNode("Users");
		root.add(userNode);
		
		slotGroups = Ontology.getInstance().getUserLabelSlots();
		for (Iterator<LabelGroup> it = slotGroups.iterator(); it.hasNext(); ) {
			userNode.add(new DefaultMutableTreeNode(it.next()));
		}

		return labelSlotTreeModel = new DefaultTreeModel(root);
	}
	
	/**
	 * Adds a new slot group (group for one or more slots for a certain label type)
	 */
	private void addSlotGroup(DefaultMutableTreeNode labellableObjectNode) {
		//Show selection dialogue
		Object[] possibilities = Ontology.getInstance().getRootTypes().toArray();
		LabelType t = (LabelType)JOptionPane.showInputDialog(
		                    this,
		                    "Select a label type",
		                    "Root label types",
		                    JOptionPane.PLAIN_MESSAGE,
		                    null,
		                    possibilities,
		                    null);
		
		if (t != null) {
			//Add
			LabelGroup grp = new LabelGroup(Ontology.getInstance().getRootType(t.getId()), 1);
			labelSlotTreeModel.insertNodeInto(new DefaultMutableTreeNode(grp), labellableObjectNode, labellableObjectNode.getChildCount());
			labelSlotTree.expandRow(labelSlotTree.getRowForPath(labelSlotTree.getSelectionPath()));
		}
	}
	
	/**
	 * Removes a new slot group (group for one or more slots for a certain label type)
	 */
	private void removeSlotGroup(DefaultMutableTreeNode slotGroupNode) {
		//DefaultMutableTreeNode parent = (DefaultMutableTreeNode)slotGroupNode.getParent();
		//LabelGroup grp = (LabelGroup)slotGroupNode.getUserObject();
		labelSlotTreeModel.removeNodeFromParent(slotGroupNode);
		//labelSlotTree.setSelectionPath(labelSlotTreeModel.getPathToRoot(parent));
	}
	
	/**
	 * Copies the slot count from the spinner control to the label group object
	 * and refreshes the tree control.
	 */
	private void updateSlotCount(DefaultMutableTreeNode slotGroupNode) {
		LabelGroup grp = (LabelGroup)slotGroupNode.getUserObject();
		grp.setMaxLabels((Integer)spinnerLabelSlots.getValue());
		labelSlotTreeModel.nodeChanged(slotGroupNode);
	}
	
	/**
	 * Copies the label slots from the slot tree to the ontology
	 */
	private void applyLabelSlots() {
		//Activity
		List<LabelGroup> slots = Ontology.getInstance().getActivityLabelSlots();
		slots.clear();
		for (int i=0; i<activityNode.getChildCount(); i++) {
			DefaultMutableTreeNode n = (DefaultMutableTreeNode)activityNode.getChildAt(i);
			LabelGroup grp = (LabelGroup)n.getUserObject();
			slots.add(grp);
		}
		//Data object
		slots = Ontology.getInstance().getDataObjectLabelSlots();
		slots.clear();
		for (int i=0; i<dataObjectNode.getChildCount(); i++) {
			DefaultMutableTreeNode n = (DefaultMutableTreeNode)dataObjectNode.getChildAt(i);
			LabelGroup grp = (LabelGroup)n.getUserObject();
			slots.add(grp);
		}
		//Users
		slots = Ontology.getInstance().getUserLabelSlots();
		slots.clear();
		for (int i=0; i<userNode.getChildCount(); i++) {
			DefaultMutableTreeNode n = (DefaultMutableTreeNode)userNode.getChildAt(i);
			LabelGroup grp = (LabelGroup)n.getUserObject();
			slots.add(grp);
		}
	}
	
	/**
	 * Creates a CSV table containing all label types
	 * @return
	 */
	private String createCsvTable() {
		StringBuilder table = new StringBuilder();
		
		//Calculate number of columns
		Collection<LabelType> rootTypes = Ontology.getInstance().getRootTypes();
		int max = 0;
		for (Iterator<LabelType> it = rootTypes.iterator(); it.hasNext(); ) {
			LabelType labelType = it.next();
			int depth = calculateHierarchyDepth(labelType, 1);
			if (depth > max)
				max = depth;
		}
		int columns = max + 1;
		
		//Activity label groups
		table.append(createCsvRow("Activity", 0, columns));
		List<LabelGroup> activityLabelGroups = Ontology.getInstance().getActivityLabelSlots();
		for (Iterator<LabelGroup> it = activityLabelGroups.iterator(); it.hasNext(); ) {
			LabelGroup labelGroup = it.next();
			
			addLabelTypeToCsv(Ontology.getInstance().getLabelType(labelGroup.getType().getId()), 1, columns, table);
		}
		
		//Data object label groups
		table.append(createCsvRow("Data Object", 0, columns));
		List<LabelGroup> dataLabelGroups = Ontology.getInstance().getDataObjectLabelSlots();
		for (Iterator<LabelGroup> it = dataLabelGroups.iterator(); it.hasNext(); ) {
			LabelGroup labelGroup = it.next();
			
			addLabelTypeToCsv(Ontology.getInstance().getLabelType(labelGroup.getType().getId()), 1, columns, table);
		}
		
		return table.toString();
	}
	
	/**
	 * Adds a CSV row for the given label type and all of its children
	 * @param labelType
	 * @param currentDepth
	 * @param columns
	 * @param table
	 */
	private void addLabelTypeToCsv(LabelType labelType, int currentDepth, int columns, StringBuilder table) {
		//Add row
		table.append(createCsvRow(labelType.getCaption(), currentDepth, columns));
		
		if (labelType.getChildren() == null)
			return;
		
		//Handle children
		for (Iterator<LabelType> it = labelType.getChildren().iterator(); it.hasNext(); ) {
			LabelType childType = it.next();
			
			addLabelTypeToCsv(childType, currentDepth + 1, columns, table);
		}
	}
	
	/**
	 * Creates a CSV row, filling one cell with the given content and leaving the rest blank
	 * @param cellContent
	 * @param columnIndex
	 * @param columns
	 * @return
	 */
	private String createCsvRow(String cellContent, int columnIndex, int columns) {
		StringBuilder row = new StringBuilder();
		
		//Commas before
		for (int i=0; i<columnIndex; i++)
			row.append(",");
		
		//Cell content
		row.append("\"");
		row.append(cellContent);
		row.append("\"");
		
		//Commas after
		for (int i=columnIndex; i<columns; i++)
			row.append(",");
		
		//Line break
		row.append("\n");
		
		return row.toString();
	}
	
	/**
	 * Calculates the maximum depth of the tree whose root is the given label type
	 * @param labelType
	 * @param currentDepth
	 * @return
	 */
	private int calculateHierarchyDepth(LabelType labelType, int currentDepth) {
		if (labelType.getChildren() == null || labelType.getChildren().isEmpty())
			return currentDepth;
		
		int max = 0;
		for (Iterator<LabelType> it = labelType.getChildren().iterator(); it.hasNext(); ) {
			LabelType childType = it.next();
			int depth = calculateHierarchyDepth(childType, 1);
			if (depth > max)
				max = depth;
		}
		
		return currentDepth + max;
	}
}
