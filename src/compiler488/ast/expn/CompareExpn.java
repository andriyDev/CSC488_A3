package compiler488.ast.expn;


import compiler488.semantics.Semantics;
import compiler488.codegen.CodeGen;

/**
 * Place holder for all ordered comparisons expression where both operands must
 * be integer expressions.  e.g. &lt; , &gt;  etc. comparisons
 */
public class CompareExpn extends BinaryExpn {
    public final static String OP_LESS 			= "<";
    public final static String OP_LESS_EQUAL 	= "<=";
    public final static String OP_GREATER 		= ">";
    public final static String OP_GREATER_EQUAL	= ">=";

    public CompareExpn(String opSymbol, Expn left, Expn right) {
        super(opSymbol, left, right);

        assert ((opSymbol == OP_LESS) ||
                (opSymbol == OP_LESS_EQUAL) ||
                (opSymbol == OP_GREATER) ||
                (opSymbol == OP_GREATER_EQUAL));
    }

    @Override
    public boolean performSemanticAnalysis(Semantics s) {
        boolean result;
        result = left.performSemanticAnalysis(s);
        result &= s.semanticAction(31, left);
        result &= right.performSemanticAnalysis(s);
        result &= s.semanticAction(31, right);
        result &= s.semanticAction(20, this);
        return result;
    }

    @Override
    public void performCodeGeneration(CodeGen c) {
        left.performCodeGeneration(c);
        right.performCodeGeneration(c);
        if (opSymbol == OP_LESS) {
            c.generateCodeForExpn(71, this);
        }
        else if (opSymbol == OP_LESS_EQUAL) {
            c.generateCodeForExpn(72, this);
        }
        else if (opSymbol == OP_GREATER) {
            c.generateCodeForExpn(73, this);
        }
        else if (opSymbol == OP_GREATER_EQUAL) {
            c.generateCodeForExpn(74, this);
        }
        else {
            // TODO

        }
        
    }
}
