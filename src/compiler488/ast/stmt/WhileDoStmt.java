package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.Expn;
import compiler488.codegen.CodeGen;
import compiler488.semantics.Semantics;

/**
 * Represents a loop in which the exit condition is evaluated before each pass.
 */
public class WhileDoStmt extends LoopingStmt {
	public WhileDoStmt(Expn expn, ASTList<Stmt> body) {
		super(expn, body);
	}

	@Override
	public void prettyPrint(PrettyPrinter p) {
		p.print("while ");
		expn.prettyPrint(p);
		p.println(" do");
		body.prettyPrintBlock(p);
	}

	@Override
	public boolean performSemanticAnalysis(Semantics s) {
		boolean result;
		result = expn.performSemanticAnalysis(s);
		result &= s.semanticAction(30, expn);
		result &= s.semanticAction(55, this);
		for(Stmt stmt : body) {
			result &= stmt.performSemanticAnalysis(s);
		}
		result &= s.semanticAction(56, this);
		return result;
	}

	@Override
	public void generateConditionCheck(CodeGen g) {
		expn.attemptConstantFolding(g);
	}
}
