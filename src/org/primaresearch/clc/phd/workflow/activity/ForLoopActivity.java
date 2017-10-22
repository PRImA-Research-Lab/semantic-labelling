package org.primaresearch.clc.phd.workflow.activity;

import java.util.Collection;
import java.util.LinkedList;

import org.primaresearch.clc.phd.ontology.label.Labels;
import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.data.DataObjectFactory;
import org.primaresearch.clc.phd.workflow.data.SingleDataObject;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.LoopPort;

/**
 * Workflow activity representing a for loop
 * 
 * @author clc
 *
 */
public class ForLoopActivity extends BaseActivity implements HasChildActivities{

	private Activity childActivity = null;
	private LoopPort loopStart;
	private LoopPort loopPosition;
	private LoopPort loopEnd;
	private LoopPort loopStep;
	
	public ForLoopActivity(Activity parentActivity, String id, IdGenerator idRegister, Labels allowedLabels) {
		super(parentActivity, id, idRegister, ActivityType.FOR_LOOP_ACTIVITY, allowedLabels);
		
		DataObjectFactory dataFactory = new DataObjectFactory(idRegister);
		
		//Add default input ports
		loopStart = dataFactory.createLoopPort(this);
		SingleDataObject forLoopStart = (SingleDataObject)loopStart.getDataObject();
		loopStart.setId("forLoopStart");
		forLoopStart.setCaption("Loop start position");
		loopStart.setType(SingleDataObject.TYPE_INTEGER);
		forLoopStart.setValue("0");
	
		loopPosition = dataFactory.createLoopPort(this);
		SingleDataObject forLoopPos = (SingleDataObject)loopPosition.getDataObject();
		loopPosition.setId("forLoopPosition");
		forLoopPos.setCaption("Current loop position");
		loopPosition.setType(SingleDataObject.TYPE_INTEGER);

		loopEnd = dataFactory.createLoopPort(this);
		SingleDataObject forLoopEnd = (SingleDataObject)loopEnd.getDataObject();
		loopEnd.setId("forLoopEnd");
		forLoopEnd.setCaption("Loop end position");
		loopEnd.setType(SingleDataObject.TYPE_INTEGER);

		loopStep = dataFactory.createLoopPort(this);
		SingleDataObject forLoopStep = (SingleDataObject)loopStep.getDataObject();
		loopStep.setId("forLoopStep");
		forLoopStep.setCaption("Loop step width");
		loopStep.setType(SingleDataObject.TYPE_INTEGER);
		forLoopStep.setValue("1");
	}
	
	/**
	 * Copy constructor
	 * @param other
	 */
	public ForLoopActivity(ForLoopActivity other) {
		super(other);
		this.childActivity = other.childActivity.clone();
		this.loopStart = other.loopStart.clone();
		this.loopPosition = other.loopPosition.clone();
		this.loopEnd = other.loopEnd.clone();
		this.loopStep = other.loopStep.clone();
	}
	
	public ForLoopActivity clone() {
		return new ForLoopActivity(this);
	}

	@Override
	public boolean isAbstract() {
		return childActivity != null && childActivity.isAbstract();
	}

	public Activity getChildActivity() {
		return childActivity;
	}

	public void setChildActivity(Activity childActivity) {
		this.childActivity = childActivity;
	}

	public LoopPort getLoopStart() {
		return loopStart;
	}

	public LoopPort getLoopPosition() {
		return loopPosition;
	}

	public void setLoopStart(LoopPort loopStart) {
		this.loopStart = loopStart;
	}

	public void setLoopPosition(LoopPort loopPosition) {
		this.loopPosition = loopPosition;
	}

	public LoopPort getLoopEnd() {
		return loopEnd;
	}

	public LoopPort getLoopStep() {
		return loopStep;
	}

	public void setLoopEnd(LoopPort loopEnd) {
		this.loopEnd = loopEnd;
	}

	public void setLoopStep(LoopPort loopStep) {
		this.loopStep = loopStep;
	}

	@Override
	public void replaceActivity(Activity old, Activity replacement) {
		if (childActivity == null)
			return;
		
		if (childActivity == old) 
			childActivity = replacement;
		else if (childActivity instanceof HasChildActivities){
			((HasChildActivities)childActivity).replaceActivity(old, replacement);
		}
	}

	@Override
	public Collection<DataPort> getSourcePortsForChildren() {
		Collection<DataPort> ret = new LinkedList<DataPort>();
		ret.addAll(getInputPorts());
		ret.add(loopStart);
		ret.add(loopEnd);
		ret.add(loopPosition);
		ret.add(loopStep);
		return ret;
	}

	@Override
	public Collection<DataPort> getTargetPortsForChildren() {
		Collection<DataPort> ret = new LinkedList<DataPort>();
		ret.addAll(getOutputPorts());
		ret.add(loopStart);
		ret.add(loopEnd);
		ret.add(loopPosition);
		ret.add(loopStep);
		return ret;
	}

	@Override
	public Collection<Activity> getChildActivities() {
		Collection<Activity> ret = new LinkedList<Activity>();
		if (childActivity != null)
			ret.add(childActivity);
		return ret;
	}

}
