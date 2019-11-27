package compiler488.ast.expn;

import compiler488.ast.Printable;
import compiler488.codegen.CodeGen;
/**
 * Represents the special literal constant associated with writing a new-line
 * character on the output device.
 */
public class SkipConstExpn extends ConstExpn implements Printable {
	public SkipConstExpn() {
		super();
	}

	@Override
	public String toString() {
		return "newline";
	}

	@Override
	public void performCodeGeneration(CodeGen c) {
		c.generateCodeForExpn(53, this);
	}
}
