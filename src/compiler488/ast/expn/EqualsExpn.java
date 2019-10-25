package compiler488.ast.expn;


import compiler488.semantics.Semantics;

/**
 * Place holder for all binary expression where both operands could be either
 * integer or boolean expressions. e.g. = and != comparisons
 */
public class EqualsExpn extends BinaryExpn {
    public final static String OP_EQUAL 	= "=";
    public final static String OP_NOT_EQUAL	= "not =";

    public EqualsExpn(String opSymbol, Expn left, Expn right) {
        super(opSymbol, left, right);

        assert ((opSymbol == OP_EQUAL) ||
                (opSymbol == OP_NOT_EQUAL));
    }

    @Override
    public void performSemanticAnalysis(Semantics s) {
        left.performSemanticAnalysis(s);
        right.performSemanticAnalysis(s);
        s.semanticAction(32, this);
        s.semanticAction(20, this);
    }
}
