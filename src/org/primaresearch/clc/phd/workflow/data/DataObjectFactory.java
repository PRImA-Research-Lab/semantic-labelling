package org.primaresearch.clc.phd.workflow.data;

import java.util.Collection;

import org.primaresearch.clc.phd.ontology.Ontology;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.ontology.label.Labels;
import org.primaresearch.clc.phd.workflow.IdGenerator;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.data.port.DataTableColumn;
import org.primaresearch.clc.phd.workflow.data.port.DataTableColumnImpl;
import org.primaresearch.clc.phd.workflow.data.port.IfElseComparisonPort;
import org.primaresearch.clc.phd.workflow.data.port.IfElseConditionPort;
import org.primaresearch.clc.phd.workflow.data.port.IfElsePort;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.InputPortImpl;
import org.primaresearch.clc.phd.workflow.data.port.LoopPort;
import org.primaresearch.clc.phd.workflow.data.port.LoopPortImpl;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPortImpl;

/**
 * Factory for workflow data ports and objects.
 * 
 * @author clc
 *
 */
public class DataObjectFactory {

	private Collection<LabelGroup> allowedLabels;
	private IdGenerator idRegister;
	
	public DataObjectFactory(IdGenerator idRegister) {
		this.idRegister = idRegister;
		initLabelTemplate();
	}
	
	private void initLabelTemplate() {
		//Get allowed label slots from the ontology
		allowedLabels = Ontology.getInstance().getDataObjectLabelSlots();
		
		//CC: Old static label slots:
		//allowedLabels = new LinkedList<LabelGroup>();
		//Processing Step
		//allowedLabels.add(new LabelGroup(	Ontology.getInstance().getRootType(Ontology.DefaultRootType.PROCESSING_STEP),
		//									Integer.MAX_VALUE));
		//Application
		//allowedLabels.add(new LabelGroup(	Ontology.getInstance().getRootType(Ontology.DefaultRootType.APPLICATION),
		//									1));
	}
	
	public OutputPort createOutputPort(Activity activity) {
		OutputPort port = new OutputPortImpl(activity, createSingleDataObject(), idRegister);
		return port;
	}
	
	public DataTableColumn createDataTableColumn() {
		DataTableColumn port = new DataTableColumnImpl(null, createDataCollection(), idRegister);
		return port;
	}

	public OutputPort createOutputPort(Activity activity, DataObject dataObject) {
		OutputPort port = new OutputPortImpl(activity, dataObject, idRegister);
		return port;
	}
	
	public InputPort createInputPort(Activity activity) {
		InputPortImpl port = new InputPortImpl(activity, createSingleDataObject(), idRegister);
		return port;
	}
	
	public InputPort createInputPort(Activity activity, DataObject dataObject) {
		InputPortImpl port = new InputPortImpl(activity, dataObject, idRegister);
		return port;
	}
	
	public LoopPort createLoopPort(Activity activity) {
		LoopPort port = new LoopPortImpl(activity, createSingleDataObject(), idRegister);
		return port;
	}
	
	public SingleDataObject createSingleDataObject() {
		SingleDataObject obj = new SingleDataObject(new Labels(allowedLabels));
		return obj;
	}
	
	public DataCollection createDataCollection() {
		DataCollection c = new DataCollectionImpl(new Labels(allowedLabels));
		return c;
	}
	
	public IfElsePort createIfElseConditionPort(Activity activity) {
		return new IfElseConditionPort(activity, createSingleDataObject(), idRegister);
	}
	
	public IfElsePort createIfElseComparisonPort(Activity activity) {
		return new IfElseComparisonPort(activity, createSingleDataObject(), idRegister);
	}
}
