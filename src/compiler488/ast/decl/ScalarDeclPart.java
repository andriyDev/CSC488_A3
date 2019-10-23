package compiler488.ast.decl;

import compiler488.semantics.Semantics;

/**
 * Represents the declaration of a simple variable.
 */
public class ScalarDeclPart extends DeclarationPart {
	public ScalarDeclPart(String name) {
		super(name);
	}

	/**
	 * Returns a string describing the name of the object being declared.
	 */
	@Override
	public String toString() {
		return name;
	}

	@Override
	public void performSemanticAnalysis(Semantics s) {
		s.semanticAction(10, this);
	}
}
