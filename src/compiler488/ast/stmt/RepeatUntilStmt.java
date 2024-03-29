package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.Expn;
import compiler488.codegen.CodeGen;
import compiler488.runtime.Machine;
import compiler488.semantics.Semantics;

/**
 * Represents a loop in which the exit condition is evaluated after each pass.
 */
public class RepeatUntilStmt extends LoopingStmt {
	public RepeatUntilStmt(Expn expn, ASTList<Stmt> body) {
		super(expn, body);
	}

	@Override
	public void prettyPrint(PrettyPrinter p) {
		p.print("repeat ");
		body.prettyPrintBlock(p);
		p.println(" until ");
		expn.prettyPrint(p);
	}

	@Override
	public boolean performSemanticAnalysis(Semantics s) {
		boolean result;
		result = s.semanticAction(55, this);
		for(Stmt stmt : body) {
			result &= stmt.performSemanticAnalysis(s);
		}
		result &= s.semanticAction(56, this);
		result &= expn.performSemanticAnalysis(s);
		result &= s.semanticAction(30, expn);
		return result;
	}

	@Override
	public void generateConditionCheck(CodeGen g) {
		g.addInstruction(Machine.PUSH);
		g.addInstruction(1);
		expn.attemptConstantFolding(g);
		g.addInstruction(Machine.SUB);
	}
}
