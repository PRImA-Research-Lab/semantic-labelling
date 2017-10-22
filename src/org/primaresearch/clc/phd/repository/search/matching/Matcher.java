package org.primaresearch.clc.phd.repository.search.matching;

import java.util.Collection;
import java.util.List;

/**
 * Matches a collection of objects against certain criteria and returns a match percentage.
 * @author clc
 *
 * @param <T>
 */
public interface Matcher<T> {

	public static final int MATCHING_FOR_REPLACING = 1;
	public static final int MATCHING_FOR_ADDING_CHILD = 2;

	public List<MatchValue<T>> match(Collection<T> objectsToMatch);
	
	public String getCaption();
}
