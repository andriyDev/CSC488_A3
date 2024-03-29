package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.Expn;
import compiler488.codegen.CodeGen;
import compiler488.runtime.Machine;
import compiler488.semantics.Semantics;

import javax.crypto.Mac;

/**
 * Represents an if-then or an if-then-else construct.
 */
public class IfStmt extends Stmt {
	/** The condition that determines which branch to execute. */
	private Expn condition;

	/** Represents the statement to execute when the condition is true. */
	private Stmt whenTrue;

	/** Represents the statement to execute when the condition is false. */
	private Stmt whenFalse = null;

	public IfStmt(Expn condition, Stmt whenTrue, Stmt whenFalse) {
		super();

		this.condition = condition;
		this.whenTrue = whenTrue;
		this.whenFalse = whenFalse;
	}

	public IfStmt(Expn condition, Stmt whenTrue) {
		this(condition, whenTrue, null);
	}

	public Expn getCondition() {
		return condition;
	}

	public Stmt getWhenTrue() {
		return whenTrue;
	}

	public Stmt getWhenFalse() {
		return whenFalse;
	}

	/**
	 * Print a description of the <strong>if-then-else</strong> construct. If
	 * the <strong>else</strong> part is empty, just print an
	 * <strong>if-then</strong> construct.
	 */
	@Override
	public void prettyPrint(PrettyPrinter p) {
		p.print("if ");
		condition.prettyPrint(p);
		p.println(" then");
		whenTrue.prettyPrint(p);

		if (whenFalse != null) {
			p.println(" else");
			whenFalse.prettyPrint(p);
		}

		p.println("end");
	}

	@Override
	public boolean performSemanticAnalysis(Semantics s) {
		boolean result;
		result = condition.performSemanticAnalysis(s);
		result &= s.semanticAction(30, condition);
		result &= whenTrue.performSemanticAnalysis(s);
		if (whenFalse != null) {
			result &= whenFalse.performSemanticAnalysis(s);
		}
		return result;
	}

	@Override
	public boolean hasReturn() {
		if(whenFalse == null) {
			return false;
		}
		return whenTrue.hasReturn() && whenFalse.hasReturn();
	}

	@Override
	public void performCodeGeneration(CodeGen g) {
		if(condition.getCachedIsConstant()) {
			if(condition.getCachedConstantValue() == 1) {
				whenTrue.performCodeGeneration(g);
			} else {
				whenFalse.performCodeGeneration(g);
			}
			return;
		}
		condition.attemptConstantFolding(g);
		g.addInstruction(Machine.PUSH);
		int addressOfElseBlock = g.getPosition();
		g.addInstruction(0); // Temporary slot for the else address
		g.addInstruction(Machine.BF);
		whenTrue.performCodeGeneration(g);
		if(whenFalse == null) {
			g.setInstruction(addressOfElseBlock, g.getPosition());
		} else {
			g.addInstruction(Machine.PUSH);
			int addressAfterElseBlock = g.getPosition();
			g.addInstruction(0);
			g.addInstruction(Machine.BR);

			g.setInstruction(addressOfElseBlock, g.getPosition());
			whenFalse.performCodeGeneration(g);
			g.setInstruction(addressAfterElseBlock, g.getPosition());
		}
	}
}
