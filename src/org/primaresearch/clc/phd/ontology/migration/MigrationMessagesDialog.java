package org.primaresearch.clc.phd.ontology.migration;

import java.util.Iterator;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;

import javax.swing.JTextPane;

/**
 * Dialog to display ontology / label migration results
 * 
 * @author clc
 *
 */
public class MigrationMessagesDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * @param messagesToShow
	 */
	public MigrationMessagesDialog(List<String> messagesToShow) {
		super();
		setTitle("Ontology Migration Messages");
		setSize(500,500);

		JTextPane textPane = new JTextPane();
		if (messagesToShow != null) {
			StringBuilder str = new StringBuilder();
			for (Iterator<String> it = messagesToShow.iterator(); it.hasNext(); ) {
				str.append(it.next());
				str.append("\n");
			}
			textPane.setText(str.toString());
		}

		JScrollPane scrollPane = new JScrollPane(textPane);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	}
}
