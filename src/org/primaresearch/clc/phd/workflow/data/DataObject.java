package org.primaresearch.clc.phd.workflow.data;

import org.primaresearch.clc.phd.ontology.label.HasLabels;

/**
 * Interface for workflow data objects.
 * 
 * @author clc
 *
 */
public interface DataObject extends HasLabels {

	public DataObject clone();
	
	public String getCaption();
	
	public void setCaption(String caption);

	public String getDescription();

	public void setDescription(String description);
	
}
