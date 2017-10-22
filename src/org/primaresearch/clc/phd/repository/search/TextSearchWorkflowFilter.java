package org.primaresearch.clc.phd.repository.search;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;

/**
 * Filter that looks for a search string with workflow and root activity meta data
 * 
 * @author clc
 *
 */
public class TextSearchWorkflowFilter implements WorkflowFilter {

	private String searchString;
	
	@Override
	public Collection<Workflow> filterWorkflows(Collection<Workflow> toBeFiltered) {
		
		if (searchString == null || searchString.isEmpty())
			return toBeFiltered;
		
		Collection<Workflow> ret = new LinkedList<Workflow>();
		
		for (Iterator<Workflow> it = toBeFiltered.iterator(); it.hasNext(); ) {
			Workflow workflow = it.next();
			if (workflow == null)
				continue;
			
			//Workflow name
			if (searchForText(workflow.getName())) {
				ret.add(workflow);
				continue;
			}
			//Workflow description
			if (searchForText(workflow.getDescription(0).getText())) {
				ret.add(workflow);
				continue;
			}
			//Root activity
			Activity act = workflow.getRootActivity();
			if (act == null)
				continue;
			//Activity caption
			if (searchForText(act.getCaption())) {
				ret.add(workflow);
				continue;
			}
			//Activity description
			if (searchForText(act.getDescription())) {
				ret.add(workflow);
				continue;
			}
			
		}
		
		return ret;
	}

	@Override
	public void init(Collection<Workflow> allWorkflows) {
		// TODO Auto-generated method stub

	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString.toLowerCase();
	}

	private boolean searchForText(String wholeText) {
		if (wholeText == null)
			return false;
		return wholeText.toLowerCase().indexOf(searchString) >= 0;
	}
}
