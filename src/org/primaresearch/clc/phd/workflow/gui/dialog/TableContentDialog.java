package org.primaresearch.clc.phd.workflow.gui.dialog;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JButton;

import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.data.DataObjectFactory;
import org.primaresearch.clc.phd.workflow.data.DataTable;
import org.primaresearch.clc.phd.workflow.gui.model.DataTableModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

/**
 * Dialog for workflow data table content. Shows an editable table view.
 * 
 * @author clc
 *
 */
public class TableContentDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private JTable table;
	private DataTableModel tableModel;
	
	public TableContentDialog(DataTable dataTable, IdGenerator idRegister) {
		super();
		setSize(800, 600);
		setTitle("Data Table Content");
		
		table = new JTable(tableModel = new DataTableModel(dataTable, new DataObjectFactory(idRegister)));
		table.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		JScrollPane scrollPane = new JScrollPane(table);
		
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		JButton btnAddRow = new JButton("Add Row");
		btnAddRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addRow();
			}
		});
		buttonPanel.add(btnAddRow);
		
		JButton btnRemoveRow = new JButton("Remove Row");
		buttonPanel.add(btnRemoveRow);
	}

	private void addRow() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					tableModel.addRow();
				}
				catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		});
	}

}
