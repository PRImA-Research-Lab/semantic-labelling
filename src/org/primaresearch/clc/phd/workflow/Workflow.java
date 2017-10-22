package org.primaresearch.clc.phd.workflow;

import java.util.Collection;

import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityIterator;
import org.primaresearch.clc.phd.workflow.activity.HasChildActivities;
import org.primaresearch.clc.phd.workflow.data.DataTable;

/**
 * Wrapper for an activity (root activity) with metadata and optional sub-workflows.
 * 
 * @author clc
 *
 */
public interface Workflow extends HasChildActivities {
	
	/** Entry point activity */
	public Activity getRootActivity();
	/** Entry point activity */
	public void setRootActivity(Activity a);

	/** Name of this workflow */
	public String getName();
	/** Name of this workflow */
	public void setName(String name);

	/** Description of this workflow */
	public DescriptionWithLabels getDescription(int index);
	/** Description of this workflow */
	public void setDescription(int index, String descr);
	/** Add description of this workflow */
	public DescriptionWithLabels addDescription(String descr);
	public int getDescriptionCount();

	/** Version number of this workflow */
	public String getVersion();
	/** Version number of this workflow */
	public void setVersion(String version);

	/** Version number of ontology used for this this workflow */
	public int getOntologyVersion();
	/** Version number of ontology used for this this workflow */
	public void setOntologyVersion(int version);

	/** Author of this workflow */
	public String getAuthor();
	/** Author of this workflow */
	public void setAuthor(String author);
	
	/** Location this workflow (e.g. file path) */
	public String getLocation();
	/** Location this workflow (e.g. file path) */
	public void setLocation(String location);

	/**
	 * Returns a collection of all sub-workflows
	 */
	public Collection<Workflow> getSubWorkflows();
	
	/**
	 * Returns an iterator for all activities of this workflow
	 */
	public ActivityIterator getActivities();
	
	/** Returns the data tables of this workflow */
	public Collection<DataTable> getDataTables();
	
	/** The workflow-wide register for IDs (e.g. data port IDs) */
	public IdGenerator getIdRegister();
	
}
