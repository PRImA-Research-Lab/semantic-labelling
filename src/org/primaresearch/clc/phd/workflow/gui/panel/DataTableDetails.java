package org.primaresearch.clc.phd.workflow.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.data.DataTable;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.DataTableColumn;
import org.primaresearch.clc.phd.workflow.gui.dialog.TableContentDialog;
import org.primaresearch.clc.phd.workflow.gui.model.DataTableTreeNode;
import org.primaresearch.clc.phd.workflow.gui.model.WorkflowTreeModel;
import org.primaresearch.clc.phd.workflow.gui.panel.DataPortPanel.DataObjectPanelListener;

/**
 * Panel with input fields for basic properties of a data table.
 * 
 * @author clc
 *
 */
public class DataTableDetails extends DetailsPanel implements DataObjectPanelListener, DocumentListener  {
	
	private DataTable dataTable;
	private Workflow workflow;
	private DetailsPanel specialisedControls;
	private volatile boolean dataTableDetailsChangeListenerActive = true;
	private WorkflowTreeModel workflowTreeModel;
	private TreeNode selectedTreeNode;
	//private DataObjectFactory dataObjectFactory;
	private IdGenerator idRegister;
	private static final long serialVersionUID = 1L;
	private JTextField dataTableId;
	private JTextField dataTableCaption;
	private JTextArea dataTableDescription;
	private JPanel columnPortsPanelContainer;

