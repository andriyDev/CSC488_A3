package compiler488.ast.expn;

import compiler488.codegen.CodeGen;
import compiler488.runtime.Machine;
import compiler488.semantics.Semantics;

/**
 * Represents negation of an integer expression
 */
public class UnaryMinusExpn extends UnaryExpn {
    public UnaryMinusExpn(Expn operand) {
        super(UnaryExpn.OP_MINUS, operand);
    }

    @Override
    public boolean performSemanticAnalysis(Semantics s) {
        boolean result;
        result = getOperand().performSemanticAnalysis(s);
        result &= s.semanticAction(31, getOperand());
        result &= s.semanticAction(21, this);
        return result;
    }

    @Override
    public void performCodeGeneration(CodeGen g) {
        getOperand().attemptConstantFolding(g);
        g.addInstruction(Machine.NEG);
    }

    @Override
    public short computeConstant() {
        return (short)(-getOperand().getCachedConstantValue());
    }
}
