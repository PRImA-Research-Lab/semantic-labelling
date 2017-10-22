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
import java.util.Iterator;
import java.util.List;

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
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.data.DataObjectFactory;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;
import org.primaresearch.clc.phd.workflow.gui.model.ActivityTreeNode;
import org.primaresearch.clc.phd.workflow.gui.model.WorkflowTreeModel;
import org.primaresearch.clc.phd.workflow.gui.panel.DataPortPanel.DataObjectPanelListener;

/**
 * Panel with input fields for basic properties of a workflow activity.
 * 
 * @author clc
 *
 */
public class ActivityDetails extends DetailsPanel implements DataObjectPanelListener, DocumentListener  {
	
	private Activity activity;
	private Workflow workflow;
	private DetailsPanel specialisedControls;
	private volatile boolean activityDetailsChangeListenerActive = true;
	private WorkflowTreeModel workflowTreeModel;
	private TreeNode selectedTreeNode;
	private DataObjectFactory dataObjectFactory;
	private IdGenerator idRegister;
	
	private LabelListPanel labelsPanel;
	private static final long serialVersionUID = 1L;
	private JTextField activityId;
	private JTextField activityCaption;
	private JTextField activityLocalName;
	private JTextArea activityDescription;
	private JPanel inputPortsPanelContainer;
	private JPanel outputPortsPanelContainer;

