package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.Readable;
import compiler488.ast.expn.Expn;
import compiler488.semantics.Semantics;

/**
 * The command to read data into one or more variables.
 */
public class ReadStmt extends Stmt {
	/** A list of locations to put the values read. */
	private ASTList<Readable> inputs;

	public ReadStmt(ASTList<Readable> inputs) {
		super();
		this.inputs = inputs;
	}

	@Override
	public void prettyPrint(PrettyPrinter p) {
		p.print("read ");
		inputs.prettyPrintCommas(p);
	}

	@Override
	public boolean performSemanticAnalysis(Semantics s) {
		boolean result = true;
		for(Readable element : inputs) {
			if(element instanceof Expn) {
				result &= element.performSemanticAnalysis(s);
				result &= s.semanticAction(31, element);
				result &= s.semanticAction(59, element);
			}
		}
		return result;
	}

	public ASTList<Readable> getInputs() {
		return inputs;
	}
}
