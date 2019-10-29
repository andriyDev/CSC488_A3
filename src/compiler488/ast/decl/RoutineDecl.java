package compiler488.ast.decl;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.stmt.Scope;
import compiler488.ast.type.Type;
import compiler488.semantics.Semantics;

/**
 * Represents the declaration of a function or procedure.
 */
public class RoutineDecl extends Declaration {
	/**
	 * The formal parameters for this routine (if any.)
	 *
	 * <p>
	 * This value must be non-<code>null</code>. If absent, use an empty list
	 * instead.
	 * </p>
	 */
	private ASTList<ScalarDecl> parameters = new ASTList<ScalarDecl>();

	/** The body of this routine (if any.) */
	private Scope body = null;

	/**
	 * Construct a function with parameters, and a definition of the body.
	 *
	 * @param name
	 *            Name of the routine
	 * @param type
	 *            Type returned by the function
	 * @param parameters
	 *            List of parameters to the routine
	 * @param body
	 *            Body scope for the routine
	 */
	public RoutineDecl(String name, Type type, ASTList<ScalarDecl> parameters, Scope body) {
		super(name, type);

		this.parameters = parameters;
		this.body = body;
	}

	/**
	 * Construct a function with no parameters, and a definition of the body.
	 *
	 * @param name
	 *            Name of the routine
	 * @param type
	 *            Type returned by the function
	 * @param body
	 *            Body scope for the routine
	 */
	public RoutineDecl(String name, Type type, Scope body) {
		this(name, type, new ASTList<ScalarDecl>(), body);
	}

	/**
	 * Construct a procedure with parameters, and a definition of the body.
	 *
	 * @param name
	 *            Name of the routine
	 * @param parameters
	 *            List of parameters to the routine
	 * @param body
	 *            Body scope for the routine
	 */
	public RoutineDecl(String name, ASTList<ScalarDecl> parameters, Scope body) {
		this(name, null, parameters, body);

		this.parameters = parameters;
		this.body = body;
	}

	/**
	 * Construct a procedure with no parameters, and a definition of the body.
	 *
	 * @param name
	 *            Name of the routine
	 * @param body
	 *            Body scope for the routine
	 */
	public RoutineDecl(String name, Scope body) {
		this(name, null, new ASTList<ScalarDecl>(), body);
	}

	public ASTList<ScalarDecl> getParameters() {
		return parameters;
	}

	public Scope getBody() {
		return body;
	}

	@Override
	public void prettyPrint(PrettyPrinter p) {
		if (type == null) {
			p.print("procedure ");
		} else {
			p.print(" function ");
		}

		p.print(name);

		if (parameters.size() > 0) {
			p.print("(");
			parameters.prettyPrintCommas(p);
			p.print(")");
		}
		/* print type of function */
		if (type != null) {
			p.print(" : ");
			type.prettyPrint(p);
		}
		if (body != null) {
			p.print(" ");
			body.prettyPrint(p);
		}
	}

	@Override
	public boolean performSemanticAnalysis(Semantics s) {
		boolean result = true;
		if(type == null) {
			if(parameters.size() == 0) {
				result = s.semanticAction(17, this);
				result &= s.semanticAction(8, this);
				result &= s.semanticAction(13, this);
				result &= body.performStatementSemanticAnalysis(s);
				result &= s.semanticAction(9, this);
			} else {
				result = s.semanticAction(8, this);
				result &= s.semanticAction(14, this);
				for(ScalarDecl decl : parameters) {
					result &= s.semanticAction(15, decl);
					result &= s.semanticAction(16, this);
				}
				result &= s.semanticAction(18, this);
				result &= s.semanticAction(13, this);
				result &= body.performStatementSemanticAnalysis(s);
				result &= s.semanticAction(9, this);
			}
		} else {
			if(parameters.size() == 0) {
				result = s.semanticAction(11, this);
				result &= s.semanticAction(4, this);
				result &= s.semanticAction(13, this);
				result &= body.performStatementSemanticAnalysis(s);
				result &= s.semanticAction(5, this);
				result &= s.semanticAction(54, body);
			} else {
				result = s.semanticAction(4, this);
				result &= s.semanticAction(14, this);
				for(ScalarDecl decl : parameters) {
					result &= s.semanticAction(15, decl);
					result &= s.semanticAction(16, this);
				}
				result &= s.semanticAction(12, this);
				result &= s.semanticAction(13, this);
				result &= body.performStatementSemanticAnalysis(s);
				result &= s.semanticAction(5, this);
				result &= s.semanticAction(54, body);
			}
		}
		return result;
	}
}
