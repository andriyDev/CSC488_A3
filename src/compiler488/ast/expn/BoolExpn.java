package compiler488.ast.expn;


import compiler488.codegen.CodeGen;
import compiler488.runtime.Machine;
import compiler488.semantics.Semantics;

/**
 * Place holder for all binary expression where both operands must be boolean
 * expressions.
 */
public class BoolExpn extends BinaryExpn {
    public final static String OP_OR 	= "or";
    public final static String OP_AND	= "and";

    public BoolExpn(String opSymbol, Expn left, Expn right) {
        super(opSymbol, left, right);

        assert ((opSymbol == OP_OR) ||
                (opSymbol == OP_AND));
    }

    @Override
    public boolean performSemanticAnalysis(Semantics s) {
        boolean result;
        result = left.performSemanticAnalysis(s);
        result &= s.semanticAction(30, left);
        result &= right.performSemanticAnalysis(s);
        result &= s.semanticAction(30, right);
        result &= s.semanticAction(20, this);
        return result;
    }

    @Override
    public void performCodeGeneration(CodeGen g) {
        left.attemptConstantFolding(g);
        right.attemptConstantFolding(g);
        switch (opSymbol) {
            case OP_AND:
                g.addInstruction(Machine.ADD);
                g.addInstruction(Machine.PUSH);
                g.addInstruction(2);
                g.addInstruction(Machine.EQ);
                return;
            case OP_OR:
                g.addInstruction(Machine.OR);
                return;
            default:
                throw new RuntimeException("Invalid arithmetic expression!");
        }
    }

    @Override
    public short computeConstant() {
        boolean leftValue = left.getCachedConstantValue() == 1;
        boolean rightValue = right.getCachedConstantValue() == 1;
        switch (opSymbol) {
            case OP_AND:
                return (short)(leftValue && rightValue ? 1 : 0);
            case OP_OR:
                return (short)(leftValue || rightValue ? 1 : 0);
            default:
                throw new RuntimeException("Invalid arithmetic expression!");
        }
    }
}
