package compiler488.ast.expn;

import compiler488.semantics.Semantics;

/**
 * Represents negation of an integer expression
 */
public class UnaryMinusExpn extends UnaryExpn {
    public UnaryMinusExpn(Expn operand) {
        super(UnaryExpn.OP_MINUS, operand);
    }

    @Override
    public void performSemanticAnalysis(Semantics s) {
        getOperand().performSemanticAnalysis(s);
        s.semanticAction(31, getOperand());
        s.semanticAction(21, this);
    }
}
