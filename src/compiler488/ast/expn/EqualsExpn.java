package compiler488.ast.expn;


import compiler488.semantics.Semantics;
import compiler488.codegen.CodeGen;

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
    public boolean performSemanticAnalysis(Semantics s) {
        boolean result;
        result = left.performSemanticAnalysis(s);
        result &= right.performSemanticAnalysis(s);
        result &= s.semanticAction(32, this);
        result &= s.semanticAction(20, this);
        return result;
    }

    @Override
	public void performCodeGeneration(CodeGen c) {
		left.performCodeGeneration(c);
        right.performCodeGeneration(c);

        if (opSymbol == OP_EQUAL) {
            c.generateCodeForExpn(69, this);
        }
        else if (opSymbol == OP_NOT_EQUAL) {
            c.generateCodeForExpn(70, this);
        }
        else {
            // TODO ???
        }
	}

}
