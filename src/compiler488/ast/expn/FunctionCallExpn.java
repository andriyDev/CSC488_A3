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
	public void performSemanticAnalysis(Semantics s) {
		if(arguments.size() > 0) {
			s.semanticAction(44, this);
			for(Expn ex : arguments) {
				ex.performSemanticAnalysis(s);
				s.semanticAction(36, ex);
				s.semanticAction(45, this);
			}
			s.semanticAction(43, this);
		} else {
			s.semanticAction(42, this);
		}
		s.semanticAction(28, this);
	}
}
