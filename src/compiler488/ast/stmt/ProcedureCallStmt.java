package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.Expn;
import compiler488.semantics.Semantics;

/**
 * Represents calling a procedure.
 */
public class ProcedureCallStmt extends Stmt {
	/** The name of the procedure being called. */
	private String name;

	/**
	 * The arguments passed to the procedure (if any.)
	 *
	 * <p>
	 * This value must be non-<code>null</code>. If the procedure takes no
	 * parameters, represent that with an empty list here instead.
	 * </p>
	 */
	private ASTList<Expn> arguments;

	public ProcedureCallStmt(String name, ASTList<Expn> arguments) {
		super();

		this.name = name;
		this.arguments = arguments;
	}

	public ProcedureCallStmt(String name) {
		this(name, new ASTList<Expn>());
	}

	public String getName() {
		return name;
	}

	public ASTList<Expn> getArguments() {
		return arguments;
	}

	@Override
	public void prettyPrint(PrettyPrinter p) {
		p.print(name);

		if ((arguments != null) && (arguments.size() > 0)) {
			p.print("(");
			arguments.prettyPrintCommas(p);
			p.print(")");
		}
	}

	@Override
	public void performSemanticAnalysis(Semantics s) {
		if(arguments.size() > 0) {
			s.semanticAction(44, this);
			for(Expn argument : arguments) {
				argument.performSemanticAnalysis(s);
				// These are reordered so as not to mess up the argument count
				s.semanticAction(36, argument);
				s.semanticAction(45, this);
			}
			s.semanticAction(43, this);
		} else {
			s.semanticAction(42, this);
		}
	}
}
