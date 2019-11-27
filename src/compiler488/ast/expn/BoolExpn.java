package compiler488.ast.expn;


import compiler488.codegen.CodeGen;
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
	public void performCodeGeneration(CodeGen c) {
        left.performCodeGeneration(c);
        right.performCodeGeneration(c);
        if (opSymbol == OP_OR) {
            c.generateCodeForExpn(67, this);
        }
        else if (opSymbol == OP_AND) {
            c.generateCodeForExpn(66, this);
        }
        else {
            // TODO ???
        }
	}
}
