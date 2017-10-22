package org.primaresearch.clc.phd.workflow.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.validation.modules.ActivtyValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.CycleDetectionValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.DataCardinalityMatchValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.DatatypeMatchValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.LabelMatchValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.MissingChildActivitiesValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.MissingDatatypesValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.UnconnectedDataPortsValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.WorkflowObjectValidationModule;
import org.primaresearch.clc.phd.workflow.validation.modules.WorkflowValidationModule;

/**
 * Validation of workflows. Contains a set of validation modules.
 * 
 * @author clc
 *
 */
public class WorkflowValidator {

	private List<WorkflowValidationModule> validationModules = new ArrayList<WorkflowValidationModule>();
	
	/**
	 * Constructor
	 */
	public WorkflowValidator() {
		//Initialise validation modules
		validationModules.add(new WorkflowObjectValidationModule());
		validationModules.add(new MissingChildActivitiesValidationModule());
		validationModules.add(new DatatypeMatchValidationModule());
		validationModules.add(new DataCardinalityMatchValidationModule());
		validationModules.add(new UnconnectedDataPortsValidationModule());
		validationModules.add(new CycleDetectionValidationModule());
		validationModules.add(new ActivtyValidationModule());
		validationModules.add(new MissingDatatypesValidationModule());
		validationModules.add(new LabelMatchValidationModule());
		
		//TODO
	}
	
	public WorkflowValidationResult validate(Workflow workflow) {
		
		WorkflowValidationResult root = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_NONE, null, "Validation Result", 
						"Select result items to see details.");
		
		WorkflowValidationResult errors = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_ERROR, null, "Errors",  
				"Errors that would preventi the workflow to run correctly");
		root.addSubResult(errors);

		WorkflowValidationResult warnings = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_WARNING, null, "Warnings",  
				"Potentially problematic conditions that should be checked");
		root.addSubResult(warnings);
		
		WorkflowValidationResult infos = new WorkflowValidationResult(WorkflowValidationResult.LEVEL_INFO, null, "Infos",  
				"Purely informational notes");
		root.addSubResult(infos);
		
		//Go through all modules and validate
		for (Iterator<WorkflowValidationModule> it = validationModules.iterator(); it.hasNext(); ) {
			WorkflowValidationModule module = it.next();
			Collection<WorkflowValidationResult> resItems = module.validate(workflow);
			if (resItems != null) {
				for (Iterator<WorkflowValidationResult> itItem = resItems.iterator(); itItem.hasNext(); ) {
					WorkflowValidationResult item = itItem.next();
					if (item.getLevel() == WorkflowValidationResult.LEVEL_NONE)
						root.addSubResult(item);
					else if (item.getLevel() == WorkflowValidationResult.LEVEL_ERROR)
						errors.addSubResult(item);
					else if (item.getLevel() == WorkflowValidationResult.LEVEL_WARNING)
						warnings.addSubResult(item);
					else if (item.getLevel() == WorkflowValidationResult.LEVEL_INFO)
						infos.addSubResult(item);
				}
			}
		}
		
		return root;
	}
}
