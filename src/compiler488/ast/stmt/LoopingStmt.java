package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.expn.Expn;
import compiler488.codegen.CodeGen;
import compiler488.runtime.Machine;

/**
 * Represents the common parts of loops.
 */
public abstract class LoopingStmt extends Stmt {
	/** The control expression for the looping construct (if any.) */
	protected Expn expn = null;

	/** The body of the looping construct. */
	protected ASTList<Stmt> body;

	public LoopingStmt(Expn expn, ASTList<Stmt> body) {
		super();

		this.expn = expn;
		this.body = body;
	}

	public LoopingStmt(ASTList<Stmt> body) {
		this(null, body);
	}

	public Expn getExpn() {
		return expn;
	}

	public ASTList<Stmt> getBody() {
		return body;
	}

	@Override
	public boolean hasReturn() {
		for(Stmt s : body) {
			if(s.hasReturn()) {
				return true;
			}
		}
		return false;
	}

	public abstract void generateConditionCheck(CodeGen g);

	@Override
	public void performCodeGeneration(CodeGen g) {
		int loopStart = g.getPosition();
		generateConditionCheck(g);
		g.addInstruction(Machine.PUSH);
		int addressOfAfterLoop = g.getPosition();
		g.addInstruction(0); // Temporary address slot
		g.addInstruction(Machine.BF);
		g.enterLoop(this);

		for(Stmt s : body) {
			s.performCodeGeneration(g);
		}
		g.addInstruction(Machine.PUSH);
		g.addInstruction(loopStart);
		g.addInstruction(Machine.BR);

		g.exitLoop(g.getPosition());
		g.setInstruction(addressOfAfterLoop, g.getPosition());
	}
}
