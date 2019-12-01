package compiler488.ast.expn;


import compiler488.codegen.CodeGen;
import compiler488.runtime.Machine;
import compiler488.semantics.Semantics;

/**
 * Represents the boolean negation of an expression.
 */
public class NotExpn extends UnaryExpn {
    public NotExpn(Expn operand) {
        super(UnaryExpn.OP_NOT, operand);
    }

    @Override
    public boolean performSemanticAnalysis(Semantics s) {
        boolean result;
        result = getOperand().performSemanticAnalysis(s);
        result &= s.semanticAction(30, getOperand());
        result &= s.semanticAction(20, this);
        return result;
    }

    @Override
    public void performCodeGeneration(CodeGen g) {
        g.addInstruction(Machine.PUSH);
        g.addInstruction(1);
        getOperand().attemptConstantFolding(g);
        g.addInstruction(Machine.SUB);
    }

    @Override
    public short computeConstant() {
        return (short)(1 - getOperand().getCachedConstantValue());
    }
}
