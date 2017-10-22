package org.primaresearch.clc.phd.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityIterator;
import org.primaresearch.clc.phd.workflow.activity.HasChildActivities;
import org.primaresearch.clc.phd.workflow.data.DataTable;

/**
 * Default implementation for the Workflow interface.
 * @author clc
 *
 */
public class WorkflowImpl implements Workflow {

	private String name;
	private List<DescriptionWithLabels> descriptions = new ArrayList<DescriptionWithLabels>();
	private String author;
	private String version;
	private Activity rootActivity;
	private String location;
	private int ontologyVersion = 1;
	private Collection<DataTable> dataTables = new LinkedList<DataTable>();
	private IdGenerator idRegister;
	
		
	public WorkflowImpl(String name) {
		super();
		this.name = name;
		this.idRegister = new IdGenerator();
	}

	@Override
	public Activity getRootActivity() {
		return rootActivity;
	}
	
	@Override
	public void setRootActivity(Activity rootActivity) {
		this.rootActivity = rootActivity;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public DescriptionWithLabels getDescription(int index) {
		if (index >= descriptions.size())
			descriptions.add(new DescriptionWithLabels(""));
		return descriptions.get(index);
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setDescription(int index, String description) {
		if (descriptions.isEmpty())
			descriptions.add(new DescriptionWithLabels(description));
		else
			this.descriptions.get(index).setDescription(description);
	}

	@Override
	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public Collection<Workflow> getSubWorkflows() {
		return null;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public void setLocation(String location) {
		this.location = location;
	}
	
	@Override
	public void replaceActivity(Activity old, Activity replacement) {
		if (rootActivity == null)
			return;
		
		if (rootActivity == old) 
			rootActivity = replacement;
		else if (rootActivity instanceof HasChildActivities){
			((HasChildActivities)rootActivity).replaceActivity(old, replacement);
		}
	}

	@Override
	public Collection<Activity> getChildActivities() {
		Collection<Activity> ret = new LinkedList<Activity>();
		if (rootActivity != null)
			ret.add(rootActivity);
		return ret;
	}
	
	@Override
	public ActivityIterator getActivities() {
		return new ActivityIterator(rootActivity);
	}

	@Override
	public int getOntologyVersion() {
		return ontologyVersion;
	}

	@Override
	public void setOntologyVersion(int version) {
		ontologyVersion = version;
	}

	@Override
	public Collection<DataTable> getDataTables() {
		return dataTables;
	}

	@Override
	public IdGenerator getIdRegister() {
		return idRegister;
	}

	@Override
	public DescriptionWithLabels addDescription(String descr) {
		DescriptionWithLabels ret = new DescriptionWithLabels(descr);
		descriptions.add(ret);
		return ret;
	}
	@Override
	public int getDescriptionCount() {
		return descriptions.size();
	}

}
