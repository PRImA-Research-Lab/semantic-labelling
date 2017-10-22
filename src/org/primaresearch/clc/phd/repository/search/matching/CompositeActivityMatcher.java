package org.primaresearch.clc.phd.repository.search.matching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.primaresearch.clc.phd.workflow.activity.Activity;

/**
 * Matcher combining multiple sub-matchers
 * 
 * @author clc
 *
 */
public class CompositeActivityMatcher implements Matcher<Activity> {

	private List<Matcher<Activity>> matchers = new ArrayList<Matcher<Activity>>();
	private List<Boolean> strictnessSwitches = new ArrayList<Boolean>();
	
	/**
	 * Adds a sub-matcher (non-strict)
	 * @param matcher
	 */
	public void addSubMatcher(Matcher<Activity> matcher) {
		addSubMatcher(matcher, false);
	}

	/**
	 * Adds a sub-matcher
	 * @param matcher
	 * @param strict If set to <code>true</code> Non-matches (0.0) of this sub-matcher will result in non-matches of this whole composite matcher
	 */
	public void addSubMatcher(Matcher<Activity> matcher, boolean strict) {
		matchers.add(matcher);
		strictnessSwitches.add(strict);
	}
	
	@Override
	public List<MatchValue<Activity>> match(Collection<Activity> objectsToMatch) {

		long start = System.nanoTime();
		List<MatchValue<Activity>> ret = new LinkedList<MatchValue<Activity>>();
		
		//Run sub-matchers
		Collection<Iterator<MatchValue<Activity>>> submatcherResultsIterators = new LinkedList<Iterator<MatchValue<Activity>>>();
		for (Iterator<Matcher<Activity>> itSubMatcher = matchers.iterator(); itSubMatcher.hasNext(); ) {
			Matcher<Activity> submatcher = itSubMatcher.next();
			List<MatchValue<Activity>> submatcherResult = submatcher.match(objectsToMatch);
			submatcherResultsIterators.add(submatcherResult.iterator());
		}
		
		//Combine results
		for (Iterator<Activity> it = objectsToMatch.iterator(); it.hasNext(); ) {
			Activity actToMatch = it.next();
			
			List<MatchValue<?>> subValues = new LinkedList<MatchValue<?>>();
			double totalScore = 0.0;
			double weightSum = 0.0;
			int count = 0;
			for (Iterator<Iterator<MatchValue<Activity>>> itSubResIts = submatcherResultsIterators.iterator(); itSubResIts.hasNext(); ) {
				Iterator<MatchValue<Activity>> subResIt = itSubResIts.next();
				MatchValue<Activity> matchValue = subResIt.next();
				subValues.add(matchValue);
				double score = matchValue.getMatchScore();
				
				//If strict, 0.0 sub-score result in 0.0 total score
				if (strictnessSwitches.get(count) && score == 0.0) {
					totalScore = 0.0;
					break;
				}
				totalScore += score * (double)matchValue.getMatchWeight();
				weightSum += (double)matchValue.getMatchWeight();
				count++;
			}
			if (weightSum > 0.0)
				totalScore /= (double)weightSum; //Weighted average
			
			ret.add(new ActivityMatchValue(actToMatch, totalScore, 1, "Combined", subValues));
		}
		
		long end = System.nanoTime();
		long nanoseconds = end - start;
		System.out.println(nanoseconds);

		return ret;
	}

	@Override
	public String getCaption() {
		return "Composite activity matcher";
	}

}
