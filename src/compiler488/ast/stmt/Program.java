package compiler488.ast.stmt;

import compiler488.semantics.Semantics;

/**
 * Placeholder for the scope that is the entire program
 */
public class Program extends Scope {
    @Override
    public void performSemanticAnalysis(Semantics s) {
        s.semanticAction(0, null);
        performStatementSemanticAnalysis(s);
        s.semanticAction(1, null);
    }
}
