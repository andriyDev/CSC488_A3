package compiler488.ast.stmt;

import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.Expn;
import compiler488.codegen.CodeGen;
import compiler488.runtime.Machine;
import compiler488.semantics.Semantics;

/**
 * The command to return from a function.
 */
public class ReturnStmt extends Stmt {
	/* The value to be returned by the function (if any.) */
	private Expn value = null;

	/**
	 * Construct a function <code>return <em>value</em></code> statement with a
	 * value expression.
	 *
	 * @param value
	 *            AST for the return expression
	 */
	public ReturnStmt(Expn value) {
		super();

		this.value = value;
	}

	/**
	 * Construct a procedure <code>return</code> statement (with no return
	 * value)
	 */
	public ReturnStmt() {
		this(null);
	}

	public Expn getValue() {
		return value;
	}

	public void prettyPrint(PrettyPrinter p) {
		p.print("return");

		if (value != null) {
			p.print(" with ");
			value.prettyPrint(p);
		}
	}

	@Override
	public boolean performSemanticAnalysis(Semantics s) {
		boolean result;
		if(value != null) {
			result = value.performSemanticAnalysis(s);
			result &= s.semanticAction(51, this);
			result &= s.semanticAction(35, value);
		}
		else {
			result = s.semanticAction(52, this);
		}
		return result;
	}

	@Override
	public boolean hasReturn() {
		return true;
	}

	@Override
	public void performCodeGeneration(CodeGen g) {
		if(value != null) {
			value.attemptConstantFolding(g);
		}
		g.addInstruction(Machine.PUSH);
		g.awaitExitCode(g.getPosition());
		g.addInstruction(0); // Space for exit code address
		g.addInstruction(Machine.BR);
	}
}
