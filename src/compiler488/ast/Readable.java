package compiler488.ast;

import compiler488.codegen.CodeGen;

/**
 * Any AST node that can be an argument in a GET statement.
 *
 * <p>
 * Don't confuse with concept with the printing of the AST itself.
 * </p>
 */
public interface Readable extends AST {

    public void generateCodeForAccessor(CodeGen g);
}