	public DataTableDetails(Workflow workflow, DetailsPanel specialisedControls, final IdGenerator idRegister) {
		this.specialisedControls = specialisedControls;
		//this.dataObjectFactory = new DataObjectFactory(idRegister);
		this.idRegister = idRegister;
		this.workflow = workflow;
		setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblDetails = new JLabel("Data Table Properties");
		lblDetails.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_lblDetails = new GridBagConstraints();
		gbc_lblDetails.anchor = GridBagConstraints.WEST;
		gbc_lblDetails.gridwidth = GridBagConstraints.REMAINDER;
		gbc_lblDetails.insets = new Insets(0, 0, 5, 0);
		gbc_lblDetails.gridx = 0;
		gbc_lblDetails.gridy = 0;
		add(lblDetails, gbc_lblDetails);

		JLabel lblDescription = new JLabel("Description");
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
		gbc_lblDescription.insets = new Insets(0, 0, 5, 0);
		gbc_lblDescription.gridx = 2;
		gbc_lblDescription.gridy = 1;
		add(lblDescription, gbc_lblDescription);
		
		dataTableDescription = new JTextArea();
		dataTableDescription.setLineWrap(true);
		dataTableDescription.setWrapStyleWord(true);
		GridBagConstraints gbc_activityDescription = new GridBagConstraints();
		gbc_activityDescription.gridheight = 2;
		gbc_activityDescription.fill = GridBagConstraints.BOTH;
		gbc_activityDescription.insets = new Insets(0, 0, 5, 0);
		gbc_activityDescription.gridx = 2;
		gbc_activityDescription.gridy = 2;
		dataTableDescription.setBorder(new LineBorder(Color.LIGHT_GRAY));
		dataTableDescription.getDocument().addDocumentListener(this);
		
		JLabel lblId = new JLabel("Id");
		GridBagConstraints gbc_lblId = new GridBagConstraints();
		gbc_lblId.insets = new Insets(0, 0, 5, 5);
		gbc_lblId.anchor = GridBagConstraints.EAST;
		gbc_lblId.gridx = 0;
		gbc_lblId.gridy = 2;
		add(lblId, gbc_lblId);
		
		dataTableId = new JTextField();
		GridBagConstraints gbc_dataTableId = new GridBagConstraints();
		gbc_dataTableId.insets = new Insets(0, 0, 5, 5);
		gbc_dataTableId.fill = GridBagConstraints.HORIZONTAL;
		gbc_dataTableId.gridx = 1;
		gbc_dataTableId.gridy = 2;
		add(dataTableId, gbc_dataTableId);
		dataTableId.setColumns(10);
		dataTableId.getDocument().addDocumentListener(this);
		
		JScrollPane descrScrollPane = new JScrollPane(dataTableDescription, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		descrScrollPane.setPreferredSize(new Dimension(100, 50));
		add(descrScrollPane, gbc_activityDescription);
		
		JLabel lblCaption = new JLabel("Caption");
		GridBagConstraints gbc_lblCaption = new GridBagConstraints();
		gbc_lblCaption.anchor = GridBagConstraints.EAST;
		gbc_lblCaption.insets = new Insets(0, 0, 5, 5);
		gbc_lblCaption.gridx = 0;
		gbc_lblCaption.gridy = 3;
		add(lblCaption, gbc_lblCaption);
		
		dataTableCaption = new JTextField();
		GridBagConstraints gbc_dataTableCaption = new GridBagConstraints();
		gbc_dataTableCaption.insets = new Insets(0, 0, 5, 5);
		gbc_dataTableCaption.fill = GridBagConstraints.HORIZONTAL;
		gbc_dataTableCaption.gridx = 1;
		gbc_dataTableCaption.gridy = 3;
		add(dataTableCaption, gbc_dataTableCaption);
		dataTableCaption.setColumns(10);
		dataTableCaption.getDocument().addDocumentListener(this);
		
		JLabel lblColumnPorts = new JLabel("Columns:");
		lblColumnPorts.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblColumnPorts = new GridBagConstraints();
		gbc_lblColumnPorts.anchor = GridBagConstraints.WEST;
		gbc_lblColumnPorts.insets = new Insets(0, 0, 5, 5);
		gbc_lblColumnPorts.gridx = 0;
		gbc_lblColumnPorts.gridy = 4;
		add(lblColumnPorts, gbc_lblColumnPorts);
		
		columnPortsPanelContainer = new JPanel();
		GridBagConstraints gbc_columnPortsPanelContainer = new GridBagConstraints();
		gbc_columnPortsPanelContainer.fill = GridBagConstraints.HORIZONTAL;
		gbc_columnPortsPanelContainer.gridwidth = 3;
		gbc_columnPortsPanelContainer.insets = new Insets(0, 0, 5, 0);
		gbc_columnPortsPanelContainer.gridx = 0;
		gbc_columnPortsPanelContainer.gridy = 5;
		add(columnPortsPanelContainer, gbc_columnPortsPanelContainer);
		columnPortsPanelContainer.setBackground(new Color(255, 255, 255));
		columnPortsPanelContainer.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		columnPortsPanelContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JButton addColumn = new JButton("+");
		addColumn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addColumn();
			}
		});
		columnPortsPanelContainer.add(addColumn);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.weighty = 4.0;
		gbc_panel.gridwidth = 3;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 6;
		add(panel, gbc_panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnEditTableContent = new JButton("Edit Table Content");
		panel.add(btnEditTableContent);
		btnEditTableContent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						TableContentDialog dlg = new TableContentDialog(dataTable, idRegister);
						dlg.setModal(true);
						dlg.setVisible(true);
					}
				});
			}
		});
		
		JPanel specialisedPanelContainer = new JPanel();
		GridBagConstraints gbc_specialisedPanelContainer = new GridBagConstraints();
		gbc_specialisedPanelContainer.weighty = 16.0;
		gbc_specialisedPanelContainer.gridwidth = 3;
		gbc_specialisedPanelContainer.fill = GridBagConstraints.BOTH;
		gbc_specialisedPanelContainer.gridx = 0;
		gbc_specialisedPanelContainer.gridy = 7;
		add(specialisedPanelContainer, gbc_specialisedPanelContainer);
		specialisedPanelContainer.setLayout(new BorderLayout(0, 0));
		
		if (specialisedControls != null)
			specialisedPanelContainer.add(specialisedControls, BorderLayout.CENTER);
	}
	
	public DataTable getDataTable() {
		return dataTable;
	}

	private void addColumn() {
		final DataTable dataTable = getDataTable();
		final DataTableColumn newCol = dataTable.addColumn();

		//Add panel
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					DataPortPanel panel = new DataPortPanel(workflow, newCol, null, DataTableDetails.this, idRegister);
					columnPortsPanelContainer.add(panel, columnPortsPanelContainer.getComponentCount()-1);
					columnPortsPanelContainer.repaint();
					columnPortsPanelContainer.revalidate();
					panel.doLayout();
					//Open the edit dialogue to enter details
					panel.openEditDialog();
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		});			
	}
	
	@Override
	public void refresh(TreeNode selectedNode, WorkflowTreeModel workflowTreeModel) {
		selectedTreeNode = selectedNode;
		this.workflowTreeModel = workflowTreeModel;
		dataTable = null;
		if (selectedNode != null && selectedNode instanceof DataTableTreeNode) {
			dataTable = ((DataTableTreeNode)selectedNode).getDataTable();
			selectedTreeNode = selectedNode;
			refresh(dataTable);
		}
		//Refresh specialised panel (different for each type of activity)
		if (specialisedControls != null)
			specialisedControls.refresh(selectedNode, workflowTreeModel);
	}
	
	public void refresh(DataTable dataTable) {
		this.dataTable = dataTable;
		
		
		//Fill port panels
		refreshDataPortPanel(columnPortsPanelContainer, dataTable.getColumns());
		
		refreshControls();
	}
	
	private void refreshDataPortPanel(JPanel mainPanel, Collection<? extends DataPort> ports) {
		
		//Remove all children except the '+' button
		while (mainPanel.getComponentCount() > 1)
			mainPanel.remove(0);

		for (Iterator<? extends DataPort> it = ports.iterator(); it.hasNext(); ) {
			DataPort port = it.next();
			
			//Add to panel
			DataPortPanel p = new DataPortPanel(workflow, port, null, this, idRegister);
			mainPanel.add(p, mainPanel.getComponentCount()-1);
			p.doLayout();
		}
		mainPanel.repaint();
		mainPanel.revalidate();
	}
	
	private void refreshControls() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					dataTableDetailsChangeListenerActive = false;
					if (dataTable != null) {
						dataTableId.setText(dataTable.getId());
						dataTableCaption.setText(dataTable.getCaption());
						dataTableDescription.setText(dataTable.getDescription());
					} 
				} catch (Exception exc) {
					exc.printStackTrace();
				}
				dataTableDetailsChangeListenerActive = true;
				DataTableDetails.this.revalidate();
			}
		});
	}


	//DocumentChangeListener
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		updateDataTableFromControls();
	}

	//DocumentChangeListener
	@Override
	public void insertUpdate(DocumentEvent arg0) {
		updateDataTableFromControls();
	}

	//DocumentChangeListener
	@Override
	public void removeUpdate(DocumentEvent arg0) {
		updateDataTableFromControls();
	}
	
	private void updateDataTableFromControls() {
		if (dataTableDetailsChangeListenerActive) {
			if (dataTable != null) {
				if (!dataTable.getId().equals(dataTableId.getText())) {
					dataTable.setId(dataTableId.getText());
				}
				dataTable.setCaption(dataTableCaption.getText());
				dataTable.setDescription(dataTableDescription.getText());
			}		
			if (selectedTreeNode != null) {
				workflowTreeModel.nodeChanged((DefaultMutableTreeNode)selectedTreeNode);
			}
		}
	}

	@Override
	public void removeDataObjectButtonClicked(final DataPortPanel panel) {
		//Remove from activity
		dataTable.getColumns().remove(panel.getDataPort());
		
		//Remove from parent panel
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Container parent = panel.getParent();
					parent.remove(panel);
					parent.repaint();
					parent.revalidate();
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		});	
	}
}
