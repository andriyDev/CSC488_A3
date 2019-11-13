package compiler488.ast.stmt;

import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.Expn;
import compiler488.codegen.CodeGen;
import compiler488.runtime.Machine;
import compiler488.semantics.Semantics;
import compiler488.ast.Readable;

/**
 * Holds the assignment of an expression to a variable.
 */
public class AssignStmt extends Stmt {
	/** The location being assigned to. */
	private Expn lval;

	/** The value being assigned. */
	private Expn rval;

	public AssignStmt(Expn lval, Expn rval) {
		super();

		this.lval = lval;
		this.rval = rval;
	}

	public Expn getLval() {
		return lval;
	}

	public Expn getRval() {
		return rval;
	}

	@Override
	public void prettyPrint(PrettyPrinter p) {
		lval.prettyPrint(p);
		p.print(" : = ");
		rval.prettyPrint(p);
	}

	@Override
	public boolean performSemanticAnalysis(Semantics s) {
		boolean result;
		result = lval.performSemanticAnalysis(s);
		result &= rval.performSemanticAnalysis(s);
		result &= s.semanticAction(34, this);
		result &= s.semanticAction(59, lval);
		return result;
	}

	@Override
	public void performCodeGeneration(CodeGen g) {
		if(lval instanceof Readable) {
			((Readable)lval).generateCodeForAccessor(g);
		} else {
			throw new RuntimeException("lval is not assignable!");
		}
		rval.performCodeGeneration(g);
		g.addInstruction(Machine.STORE);
	}
}
