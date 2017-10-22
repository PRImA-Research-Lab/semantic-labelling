package org.primaresearch.clc.phd.repository.search.matching;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Interface template for workflow component matching.
 * @author clc
 *
 * @param <T>
 */
public interface MatchValue<T> extends Comparable<MatchValue<T>>{

	public static DecimalFormat twoDecimalPointsformat = new DecimalFormat(".##");
	
	/**
	 * Returns the match score (percentage)
	 */
	public double getMatchScore();
	
	/**
	 * Returns details for the match
	 */
	public String getMatchDescription();
	
	/**
	 * Returns a short description for the match
	 */
	public String getCaption();
	
	/**
	 * Returns a map with match scores and match descriptions (why this particular score).
	 */
	public List<MatchValue<?>> getSubValues();
	
	/**
	 * Returns the object to match
	 */
	public T getObject();
	
	/**
	 * A weight that can be used to combine this match value with another one
	 * @return Integer >= 0 
	 */
	public int getMatchWeight();
}
