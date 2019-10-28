package compiler488.ast.stmt;

import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.Expn;
import compiler488.semantics.Semantics;

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
		return result;
	}
}
