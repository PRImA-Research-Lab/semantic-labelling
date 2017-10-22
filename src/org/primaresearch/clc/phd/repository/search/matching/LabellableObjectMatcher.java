package org.primaresearch.clc.phd.repository.search.matching;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.primaresearch.clc.phd.ontology.label.HasLabels;
import org.primaresearch.clc.phd.ontology.label.Label;
import org.primaresearch.clc.phd.ontology.label.LabelGroup;
import org.primaresearch.clc.phd.ontology.label.LabelType;

/**
 * Semantic label based matcher
 * @author clc
 *
 */
public class LabellableObjectMatcher implements Matcher<HasLabels> {
	
	private HasLabels referenceObject;
	
	/**
	 * Constructor
	 * @param referenceObject
	 */
	public LabellableObjectMatcher(HasLabels referenceObject) {
		this.referenceObject = referenceObject;
	}

	@Override
	public List<MatchValue<HasLabels>> match(Collection<HasLabels> objectsToMatch) {
		
		List<MatchValue<HasLabels>> ret = new LinkedList<MatchValue<HasLabels>>();
		
		//TODO
		for (Iterator<HasLabels> it=objectsToMatch.iterator(); it.hasNext(); ) {
			ret.add(match(it.next()));
		}
		
		return ret;
	}
	
	public MatchValue<HasLabels> match(HasLabels objectToMatch) {
		
		Collection<LabelGroup> labelGroups = referenceObject.getLabels();
		
		List<LabelMatchValue> subValues = new LinkedList<LabelMatchValue>();
		double avg = 0.0;
		int count = 0;
		for (Iterator<LabelGroup> it = labelGroups.iterator(); it.hasNext(); ) {
			LabelGroup labelGroup = it.next();
			List<Label> labels = labelGroup.getLabels();
			
			for (Iterator<Label> itLabel = labels.iterator(); itLabel.hasNext(); ) {
				Label refLabel = itLabel.next();
				
				LabelMatchValue matchLevel = matchLabel(refLabel, objectToMatch);
				count++;
				if (matchLevel != null) {
					subValues.add(matchLevel);
					avg += matchLevel.getMatchScore();
				} 
				else
					subValues.add(new LabelMatchValue(objectToMatch, 0.0, "No match for '"+refLabel.getType().getCaption()+"'"));
			}
		}
		
		if (count > 0)
			avg /= (double)count;
		
		return new LabelMatchValue(objectToMatch, avg, subValues);
	}
	
	/**
	 * Checks how well the given reference label matches the given other labels
	 * @param refLabel
	 * @param objectToMatch
	 * @return Best match
	 */
	private LabelMatchValue matchLabel(Label refLabel, HasLabels objectToMatch) {
		Collection<LabelGroup> labelGroups = objectToMatch.getLabels();
		
		double maxScore = 0.0;
		LabelMatchValue maxVal = null;
		for (Iterator<LabelGroup> it = labelGroups.iterator(); it.hasNext(); ) {
			LabelGroup labelGroup = it.next();
			List<Label> labels = labelGroup.getLabels();
			
			for (Iterator<Label> itLabel = labels.iterator(); itLabel.hasNext(); ) {
				Label labelToMatch = itLabel.next();
				LabelMatchValue matchLevel = matchLabel(refLabel.getType(), labelToMatch.getType());
				if (matchLevel.getMatchScore() >= maxScore) {
					maxScore = matchLevel.getMatchScore();
					maxVal = matchLevel;
				}
			}
		}
		return maxVal;
	}

	/**
	 * Checks how well the given reference label matches the given other label
	 * @param refLabel
	 * @param objectToMatch
	 * @return Best match
	 */
	private LabelMatchValue matchLabel(LabelType refLabelType, LabelType typeOfLabelToMatch) {
		String labelTypes = refLabelType.getCaption() + " <-> " + typeOfLabelToMatch;
				
		//Root type (no match)
		if (refLabelType.getParent() == null)
			return new LabelMatchValue(null, 0.0, labelTypes + ": no matching label type");
			
		//Check if equal (100% match)
		if (refLabelType.equals(typeOfLabelToMatch))
			return new LabelMatchValue(null, 100.0, labelTypes + ": full match");
			
		//Check if different root type (no match)
		if (!refLabelType.getRootType().equals(typeOfLabelToMatch.getRootType()))
			return new LabelMatchValue(null, 0.0, refLabelType.getCaption() + " <-> - : no matching label type");
		
		//Check if reference label is subtype of other label (no match)
		if (refLabelType.isSubtypeOf(typeOfLabelToMatch))
			return new LabelMatchValue(null, 0.0, refLabelType.getCaption() + " <-> - : no matching label type");
		
		//Recursion
		return new LabelMatchValue(null, matchLabel(refLabelType.getParent(), typeOfLabelToMatch).getMatchScore() - 10.0, 
				labelTypes + ": partial match");
	}
	
	@Override
	public String getCaption() {
		return "Labellable object matcher";
	}

	
	/**
	 * MatchValue implementation for Label.
	 * @author clc
	 *
	 */
	public static class LabelMatchValue implements MatchValue<HasLabels> {
		
		private double percentage = 0.0;
		private String description = null;
		private HasLabels object;
		private List<MatchValue<?>> subValues = new LinkedList<MatchValue<?>>();

		public LabelMatchValue(HasLabels object, double percentage, String matchDescription) {
			this.object = object;
			this.percentage = percentage;
			description = matchDescription;
		}
		
		public LabelMatchValue(HasLabels object, double percentage, List<LabelMatchValue> subValues) {
			this.object = object;
			this.percentage = percentage;
			this.subValues.addAll(subValues);
		}
		
		@Override
		public double getMatchScore() {
			return percentage;
		}
		
		@Override
		public HasLabels getObject() {
			return object;
		}

		@Override
		public int compareTo(MatchValue<HasLabels> other) {
			return Double.compare(other.getMatchScore(), percentage);
		}

		@Override
		public List<MatchValue<?>> getSubValues() {
			return subValues;
		}

		@Override
		public String getMatchDescription() {
			if (description != null && subValues.isEmpty())
				return twoDecimalPointsformat.format(getMatchScore()) + "%  " + description;
			
			//Compose description
			if (subValues.isEmpty())
				return "";
			String ret = "";
			for (Iterator<MatchValue<?>> it=subValues.iterator(); it.hasNext(); ) {
				MatchValue<?> matchVal = it.next();
				String txt = matchVal.getMatchDescription();
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
			// TODO Auto-generated method stub
			return 0;
		}
	}

}
