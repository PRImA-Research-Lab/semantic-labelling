package org.primaresearch.clc.phd.workflow.activity;

import java.util.Collection;
import java.util.LinkedList;

import org.primaresearch.clc.phd.ontology.label.Labels;
import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;

/**
 * Workflow activity representing an algorithm, a tool, or a method.
 * 
 * @author clc
 *
 */
public class AtomicActivity extends BaseActivity {

	/**	Specifies if this activity represents an actual existing method/tool (concrete, not abstract) or a range of activities (abstract)*/
	boolean isAbstract;
	
	/** Name of a specific method/tool (concrete activity) */
	String methodName;
	
	/** Version of a specific method/tool (concrete activity) */
	String methodVersion;
	
	/**
	 * Constructor
	 */
	AtomicActivity(Activity parentActivity, String id, IdGenerator idRegister, boolean isAbstract, Labels allowedLabels) {
		super(parentActivity, id, idRegister, ActivityType.ATOMIC_ACTIVITY, allowedLabels);
		this.isAbstract = isAbstract;
	}
	
	/**
	 * Copy constructor
	 * @param other
	 */
	private AtomicActivity(AtomicActivity other) {
		super(other);
		this.isAbstract = other.isAbstract;
		this.methodName = other.methodName;
		this.methodVersion = other.methodVersion;
	}
	
	public Activity clone() {
		return new AtomicActivity(this);
	}

	/**
	 * Specifies if this activity represents an actual existing method/tool (concrete, not abstract) or a range of activities (abstract)
	 * @return Returns <code>true</code> if this activity is abstract and <code>false</code> if it is concrete.
	 */
	@Override
	public boolean isAbstract() {
		return isAbstract;
	}

	/**
	 * Specifies if this activity represents an actual existing method/tool (concrete, not abstract) or a range of activities (abstract)
	 * @return Returns <code>true</code> if this activity is abstract and <code>false</code> if it is concrete.
	 */
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	/** Name of a specific method/tool (concrete activity) */
	public String getMethodName() {
		return methodName;
	}

	/** Name of a specific method/tool (concrete activity) */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/** Version of a specific method/tool (concrete activity) */
	public String getMethodVersion() {
		return methodVersion;
	}

	/** Version of a specific method/tool (concrete activity) */
	public void setMethodVersion(String methodVersion) {
		this.methodVersion = methodVersion;
	}

	@Override
	public Collection<DataPort> getSourcePortsForChildren() {
		return new LinkedList<DataPort>();
	}

	@Override
	public Collection<DataPort> getTargetPortsForChildren() {
		return new LinkedList<DataPort>();
	}

	
}
