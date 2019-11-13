package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.Printable;
import compiler488.ast.expn.Expn;
import compiler488.ast.expn.SkipConstExpn;
import compiler488.ast.expn.TextConstExpn;
import compiler488.codegen.CodeGen;
import compiler488.runtime.Machine;
import compiler488.semantics.Semantics;

/**
 * The command to write data on the output device.
 */
public class WriteStmt extends Stmt {
	/** The objects to be printed. */
	private ASTList<Printable> outputs;

	public WriteStmt(ASTList<Printable> outputs) {
		super();
		this.outputs = outputs;
	}

	@Override
	public void prettyPrint(PrettyPrinter p) {
		p.print("write ");
		outputs.prettyPrintCommas(p);
	}

	@Override
	public boolean performSemanticAnalysis(Semantics s) {
		boolean result = true;
		for(Printable element : outputs) {
			if(element instanceof Expn) {
				if(element instanceof SkipConstExpn || element instanceof TextConstExpn) {
					continue;
				}
				result &= element.performSemanticAnalysis(s);
				result &= s.semanticAction(31, element);
			}
		}
		return result;
	}

	@Override
	public void performCodeGeneration(CodeGen g) {
		for(Printable element : outputs) {
			if (element instanceof Expn) {
				if(element instanceof SkipConstExpn) {
					g.addInstruction(Machine.PUSH);
					g.addInstruction('\n');
					g.addInstruction(Machine.PRINTC);
				} else if(element instanceof TextConstExpn) {
					String s = ((TextConstExpn)element).getValue();
					for(char c : s.toCharArray()) {
						g.addInstruction(Machine.PUSH);
						g.addInstruction(c);
						g.addInstruction(Machine.PRINTC);
					}
				} else {
					((Expn)element).performCodeGeneration(g);
					g.addInstruction(Machine.PRINTI);
				}
			} // We don't know what to do otherwise
		}
	}

	public ASTList<Printable> getOutputs() {
		return outputs;
	}
}
