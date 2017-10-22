package org.primaresearch.clc.phd.workflow.activity;

import java.util.Collection;
import java.util.List;

import org.primaresearch.clc.phd.ontology.label.HasLabels;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;

/**
 * Interface for workflow activities (tools, algorithms, control flow, ...) with input and output data ports.
 * 
 * @author clc
 *
 */
public interface Activity extends HasLabels {

	public Activity clone();
	
	/** ID of this activity */
	public String getId();
	/** ID of this activity */
	public void setId(String id);
	
	/** Type of this activity */
	public ActivityType getType();
	
	/** Caption of this activity (e.g. for displaying in graphical user interface) */
	public String getCaption();
	/** Caption of this activity (e.g. for displaying in graphical user interface) */
	public void setCaption(String caption);
	
	/** Description of this activity */
	public String getDescription();
	/** Description of this activity */
	public void setDescription(String description);

	/**
	 * Local names can be used to force several abstract activities within a workflow to be
	 * instantiated with one and the same concrete activity (same local name) or different
	 * activities (different local names).<br>
	 * Example:<br>Two OCR methods are to be evaluated. The abstract activities for the methods
	 * could get local names 'Method_A' and 'Method_B'. The activities for the evaluation, on
	 * the other hand, should both get the same local name 'EvaluationMethod' to ensure the
	 * same evaluation method is used for both OCR methods.
	 * 
	 * @return The local name of this activity.
	 */
	public String getLocalName();
	
	/**
	 * Local names can be used to force several abstract activities within a workflow to be
	 * instantiated with one and the same concrete activity (same local name) or different
	 * activities (different local names).<br>
	 * Example:<br>Two OCR methods are to be evaluated. The abstract activities for the methods
	 * could get local names 'Method_A' and 'Method_B'. The activities for the evaluation, on
	 * the other hand, should both get the same local name 'EvaluationMethod' to ensure the
	 * same evaluation method is used for both OCR methods.
	 */
	public void setLocalName(String localName);
	
	/**
	 * Returns a list of all input data ports
	 */
	public List<InputPort> getInputPorts();
	
	/**
	 * Returns a list of all output data ports
	 */
	public List<OutputPort> getOutputPorts();
	
	/**
	 * Returns all data ports that can be used as input for child activities.
	 * @return Collection of ports (may be empty)
	 */
	public Collection<DataPort> getSourcePortsForChildren();
	
	/**
	 * Returns all data ports to which child activities can send their output data.
	 * @return Collection of ports (may be empty)
	 */
	public Collection<DataPort> getTargetPortsForChildren();
	
	/**
	 * Returns the abstraction level of this activity (abstract or concrete). 
	 * A compound activity is abstract if one or more child activities are abstract.
	 * @return Returns <code>true</code> if this activity is abstract and <code>false</code> if it is concrete.
	 */
	public boolean isAbstract();
	
	/**
	 * Returns the parent activity (e.g. a loop activity or a directed graph activity).
	 * @return Activity or null (no parent)
	 */
	public Activity getParentActivity();

	/**
	 * Sets the parent activity (e.g. a loop activity or a directed graph activity).
	 */
	public void setParentActivity(Activity parent);
}
