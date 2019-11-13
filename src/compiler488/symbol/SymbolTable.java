package compiler488.symbol;

import compiler488.Pair;
import compiler488.ast.AST;

import java.io.*;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

/** Symbol Table
 *  This almost empty class is a framework for implementing
 *  a Symbol Table class for the CSC488S compiler
 *  
 *  Each implementation can change/modify/delete this class
 *  as they see fit.
 *
 *  @author  <B> PUT YOUR NAMES HERE </B>
 */

public class SymbolTable {

	public static class SymbolScope {
		public SymbolScope parent;
		public Symbol creatingSymbol;
		public int lexicalLevel;
		public int offset;

		public HashMap<String, Pair<Symbol, Integer>> symbols;

		public SymbolScope(SymbolScope parent, Symbol creatingSymbol) {
			this.parent = parent;
			if(parent == null) {
				lexicalLevel = 0;
				offset = 0;
			} else {
				lexicalLevel = parent.lexicalLevel + (creatingSymbol != null ? 1 : 0);
				offset = creatingSymbol != null ? 0 : parent.offset;
			}
			this.symbols = new HashMap<>();
			this.creatingSymbol = creatingSymbol;
		}

		public boolean containsSymbol(String name) {
			return symbols.containsKey(name);
		}

		public Symbol getSymbol(String name) {
			if(containsSymbol(name)) {
				return symbols.get(name).getKey();
			} else if (parent != null) {
				return parent.getSymbol(name);
			} else {
				return null;
			}
		}

		public Pair<Integer, Integer> getSymbolLocation(String name) {
			if(containsSymbol(name)) {
				return new Pair<>(lexicalLevel, symbols.get(name).getValue());
			} else if (parent != null) {
				return parent.getSymbolLocation(name);
			} else {
				return null;
			}
		}

		public void addSymbol(String name, Symbol sym) {
			if(symbols.containsKey(name)) {
				throw new RuntimeException("Symbol already exists in scope!");
			}
			symbols.put(name, new Pair<>(sym, offset));
			offset += sym.getDataSize();
		}
	}

	public SymbolScope globalScope;

	public HashMap<AST, SymbolScope> allScopes;
	
	/** Symbol Table  constructor
         *  Create and initialize a symbol table 
	 */
	public SymbolTable  (){
	
	}

	/**  Initialize - called once by semantic analysis  
	 *                at the start of  compilation     
	 *                May be unnecessary if constructor
 	 *                does all required initialization	
	 */
	public void Initialize() {
	
	   /**   Initialize the symbol table             
	    *	Any additional symbol table initialization
	    *  GOES HERE                                	
	    */
	   this.globalScope = new SymbolScope(null, null);
	   this.allScopes = new HashMap<>();
	}

	/**  Finalize - called once by Semantics at the end of compilation
	 *              May be unnecessary 		
	 */
	public void Finalize(){
	
	  /**  Additional finalization code for the 
	   *  symbol table  class GOES HERE.
	   *  
	   */
	}
	

	/** The rest of Symbol Table
	 *  Data structures, public and private functions
 	 *  to implement the Symbol Table
	 *  GO HERE.				
	 */

	public SymbolScope createNewScope(SymbolScope currentScope, Symbol creatingSymbol, AST creator) {
		assert currentScope != null;
		SymbolScope newScope = new SymbolScope(currentScope, creatingSymbol);
		this.allScopes.put(creator, newScope);
		return newScope;
	}
}
