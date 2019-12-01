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
        boolean leftIsConstant = left.getCachedIsConstant();
        boolean rightIsConstant = right.getCachedIsConstant();
        boolean leftConstantValue = left.getCachedConstantValue() == 1;
        boolean rightConstantValue = right.getCachedConstantValue() == 1;

        // Algebraic simplification
        if(leftIsConstant) {
            switch (opSymbol) {
                case OP_AND:
                    if(leftConstantValue) {
                        right.attemptConstantFolding(g);
                    } else {
                        g.addInstruction(Machine.PUSH);
                        g.addInstruction(0);
                    }
                    return;
                case OP_OR:
                    if(leftConstantValue) {
                        g.addInstruction(Machine.PUSH);
                        g.addInstruction(1);
                    } else {
                        right.attemptConstantFolding(g);
                    }
                    return;
            }
        } else if(rightIsConstant) {
            left.attemptConstantFolding(g);
            switch (opSymbol) {
                case OP_AND:
                    if(!rightConstantValue) {
                        g.addInstruction(Machine.POP);
                        g.addInstruction(Machine.PUSH);
                        g.addInstruction(0);
                    }
                    return;
                case OP_OR:
                    if(rightConstantValue) {
                        g.addInstruction(Machine.POP);
                        g.addInstruction(Machine.PUSH);
                        g.addInstruction(1);
                    }
                    return;
            }
        }

        left.attemptConstantFolding(g);
        // Short circuiting
        int shortCircuitAddress = 0;
        if(opSymbol == OP_AND) {
            g.addInstruction(Machine.DUP);
            g.addInstruction(Machine.PUSH);
            shortCircuitAddress = g.getPosition();
            g.addInstruction(0);
            g.addInstruction(Machine.BF);
        } else if(opSymbol == OP_OR) {
            g.addInstruction(Machine.DUP);
            g.addInstruction(Machine.PUSH);
            g.addInstruction(1);
            g.addInstruction(Machine.SWAP);
            g.addInstruction(Machine.SUB);
            g.addInstruction(Machine.PUSH);
            shortCircuitAddress = g.getPosition();
            g.addInstruction(0);
            g.addInstruction(Machine.BF);
        }
        right.attemptConstantFolding(g);
        switch (opSymbol) {
            case OP_AND:
                g.addInstruction(Machine.ADD);
                g.addInstruction(Machine.PUSH);
                g.addInstruction(2);
                g.addInstruction(Machine.EQ);
                break;
            case OP_OR:
                g.addInstruction(Machine.OR);
                break;
            default:
                throw new RuntimeException("Invalid arithmetic expression!");
        }
        g.setInstruction(shortCircuitAddress, g.getPosition());
    }

    @Override
    public boolean isConstant() {
        boolean leftCache = left.getCachedIsConstant();
        boolean rightCache = right.getCachedIsConstant();
        if(leftCache && rightCache) {
            return true;
        }
        boolean leftVal = leftCache && left.getCachedConstantValue() == 1;
        switch (opSymbol) {
            case OP_AND:
                if(leftCache && !leftVal) {
                    return true;
                }
                break;
            case OP_OR:
                if(leftCache && leftVal) {
                    return true;
                }
                break;
            default:
                throw new RuntimeException("Invalid arithmetic expression!");
        }
        return false;
    }

    @Override
    public short computeConstant() {
        boolean leftCache = left.getCachedIsConstant();
        boolean rightCache = right.getCachedIsConstant();
        boolean leftValue = left.getCachedConstantValue() == 1;
        boolean rightValue = right.getCachedConstantValue() == 1;
        if(leftCache && rightCache) {
            switch (opSymbol) {
                case OP_AND:
                    return (short) (leftValue && rightValue ? 1 : 0);
                case OP_OR:
                    return (short) (leftValue || rightValue ? 1 : 0);
                default:
                    throw new RuntimeException("Invalid arithmetic expression!");
            }
        } else if(leftCache) {
            switch (opSymbol) {
                case OP_AND:
                    if(!leftValue) {
                        return 0;
                    }
                    break;
                case OP_OR:
                    if(leftValue) {
                        return 1;
                    }
                    break;
                default:
                    throw new RuntimeException("Invalid arithmetic expression!");
            }
        }
        throw new RuntimeException("Not constant!");
    }
}
