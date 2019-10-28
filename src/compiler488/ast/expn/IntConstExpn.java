package compiler488.ast.expn;

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
}
