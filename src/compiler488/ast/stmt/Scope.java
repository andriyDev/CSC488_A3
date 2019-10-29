package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.decl.Declaration;
import compiler488.semantics.Semantics;

/**
 * Represents the declarations and statements of a scope construct.
 */
public class Scope extends Stmt {
	/** Body of the scope, optional declarations, optional statements */
	protected ASTList<Declaration> declarations;
	protected ASTList<Stmt> statements;

	public Scope(ASTList<Declaration> decls, ASTList<Stmt> stmts)
	{
		declarations = new ASTList<Declaration>();
		statements = new ASTList<Stmt>();
		
		if (decls != null)
			declarations = decls;

		if (stmts != null)
			statements = stmts;
	}

	public void setDeclarations(ASTList<Declaration> declarations) {
		this.declarations = declarations;
	}

	public ASTList<Declaration> getDeclarations() {
		return declarations;
	}

	public void setStatements(ASTList<Stmt> statements) {
		this.statements = statements;
	}

	public ASTList<Stmt> getStatements() {
		return statements;
	}

	@Override
	public void prettyPrint(PrettyPrinter p) {
		p.println(" { ");
		if (declarations != null && declarations.size() > 0) {
			declarations.prettyPrintBlock(p);
		}
		if (statements != null && statements.size() > 0) {
			statements.prettyPrintBlock(p);
		}
		p.print(" } ");
	}

	@Override
	public boolean performSemanticAnalysis(Semantics s) {
		boolean result;
		result = s.semanticAction(6, this);
		result &= performStatementSemanticAnalysis(s);
		result &= s.semanticAction(7, this);
		return result;
	}

	public boolean performStatementSemanticAnalysis(Semantics s) {
		boolean result = true;
		for(Declaration decl : declarations) {
			result &= decl.performSemanticAnalysis(s);
		}
		result &= s.semanticAction(2, this);
		for(Stmt stmt : statements) {
			result &= stmt.performSemanticAnalysis(s);
		}
		return result;
	}

	@Override
	public boolean hasReturn() {
		for(Stmt stmt : statements) {
			if(stmt.hasReturn()) {
				return true;
			}
		}
		return false;
	}
}
