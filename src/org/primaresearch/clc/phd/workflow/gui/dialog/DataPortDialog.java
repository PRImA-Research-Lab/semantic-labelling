package org.primaresearch.clc.phd.workflow.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity;
import org.primaresearch.clc.phd.workflow.data.DataCollection;
import org.primaresearch.clc.phd.workflow.data.DataObject;
import org.primaresearch.clc.phd.workflow.data.DataObjectFactory;
import org.primaresearch.clc.phd.workflow.data.SingleDataObject;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;
import org.primaresearch.clc.phd.workflow.gui.panel.DataPortPanel;
import org.primaresearch.clc.phd.workflow.gui.panel.LabelListPanel;

import java.awt.GridLayout;

/**
 * Dialogue to create or edit a data port (input port or output port)
 * 
 * @author clc
 *
 */
public class DataPortDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField dataObjectCaption;
	private JTextField dataPortId;
	private JTextArea dataObjectDescription;
	private DataObjectFactory dataFactory;
	private DataPort dataPort;
	private JTextField dataObjectValue;
	private JPanel inputSourcePanelContainer;
	private JTextArea dataTypes;
	private IdGenerator idRegister;
	private LabelListPanel labelsPanel;
	private Activity activity;
	private Workflow workflow;

	/**
	 * Constructor
	 */
	public DataPortDialog(Workflow workflow, DataPort dataPort, final Activity activity, IdGenerator idRegister) {
		
		this.dataFactory = new DataObjectFactory(idRegister);
		this.dataPort = dataPort;
		this.idRegister = idRegister;
		this.activity = activity;
		this.workflow = workflow;
		if (dataPort.getDataObject() == null) {
			dataPort.setDataObject(dataFactory.createSingleDataObject());
		}
		
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Data Port");
		
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new LineBorder(Color.GRAY));
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.gridwidth = 2;
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 0;
		panel.add(panel_3, gbc_panel_3);
		
		final JRadioButton rdbtnDataCollection = new JRadioButton("Data collection");
		final JRadioButton rdbtnSingleDataobject = new JRadioButton("Single data object");
		rdbtnSingleDataobject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						rdbtnDataCollection.setSelected(false);
						createSingleDataObject();
					}
				});
			}
		});
		rdbtnSingleDataobject.setSelected(true);
		panel_3.add(rdbtnSingleDataobject);
		
		rdbtnDataCollection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						rdbtnSingleDataobject.setSelected(false);
						createDataCollection();
					}
				});
			}
		});
		panel_3.add(rdbtnDataCollection);
		
		//Select correct radio button
		boolean isDataCollection = dataPort.getDataObject() instanceof DataCollection;
		rdbtnSingleDataobject.setSelected(!isDataCollection);
		rdbtnDataCollection.setSelected(isDataCollection);
		
		JLabel lblCaption = new JLabel("Caption");
		GridBagConstraints gbc_lblCaption = new GridBagConstraints();
		gbc_lblCaption.insets = new Insets(0, 0, 5, 5);
		gbc_lblCaption.anchor = GridBagConstraints.EAST;
		gbc_lblCaption.gridx = 0;
		gbc_lblCaption.gridy = 1;
		panel.add(lblCaption, gbc_lblCaption);
		
		dataObjectCaption = new JTextField(dataPort.getDataObject().getCaption());
		GridBagConstraints gbc_dataObjectCaption = new GridBagConstraints();
		gbc_dataObjectCaption.weightx = 3.0;
		gbc_dataObjectCaption.insets = new Insets(0, 0, 5, 0);
		gbc_dataObjectCaption.fill = GridBagConstraints.HORIZONTAL;
		gbc_dataObjectCaption.gridx = 1;
		gbc_dataObjectCaption.gridy = 1;
		panel.add(dataObjectCaption, gbc_dataObjectCaption);
		dataObjectCaption.setColumns(10);
		
		JLabel lblId = new JLabel("ID");
		GridBagConstraints gbc_lblId = new GridBagConstraints();
		gbc_lblId.anchor = GridBagConstraints.EAST;
		gbc_lblId.insets = new Insets(0, 0, 5, 5);
		gbc_lblId.gridx = 0;
		gbc_lblId.gridy = 2;
		panel.add(lblId, gbc_lblId);
		
		dataPortId = new JTextField(dataPort.getId());
		GridBagConstraints gbc_dataObjectId = new GridBagConstraints();
		gbc_dataObjectId.insets = new Insets(0, 0, 5, 0);
		gbc_dataObjectId.fill = GridBagConstraints.HORIZONTAL;
		gbc_dataObjectId.gridx = 1;
		gbc_dataObjectId.gridy = 2;
		panel.add(dataPortId, gbc_dataObjectId);
		dataPortId.setColumns(10);
		
		JLabel lblDescription = new JLabel("Description");
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
		gbc_lblDescription.anchor = GridBagConstraints.EAST;
		gbc_lblDescription.insets = new Insets(0, 0, 5, 5);
		gbc_lblDescription.gridx = 0;
		gbc_lblDescription.gridy = 3;
		panel.add(lblDescription, gbc_lblDescription);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(Color.LIGHT_GRAY));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 3;
		panel.add(panel_1, gbc_panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		dataObjectDescription = new JTextArea(dataPort.getDataObject().getDescription());
		panel_1.add(dataObjectDescription, BorderLayout.CENTER);
		
		JPanel dataTypePanel = new JPanel();
		GridBagConstraints gbc_dataTypePanel = new GridBagConstraints();
		gbc_dataTypePanel.insets = new Insets(0, 0, 5, 0);
		gbc_dataTypePanel.fill = GridBagConstraints.BOTH;
		gbc_dataTypePanel.gridx = 1;
		gbc_dataTypePanel.gridy = 4;
		panel.add(dataTypePanel, gbc_dataTypePanel);
		dataTypePanel.setLayout(new BorderLayout(0, 0));
		
		JButton selectDefaultDataType = new JButton(dataPort instanceof OutputPort ? "..." : "+");
		dataTypePanel.add(selectDefaultDataType, BorderLayout.EAST);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new LineBorder(Color.LIGHT_GRAY));
		dataTypePanel.add(panel_4, BorderLayout.CENTER);
		panel_4.setLayout(new BorderLayout(0, 0));
		
		dataTypes = new JTextArea();
		panel_4.add(dataTypes, BorderLayout.CENTER);
		selectDefaultDataType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showDefaultDataTypeSelection();
			}
		});
		//Fill
		if (dataPort instanceof OutputPort) {
			if (((OutputPort)dataPort).getType() != null)
				dataTypes.setText(((OutputPort)dataPort).getType());
			dataTypes.setRows(1);
			dataTypes.addKeyListener(new KeyAdapter(){
		            @Override
		            public void keyReleased(KeyEvent e) {
		                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
		                	dataTypes.setText(dataTypes.getText().trim());
		                }
		            }
		        });
		}
		else if (dataPort instanceof InputPort) {
			for (Iterator<String> it = ((InputPort)dataPort).getAllowedTypes().iterator(); it.hasNext(); ) {
				String type = it.next();
				if(!dataTypes.getText().isEmpty())
					dataTypes.append("\n");
				dataTypes.append(type);
			}
		}		
		
		JLabel lblSource = new JLabel(dataPort instanceof InputPort ? "Source" : "Forwarded from");
		GridBagConstraints gbc_lblSource = new GridBagConstraints();
		gbc_lblSource.anchor = GridBagConstraints.EAST;
		gbc_lblSource.insets = new Insets(0, 0, 5, 5);
		gbc_lblSource.gridx = 0;
		gbc_lblSource.gridy = 5;
		panel.add(lblSource, gbc_lblSource);
	
		inputSourcePanelContainer = new JPanel();
		JPanel inputSourcePanelParentContainer = new JPanel();
		inputSourcePanelParentContainer.setBorder(new LineBorder(Color.LIGHT_GRAY));
		GridBagConstraints gbc_inputSourcePanelParentContainer = new GridBagConstraints();
		gbc_inputSourcePanelParentContainer.insets = new Insets(0, 0, 5, 0);
		gbc_inputSourcePanelParentContainer.fill = GridBagConstraints.BOTH;
		gbc_inputSourcePanelParentContainer.gridx = 1;
		gbc_inputSourcePanelParentContainer.gridy = 5;
		panel.add(inputSourcePanelParentContainer, gbc_inputSourcePanelParentContainer);
		inputSourcePanelParentContainer.setLayout(new BorderLayout(0, 0));
		//Fill
		if (dataPort instanceof InputPort && ((InputPort)dataPort).getSource() != null) {
			
			DataPortPanel dataportPanel = new DataPortPanel(workflow, ((InputPort)dataPort).getSource(), null, null, idRegister);
			inputSourcePanelContainer.add(dataportPanel);
		}
		else if (dataPort instanceof OutputPort && ((OutputPort)dataPort).getForwardedPorts() != null) {
			
			for (Iterator<OutputPort> it = ((OutputPort)dataPort).getForwardedPorts().iterator(); it.hasNext(); ) {
				DataPortPanel dataportPanel = new DataPortPanel(workflow, it.next(), null, null, idRegister);
				inputSourcePanelContainer.add(dataportPanel);
			}
		}
		
		JButton selectInputPortSource = new JButton("...");
		selectInputPortSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPortLinkSelectionDialog(inputSourcePanelContainer, true);
			}
		});
		inputSourcePanelParentContainer.add(selectInputPortSource, BorderLayout.EAST);
		
		JScrollPane scrollPane = new JScrollPane(inputSourcePanelContainer);
		inputSourcePanelContainer.setLayout(new GridLayout(0, 1, 0, 0));
		inputSourcePanelParentContainer.add(scrollPane, BorderLayout.CENTER);
		
			
		JLabel lblDataCollectionPosition = new JLabel("Data collection position");
		GridBagConstraints gbc_lblDataCollectionPosition = new GridBagConstraints();
		gbc_lblDataCollectionPosition.anchor = GridBagConstraints.EAST;
		gbc_lblDataCollectionPosition.insets = new Insets(0, 0, 5, 5);
		gbc_lblDataCollectionPosition.gridx = 0;
		gbc_lblDataCollectionPosition.gridy = 6;
		panel.add(lblDataCollectionPosition, gbc_lblDataCollectionPosition);
		
		final JPanel dataCollectionPositionProviderContainer = new JPanel();
		dataCollectionPositionProviderContainer.setBorder(new LineBorder(Color.LIGHT_GRAY));
		GridBagConstraints gbc_dataCollectionPositionProviderContainer = new GridBagConstraints();
		gbc_dataCollectionPositionProviderContainer.insets = new Insets(0, 0, 5, 0);
		gbc_dataCollectionPositionProviderContainer.fill = GridBagConstraints.BOTH;
		gbc_dataCollectionPositionProviderContainer.gridx = 1;
		gbc_dataCollectionPositionProviderContainer.gridy = 6;
		panel.add(dataCollectionPositionProviderContainer, gbc_dataCollectionPositionProviderContainer);
		dataCollectionPositionProviderContainer.setLayout(new BorderLayout(0, 0));
		//Fill
		if (dataPort.getCollectionPositionProvider() != null) {
			
			DataPortPanel dataportPanel = new DataPortPanel(workflow, dataPort.getCollectionPositionProvider(), null, null, idRegister);
			dataCollectionPositionProviderContainer.add(dataportPanel, 0);
		}
		
		JButton chooseDataCollectionPositionProvider = new JButton("...");
		dataCollectionPositionProviderContainer.add(chooseDataCollectionPositionProvider, BorderLayout.EAST);
		chooseDataCollectionPositionProvider.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPortLinkSelectionDialog(dataCollectionPositionProviderContainer, false);
			}
		});

		if (dataPort instanceof InputPort) {
			
			JLabel lblStaticValue = new JLabel("Fixed value");
			GridBagConstraints gbc_lblStaticValue = new GridBagConstraints();
			gbc_lblStaticValue.anchor = GridBagConstraints.EAST;
			gbc_lblStaticValue.insets = new Insets(0, 0, 5, 5);
			gbc_lblStaticValue.gridx = 0;
			gbc_lblStaticValue.gridy = 7;
			panel.add(lblStaticValue, gbc_lblStaticValue);
			
			GridBagConstraints gbc_dataObjectValue = new GridBagConstraints();
			gbc_dataObjectValue.anchor = GridBagConstraints.WEST;
			gbc_dataObjectValue.insets = new Insets(0, 0, 5, 0);
			gbc_dataObjectValue.fill = GridBagConstraints.HORIZONTAL;
			gbc_dataObjectValue.gridx = 1;
			gbc_dataObjectValue.gridy = 7;
	
			boolean singleDataObject = dataPort.getDataObject() instanceof SingleDataObject;
			dataObjectValue = new JTextField(singleDataObject ? ((SingleDataObject)dataPort.getDataObject()).getValue() : "");
			dataObjectValue.setColumns(25);
			panel.add(dataObjectValue, gbc_dataObjectValue);
		}


		JLabel lblDataType = new JLabel(dataPort instanceof OutputPort ? "Data type" : "Allowed types");
		GridBagConstraints gbc_lblDataType = new GridBagConstraints();
		gbc_lblDataType.anchor = GridBagConstraints.EAST;
		gbc_lblDataType.insets = new Insets(0, 0, 5, 5);
		gbc_lblDataType.gridx = 0;
		gbc_lblDataType.gridy = 4;
		panel.add(lblDataType, gbc_lblDataType);

		
		JLabel lblLabels = new JLabel("Labels");
		GridBagConstraints gbc_lblLabels = new GridBagConstraints();
		gbc_lblLabels.insets = new Insets(0, 0, 5, 0);
		gbc_lblLabels.anchor = GridBagConstraints.WEST;
		gbc_lblLabels.gridwidth = 2;
		gbc_lblLabels.gridx = 0;
		gbc_lblLabels.gridy = 8;
		panel.add(lblLabels, gbc_lblLabels);
		
		JPanel labelPanelContainer = new JPanel();
		GridBagConstraints gbc_labelPanelContainer = new GridBagConstraints();
		gbc_labelPanelContainer.insets = new Insets(0, 0, 5, 0);
		gbc_labelPanelContainer.gridwidth = 2;
		gbc_labelPanelContainer.fill = GridBagConstraints.BOTH;
		gbc_labelPanelContainer.gridx = 0;
		gbc_labelPanelContainer.gridy = 9;
		panel.add(labelPanelContainer, gbc_labelPanelContainer);
		labelPanelContainer.setLayout(new BorderLayout(0, 0));
		
		labelsPanel = new LabelListPanel(dataPort.getDataObject());
		
		JScrollPane labelsScrollPane = new JScrollPane(labelsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		labelsScrollPane.setPreferredSize(new Dimension(100, 85));
		labelPanelContainer.add(labelsScrollPane, BorderLayout.CENTER);

		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.anchor = GridBagConstraints.SOUTHEAST;
		gbc_panel_2.gridwidth = 2;
		gbc_panel_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 10;
		panel.add(panel_2, gbc_panel_2);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						apply(DataPortDialog.this.dataPort);
						setVisible(false);
						dispatchEvent(new WindowEvent(DataPortDialog.this, WindowEvent.WINDOW_CLOSING));
					}
				});
			}
		});
		panel_2.add(btnOk);
		labelsPanel.refresh(dataPort.getDataObject());
	}
	
	/**
	 * Creates a single data object and adds it to the data port. Copies all attributes of the old data object of the port.
	 */
	private void createSingleDataObject() {
		DataObject d = dataFactory.createSingleDataObject();
		copyAttributes(dataPort.getDataObject(), d);
		dataPort.setDataObject(d);
		if (dataObjectValue != null)
			dataObjectValue.setEnabled(true);
		labelsPanel.setLabelledObject(d);
	}
	
	/**
	 * Creates a data collection object and adds it to the data port. Copies all attributes of the old data object of the port.
	 */
	private void createDataCollection() {
		DataObject d = dataFactory.createDataCollection();
		copyAttributes(dataPort.getDataObject(), d);
		dataPort.setDataObject(d);
		if (dataObjectValue != null)
			dataObjectValue.setEnabled(false);
	}
	
	/**
	 * Copies all attributes of one data object to another.
	 */
	private void copyAttributes(DataObject from, DataObject to) {
		//to.setType(from.getType());
		to.setCaption(from.getCaption());
		to.setDescription(from.getDescription());
	}
	
	/**
	 * Apply changes
	 */
	private void apply(final DataPort dataPort) {
		DataObject dataObject = dataPort.getDataObject();
		dataObject.setCaption(dataObjectCaption.getText());
		dataObject.setDescription(dataObjectDescription.getText());
		if (dataObject instanceof SingleDataObject && dataObjectValue != null)
			((SingleDataObject)dataObject).setValue(dataObjectValue.getText());
		dataPort.setId(dataPortId.getText());
		
		//Output port
		if (dataPort instanceof OutputPort) {
			((OutputPort)dataPort).setType(dataTypes.getText());
		}
		//Input port
		if (dataPort instanceof InputPort) {
			String text = dataTypes.getText();
			((InputPort)dataPort).getAllowedTypes().clear();
			if (!text.isEmpty()) {
				try {
					for (int i=0; i<dataTypes.getLineCount(); i++) {
						String type = text.substring(dataTypes.getLineStartOffset(i), dataTypes.getLineEndOffset(i));
						((InputPort)dataPort).addAllowedType(type);
					}
				} catch(Exception exc) {
					exc.printStackTrace();
				}
			}
		}
	}
	
	private void showDefaultDataTypeSelection() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
	
				/*Object[] possibilities = {SingleDataObject.TYPE_INTEGER};
				String s = (String)JOptionPane.showInputDialog(
				                    this,
				                    "Select a default type",
				                    "Default data types",
				                    JOptionPane.PLAIN_MESSAGE,
				                    null,
				                    possibilities,
				                    SingleDataObject.TYPE_INTEGER);
				*/
				
				DataTypeDialog dlg = new DataTypeDialog();
				dlg.setModal(true);
				dlg.setVisible(true);
				
				if ((dlg.getSelectedType() != null) && (dlg.getSelectedType().length() > 0)) {
					if (dataPort instanceof OutputPort) //Replace
						dataTypes.setText(dlg.getSelectedType());
					else { //Add
						if (!dataTypes.getText().isEmpty())
							dataTypes.append("\n");
						dataTypes.append(dlg.getSelectedType());
					}
				}
			}
		});
	}
	
	private void showPortLinkSelectionDialog(final JPanel portPanelContainer, final boolean forInputPortSource) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SelectPortLinkDialog dlg = new SelectPortLinkDialog(workflow, dataPort);
				dlg.setSize(500, 300);
				dlg.setLocation((int)(portPanelContainer.getLocationOnScreen().getX() + 5.0),
						(int)(portPanelContainer.getLocationOnScreen().getY() + portPanelContainer.getHeight() + 10.0));
				dlg.setVisible(true);
		
				//Apply
				final DataPort linkSource = dlg.getLinkSource();
				if (linkSource != null) {
					//Input source
					if (forInputPortSource) {
						if (dataPort instanceof InputPort)
							((InputPort)dataPort).setSource(linkSource);
						else if (dataPort instanceof OutputPort) {
							if (!(activity instanceof IfElseActivity))
								((OutputPort)dataPort).getForwardedPorts().clear(); //Only one forwarded port allowed for non-if-else activities
							((OutputPort)dataPort).addForwardedPort((OutputPort)linkSource);
						}
					}
					//Loop position source
					else {
						dataPort.setCollectionPositionProvider(linkSource);
					}
					
					//Add panel
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							try {
								if (!(activity instanceof IfElseActivity))
									portPanelContainer.removeAll();
								DataPortPanel dataportPanel = new DataPortPanel(workflow, linkSource, null, null, DataPortDialog.this.idRegister);
								portPanelContainer.add(dataportPanel);
								portPanelContainer.repaint();
								portPanelContainer.revalidate();
								//inputSourcePanelContainer.repaint();
								//showRelativeTo.getParent().doLayout();
								//showRelativeTo.getParent().repaint();
								//showRelativeTo.getParent().getParent().doLayout();
								dataportPanel.doLayout();
								//dataportPanel.repaint();
							} catch (Exception exc) {
								exc.printStackTrace();
							}
						}
					});
				}
			}
		});
	}
}
