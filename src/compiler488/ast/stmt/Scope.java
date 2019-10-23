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

	public Scope() {
		declarations = new ASTList<Declaration>();
		statements = new ASTList<Stmt>();
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
	public void performSemanticAnalysis(Semantics s) {
		s.semanticAction(6, null);
		performStatementSemanticAnalysis(s);
		s.semanticAction(7, null);
	}

	void performStatementSemanticAnalysis(Semantics s) {
		for(Declaration decl : declarations) {
			decl.performSemanticAnalysis(s);
		}
		s.semanticAction(2, null);
		for(Stmt stmt : statements) {
			stmt.performSemanticAnalysis(s);
		}
	}
}
