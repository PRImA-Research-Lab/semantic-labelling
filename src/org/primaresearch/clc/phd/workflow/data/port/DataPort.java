package org.primaresearch.clc.phd.workflow.data.port;

import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.data.DataObject;

/**
 * Interface for data ports (input or output)
 * 
 * @author clc
 *
 */
public interface DataPort {
	
	public DataPort clone();
	
	public Activity getActivity();
	
	public DataObject getDataObject();
	public void setDataObject(DataObject dataObject);

	public String getId();
	public void setId(String id);
	
	/**
	 * The collection position is used for input as well as output ports.<br>
	 * For input ports it provides the position that is to be used to extract 
	 * a single data item from a source port that returns a data collection.<br>
	 * For output ports it specifies the position where to put a single
	 * data item that is returned from a forwarded port and the output port 
	 * is a data collection.
	 */
	public DataPort getCollectionPositionProvider();

	/**
	 * The collection position is used for input as well as output ports.<br>
	 * For input ports it provides the position that is to be used to extract 
	 * a single data item from a source port that returns a data collection.<br>
	 * For output ports it specifies the position where to put a single
	 * data item that is returned from a forwarded port and the output port 
	 * is a data collection.
	 */
	public void setCollectionPositionProvider(DataPort port);
}
