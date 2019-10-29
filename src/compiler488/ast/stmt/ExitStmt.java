package compiler488.ast.stmt;

import compiler488.ast.expn.*;
import compiler488.semantics.Semantics;

/**
 * Represents the command to exit from a loop.
 */
public class ExitStmt extends Stmt {
	/** Condition for 'exit when'. */
	private Expn expn = null;

	/** Number of levels to exit. */
	private Integer level = -1;

	public ExitStmt() {
		super();
	}

	public ExitStmt(Integer level) {
		super();

		this.level = level;
	}

	public ExitStmt(Expn expn) {
		super();

		this.expn = expn;
	}

	public ExitStmt(Integer level, Expn expn) {
		super();
		
		this.level = level;
		this.expn = expn;
	}


	/**
	 * Returns the string <b>"exit"</b> or <b>"exit when e"</b>" or
	 * <b>"exit"</b> level or <b>"exit"</b> level when e
	 */
	@Override
	public String toString() {
		String stmt = "exit";

		if (level >= 0) {
			stmt = " " + stmt + level;
		}

		if (expn != null) {
			stmt = " " + stmt + "when " + expn + " ";
		}

		return stmt;
	}

	public Expn getExpn() {
		return expn;
	}

	public void setExpn(Expn expn) {
		this.expn = expn;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@Override
	public boolean performSemanticAnalysis(Semantics s) {
		boolean result = true;
		if(expn != null) {
			result = expn.performSemanticAnalysis(s);
		}

		result &= s.semanticAction(50, this);
		if(level != -1)
		{
			// Use this object to figure out if there are enough loops to leave.
			result &= s.semanticAction(53, this);
		}
		return result;
	}
}
