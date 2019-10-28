package compiler488.ast.type;


import compiler488.semantics.Semantics;

/**
 * The type of things that may be true or false.
 */
public class BooleanType extends Type {
    @Override
    public String toString() {
        return "boolean";
    }

    @Override
    public boolean performSemanticAnalysis(Semantics s) {
        return s.semanticAction(20, null);
    }
}
