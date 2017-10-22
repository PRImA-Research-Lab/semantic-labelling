package org.primaresearch.clc.phd.workflow.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.primaresearch.clc.phd.ontology.label.Label;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.ontology.label.LabelGroup.TooManyLabelsInGroupException;
import org.primaresearch.clc.phd.ontology.label.LabelType;
import org.primaresearch.clc.phd.ontology.label.Labels;
import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;

/**
 * Base implementation of workflow activities. Has common properties such as lanels, id, type, caption, description, data ports etc.
 * 
 * @author clc
 *
 */
public abstract class BaseActivity implements Activity, Comparable<Activity> {

	private Labels labels;
	private String id;
	private ActivityType type;
	private String caption;
	private String description;
	private String localName = null;
	private List<InputPort> inputPorts = new ArrayList<InputPort>();
	private List<OutputPort> outputPorts = new ArrayList<OutputPort>();
	private Activity parentActivity;
	private IdGenerator idRegister;
	
	/**
	 * Constructor
	 */
	public BaseActivity(Activity parentActivity, String id, IdGenerator idRegister, ActivityType type, Labels allowedLabels) {
		this.parentActivity = parentActivity;
		if (id == null)
			id = idRegister.generateId(this);
		else {
			this.id = id;
			idRegister.registerId(id);
		}
		this.idRegister = idRegister;
		this.type = type;
		this.labels = allowedLabels; 
	}
	
	/**
	 * Copy constructor
	 */
	protected BaseActivity(BaseActivity other) {
		this.parentActivity = other.parentActivity;
		this.id = other.id;
		this.idRegister = other.idRegister;
		this.type = other.type;
		this.labels = other.labels.clone(); 
		this.caption = other.caption;
		this.description = other.description;
		this.localName = other.localName;
		for (Iterator<InputPort> it = other.inputPorts.iterator(); it.hasNext(); )
			inputPorts.add(it.next().clone());
		for (Iterator<OutputPort> it = other.outputPorts.iterator(); it.hasNext(); )
			outputPorts.add(it.next().clone());
	}
		
	@Override
	public void setId(String id) {
		idRegister.unregisterId(this.id);
		idRegister.registerId(id);
		this.id = id;
	}

	@Override
	public Collection<LabelGroup> getLabels() {
		return labels.getAllLabels();
	}
	
	@Override
	public Label addLabel(LabelType labelType) throws TooManyLabelsInGroupException {
		return labels.add(labelType);
	}
	
	@Override
	public void removeLabel(Label label) {
		labels.remove(label);
	}

	@Override
	public String getCaption() {
		return caption;
	}

	@Override
	public void setCaption(String caption) {
		this.caption = caption;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getId() {
		return id;
	}
		
	@Override
	public ActivityType getType() {
		return type;
	}

	@Override
	public List<InputPort> getInputPorts() {
		return inputPorts;
	}
	
	public void addInputPort(InputPort port) {
		inputPorts.add(port);
	}
	
	@Override
	public List<OutputPort> getOutputPorts() {
		return outputPorts;
	}
	
	public void addOutputPort(OutputPort port) {
		outputPorts.add(port);
	}

	@Override
	public String getLocalName() {
		return localName;
	}

	@Override
	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public Activity getParentActivity() {
		return parentActivity;
	}

	public void setParentActivity(Activity parentActivity) {
		this.parentActivity = parentActivity;
	}
	
	public abstract Activity clone();
	
	@Override
	public boolean hasLabels() {
		if (labels == null)
			return false;
		return !labels.isEmpty();
	}
	
	public int compareTo(Activity other) {
		return this.id.compareTo(other.getId());
	}

	public IdGenerator getIdRegister() {
		return idRegister;
	}
	
}
