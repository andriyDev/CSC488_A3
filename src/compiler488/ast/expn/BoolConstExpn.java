package compiler488.ast.expn;

import compiler488.codegen.CodeGen;
import compiler488.runtime.Machine;
import compiler488.semantics.Semantics;

/**
 * Boolean literal constants.
 */
public class BoolConstExpn extends ConstExpn {
	/** The value of the constant */
	private boolean value;

	public BoolConstExpn(boolean value) {
		super();

		this.value = value;
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value ? "true" : "false";
	}

	@Override
	public boolean performSemanticAnalysis(Semantics s) {
		return s.semanticAction(20, this);
	}

	@Override
	public void performCodeGeneration(CodeGen g) {
		g.addInstruction(Machine.PUSH);
		g.addInstruction(value ? 1 : 0);
	}

	@Override
	public short computeConstant() {
		return (short)(value ? 1 : 0);
	}
}
