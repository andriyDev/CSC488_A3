package compiler488.ast.expn;

import compiler488.ast.Readable;
import compiler488.codegen.CodeGen;
import compiler488.runtime.Machine;
import compiler488.semantics.Semantics;
import compiler488.symbol.Symbol;
import compiler488.Pair;

/**
 * References to a scalar variable or function call without parameters.
 */
public class IdentExpn extends Expn implements Readable {
	/** Name of the identifier. */
	private String ident;

	public IdentExpn(String ident) {
		super();

		this.ident = ident;
	}

	public String getIdent() {
		return ident;
	}

	/**
	 * Returns the name of the variable or function.
	 */
	@Override
	public String toString() {
		return ident;
	}

	@Override
	public boolean performSemanticAnalysis(Semantics s) {
		boolean result;
		Symbol sym = s.getScopeSymbol(ident);
		if(sym == null) {
			result = s.semanticAction(57, this);
		}
		else if(sym.type == Symbol.SymbolType.Scalar) {
			result = s.semanticAction(37, this);
			result = s.semanticAction(26, this);
		} else if(sym.type == Symbol.SymbolType.Routine) {
			result = s.semanticAction(40, this);
			result &= s.semanticAction(42, this);
			result &= s.semanticAction(28, this);
		} else {
			result = s.semanticAction(57, this);
		}
		return result;
	}

	@Override
	public void generateCodeForAccessor(CodeGen g) {
		Pair<Integer, Integer> p = g.getCurrentScope().getSymbolLocation(ident);
		g.addInstruction(Machine.ADDR);
		g.addInstruction(p.getKey());
		g.addInstruction(p.getValue());
	}

	@Override
	public void performCodeGeneration(CodeGen g) {
		if(g.getCurrentScope().getSymbol(ident).type == Symbol.SymbolType.Routine) {
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
		} else {
			generateCodeForAccessor(g);
			g.addInstruction(Machine.LOAD);
		}
	}

	@Override
	public boolean isConstant() {
		return false;
	}
}
