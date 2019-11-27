package compiler488.ast.expn;


import compiler488.semantics.Semantics;
import compiler488.codegen.CodeGen;

/** Represents a conditional expression (i.e., x>0?3:4). */
public class ConditionalExpn extends Expn {
	private Expn condition; // Evaluate this to decide which value to yield.

	private Expn trueValue; // The value is this when the condition is true.

	private Expn falseValue; // Otherwise, the value is this.

	public ConditionalExpn(Expn condition, Expn trueValue, Expn falseValue) {
		this.condition = condition;
		this.trueValue = trueValue;
		this.falseValue = falseValue;
	}

	/** Returns a string that describes the conditional expression. */
	@Override
	public String toString() {
		return "(" + condition + " ? " + trueValue + " : " + falseValue + ")";
	}

	public Expn getCondition() {
		return condition;
	}

	public void setCondition(Expn condition) {
		this.condition = condition;
	}

	public Expn getFalseValue() {
		return falseValue;
	}

	public void setFalseValue(Expn falseValue) {
		this.falseValue = falseValue;
	}

	public Expn getTrueValue() {
		return trueValue;
	}

	public void setTrueValue(Expn trueValue) {
		this.trueValue = trueValue;
	}

	@Override
	public boolean performSemanticAnalysis(Semantics s) {
		boolean result;
		result = condition.performSemanticAnalysis(s);
		result &= s.semanticAction(30, condition);
		result &= trueValue.performSemanticAnalysis(s);
		result &= falseValue.performSemanticAnalysis(s);
		result &= s.semanticAction(33, this);
		result &= s.semanticAction(24, this);
		return result;
	}

	@Override
	public void performCodeGeneration(CodeGen c) {
		// TODO
	}
}
