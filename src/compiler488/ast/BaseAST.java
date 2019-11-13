package compiler488.ast;

import compiler488.codegen.CodeGen;
import compiler488.semantics.Semantics;
import compiler488.Pair;

/**
 * Base class implementation for the AST hierarchy.
 *
 * This is a convenient place to add common behaviours.
 *
 * @author Dave Wortman, Marsha Chechik, Danny House, Peter McCormick
 */
public abstract class BaseAST implements AST {
	/**
	 * Default constructor.
	 *
	 * <p>
	 * Add additional information to your AST tree nodes here.
	 * </p>
	 */
	public BaseAST() {
	}

	public int line;
	public int column;

	public void setPosition(int line, int column) {
		this.line = line;
		this.column = column;
	}

	public void setPosition(BaseAST other) {
		Pair<Integer, Integer> i = other.getPosition();
		this.line = i.getKey();
		this.column = i.getValue();
	}

	public Pair<Integer, Integer> getPosition() {
		return new Pair<>(line, column);
	}

	/**
	 * A default pretty-printer implementation that uses <code>toString</code>.
	 *
	 * @param p
	 *            the printer to use
	 */
	@Override
	public void prettyPrint(PrettyPrinter p) {
		p.print(toString());
	}

	public boolean performSemanticAnalysis(Semantics s) { return true; }
	public void performCodeGeneration(CodeGen g) {  }
}
