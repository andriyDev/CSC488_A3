package compiler488.ast;

import compiler488.codegen.CodeGen;
import compiler488.semantics.Semantics;
import compiler488.Pair;

/**
 * Common interface for all Abstract Syntax Tree nodes.
 *
 * <p>
 * Every node must be able to pretty-print itself
 * </p>
 *
 * <p>
 * Consider adding further support for visitor patterns, type checking
 * information and source coordinates here.
 * </p>
 *
 * @see compiler488.ast.BaseAST
 */
public interface AST extends PrettyPrintable {
	public void prettyPrint(PrettyPrinter p);

	public boolean performSemanticAnalysis(Semantics s);
	public void performCodeGeneration(CodeGen g);

	public Pair<Integer, Integer> getPosition();
}
