package compiler488.ast.expn;

import compiler488.Pair;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.Readable;
import compiler488.codegen.CodeGen;
import compiler488.runtime.Machine;
import compiler488.semantics.Semantics;
import compiler488.symbol.Symbol;

/**
 * References to an array element variable
 */
public class SubsExpn extends Expn implements Readable {
	/** Name of the array variable. */
	private String variable;

	/** First subscript. */
	private Expn subscript1;

	/** Second subscript (if any.) */
	private Expn subscript2 = null;

	/** Subscript 2 dimensional array */
	public SubsExpn(String variable, Expn subscript1, Expn subscript2) {
		super();

		this.variable = variable;
		this.subscript1 = subscript1;
		this.subscript2 = subscript2;
	}

	/** Subscript 1 dimensional array */
	public SubsExpn(String variable, Expn subscript1) {
		this(variable, subscript1, null);
	}

	public String getVariable() {
		return variable;
	}

	public Expn getSubscript1() {
		return subscript1;
	}

	public Expn getSubscript2() {
		return subscript2;
	}

	public int numSubscripts() {
		return 1 + (subscript2 != null ? 1 : 0);
	}

	public void prettyPrint(PrettyPrinter p) {
		p.print(variable + "[");

		subscript1.prettyPrint(p);

		if (subscript2 != null) {
			p.print(", ");
			subscript2.prettyPrint(p);
		}

		p.print("]");
	}

	@Override
	public boolean performSemanticAnalysis(Semantics s) {
		boolean result;
		result = s.semanticAction(38, this);
		result &= s.semanticAction(58, this);
		result &= subscript1.performSemanticAnalysis(s);
		result &= s.semanticAction(31, subscript1);
		if(subscript2 != null) {
			result &= subscript2.performSemanticAnalysis(s);
			result &= s.semanticAction(31, subscript2);
		}
		result &= s.semanticAction(27, this);
		return result;
	}

	@Override
	public void generateCodeForAccessor(CodeGen g) {
		Symbol sym =g.getCurrentScope().getSymbol(variable);
		Pair<Integer, Integer> p = g.getCurrentScope().getSymbolLocation(variable);
		g.addInstruction(Machine.ADDR);
		g.addInstruction(p.getKey());
		g.addInstruction(p.getValue());
		subscript1.performCodeGeneration(g);
		if(sym.bounds.minX != 0) {
			g.addInstruction(Machine.PUSH);
			g.addInstruction(sym.bounds.minX);
			g.addInstruction(Machine.SUB);
		}
		if(subscript2 != null) {
			g.addInstruction(Machine.PUSH);
			// Stride of dimension 2
			g.addInstruction(sym.bounds.maxY - sym.bounds.minY + 1);
			g.addInstruction(Machine.MUL);
			subscript2.performCodeGeneration(g);
			if (sym.bounds.minY != 0) {
				g.addInstruction(Machine.PUSH);
				g.addInstruction(sym.bounds.minY);
				g.addInstruction(Machine.SUB);
			}
			g.addInstruction(Machine.ADD);
		}
		g.addInstruction(Machine.ADD);
	}

	@Override
	public void performCodeGeneration(CodeGen g) {
		generateCodeForAccessor(g);
		g.addInstruction(Machine.LOAD);
	}
}
