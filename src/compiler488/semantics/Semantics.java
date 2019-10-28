package compiler488.semantics;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import compiler488.ast.AST;
import compiler488.ast.decl.RoutineDecl;
import compiler488.ast.decl.ScalarDecl;
import compiler488.ast.stmt.Program;
import compiler488.ast.type.BooleanType;
import compiler488.symbol.Symbol;
import compiler488.symbol.SymbolTable;

/** Implement semantic analysis for compiler 488 
 *  @author  <B> Put your names here </B>
 */
public class Semantics {
	
        /** flag for tracing semantic analysis */
	private boolean traceSemantics = false;
	/** file sink for semantic analysis trace */
	private String traceFile = new String();
	public FileWriter Tracer;
	public File f;

	private SymbolTable symbols;

	private Stack<SymbolTable.SymbolScope> scopes;
     
     /** SemanticAnalyzer constructor */
	public Semantics (){
	
	}

	public boolean analyze(Program AST) {
		Initialize();

		AST.performSemanticAnalysis(this);

		Finalize();
		return true;
	}

	/**  semanticsInitialize - called once by the parser at the      */
	/*                        start of  compilation                 */
	void Initialize() {
	
	   /*   Initialize the symbol table             */

		symbols = new SymbolTable();
	    symbols.Initialize();

	    scopes = new Stack<>();
	    scopes.push(symbols.globalScope);
	   
	   /*********************************************/
	   /*  Additional initialization code for the   */
	   /*  semantic analysis module                 */
	   /*  GOES HERE                                */
	   /*********************************************/
	   
	}

	/**  semanticsFinalize - called by the parser once at the        */
	/*                      end of compilation                      */
	void Finalize(){
	
	  /*  Finalize the symbol table                 */

		if(symbols != null) {
			symbols.Finalize();
		}
	  // Symbol.Finalize();
	  
	   /*********************************************/
	  /*  Additional finalization code for the      */
	  /*  semantics analysis module                 */
	  /*  GOES here.                                */
	  /**********************************************/
	  
	}
	
	/**
	 *  Perform one semantic analysis action
         *  @param  actionNumber  semantic analysis action number
         */
	public void semanticAction( int actionNumber, AST target ) {

	if( traceSemantics ){
		if(traceFile.length() > 0 ){
	 		//output trace to the file represented by traceFile
	 		try{
	 			//open the file for writing and append to it
	 			File f = new File(traceFile);
	 		    Tracer = new FileWriter(traceFile, true);
	 				          
	 		    Tracer.write("Sematics: S" + actionNumber + "\n");
	 		    //always be sure to close the file
	 		    Tracer.close();
	 		}
	 		catch (IOException e) {
	 		  System.out.println(traceFile + 
				" could be opened/created.  It may be in use.");
	 	  	}
	 	}
	 	else{
	 		//output the trace to standard out.
	 		System.out.println("Sematics: S" + actionNumber );
	 	}
	 
	}
	                     
	   /*************************************************************/
	   /*  Code to implement each semantic action GOES HERE         */
	   /*  This stub semantic analyzer just prints the actionNumber */   
	   /*                                                           */
           /*  FEEL FREE TO ignore or replace this procedure            */
	   /*************************************************************/

	   if(actionNumber == 2) {

	   } else if(actionNumber < 10) {
		   handleScopeActions(actionNumber);
	   } else if(actionNumber < 13) {
		   handleDeclActions(actionNumber, target);
	   }

	   System.out.println("Semantic Action: S" + actionNumber  );
	   return ;
	}

	private void handleScopeActions(int actionNumber) {
		if (actionNumber % 2 == 0) {
			SymbolTable.SymbolScope currentScope = scopes.peek();
			scopes.push(symbols.createNewScope(currentScope));
		} else {
			scopes.pop();
		}
	}

	private void handleDeclActions(int actionNumber, AST target) {
		SymbolTable.SymbolScope currentScope = scopes.peek();
		if(actionNumber == 10) {
			ScalarDecl decl = (ScalarDecl)target;
			currentScope.addSymbol(decl.getName(), new Symbol(Symbol.DTFromAST(decl.getType())));
		} else if(actionNumber == 11 || actionNumber == 12) {
			RoutineDecl decl = (RoutineDecl)target;
			List<Symbol.DataType> parameters = new ArrayList<>();
			for(ScalarDecl param : decl.getParameters()) {
				parameters.add(Symbol.DTFromAST(param.getType()));
			}
			currentScope.addSymbol(decl.getName(), new Symbol(Symbol.DTFromAST(decl.getType()), parameters));
		}
	}
}
