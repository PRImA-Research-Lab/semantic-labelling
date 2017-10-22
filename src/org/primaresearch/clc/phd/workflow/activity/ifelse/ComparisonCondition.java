package org.primaresearch.clc.phd.workflow.activity.ifelse;

import org.primaresearch.clc.phd.workflow.data.port.InputPort;

/**
 * Implementation of condition that compares two values using a specified operator
 * 
 * @author clc
 *
 */
public class ComparisonCondition implements IfCondition {

	private InputPort leftOperand;
	private InputPort rightOperand;
	private ComparisonConditionOperator operator;
	
	/**
	 * Constructor 
	 */
	public ComparisonCondition(InputPort leftOperand, ComparisonConditionOperator operator, InputPort rightOperand) {
		this.leftOperand = leftOperand;
		this.operator = operator;
		this.rightOperand = rightOperand;
	}
	
	/**
	 * Constructor for deferred operand and operator addition (use setLeftOperand, setRightOperand and setOperator)
	 * @param operator
	 */
	public ComparisonCondition() {
	}
	
	public void setLeftOperand(InputPort operand) {
		this.leftOperand = operand;
	}

	public void setRightOperand(InputPort operand) {
		this.rightOperand = operand;
	}
	
	public void setOperator(ComparisonConditionOperator operator) {
		this.operator = operator;
	}
		
	public InputPort getLeftOperand() {
		return leftOperand;
	}

	public InputPort getRightOperand() {
		return rightOperand;
	}

	public ComparisonConditionOperator getOperator() {
		return operator;
	}

	@Override
	public boolean IsTrue() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public IfCondition clone() {
		ComparisonCondition copy = new ComparisonCondition();
		if (leftOperand != null)
			copy.setLeftOperand(leftOperand.clone());
		if (rightOperand != null)
			copy.setLeftOperand(rightOperand.clone());
		copy.setOperator(operator);
		return copy;
	}
	
	
	public static class ComparisonConditionOperator {
		
		public static ComparisonConditionOperator Equals = new ComparisonConditionOperator("equals");
		public static ComparisonConditionOperator NotEquals = new ComparisonConditionOperator("notEquals");
		public static ComparisonConditionOperator LessThan = new ComparisonConditionOperator("lessThan");
		public static ComparisonConditionOperator LessOrEqual = new ComparisonConditionOperator("lessOrEqual");
		public static ComparisonConditionOperator GreaterThan = new ComparisonConditionOperator("greaterThan");
		public static ComparisonConditionOperator GreaterOrEqual = new ComparisonConditionOperator("greaterOrEqual");
		
		/**
		 * Returns an operator type that matches the given ID
		 */
		public static ComparisonConditionOperator getById(String opId) {
			if ("equals".equals(opId))
				return Equals;
			else if ("notEquals".equals(opId))
				return NotEquals;
			else if ("lessThan".equals(opId))
				return LessThan;
			else if ("lessOrEqual".equals(opId))
				return LessOrEqual;
			else if ("greaterThan".equals(opId))
				return GreaterThan;
			else if ("greaterOrEqual".equals(opId))
				return GreaterOrEqual;
			return Equals; //Fall-back
		}
		
		
		private String id;
		
		public ComparisonConditionOperator(String id) {
			this.id = id;
		}
		
		public final String toString() {
			return id;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof ComparisonConditionOperator) {
				return id.equals(((ComparisonConditionOperator)other).id);
			}
			return false;
		}
	}

}
