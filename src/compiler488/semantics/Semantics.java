package compiler488.semantics;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import compiler488.ast.AST;
import compiler488.ast.decl.*;
import compiler488.ast.expn.FunctionCallExpn;
import compiler488.ast.expn.IdentExpn;
import compiler488.ast.stmt.*;
import compiler488.symbol.Symbol;
import compiler488.symbol.SymbolTable;
import javafx.util.Pair;

import static compiler488.symbol.Symbol.DataType.None;

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

	private enum ScopeType {
		Function, Procedure, Program, Normal
	}

	private Stack<Pair<ScopeType, SymbolTable.SymbolScope>> scopes;
	private Stack<Pair<Symbol, Integer>> funcInfo;

	private List<Symbol> partsAwaitingType;

	private Symbol lastRoutine = null;
	private Symbol checkingCall = null;
	private int callArg = 0;
     
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
	    scopes.push(new Pair<>(ScopeType.Program, symbols.globalScope));

	    funcInfo = new Stack<>();

	    partsAwaitingType = new ArrayList<>();
	   
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
	public void semanticAction( int actionNumber, AST target ) throws Exception {

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
		   handleScopeActions(actionNumber, target);
	   } else if(actionNumber < 20 || actionNumber == 46 || actionNumber == 47) {
		   handleDeclActions(actionNumber, target);
	   } else if(actionNumber >= 55) {
		   handleSpecialActions(actionNumber);
	   } else if(actionNumber >= 50) {
		   handleStatementActions(actionNumber, target);
	   } else if(actionNumber >= 40 && actionNumber <= 45) {
		   handleCallActions(actionNumber, target);
	   }

	   System.out.println("Semantic Action: S" + actionNumber  );
	   return ;
	}

	private void handleSpecialActions(int actionNumber) throws Exception {
		if(actionNumber == 55) {
			funcInfo.set(funcInfo.size() - 1, new Pair<>(funcInfo.peek().getKey(), funcInfo.peek().getValue() + 1));
		} else if(actionNumber == 56) {
			funcInfo.set(funcInfo.size() - 1, new Pair<>(funcInfo.peek().getKey(), funcInfo.peek().getValue() - 1));
		} else if(actionNumber == 57) {
			throw new Exception("Variable is not a routine or a variable!");
		}
	}

	private void handleScopeActions(int actionNumber, AST target) {
		if (actionNumber % 2 == 1) {
			if(scopes.peek().getKey() == ScopeType.Function
				|| scopes.peek().getKey() == ScopeType.Procedure) {
				funcInfo.pop();
			}
			scopes.pop();
			return;
		}

		SymbolTable.SymbolScope newScope = symbols.createNewScope(scopes.peek().getValue());
		if(actionNumber == 4) {
			scopes.push(new Pair<>(ScopeType.Function, newScope));
			// Action 13 will handle adding to funcInfo
		} else if (actionNumber == 6) {
			scopes.push(new Pair<>(ScopeType.Normal, newScope));
		} else if (actionNumber == 8) {
			scopes.push(new Pair<>(ScopeType.Procedure, newScope));
			// Action 13 will handle adding to funcInfo
		}
	}

	private void handleDeclActions(int actionNumber, AST target) {
		SymbolTable.SymbolScope currentScope = scopes.peek().getValue();
		if(actionNumber == 15) {
			ScalarDecl decl = (ScalarDecl)target;
			currentScope.addSymbol(decl.getName(), new Symbol(Symbol.DTFromAST(decl.getType())));
		} else if(actionNumber == 10) {
			ScalarDeclPart part = (ScalarDeclPart)target;
			Symbol s = new Symbol(null);
			currentScope.addSymbol(part.getName(), s);
			partsAwaitingType.add(s);
		} else if(actionNumber == 11 || actionNumber == 12
			|| actionNumber == 17 || actionNumber == 18) {
			RoutineDecl decl = (RoutineDecl)target;
			List<Symbol.DataType> parameters = new ArrayList<>();
			for(ScalarDecl param : decl.getParameters()) {
				parameters.add(Symbol.DTFromAST(param.getType()));
			}
			lastRoutine = new Symbol(Symbol.DTFromAST(decl.getType()), parameters);
			currentScope.addSymbol(decl.getName(), lastRoutine);
		} else if(actionNumber == 13) {
			funcInfo.push(new Pair<>(lastRoutine, 0));
		} else if(actionNumber == 19) {
			ArrayDeclPart part = (ArrayDeclPart)target;
			Symbol.ArrayBounds bounds = new Symbol.ArrayBounds(part);
			Symbol s = new Symbol(null, bounds);
			currentScope.addSymbol(part.getName(), s);
			partsAwaitingType.add(s);
		} else if(actionNumber == 46) {
			ArrayDeclPart part = (ArrayDeclPart)target;
			assert part.getLowerBoundary1() <= part.getUpperBoundary1();
			assert !part.getTwoDimensional() || part.getLowerBoundary2() <= part.getUpperBoundary2();
		} else if (actionNumber == 47) {
			MultiDeclarations decl = (MultiDeclarations)target;
			for(Symbol part : partsAwaitingType) {
				part.resultantType = Symbol.DTFromAST(decl.getType());
			}
			partsAwaitingType.clear();
		} else {
			// Do nothing
		}
	}

	private void handleStatementActions(int actionNumber, AST target) {
		assert funcInfo.size() > 0;
		if(actionNumber == 50) {
			assert funcInfo.peek().getValue() > 0;
		} else if(actionNumber == 51) {
			assert funcInfo.peek().getKey().resultantType != None;
		} else if(actionNumber == 52) {
			assert funcInfo.peek().getKey().resultantType == None;
		} else if(actionNumber == 53) {
			ExitStmt exit = (ExitStmt)target;
			assert 0 < exit.getLevel() && exit.getLevel() <= funcInfo.peek().getValue();
		} else {
			Scope s = (Scope) target;
			Stmt retStmt = null;
			for(Stmt stmt : s.getStatements()) {
				if(stmt instanceof ReturnStmt) {
					retStmt = stmt;
					break;
				}
			}
			assert retStmt != null;
		}
	}

	private void handleCallActions(int actionNumber, AST target) {
		if(actionNumber == 40) {
			Symbol sym;
			if(target instanceof FunctionCallExpn) {
				FunctionCallExpn call = (FunctionCallExpn)target;
				 sym = getScopeSymbol(call.getIdent());
			} else {
				// Therefore, this must be an IdentExpn
				IdentExpn call = (IdentExpn)target;
				sym = getScopeSymbol(call.getIdent());
			}
			assert sym.type == Symbol.SymbolType.Routine;
			assert sym.resultantType != None;
		} else if(actionNumber == 41) {
			ProcedureCallStmt call = (ProcedureCallStmt)target;
			Symbol sym = getScopeSymbol(call.getName());
			assert sym.type == Symbol.SymbolType.Routine;
			assert sym.resultantType == None;
		} else if(actionNumber == 42) {
			Symbol sym;
			if(target instanceof IdentExpn) {
				IdentExpn call = (IdentExpn)target;
				sym = getScopeSymbol(call.getIdent());
			} else {
				// So it must be a ProcedureCallStmt
				ProcedureCallStmt call = (ProcedureCallStmt)target;
				sym = getScopeSymbol(call.getName());
			}
			assert sym.parameters != null && sym.parameters.size() == 0;
		} else if(actionNumber == 43) {
			Symbol sym;
			int providedCount;
			if(target instanceof FunctionCallExpn) {
				FunctionCallExpn call = (FunctionCallExpn) target;
				sym = getScopeSymbol(call.getIdent());
				providedCount = call.getArguments().size();
			} else {
				// So it must be a ProcedureCallStmt
				ProcedureCallStmt call = (ProcedureCallStmt)target;
				sym = getScopeSymbol(call.getName());
				providedCount = call.getArguments().size();
			}
			assert sym.parameters != null && sym.parameters.size() == providedCount;
		} else if(actionNumber == 44) {
			if(target instanceof FunctionCallExpn) {
				FunctionCallExpn call = (FunctionCallExpn) target;
				checkingCall = getScopeSymbol(call.getIdent());
			} else {
				// So it must be a ProcedureCallStmt
				ProcedureCallStmt call = (ProcedureCallStmt)target;
				checkingCall = getScopeSymbol(call.getName());
			}
			callArg = 0;
		} else if(actionNumber == 45) {
			callArg++;
		}
	}

	public Symbol getScopeSymbol(String name) {
		return scopes.peek().getValue().getSymbol(name);
	}
}
