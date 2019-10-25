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
    public void performSemanticAnalysis(Semantics s) {
        getOperand().performSemanticAnalysis(s);
        s.semanticAction(30, getOperand());
        s.semanticAction(20, this);
    }
}
