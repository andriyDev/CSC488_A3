package compiler488.ast.expn;

import compiler488.ast.Readable;
import compiler488.semantics.Semantics;
import compiler488.symbol.Symbol;

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
	public void performSemanticAnalysis(Semantics s) {
		Symbol sym = s.getScopeSymbol(ident);
		if(sym.type == Symbol.SymbolType.Scalar) {
			s.semanticAction(37, this);
		} else if(sym.type == Symbol.SymbolType.Routine) {
			s.semanticAction(37, this);
			s.semanticAction(42, this);
			s.semanticAction(28, this);
		} else {
			s.semanticAction(57, this);
		}
	}
}
