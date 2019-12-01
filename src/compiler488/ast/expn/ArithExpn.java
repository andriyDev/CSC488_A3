package compiler488.ast.expn;


import compiler488.codegen.CodeGen;
import compiler488.runtime.Machine;
import compiler488.semantics.Semantics;

/**
 * Place holder for all binary expression where both operands must be integer
 * expressions.
 */
public class ArithExpn extends BinaryExpn {
    public final static String OP_PLUS 		= "+";
    public final static String OP_MINUS 	= "-";
    public final static String OP_TIMES 	= "*";
    public final static String OP_DIVIDE 	= "/";

    public ArithExpn(String opSymbol, Expn left, Expn right) {
        super(opSymbol, left, right);

        assert ((opSymbol == OP_PLUS) ||
                (opSymbol == OP_MINUS) ||
                (opSymbol == OP_TIMES) ||
                (opSymbol == OP_DIVIDE));
    }

    @Override
    public boolean performSemanticAnalysis(Semantics s) {
        boolean result;
        result = left.performSemanticAnalysis(s);
        result &= s.semanticAction(31, left);
        result &= right.performSemanticAnalysis(s);
        result &= s.semanticAction(31, right);
        result &= s.semanticAction(21, this);
        return result;
    }

    @Override
    public void performCodeGeneration(CodeGen g) {
        left.attemptConstantFolding(g);
        right.attemptConstantFolding(g);
        switch (opSymbol) {
            case OP_PLUS:
                g.addInstruction(Machine.ADD);
                return;
            case OP_MINUS:
                g.addInstruction(Machine.SUB);
                return;
            case OP_TIMES:
                g.addInstruction(Machine.MUL);
                return;
            case OP_DIVIDE:
                g.addInstruction(Machine.DIV);
                return;
            default:
                throw new RuntimeException("Invalid arithmetic expression!");
        }
    }

    @Override
    public short computeConstant() {
        short leftValue = left.getCachedConstantValue();
        short rightValue = right.getCachedConstantValue();
        switch (opSymbol) {
            case OP_PLUS:
                return (short)(leftValue + rightValue);
            case OP_MINUS:
                return (short)(leftValue - rightValue);
            case OP_TIMES:
                return (short)(leftValue * rightValue);
            case OP_DIVIDE:
                if(rightValue == 0) {
                    throw new RuntimeException("Division by zero!");
                } else {
                    return (short)(leftValue / rightValue);
                }
            default:
                throw new RuntimeException("Invalid arithmetic expression!");
        }
    }
}
