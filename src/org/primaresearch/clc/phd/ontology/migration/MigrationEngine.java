package org.primaresearch.clc.phd.ontology.migration;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.primaresearch.clc.phd.ontology.Ontology;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.ontology.label.LabelType;


/**
 * Migrate the labels of a workflow from an older ontology to the latest one.
 * 
 * @author clc
 *
 */
public class MigrationEngine {
	private List<String> messages = new LinkedList<String>();

	/**	
	 * Migration messages (which labels were changed and which removed)
	 */
	public List<String> getMessages() {
		return messages;
	}

	/**
	 * 
	 * @param oldTypeId
	 * @param oldVersion
	 * @param labelForActivity
	 * @return
	 */
	public List<LabelType> getMigratedLabelTypes(String oldTypeId, int oldVersion, boolean labelForActivity) {
		Ontology ontology = Ontology.getInstance();
		
		List<LabelType> ret = new LinkedList<LabelType>();

		if (oldVersion >= ontology.getVersion()) { //Latest version already
			ret.add(ontology.getLabelType(oldTypeId));
			return ret; 
		}
		
		Map<String,Set<String>> migrationRules = ontology.getMigrationRules(oldVersion, false); 
		if (migrationRules == null) { //No migration rules defined for the old ontology version to the new one
			ret.add(ontology.getLabelType(oldTypeId));
			return ret; 
		}

		migrate(oldTypeId, labelForActivity, ontology, migrationRules, ret);
		
		return ret;
	}
	
//	/**
//	 * Migrates the labels of the given workflow to the latest ontology version
//	 * @param workflow
//	 * @return List of conversion messages
//	 */
//	public List<String> migrate(Workflow workflow) {
//		
//		if (workflow == null)
//			return messages;
//		
//		Ontology ontology = Ontology.getInstance();
//		int workflowOntologyVersion = workflow.getOntologyVersion();
//		if (workflowOntologyVersion >= ontology.getVersion())
//			return messages; //Latest version already
//		
//		Map<String,Set<String>> migrationRules = ontology.getMigrationRules(workflowOntologyVersion, false); 
//		if (migrationRules == null)
//			return messages; //No migration rules defined for the old ontology version to the new one
//		
//		//Migrate activities
//		for (ActivityIterator it = workflow.getActivities(); it.hasNext(); )
//			migrate(it.next(), ontology, migrationRules);
//		
//		//Update version number in workflow
//		workflow.setOntologyVersion(ontology.getVersion());
//		
//		return messages;
//	}
//	
//	/**
//	 * Migrates the labels of the given activity
//	 * @param activity Workflow activity (possibly) with labels
//	 * @param ontology Latest ontology
//	 * @param messages List of conversion messages
//	 */
//	private void migrate(Activity activity, Ontology ontology, Map<String,Set<String>> migrationRules) {
//		
//		//Activity labels
//		migrateLabelHolder(activity, ontology, migrationRules);
//		
//		//Input ports
//		List<InputPort> inputPorts = activity.getInputPorts();
//		if (inputPorts != null) {
//			for (Iterator<InputPort> it = inputPorts.iterator(); it.hasNext(); ) {
//				migrateLabelHolder(it.next().getDataObject(), ontology, migrationRules);
//			}
//		}
//		
//		//Output ports
//		List<OutputPort> outputPorts = activity.getOutputPorts();
//		if (outputPorts != null) {
//			for (Iterator<OutputPort> it = outputPorts.iterator(); it.hasNext(); ) {
//				migrate(it.next(), ontology, migrationRules);
//			}
//		}
//		
//		//Loop ports
//		if (activity instanceof ForLoopActivity) {
//			ForLoopActivity forLoop = (ForLoopActivity)activity;
//			migrate(forLoop.getLoopStart(), ontology, migrationRules);
//			migrate(forLoop.getLoopEnd(), ontology, migrationRules);
//			migrate(forLoop.getLoopStep(), ontology, migrationRules);
//			migrate(forLoop.getLoopPosition(), ontology, migrationRules);
//		}
//	}
//	
//	/**
//	 * Migrates the labels of a data port
//	 * @param port
//	 * @param ontology
//	 * @param messages
//	 */
//	private void migrate(DataPort port, Ontology ontology, Map<String,Set<String>> migrationRules) {
//		if (port == null)
//			return;
//		migrateLabelHolder(port.getDataObject(), ontology, migrationRules);
//	}
//	
//	/**
//	 * Migrates the labels of a labellable object
//	 * @param labelHolder
//	 * @param ontology
//	 * @param messages
//	 */
//	private void migrateLabelHolder(HasLabels labelHolder, Ontology ontology, Map<String,Set<String>> migrationRules) {
//		if (labelHolder == null)
//			return;
//		
//		Collection<LabelGroup> labelGroups = labelHolder.getLabels();
//				
//		if (labelGroups == null)
//			return;
//		
//		//All labels
//		for (Iterator<LabelGroup> it = labelGroups.iterator(); it.hasNext(); ) {
//			LabelGroup group = it.next();
//			if (group == null)
//				continue;
//			
//			List<Label> toRemove = new LinkedList<Label>();
//			
//			for (Iterator<Label> itLabel = group.getLabels().iterator(); itLabel.hasNext(); ) {
//				Label label = itLabel.next();
//				if (!migrate(label, labelHolder instanceof Activity, labelGroups, ontology, migrationRules)) {
//					toRemove.add(label);
//				}
//			}
//			
//			//Remove invalid labels
//			for (Iterator<Label> itLabel = toRemove.iterator(); itLabel.hasNext(); ) {
//				Label label = itLabel.next();
//				group.removeLabel(label);
//				messages.add("Invalid label removed: "+labelHolderName+" - "+label.getType().getId())
//			}
//			
//			//TODO: Check for duplicates
//		}
//	}
	
