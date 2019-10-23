package compiler488.ast.type;


import compiler488.semantics.Semantics;

/**
 * Used to declare objects that yield integers.
 */
public class IntegerType extends Type {
    public String toString() {
        return "integer";
    }

    @Override
    public void performSemanticAnalysis(Semantics s) {
        s.semanticAction(21, null);
    }
}
