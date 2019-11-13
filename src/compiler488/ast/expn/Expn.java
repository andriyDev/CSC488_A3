package compiler488.ast.expn;

import compiler488.ast.BaseAST;
import compiler488.ast.Printable;
import compiler488.codegen.CodeGen;

/**
 * A placeholder for all expressions.
 */
public abstract class Expn extends BaseAST implements Printable {

    public void generateCodeForAccessor(CodeGen g) { throw new RuntimeException("Expression cannot be an accessor"); }
}
