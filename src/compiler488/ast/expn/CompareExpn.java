package compiler488.ast.expn;


import compiler488.codegen.CodeGen;
import compiler488.runtime.Machine;
import compiler488.semantics.Semantics;

/**
 * Place holder for all ordered comparisons expression where both operands must
 * be integer expressions.  e.g. &lt; , &gt;  etc. comparisons
 */
public class CompareExpn extends BinaryExpn {
    public final static String OP_LESS 			= "<";
    public final static String OP_LESS_EQUAL 	= "<=";
    public final static String OP_GREATER 		= ">";
    public final static String OP_GREATER_EQUAL	= ">=";

    public CompareExpn(String opSymbol, Expn left, Expn right) {
        super(opSymbol, left, right);

        assert ((opSymbol == OP_LESS) ||
                (opSymbol == OP_LESS_EQUAL) ||
                (opSymbol == OP_GREATER) ||
                (opSymbol == OP_GREATER_EQUAL));
    }

    @Override
    public boolean performSemanticAnalysis(Semantics s) {
        boolean result;
        result = left.performSemanticAnalysis(s);
        result &= s.semanticAction(31, left);
        result &= right.performSemanticAnalysis(s);
        result &= s.semanticAction(31, right);
        result &= s.semanticAction(20, this);
        return result;
    }

    @Override
    public void performCodeGeneration(CodeGen g) {
        if (opSymbol == OP_LESS) {
            left.performCodeGeneration(g);
            right.performCodeGeneration(g);
            g.addInstruction(Machine.LT);
        } else if(opSymbol == OP_GREATER_EQUAL) {
            g.addInstruction(Machine.PUSH);
            g.addInstruction(1);
            left.performCodeGeneration(g);
            right.performCodeGeneration(g);
            g.addInstruction(Machine.LT);
            g.addInstruction(Machine.SUB);
        } else if(opSymbol == OP_GREATER) {
            left.performCodeGeneration(g);
            right.performCodeGeneration(g);
            g.addInstruction(Machine.SWAP);
            g.addInstruction(Machine.LT);
        } else if(opSymbol == OP_LESS_EQUAL) {
            g.addInstruction(Machine.PUSH);
            g.addInstruction(1);
            left.performCodeGeneration(g);
            right.performCodeGeneration(g);
            g.addInstruction(Machine.SWAP);
            g.addInstruction(Machine.LT);
            g.addInstruction(Machine.SUB);
        } else {
            throw new RuntimeException("Invalid comparison expression");
        }
    }

    @Override
    public short computeConstant() {
        short leftValue = left.getCachedConstantValue();
        short rightValue = right.getCachedConstantValue();
        switch (opSymbol) {
            case OP_LESS:
                return (short)(leftValue < rightValue ? 1 : 0);
            case OP_GREATER:
                return (short)(leftValue > rightValue ? 1 : 0);
            case OP_LESS_EQUAL:
                return (short)(leftValue <= rightValue ? 1 : 0);
            case OP_GREATER_EQUAL:
                return (short)(leftValue >= rightValue ? 1 : 0);
            default:
                throw new RuntimeException("Invalid comparison expression");
        }
    }
}