	public ActivityDetails(Workflow workflow, DetailsPanel specialisedControls, IdGenerator idRegister) {
		this.specialisedControls = specialisedControls;
		this.dataObjectFactory = new DataObjectFactory(idRegister);
		this.idRegister = idRegister;
		this.workflow = workflow;
		setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblActivityDetails = new JLabel("Activity Properties");
		lblActivityDetails.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_lblActivityDetails = new GridBagConstraints();
		gbc_lblActivityDetails.anchor = GridBagConstraints.WEST;
		gbc_lblActivityDetails.gridwidth = GridBagConstraints.REMAINDER;
		gbc_lblActivityDetails.insets = new Insets(0, 0, 5, 0);
		gbc_lblActivityDetails.gridx = 0;
		gbc_lblActivityDetails.gridy = 0;
		add(lblActivityDetails, gbc_lblActivityDetails);
		
		JLabel lblId = new JLabel("Id");
		GridBagConstraints gbc_lblId = new GridBagConstraints();
		gbc_lblId.insets = new Insets(0, 0, 5, 5);
		gbc_lblId.anchor = GridBagConstraints.EAST;
		gbc_lblId.gridx = 0;
		gbc_lblId.gridy = 1;
		add(lblId, gbc_lblId);
		
		activityId = new JTextField();
		GridBagConstraints gbc_activityId = new GridBagConstraints();
		gbc_activityId.insets = new Insets(0, 0, 5, 5);
		gbc_activityId.fill = GridBagConstraints.HORIZONTAL;
		gbc_activityId.gridx = 1;
		gbc_activityId.gridy = 1;
		add(activityId, gbc_activityId);
		activityId.setColumns(10);
		activityId.getDocument().addDocumentListener(this);

		JLabel lblDescription = new JLabel("Description");
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
		gbc_lblDescription.insets = new Insets(0, 0, 5, 0);
		gbc_lblDescription.gridx = 2;
		gbc_lblDescription.gridy = 1;
		add(lblDescription, gbc_lblDescription);
		
		JLabel lblCaption = new JLabel("Caption");
		GridBagConstraints gbc_lblCaption = new GridBagConstraints();
		gbc_lblCaption.anchor = GridBagConstraints.EAST;
		gbc_lblCaption.insets = new Insets(0, 0, 5, 5);
		gbc_lblCaption.gridx = 0;
		gbc_lblCaption.gridy = 2;
		add(lblCaption, gbc_lblCaption);
		
		activityCaption = new JTextField();
		GridBagConstraints gbc_activityCaption = new GridBagConstraints();
		gbc_activityCaption.insets = new Insets(0, 0, 5, 5);
		gbc_activityCaption.fill = GridBagConstraints.HORIZONTAL;
		gbc_activityCaption.gridx = 1;
		gbc_activityCaption.gridy = 2;
		add(activityCaption, gbc_activityCaption);
		activityCaption.setColumns(10);
		activityCaption.getDocument().addDocumentListener(this);
		
		activityDescription = new JTextArea();
		activityDescription.setLineWrap(true);
		activityDescription.setWrapStyleWord(true);
		GridBagConstraints gbc_activityDescription = new GridBagConstraints();
		gbc_activityDescription.gridheight = 2;
		gbc_activityDescription.fill = GridBagConstraints.BOTH;
		gbc_activityDescription.insets = new Insets(0, 0, 5, 0);
		gbc_activityDescription.gridx = 2;
		gbc_activityDescription.gridy = 2;
		activityDescription.setBorder(new LineBorder(Color.LIGHT_GRAY));
		activityDescription.getDocument().addDocumentListener(this);
		
		JScrollPane descrScrollPane = new JScrollPane(activityDescription, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		descrScrollPane.setPreferredSize(new Dimension(100, 50));
		add(descrScrollPane, gbc_activityDescription);

		JLabel lblLocalName = new JLabel("Local name");
		GridBagConstraints gbc_lblLocalName = new GridBagConstraints();
		gbc_lblLocalName.anchor = GridBagConstraints.EAST;
		gbc_lblLocalName.insets = new Insets(0, 0, 5, 5);
		gbc_lblLocalName.gridx = 0;
		gbc_lblLocalName.gridy = 3;
		add(lblLocalName, gbc_lblLocalName);
		
		activityLocalName = new JTextField();
		GridBagConstraints gbc_activityLocalName = new GridBagConstraints();
		gbc_activityLocalName.insets = new Insets(0, 0, 5, 5);
		gbc_activityLocalName.fill = GridBagConstraints.HORIZONTAL;
		gbc_activityLocalName.gridx = 1;
		gbc_activityLocalName.gridy = 3;
		add(activityLocalName, gbc_activityLocalName);
		activityLocalName.setColumns(10);
		activityLocalName.getDocument().addDocumentListener(this);
		
		JLabel lblLabels = new JLabel("Labels");
		lblLabels.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblLabels = new GridBagConstraints();
		gbc_lblLabels.anchor = GridBagConstraints.WEST;
		gbc_lblLabels.gridwidth = 3;
		gbc_lblLabels.insets = new Insets(0, 0, 5, 0);
		gbc_lblLabels.gridx = 0;
		gbc_lblLabels.gridy = 4;
		add(lblLabels, gbc_lblLabels);
		
		labelsPanel = new LabelListPanel(activity);
		labelsPanel.setBackground(new Color(255, 255, 255));
		GridBagConstraints gbc_labelPanel = new GridBagConstraints();
		gbc_labelPanel.weighty = 4.0;
		gbc_labelPanel.insets = new Insets(0, 0, 5, 0);
		gbc_labelPanel.gridwidth = 3;
		gbc_labelPanel.fill = GridBagConstraints.BOTH;
		gbc_labelPanel.gridx = 0;
		gbc_labelPanel.gridy = 5;
		add(labelsPanel, gbc_labelPanel);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.weighty = 4.0;
		gbc_panel.gridwidth = 3;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 6;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblInputPorts = new JLabel("Input Ports");
		GridBagConstraints gbc_lblInputPorts = new GridBagConstraints();
		gbc_lblInputPorts.anchor = GridBagConstraints.WEST;
		gbc_lblInputPorts.insets = new Insets(0, 0, 5, 5);
		gbc_lblInputPorts.gridx = 0;
		gbc_lblInputPorts.gridy = 0;
		panel.add(lblInputPorts, gbc_lblInputPorts);
		
		JLabel lblOutputPorts = new JLabel("Output Ports");
		GridBagConstraints gbc_lblOutputPorts = new GridBagConstraints();
		gbc_lblOutputPorts.anchor = GridBagConstraints.WEST;
		gbc_lblOutputPorts.insets = new Insets(0, 0, 5, 0);
		gbc_lblOutputPorts.gridx = 1;
		gbc_lblOutputPorts.gridy = 0;
		panel.add(lblOutputPorts, gbc_lblOutputPorts);
		
		inputPortsPanelContainer = new JPanel();
		inputPortsPanelContainer.setBackground(new Color(255, 255, 255));
		inputPortsPanelContainer.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_inputPortsPanelContainer = new GridBagConstraints();
		gbc_inputPortsPanelContainer.insets = new Insets(0, 0, 0, 5);
		gbc_inputPortsPanelContainer.fill = GridBagConstraints.BOTH;
		gbc_inputPortsPanelContainer.gridx = 0;
		gbc_inputPortsPanelContainer.gridy = 1;
		panel.add(inputPortsPanelContainer, gbc_inputPortsPanelContainer);
		inputPortsPanelContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JButton addInputPort = new JButton("+");
		addInputPort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addInputPort();
			}
		});
		inputPortsPanelContainer.add(addInputPort);
		
		outputPortsPanelContainer = new JPanel();
		outputPortsPanelContainer.setBackground(new Color(255, 255, 255));
		outputPortsPanelContainer.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_outputPortsPanelContainer = new GridBagConstraints();
		gbc_outputPortsPanelContainer.fill = GridBagConstraints.BOTH;
		gbc_outputPortsPanelContainer.gridx = 1;
		gbc_outputPortsPanelContainer.gridy = 1;
		panel.add(outputPortsPanelContainer, gbc_outputPortsPanelContainer);
		outputPortsPanelContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JButton addOutputPort = new JButton("+");
		addOutputPort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addOutputPort();
			}
		});
		outputPortsPanelContainer.add(addOutputPort);
		
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
	
	public Activity getActivity() {
		return activity;
	}
	
	private void addInputPort() {
		final Activity activity = getActivity();
		final InputPort newPort = dataObjectFactory.createInputPort(activity);
		activity.getInputPorts().add(newPort);
		//Add panel
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					DataPortPanel panel = new DataPortPanel(workflow, newPort, activity, ActivityDetails.this, idRegister);
					inputPortsPanelContainer.add(panel, inputPortsPanelContainer.getComponentCount()-1);
					inputPortsPanelContainer.doLayout();
					panel.doLayout();
					//Open the edit dialogue to enter details
					panel.openEditDialog();
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		});
	}

	private void addOutputPort() {
		final Activity activity = getActivity();
		final OutputPort newPort = dataObjectFactory.createOutputPort(activity);
		activity.getOutputPorts().add(newPort);
		//Add panel
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					DataPortPanel panel = new DataPortPanel(workflow, newPort, activity, ActivityDetails.this, idRegister);
					outputPortsPanelContainer.add(panel, outputPortsPanelContainer.getComponentCount()-1);
					outputPortsPanelContainer.doLayout();
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
		activity = null;
		if (selectedNode != null && selectedNode instanceof ActivityTreeNode) {
			activity = ((ActivityTreeNode)selectedNode).getActivity();
			selectedTreeNode = selectedNode;
			refresh(activity);
		}
		//Refresh specialised panel (different for each type of activity)
		if (specialisedControls != null)
			specialisedControls.refresh(selectedNode, workflowTreeModel);
	}
	
	public void refresh(Activity activity) {
		this.activity = activity;
		
		//Fill labels panel
		labelsPanel.refresh(activity);
		
		//Fill port panels
		refreshDataPortPanel(inputPortsPanelContainer, activity.getInputPorts());
		refreshDataPortPanel(outputPortsPanelContainer, activity.getOutputPorts());
		
		refreshControls();
	}
	
	private void refreshDataPortPanel(JPanel mainPanel, List<? extends DataPort> ports) {
		
		//Remove all children except the '+' button
		while (mainPanel.getComponentCount() > 1)
			mainPanel.remove(0);

		for (Iterator<? extends DataPort> it = ports.iterator(); it.hasNext(); ) {
			DataPort port = it.next();
			
			//Add to panel
			DataPortPanel p = new DataPortPanel(workflow, port, activity, this, idRegister);
			mainPanel.add(p, mainPanel.getComponentCount()-1);
			p.doLayout();
		}
		mainPanel.doLayout();
	}
	
	private void refreshControls() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					activityDetailsChangeListenerActive = false;
					if (activity != null) {
						activityId.setText(activity.getId());
						activityCaption.setText(activity.getCaption());
						activityLocalName.setText(activity.getLocalName());
						activityDescription.setText(activity.getDescription());
					} 
				} catch (Exception exc) {
					exc.printStackTrace();
				}
				activityDetailsChangeListenerActive = true;
				ActivityDetails.this.revalidate();
			}
		});
	}


	//DocumentChangeListener
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		updateActivityFromControls();
	}

	//DocumentChangeListener
	@Override
	public void insertUpdate(DocumentEvent arg0) {
		updateActivityFromControls();
	}

	//DocumentChangeListener
	@Override
	public void removeUpdate(DocumentEvent arg0) {
		updateActivityFromControls();
	}
	
	private void updateActivityFromControls() {
		if (activityDetailsChangeListenerActive) {
			if (activity != null) {
				if (!activity.getId().equals(activityId.getText())) {
					activity.setId(activityId.getText());
				}
				activity.setCaption(activityCaption.getText());
				activity.setLocalName(activityLocalName.getText());
				activity.setDescription(activityDescription.getText());
			}		
			if (selectedTreeNode != null) {
				workflowTreeModel.nodeChanged((DefaultMutableTreeNode)selectedTreeNode);
			}
		}
	}

	@Override
	public void removeDataObjectButtonClicked(final DataPortPanel panel) {
		//Remove from activity
		activity.getInputPorts().remove(panel.getDataPort());
		
		//Remove from parent panel
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Container parent = panel.getParent();
					parent.remove(panel);
					parent.doLayout();
					parent.repaint();
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		});	
	}
}
