package org.primaresearch.clc.phd.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.WorkflowImpl;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityIterator;

/**
 * Special workflow repository that lists all child activities of a given workflow
 * 
 * @author clc
 *
 */
public class ChildActivityRepository implements WorkflowRepository {

	private List<Workflow> workflows = new ArrayList<Workflow>();
	private Workflow sourceWorkflow;
	
	public ChildActivityRepository(Workflow sourceWorkflow) {
		this.sourceWorkflow = sourceWorkflow;
		createWorkflows();
	}
	
	private void createWorkflows() {
		
		for (ActivityIterator it = sourceWorkflow.getActivities(); it.hasNext(); ) {
			Activity act = it.next();
			WorkflowImpl w = new WorkflowImpl(act.getCaption());
			w.setRootActivity(act);
			workflows.add(w);
		}
	}
	
	@Override
	public int getWorkflowCount() {
		return workflows.size();
	}

	@Override
	public Workflow getWorkflow(int index) {
		return workflows.get(index);
	}

	@Override
	public void deleteWorkflow(int index) {
		workflows.remove(index);
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
	}

	@Override
	public Collection<Workflow> getWorkflows() {
		return workflows;
	}

	@Override
	public String getId() {
		return "Activities of '"+sourceWorkflow.getName()+"'";
	}

}
