package compiler488.ast.expn;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.semantics.Semantics;

/**
 * Represents a function call with arguments.
 */
public class FunctionCallExpn extends Expn {
	/** The name of the function. */
	private String ident;

	/** The arguments passed to the function. */
	private ASTList<Expn> arguments;

	public FunctionCallExpn(String ident, ASTList<Expn> arguments) {
		super();

		this.ident = ident;
		this.arguments = arguments;
	}

	public ASTList<Expn> getArguments() {
		return arguments;
	}

	public String getIdent() {
		return ident;
	}

	public void prettyPrint(PrettyPrinter p) {
		p.print(ident);

		if (arguments.size() > 0) {
			p.print("(");
			arguments.prettyPrintCommas(p);
			p.print(")");
		}
	}

	@Override
	public boolean performSemanticAnalysis(Semantics s) {
		boolean result = true;
		if(arguments.size() > 0) {
			result = s.semanticAction(44, this);
			for(Expn ex : arguments) {
				result &= ex.performSemanticAnalysis(s);
				result &= s.semanticAction(36, ex);
				result &= s.semanticAction(45, this);
			}
			result &= s.semanticAction(43, this);
		} else {
			result = s.semanticAction(42, this);
		}
		result &= s.semanticAction(28, this);
		return result;
	}
}
