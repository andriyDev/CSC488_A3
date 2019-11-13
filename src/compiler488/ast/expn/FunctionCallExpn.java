package compiler488.ast.expn;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.codegen.CodeGen;
import compiler488.runtime.Machine;
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
			ex.performCodeGeneration(g);
		}
		g.addInstruction(Machine.PUSH);
		int routineAddress = g.getRoutineAddress(g.getCurrentScope().getSymbol(ident), g.getPosition());
		g.addInstruction(routineAddress); // Note that this routine address may be -1, but it will be overwritten once the routine is generated.
		g.addInstruction(Machine.BR); // Jump to the function
		g.setInstruction(addressAfterBranch, g.getPosition());
		// We need to swap for function calls
		g.addInstruction(Machine.SWAP);

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
