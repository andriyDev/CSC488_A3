package compiler488.ast.expn;

import compiler488.ast.Printable;
import compiler488.codegen.CodeGen;
/**
 * Represents a literal text constant.
 */
public class TextConstExpn extends ConstExpn implements Printable {
	/** The value of this literal. */
	private String value;

	public TextConstExpn(String value) {
		super();

		this.value = value;
	}

	public String getValue() {
		return value;
	}

	/**
	 * Returns a description of the literal text constant.
	 */
	@Override
	public String toString() {
		return "\"" + value + "\"";
	}

	@Override
	public void performCodeGeneration(CodeGen c) {
		c.generateCodeForExpn(52, this);
	}
}
