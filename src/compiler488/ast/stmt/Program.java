package compiler488.ast.stmt;

import compiler488.semantics.Semantics;

/**
 * Placeholder for the scope that is the entire program
 */
public class Program extends Scope {

    public Program (Scope scope) {
        super(scope.getDeclarations(), scope.getStatements()); 
    }

    @Override
    public boolean performSemanticAnalysis(Semantics s) {
        boolean result;
        result = s.semanticAction(0, this);
        result &= performStatementSemanticAnalysis(s);
        result &= s.semanticAction(1, this);
        return result;
    }
}