	/**
	 * Migrates a single label
	 * @param label
	 * @param ontology
	 * @param messages
	 */
	private void migrate(String oldLabelTypeId, boolean labelForActivity, Ontology ontology, Map<String,Set<String>> migrationRules,
			List<LabelType> migratedLabelTypes) {
		
		if (oldLabelTypeId == null)
			return;
				
		//Is there a rule for the old label type
		if (migrationRules.containsKey(oldLabelTypeId)) {
			Set<String> targetLabelTypeIds = migrationRules.get(oldLabelTypeId);
			if (targetLabelTypeIds == null)
				return; //Should not happen
			
			for (Iterator<String> it = targetLabelTypeIds.iterator(); it.hasNext(); ) {
				String targetLabelTypeId = it.next();
				
				processTargetLabelType(oldLabelTypeId, targetLabelTypeId, labelForActivity, ontology, migrationRules, true, migratedLabelTypes);
			}
		}
		else //No rule for the old label type
		{
			processTargetLabelType(oldLabelTypeId, oldLabelTypeId, labelForActivity, ontology, migrationRules, false, migratedLabelTypes);
		}
	}
	
	private void processTargetLabelType(String oldLabelTypeId, String targetLabelTypeId, boolean labelForActivity, Ontology ontology, Map<String,Set<String>> migrationRules,
			boolean migrating, List<LabelType> migratedLabelTypes) {
		
		//Does the type still exist
		LabelType newLabelType = ontology.getLabelType(targetLabelTypeId);
		if (newLabelType == null) {
			//Doesn't exist any longer -> ignore label
			if (migrating)
				messages.add("Invalid migration rule target: "+targetLabelTypeId);
			else
				messages.add("Invalid label ignored: "+targetLabelTypeId);
			return;
		}
		//The type still exists, but has it moved from activity to data object or vice versa?
		if (labelForActivity) {
			if (doesOneLabelGroupContainTheGivenType(ontology.getActivityLabelSlots(), newLabelType)) {
				migratedLabelTypes.add(newLabelType);
				if (migrating)
					messages.add("Activity label changed from "+oldLabelTypeId+" to "+targetLabelTypeId);
			}
			else 
				messages.add("Invalid target label ignored: "+targetLabelTypeId);
		} else { //Label for data object
			if (doesOneLabelGroupContainTheGivenType(ontology.getDataObjectLabelSlots(), newLabelType)) {
				migratedLabelTypes.add(newLabelType);
				if (migrating)
					messages.add("Data label changed from "+oldLabelTypeId+" to "+targetLabelTypeId);
				return;
			}
			else 
				messages.add("Invalid target label ignored: "+targetLabelTypeId);
		}
	}
	
	/**
	 * Checks if the given label type belongs to one of the given label groups
	 * @param labelGroups
	 * @param type
	 * @return
	 */
	private boolean doesOneLabelGroupContainTheGivenType(List<LabelGroup> labelGroups, LabelType type) {
		if (labelGroups == null || type == null)
			return false;
		
		LabelType rootType = type.getRootType();
		
		for (Iterator<LabelGroup> it = labelGroups.iterator(); it.hasNext(); ) {
			LabelGroup group = it.next();
			
			if (group.getType().getId().equals(rootType.getId()))
				return true; //Group is for the given label type
		}
		return false; //No group is for the given label type
	}
}
