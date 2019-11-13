package compiler488.ast.expn;

import compiler488.codegen.CodeGen;
import compiler488.runtime.Machine;
import compiler488.semantics.Semantics;

/**
 * Represents a literal integer constant.
 */
public class IntConstExpn extends ConstExpn {
	/**
	 * The value of this literal.
	 */
	private Integer value;

	public IntConstExpn(Integer value) {
		super();

		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public boolean performSemanticAnalysis(Semantics s) {
		return s.semanticAction(21, this);
	}

	@Override
	public void performCodeGeneration(CodeGen g) {
		g.addInstruction(Machine.PUSH);
		g.addInstruction(value);
	}
}
