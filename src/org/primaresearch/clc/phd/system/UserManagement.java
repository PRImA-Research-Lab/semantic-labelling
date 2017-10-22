package org.primaresearch.clc.phd.system;

import org.primaresearch.clc.phd.ontology.Ontology;
import org.primaresearch.clc.phd.ontology.label.Label;
import org.primaresearch.clc.phd.ontology.label.LabelGroup.TooManyLabelsInGroupException;


/**
 * Singleton for user-related functionality
 * 
 * @author clc
 *
 */
public class UserManagement {

	private static UserManagement instance = new UserManagement();
	
	private User currentUser;
	
	private UserManagement() {
		
		currentUser = new UserImpl("Christian");
		
		//Add labels
		try {
			Label label = currentUser.addLabel(Ontology.getInstance().getLabelType("user-groups"));
		} catch (TooManyLabelsInGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO
	}
	
	/**
	 * Returns the singleton instance
	 */
	public static UserManagement getInstance() {
		return instance;
	}
	
	/**
	 * Returns the current workflow system user
	 * @return
	 */
	public User getCurrentUser() {
		return currentUser;
	}
}
