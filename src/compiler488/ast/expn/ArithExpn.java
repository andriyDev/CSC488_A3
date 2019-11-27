package compiler488.ast.expn;


import compiler488.semantics.Semantics;
import compiler488.codegen.CodeGen;

/**
 * Place holder for all binary expression where both operands must be integer
 * expressions.
 */
public class ArithExpn extends BinaryExpn {
    public final static String OP_PLUS 		= "+";
    public final static String OP_MINUS 	= "-";
    public final static String OP_TIMES 	= "*";
    public final static String OP_DIVIDE 	= "/";

    public ArithExpn(String opSymbol, Expn left, Expn right) {
        super(opSymbol, left, right);

        assert ((opSymbol == OP_PLUS) ||
                (opSymbol == OP_MINUS) ||
                (opSymbol == OP_TIMES) ||
                (opSymbol == OP_DIVIDE));
    }

    @Override
    public boolean performSemanticAnalysis(Semantics s) {
        boolean result;
        result = left.performSemanticAnalysis(s);
        result &= s.semanticAction(31, left);
        result &= right.performSemanticAnalysis(s);
        result &= s.semanticAction(31, right);
        result &= s.semanticAction(21, this);
        return result;
    }

    @Override
    public void performCodeGeneration(CodeGen c) {
        left.performCodeGeneration(c);
        right.performCodeGeneration(c);

        if (opSymbol == OP_MINUS) {
            c.generateCodeForExpn(62, this);
        }
        else if (opSymbol == OP_PLUS) {
            c.generateCodeForExpn(61, this);
        }
        else if (opSymbol == OP_TIMES) {
            c.generateCodeForExpn(63, this);
        }
        else if (opSymbol == OP_DIVIDE) {
            c.generateCodeForExpn(64, this);
        }
        else {
            // TODO
            System.err.println("invalid arithmetic operation");
        }
    }
}
