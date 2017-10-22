package org.primaresearch.clc.phd.repository.search;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.primaresearch.clc.phd.ontology.Ontology;
import org.primaresearch.clc.phd.ontology.label.HasLabels;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityIterator;
import org.primaresearch.clc.phd.workflow.data.DataTable;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.DataTableColumn;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;

/**
 * Workflow filter implementation using labels.
 * 
 * @author clc
 *
 */
public class LabelWorkflowFilter implements WorkflowFilter, FilterChangeListener {

	private LabellableObjectFilter rootActivityFilter;
	private LabellableObjectFilter inputDataFilter;
	private LabellableObjectFilter outputDataFilter;
	private LabellableObjectFilter dataTableFilter;
	private Collection<FilterChangeListener> listeners = new LinkedList<FilterChangeListener>();
	private boolean includeChildActivities = false;
	
	/**
	 * Constructor
	 */
	public LabelWorkflowFilter() {
		rootActivityFilter = new LabellableObjectFilter(Ontology.getInstance().getActivityLabelSlots());
		rootActivityFilter.addListener(this);
		inputDataFilter = new LabellableObjectFilter(Ontology.getInstance().getDataObjectLabelSlots());
		inputDataFilter.addListener(this);
		outputDataFilter = new LabellableObjectFilter(Ontology.getInstance().getDataObjectLabelSlots());
		outputDataFilter.addListener(this);
		dataTableFilter = new LabellableObjectFilter(Ontology.getInstance().getDataObjectLabelSlots());
		dataTableFilter.addListener(this);
	}
	
