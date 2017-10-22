package org.primaresearch.clc.phd.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.primaresearch.clc.phd.workflow.Workflow;

/**
 * A meta repository combining several workflow repositories 
 */
public class CombinedRepository implements WorkflowRepository {

	private List<WorkflowRepository> childRepositories = new ArrayList<WorkflowRepository>();
	private String id;
	
	/**
	 * Constructor
	 * @param id
	 */
	public CombinedRepository(String id) {
		this.id = id;
	}
	
	/**
	 * Adds a child repository 
	 */
	public void addChildRepository(WorkflowRepository repository) {
		childRepositories.add(repository);
	}
	
	@Override
	public int getWorkflowCount() {
		int sum = 0;
		for (int i=0; i<childRepositories.size(); i++)
			sum += childRepositories.get(i).getWorkflowCount();
		return sum;
	}

	@Override
	public Workflow getWorkflow(int index) {
		int offset = 0;
		for (int i=0; i<childRepositories.size(); i++) {
			if (index < offset + childRepositories.get(i).getWorkflowCount())
				return childRepositories.get(i).getWorkflow(index - offset); 
			offset += childRepositories.get(i).getWorkflowCount();
		}
		return null;
	}

	@Override
	public void deleteWorkflow(int index) {
		int offset = 0;
		for (int i=0; i<childRepositories.size(); i++) {
			if (index < offset + childRepositories.get(i).getWorkflowCount()) {
				childRepositories.get(i).deleteWorkflow(index - offset); 
				return;
			}
			offset += childRepositories.get(i).getWorkflowCount();
		}
	}

	@Override
	public void refresh() {
		for (int i=0; i<childRepositories.size(); i++)
			childRepositories.get(i).refresh();
	}

	@Override
	public Collection<Workflow> getWorkflows() {
		Collection<Workflow> allWorkflows = new LinkedList<Workflow>();
		for (int i=0; i<childRepositories.size(); i++)
			allWorkflows.addAll(childRepositories.get(i).getWorkflows());
		return allWorkflows;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Combined Repository '"+id+"'";
	}
}
