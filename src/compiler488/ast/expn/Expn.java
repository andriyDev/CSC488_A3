package compiler488.ast.expn;

import compiler488.ast.BaseAST;
import compiler488.ast.Printable;
import compiler488.codegen.CodeGen;
import compiler488.runtime.Machine;

/**
 * A placeholder for all expressions.
 */
public abstract class Expn extends BaseAST implements Printable {
    private boolean cachedConstant = false;
    private boolean cachedIsConstant = false;
    private short cachedConstantValue;

    public abstract boolean isConstant();
    public short computeConstant() { throw new RuntimeException("This expression is not constant!"); }

     public void attemptConstantFolding(CodeGen g) {
        if(getCachedIsConstant()) {
            g.addInstruction(Machine.PUSH);
            g.addInstruction(getCachedConstantValue());
        } else {
            performCodeGeneration(g);
        }
     }

     public boolean getCachedIsConstant() {
        if(cachedConstant) {
            return cachedIsConstant;
        } else {
            cachedIsConstant = isConstant();
            cachedConstant = true;
            if(cachedIsConstant) {
                cachedConstantValue = computeConstant();
            }
            return cachedIsConstant;
        }
     }

     public short getCachedConstantValue() {
         if(cachedConstant) {
             return cachedConstantValue;
         } else {
             cachedIsConstant = isConstant();
             cachedConstant = true;
             if(cachedIsConstant) {
                 cachedConstantValue = computeConstant();
                 return cachedConstantValue;
             } else {
                 throw new RuntimeException("Oh no!");
             }
         }
     }
}
