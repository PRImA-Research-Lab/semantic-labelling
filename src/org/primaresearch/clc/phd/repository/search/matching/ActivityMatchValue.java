package org.primaresearch.clc.phd.repository.search.matching;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.primaresearch.clc.phd.workflow.activity.Activity;

/**
 * MatchValue implementation for Activity matching results (has sub values).
 * 
 * @author clc
 *
 */
public class ActivityMatchValue implements MatchValue<Activity> {
	
	private double percentage = 0.0;
	private Activity activity;
	private String description = null;
	private List<MatchValue<?>> subValues = new LinkedList<MatchValue<?>>();
	private int weight = 1;

	public ActivityMatchValue(Activity act, double percentage, int weight, List<MatchValue<?>> subValues) {
		this.activity = act;
		this.percentage = percentage;
		this.weight = weight;
		this.subValues.addAll(subValues);
	}
	
	public ActivityMatchValue(Activity act, double percentage, int weight, String description) {
		this.activity = act;
		this.percentage = percentage;
		this.weight = weight;
		this.description = description;
	}
	
	public ActivityMatchValue(Activity act, double percentage, int weight, String description, List<MatchValue<?>> subValues) {
		this.activity = act;
		this.percentage = percentage;
		this.weight = weight;
		this.description = description;
		this.subValues.addAll(subValues);
	}
	
	@Override
	public double getMatchScore() {
		return percentage;
	}

	@Override
	public int compareTo(MatchValue<Activity> other) {
		return Double.compare(other.getMatchScore(), percentage);
	}

	@Override
	public Activity getObject() {
		return activity;
	}
	
	@Override
	public List<MatchValue<?>> getSubValues() {
		return subValues;
	}

	@Override
	public String getMatchDescription() {
		if (description != null && subValues.isEmpty())
			return description +" ("+ twoDecimalPointsformat.format(percentage)+"%, weight "+getMatchWeight()+")";
		
		//Compose description
		if (subValues.isEmpty())
			return "";
		
		String ret = "";
		if (description != null)
			ret += "<b>"+description+" ("+twoDecimalPointsformat.format(percentage)+"%, weight "+getMatchWeight()+")</b>";
				
		for (Iterator<MatchValue<?>> it=subValues.iterator(); it.hasNext(); ) {
			String txt = it.next().getMatchDescription();
			if (!txt.isEmpty()) {
				if (!ret.isEmpty())
					ret += "\n";
				ret += txt;
			}
		}
		return ret;
	}

	@Override
	public String getCaption() {
		return description;
	}
	
	@Override
	public int getMatchWeight() {
		//No sub values -> always return the local weight (but has to be >= 0)
		if (subValues.isEmpty())
			return weight >= 0 ? weight : 0;
		
		//If the weight is >= 0 return it, otherwise use the sub values
		if (weight >= 0)
			return weight;
		
		int sum = 0;
		for (Iterator<MatchValue<?>> it = subValues.iterator(); it.hasNext(); )
			sum += it.next().getMatchWeight();
		return sum > 0 ? sum : 1; //can never be 0
	}
	
	public void setMatchWeight(int w) {
		this.weight = w;
	}
}