	public void addListener(FilterChangeListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(FilterChangeListener listener) {
		listeners.remove(listener);
	}
	
	private void notifyListeners() {
		for (Iterator<FilterChangeListener> it = listeners.iterator(); it.hasNext(); )
			it.next().filterChanged(this);
	}
	
	/**
	 * Include child activities when filtering (otherwise only the root activity of a workflow is used)
	 */
	public boolean isIncludeChildActivities() {
		return includeChildActivities;
	}

	/**
	 * Include child activities when filtering (otherwise only the root activity of a workflow is used)
	 */
	public void setIncludeChildActivities(boolean includeChildActivities) {
		this.includeChildActivities = includeChildActivities;
	}

	@Override
	public Collection<Workflow> filterWorkflows(Collection<Workflow> toBeFiltered) {

		Collection<Workflow> filtered = new LinkedList<Workflow>();
		
		rootActivityFilter.resetCounts();
		inputDataFilter.resetCounts();
		outputDataFilter.resetCounts();
		dataTableFilter.resetCounts();
		
		//Filter
		for (Iterator<Workflow> it = toBeFiltered.iterator(); it.hasNext(); ) {
			Workflow workflow = it.next();
			
			if (workflow.getRootActivity() == null)
				continue;
			
			//Activity (or activities)
			Collection<HasLabels> objectsWithLabels = new LinkedList<HasLabels>();
			if (includeChildActivities) {
				for (ActivityIterator itAct = workflow.getActivities(); itAct.hasNext(); ) 
					objectsWithLabels.add(itAct.next());
			}
			else
				objectsWithLabels.add(workflow.getRootActivity());
			boolean rootActivityFilterResult = rootActivityFilter.filterAsGroup(objectsWithLabels, workflow);
			
			//Input data
			objectsWithLabels = new LinkedList<HasLabels>();
			for (Iterator<InputPort> itPorts = workflow.getRootActivity().getInputPorts().iterator(); itPorts.hasNext();) {
				DataPort port = itPorts.next();
				objectsWithLabels.add(port.getDataObject());
			}
			boolean inputDataFilterResult = inputDataFilter.filterAsGroup(objectsWithLabels, workflow);
			
			//Output data
			objectsWithLabels = new LinkedList<HasLabels>();
			for (Iterator<OutputPort> itPorts = workflow.getRootActivity().getOutputPorts().iterator(); itPorts.hasNext();) {
				DataPort port = itPorts.next();
				objectsWithLabels.add(port.getDataObject());
			}
			boolean outputDataFilterResult = outputDataFilter.filterAsGroup(objectsWithLabels, workflow);
			
			//Data tables
			objectsWithLabels = new LinkedList<HasLabels>();
			for (Iterator<DataTable> itTables = workflow.getDataTables().iterator(); itTables.hasNext(); ) {
				DataTable table = itTables.next();
				
				for (Iterator<DataTableColumn> itColumns = table.getColumns().iterator(); itColumns.hasNext(); ) {
					DataTableColumn column = itColumns.next();
					objectsWithLabels.add(column.getDataObject());
				}
			}
			boolean dataTableFilterResult = dataTableFilter.filterAsGroup(objectsWithLabels, workflow);
			
			//Filter
			if (rootActivityFilterResult && inputDataFilterResult && outputDataFilterResult && dataTableFilterResult)
				filtered.add(workflow);
		}
		
		//Count
		for (Iterator<Workflow> it = filtered.iterator(); it.hasNext(); ) {
			Workflow workflow = it.next();
			
			if (workflow.getRootActivity() == null)
				continue;
			
			//Activity
			Collection<HasLabels> objectsWithLabels = new LinkedList<HasLabels>();
			objectsWithLabels.add(workflow.getRootActivity());
			rootActivityFilter.countAsGroup(objectsWithLabels, workflow);
			
			//Input data
			objectsWithLabels = new LinkedList<HasLabels>();
			for (Iterator<InputPort> itPorts = workflow.getRootActivity().getInputPorts().iterator(); itPorts.hasNext();) {
				DataPort port = itPorts.next();
				objectsWithLabels.add(port.getDataObject());
			}
			inputDataFilter.countAsGroup(objectsWithLabels, workflow);
			
			//Output data
			objectsWithLabels = new LinkedList<HasLabels>();
			for (Iterator<OutputPort> itPorts = workflow.getRootActivity().getOutputPorts().iterator(); itPorts.hasNext();) {
				DataPort port = itPorts.next();
				objectsWithLabels.add(port.getDataObject());
			}
			outputDataFilter.countAsGroup(objectsWithLabels, workflow);

			//Data tables
			objectsWithLabels = new LinkedList<HasLabels>();
			for (Iterator<DataTable> itTables = workflow.getDataTables().iterator(); itTables.hasNext(); ) {
				DataTable table = itTables.next();
				
				for (Iterator<DataTableColumn> itColumns = table.getColumns().iterator(); itColumns.hasNext(); ) {
					DataTableColumn column = itColumns.next();
					objectsWithLabels.add(column.getDataObject());
				}
			}
			dataTableFilter.countAsGroup(objectsWithLabels, workflow);
			
		}
		
		return filtered;
	}

	public LabellableObjectFilter getRootActivityFilter() {
		return rootActivityFilter;
	}

	public LabellableObjectFilter getInputDataFilter() {
		return inputDataFilter;
	}

	public LabellableObjectFilter getOutputDataFilter() {
		return outputDataFilter;
	}

	public LabellableObjectFilter getDataTableFilter() {
		return dataTableFilter;
	}

	@Override
	public void filterChanged(Filter filter) {
		notifyListeners();
	}

	@Override
	public void init(Collection<Workflow> allWorkflows) {
		Collection<HasLabels> rootActivities = new LinkedList<HasLabels>();
		Collection<HasLabels> inputData = new LinkedList<HasLabels>();
		Collection<HasLabels> outputData = new LinkedList<HasLabels>();
		Collection<HasLabels> dataTableColumns = new LinkedList<HasLabels>();
		
		for (Iterator<Workflow> it = allWorkflows.iterator(); it.hasNext(); ) {
			Workflow workflow = it.next();
			Activity activity = workflow.getRootActivity();
			if (activity == null)
				continue;
			rootActivities.add(activity);
			
			for (Iterator<InputPort> itPorts = activity.getInputPorts().iterator(); itPorts.hasNext();) {
				DataPort port = itPorts.next();
				inputData.add(port.getDataObject());
			}
			
			for (Iterator<OutputPort> itPorts = activity.getOutputPorts().iterator(); itPorts.hasNext();) {
				DataPort port = itPorts.next();
				outputData.add(port.getDataObject());
			}
			
			for (Iterator<DataTable> itTables = workflow.getDataTables().iterator(); itTables.hasNext(); ) {
				DataTable table = itTables.next();
				
				for (Iterator<DataTableColumn> itColumns = table.getColumns().iterator(); itColumns.hasNext(); ) {
					DataTableColumn column = itColumns.next();
					dataTableColumns.add(column.getDataObject());
				}
			}
		}
		
		rootActivityFilter.init(rootActivities);
		inputDataFilter.init(inputData);
		outputDataFilter.init(outputData);
		dataTableFilter.init(dataTableColumns);
	}

	
}
