package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.Expn;
import compiler488.codegen.CodeGen;
import compiler488.runtime.Machine;
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
	public boolean performSemanticAnalysis(Semantics s) {
		boolean result = true;
		if(arguments.size() > 0) {
			for(Expn argument : arguments) {
				result &= argument.performSemanticAnalysis(s);
			}
			result &= s.semanticAction(44, this);
			for(Expn argument : arguments) {
				// These are reordered so as not to mess up the argument count
				result &= s.semanticAction(36, argument);
				result &= s.semanticAction(45, this);
			}
			result &= s.semanticAction(43, this);
		} else {
			result = s.semanticAction(42, this);
		}
		return result;
	}

	@Override
	public void performCodeGeneration(CodeGen g) {
		int ll = g.getCurrentLexicalLevel();
		// Creating the activation record
		g.addInstruction(Machine.ADDR);
		g.addInstruction(ll);
		g.addInstruction(0);
		g.addInstruction(Machine.PUSH);
		int addressAfterBranch = g.getPosition();
		g.addInstruction(0); // Space to put the address after branch
		g.addInstruction(Machine.PUSH);
		g.addInstruction(0); // Space for static link
		// Parameters
		for(Expn ex : arguments) {
			ex.attemptConstantFolding(g);
		}
		g.addInstruction(Machine.PUSH);
		int routineAddress = g.getRoutineAddress(g.getCurrentScope().getSymbol(name), g.getPosition());
		g.addInstruction(routineAddress); // Note that this routine address may be -1, but it will be overwritten once the routine is generated.
		g.addInstruction(Machine.BR); // Jump to the function
		g.setInstruction(addressAfterBranch, g.getPosition());

		// Display Management Strategy
		for(int i = ll; i >= 0; i--) {
			if(i != ll) {
				g.addInstruction(Machine.ADDR);
				g.addInstruction(i + 1);
				g.addInstruction(-1);
			}
			g.addInstruction(Machine.SETD);
			g.addInstruction(i);
		}
	}
}
