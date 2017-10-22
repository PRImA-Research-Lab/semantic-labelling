package org.primaresearch.clc.phd.workflow.activity;

import java.util.Collection;

import org.primaresearch.clc.phd.ontology.Ontology;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.ontology.label.Labels;
import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity;

/**
 * Factory for different types of activity objects (atomic, loop, if-else etc.)
 * 
 * @author clc
 *
 */
public class ActivityFactory {

	/** Template for a list of label types that are allowed for activities according to the ontology */
	private Collection<LabelGroup> allowedLabels;
	
	//private DataObjectFactory dataFactory = new DataObjectFactory();
	
	private IdGenerator idRegister;

	/**
	 * Constructor
	 */
	public ActivityFactory(IdGenerator idRegister) {
		this.idRegister = idRegister;
		initLabelTemplate();
	}
	
	/** 
	 * Creates the template for a list of label types that are allowed for activities according to the ontology 
	 */
	private void initLabelTemplate() {

		//Get allowed label slots from the ontology
		allowedLabels = Ontology.getInstance().getActivityLabelSlots();

		//CC: Old static label slots:
		//allowedLabels = new LinkedList<LabelGroup>();
		//Processing Step
		//allowedLabels.add(new LabelGroup(	Ontology.getInstance().getRootType(Ontology.DefaultRootType.PROCESSING_STEP),
		//									Integer.MAX_VALUE));
		//Application
		//allowedLabels.add(new LabelGroup(	Ontology.getInstance().getRootType(Ontology.DefaultRootType.APPLICATION),
		//									1));
		//Automation
		//allowedLabels.add(new LabelGroup(	Ontology.getInstance().getRootType(Ontology.DefaultRootType.AUTOMATION),
		//									1));
	}
	
	/**
	 * Creates a new activity of the given type
	 * @param type Activity type
	 * @param id ID to be assigned
	 * @return The new activity
	 */
	public Activity createActivity(Activity parentActivity, ActivityType type, String id) {
		if (type.equals(ActivityType.ATOMIC_ACTIVITY))
			return createAtomicActivity(parentActivity, id, true);
		else if (type.equals(ActivityType.DIRECTED_GRAPH_ACTIVITY))
			return createDirectedGraphActivity(parentActivity, id);
		else if (type.equals(ActivityType.FOR_LOOP_ACTIVITY))
			return createForLoopActivity(parentActivity, id);
		else if (type.equals(ActivityType.IF_ELSE_ACTIVITY))
			return createIfElseActivity(parentActivity, id);
		throw new IllegalArgumentException("Activity type not supported: "+type);
	}
	
	/**
	 * Creates an atomic activity
	 * @param id Activity ID
	 * @param isAbstract Is the activity abstract (true) or concrete (false)
	 * @return The new activity
	 */
	public AtomicActivity createAtomicActivity(Activity parentActivity, String id, boolean isAbstract) {
		AtomicActivity activity = new AtomicActivity(parentActivity, id, idRegister, isAbstract, new Labels(allowedLabels));
		return activity;
	}
	
	/**
	 * Creates 'directed acyclic graph' (DAG) activity
	 * @param id Activity ID
	 * @return The new activity
	 */
	public DirectedGraphActivity createDirectedGraphActivity(Activity parentActivity, String id) {
		DirectedGraphActivity activity = new DirectedGraphActivity(parentActivity, id, idRegister, new Labels(allowedLabels));
		return activity;
	}
	
	/**
	 * Creates 'for loop' activity
	 * @param id Activity ID
	 * @return The new activity
	 */
	public ForLoopActivity createForLoopActivity(Activity parentActivity, String id) {
		ForLoopActivity activity = new ForLoopActivity(parentActivity, id, idRegister, new Labels(allowedLabels));
		
		return activity;
	}
	
	/**
	 * Creates 'if-else' activity
	 * @param id Activity ID
	 * @return The new activity
	 */
	public IfElseActivity createIfElseActivity(Activity parentActivity, String id) {
		IfElseActivity activity = new IfElseActivity(parentActivity, id, idRegister, new Labels(allowedLabels));
		
		return activity;
	}
}
