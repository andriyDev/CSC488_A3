package compiler488.ast.expn;


import compiler488.codegen.CodeGen;
import compiler488.runtime.Machine;
import compiler488.semantics.Semantics;

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
	public void performCodeGeneration(CodeGen g) {
		condition.performCodeGeneration(g);
		g.addInstruction(Machine.PUSH);
		int addrOfFalseExprFillIn = g.getPosition();
		g.addInstruction(0); // Temporary to be filled in later
		g.addInstruction(Machine.BF);
		trueValue.performCodeGeneration(g);
		g.addInstruction(Machine.PUSH);
		int addrOfAfterFalseExprFillIn = g.getPosition();
		g.addInstruction(0); // Temporary to be filled in later
		g.addInstruction(Machine.BR);
		g.setInstruction(addrOfFalseExprFillIn, g.getPosition()); // Fill in the address
		falseValue.performCodeGeneration(g);
		g.setInstruction(addrOfAfterFalseExprFillIn, g.getPosition()); // Fill in the address
	}

	@Override
	public boolean isConstant() {
		if(!condition.getCachedIsConstant()) {
			return false;
		}
		boolean val = condition.getCachedConstantValue() == 1;
		if(val) {
			return trueValue.getCachedIsConstant();
		} else {
			return falseValue.getCachedIsConstant();
		}
	}

	@Override
	public short computeConstant() {
		boolean val = condition.getCachedConstantValue() == 1;
		if(val) {
			return trueValue.getCachedConstantValue();
		} else {
			return falseValue.getCachedConstantValue();
		}
	}
}
