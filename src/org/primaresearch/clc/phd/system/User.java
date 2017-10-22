package org.primaresearch.clc.phd.system;

import org.primaresearch.clc.phd.ontology.label.HasLabels;

/**
 * Interface for workflow system users
 * @author clc
 *
 */
public interface User extends HasLabels {

	public String getName();
}
