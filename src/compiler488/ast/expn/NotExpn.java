package compiler488.ast.expn;


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
}
