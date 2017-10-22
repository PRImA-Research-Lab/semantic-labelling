package org.primaresearch.clc.phd.repository.io;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.primaresearch.clc.phd.ontology.label.HasLabels;
import org.primaresearch.clc.phd.ontology.label.Label;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.repository.WorkflowRepository;
import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.ActivityIterator;


/**
 * Export of all used labels and their counts from all workflows of a repository
 * 
 * Table format:<br/> 
 * Workflow, [label type 1], [label type 2], ...<br/> 
 * aletheia, 1, 0, ...
 * 
 * @author clc
 *
 */
public class WorkflowRepositoryLabelExporter {

	private Map<String,Integer> globalActivityLabelMap = new TreeMap<String,Integer>();
	private Map<String,Integer> globalInputPortLabelMap = new TreeMap<String,Integer>();
	private Map<String,Integer> globalOutputPortLabelMap = new TreeMap<String,Integer>();
	
	
	
	/**
	 * Export of all used labels and their counts from all workflows of the given repository
	 * @param repository Repository with workflows
	 * @param csvFilePath Output file path
	 */
	public void exportLabels(WorkflowRepository repository, String csvFilePath) {
		
		//Collect all labels
		for (int i=0; i<repository.getWorkflowCount(); i++) {
			Workflow workflow = repository.getWorkflow(i);
			if (workflow == null)
				continue;
			
			collectLabels(workflow, globalActivityLabelMap, globalInputPortLabelMap, globalOutputPortLabelMap);
		}
		
		//Export
		exportCsv(repository, csvFilePath);
	}
	
	/**
	 * Creates a CSV table and saves it to a text file
	 * @param csvFilePath
	 */
	private void exportCsv(WorkflowRepository repository, String csvFilePath) {
		//Create table
		StringBuilder csv = new StringBuilder();
		
		//Headers
		csv.append("Workflow");
		
		for (Iterator<String> it = globalActivityLabelMap.keySet().iterator(); it.hasNext(); ) {
			String labelType = it.next();
			csv.append(",");
			csv.append("act."+labelType);
		}
		for (Iterator<String> it = globalInputPortLabelMap.keySet().iterator(); it.hasNext(); ) {
			String labelType = it.next();
			csv.append(",");
			csv.append("input."+labelType);
		}
		for (Iterator<String> it = globalOutputPortLabelMap.keySet().iterator(); it.hasNext(); ) {
			String labelType = it.next();
			csv.append(",");
			csv.append("output."+labelType);
		}
		csv.append("\n");
		
		//Values
		// Go through workflows
		for (int i=0; i<repository.getWorkflowCount(); i++) {
			Workflow workflow = repository.getWorkflow(i);
			
			appendRow(csv, workflow);
		}
		
		//Save to file
		try {
			if (!csvFilePath.toLowerCase().endsWith(".csv"))
				csvFilePath = csvFilePath + ".csv";
			FileUtils.writeStringToFile(new File(csvFilePath), csv.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Appends a single CSV row
	 * @param csv
	 * @param workflow
	 */
	private void appendRow(StringBuilder csv, Workflow workflow) {
		csv.append(workflow.getName());
		
		Map<String,Integer> workflowActivityLabelMap = new TreeMap<String,Integer>();
		Map<String,Integer> workflowInputLabelMap = new TreeMap<String,Integer>();
		Map<String,Integer> workflowOutputLabelMap = new TreeMap<String,Integer>();

		collectLabels(workflow, workflowActivityLabelMap, workflowInputLabelMap, workflowOutputLabelMap);
		
		for (Iterator<String> it = globalActivityLabelMap.keySet().iterator(); it.hasNext(); ) {
			String labelType = it.next();
			
			csv.append(",");
			if (workflowActivityLabelMap.containsKey(labelType))
				csv.append("T");
			else
				csv.append("F");
		}
		for (Iterator<String> it = globalInputPortLabelMap.keySet().iterator(); it.hasNext(); ) {
			String labelType = it.next();
			
			csv.append(",");
			if (workflowInputLabelMap.containsKey(labelType))
				csv.append("T");
			else
				csv.append("F");
		}
		for (Iterator<String> it = globalOutputPortLabelMap.keySet().iterator(); it.hasNext(); ) {
			String labelType = it.next();
			
			csv.append(",");
			if (workflowOutputLabelMap.containsKey(labelType))
				csv.append("T");
			else
				csv.append("F");
		}
		csv.append("\n");
	}
	
	/**
	 * Collects all labels from within the given workflow
	 * @param workflow
	 */
	private void collectLabels(Workflow workflow, Map<String,Integer> activityLabelMap, 
							Map<String,Integer> inputPortLabelMap, Map<String,Integer> outputPortLabelMap) {
		ActivityIterator it = workflow.getActivities();
		if (it == null)
			return;
		
		while (it.hasNext()) {
			Activity activity = it.next();
			collectLabels(activity, activityLabelMap, inputPortLabelMap, outputPortLabelMap);
		}
	}
	
	/**
	 * Collects all labels from within the given activity
	 * @param workflow
	 */
	private void collectLabels(Activity activity, Map<String,Integer> activityLabelMap, 
							Map<String,Integer> inputPortLabelMap, Map<String,Integer> outputPortLabelMap) {
		if (activity == null)
			return;
		
		//Activity labels
		collect(activity, activityLabelMap);
		
		//Ports
		for (int i=0; i<activity.getInputPorts().size(); i++)
			collect(activity.getInputPorts().get(i).getDataObject(), inputPortLabelMap);
		for (int i=0; i<activity.getOutputPorts().size(); i++)
			collect(activity.getOutputPorts().get(i).getDataObject(), outputPortLabelMap);
		//TODO: Loop ports
	}
	
	/**
	 * Collects all labels from within the given labellable object
	 * @param workflow
	 */
	private void collect(HasLabels labelHolder, Map<String,Integer> labelMap) {
		if (labelHolder == null)
			return;
		
		for (Iterator<LabelGroup> itGroup = labelHolder.getLabels().iterator(); itGroup.hasNext(); ) {
			LabelGroup group = itGroup.next();
			
			if (group == null)
				continue;
			
			for (Iterator<Label> itLabel = group.getLabels().iterator(); itLabel.hasNext(); ) {
				Label label = itLabel.next();
				
				if (label != null) {
					String typeId = label.getType().getId();
					if (labelMap.containsKey(typeId)) {
						//Update
						Integer oldCount = labelMap.get(typeId);
						if (oldCount != null) {
							labelMap.put(typeId, new Integer(oldCount.intValue() + 1));
						}
					} else {
						//Insert
						labelMap.put(typeId, new Integer(1));
					}
				}
			}
		}
	}
}
