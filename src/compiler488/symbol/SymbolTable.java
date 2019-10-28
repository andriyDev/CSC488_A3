package compiler488.symbol;

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

		public HashMap<String, Symbol> symbols;

		public SymbolScope(SymbolScope parent) {
			this.parent = parent;
			this.symbols = new HashMap<String, Symbol>();
		}

		public boolean containsSymbol(String name) {
			return symbols.containsKey(name);
		}

		public Symbol getSymbol(String name) {
			if(containsSymbol(name)) {
				return symbols.get(name);
			} else if (parent != null) {
				return parent.getSymbol(name);
			} else {
				return null;
			}
		}

		public void addSymbol(String name, Symbol sym) {
		    assert !symbols.containsKey(name);
			symbols.put(name, sym);
		}
	}

	public SymbolScope globalScope;

	public List<SymbolScope> allScopes;
	
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
	   this.globalScope = new SymbolScope(null);
	   this.allScopes = new ArrayList<>();
	}

	/**  Finalize - called once by Semantics at the end of compilation
	 *              May be unnecessary 		
	 */
	public void Finalize(){
	
	  /**  Additional finalization code for the 
	   *  symbol table  class GOES HERE.
	   *  
	   */
	  this.globalScope = null;
	  this.allScopes = null;
	}
	

	/** The rest of Symbol Table
	 *  Data structures, public and private functions
 	 *  to implement the Symbol Table
	 *  GO HERE.				
	 */

	public SymbolScope createNewScope(SymbolScope currentScope) {
		assert currentScope != null;
		SymbolScope newScope = new SymbolScope(currentScope);
		this.allScopes.add(newScope);
		return newScope;
	}
}
