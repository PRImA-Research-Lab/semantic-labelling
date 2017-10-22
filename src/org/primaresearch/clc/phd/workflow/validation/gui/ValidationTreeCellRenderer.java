package org.primaresearch.clc.phd.workflow.validation.gui;

import java.awt.Component;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.primaresearch.clc.phd.workflow.validation.WorkflowValidationResult;
import org.primaresearch.clc.phd.workflow.validation.gui.model.ValidationResultTreeItem;

/**
 * Specialised tree renderer that adds icons for validation errors, warnings and notes.
 * 
 * @author clc
 *
 */
public class ValidationTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;
	
	private static ImageIcon iconError;
	private static ImageIcon iconWarning;
	private static ImageIcon iconInfo;
	
	static {
		URL iconURL = "".getClass().getResource("/org/primaresearch/clc/phd/workflow/validation/gui/res/icon_error.png");
		iconError = new ImageIcon(iconURL);

		iconURL = "".getClass().getResource("/org/primaresearch/clc/phd/workflow/validation/gui/res/icon_warning.png");
		iconWarning = new ImageIcon(iconURL);
		
		iconURL = "".getClass().getResource("/org/primaresearch/clc/phd/workflow/validation/gui/res/icon_info.png");
		iconInfo = new ImageIcon(iconURL);
	}

	@Override
	public Component getTreeCellRendererComponent(	JTree tree, Object value, boolean selected, boolean expanded,
													boolean leaf, int row, boolean hasFocus) {     
	   super.getTreeCellRendererComponent(tree,value,selected,expanded,leaf,row,hasFocus);
	   
	   if (value instanceof ValidationResultTreeItem) {
		   ValidationResultTreeItem node = (ValidationResultTreeItem)value;
		   
		   if (node.getValidationResult() != null) {
			   if (node.getValidationResult().getLevel() == WorkflowValidationResult.LEVEL_ERROR)
				   setIcon(iconError);
			   else if (node.getValidationResult().getLevel() == WorkflowValidationResult.LEVEL_WARNING)
				   setIcon(iconWarning);
			   else if (node.getValidationResult().getLevel() == WorkflowValidationResult.LEVEL_INFO)
				   setIcon(iconInfo);
		   }
	   }
	   return this;
	}
}
