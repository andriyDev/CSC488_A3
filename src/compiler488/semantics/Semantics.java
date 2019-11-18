package compiler488.semantics;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.HashMap;

import compiler488.ast.AST;
import compiler488.ast.decl.*;
import compiler488.ast.expn.*;
import compiler488.ast.stmt.*;
import compiler488.symbol.Symbol;
import compiler488.symbol.SymbolTable;
import compiler488.Pair;
import compiler488.ast.type.BooleanType;
import compiler488.ast.type.IntegerType;

import static compiler488.symbol.Symbol.DataType.Int;
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

	private HashMap<Expn, Symbol.DataType> expns;

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

		boolean result = AST.performSemanticAnalysis(this);

		Finalize();
		return result;
	}

	private static String positionString(AST target) {
		Pair<Integer, Integer> pos = target.getPosition();
		return "Line " + (pos.getKey() + 1) + " Column " + (pos.getValue() + 1);
	}

	/**  semanticsInitialize - called once by the parser at the      */
	/*                        start of  compilation                 */
	void Initialize() {
	
	   /*   Initialize the symbol table             */

		symbols = new SymbolTable();
	    symbols.Initialize();

	    expns = new HashMap<>();

	    scopes = new Stack<>();
	    scopes.push(new Pair<>(ScopeType.Program, symbols.globalScope));

	    funcInfo = new Stack<>();
	    funcInfo.push(new Pair<>(null, 0));

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
	public boolean semanticAction( int actionNumber, AST target ) {

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

	   boolean result = true;

	   if(actionNumber == 2) {

	   } else if(actionNumber < 10) { // Scopes and Program
		   result = handleScopeActions(actionNumber, target);
	   } else if(actionNumber < 20 || actionNumber == 46 || actionNumber == 47) { // Declarations
		   result = handleDeclActions(actionNumber, target);
	   } else if (actionNumber < 29) { // Expression Types
	   	   result = handleExprTypeActions(actionNumber, target);
	   } else if (actionNumber < 40) { // Expression Type Checking
	   	   result = handleExprTypeCheckActions(actionNumber, target);
	   } else if(actionNumber >= 55) { // Other
		   result = handleSpecialActions(actionNumber, target);
	   } else if(actionNumber >= 50) { // Statement Checking
		   result = handleStatementActions(actionNumber, target);
	   } else if(actionNumber <= 45) { // Functions, procedures and arguments
		   result = handleCallActions(actionNumber, target);
	   }
	   return result;
	}

	private boolean handleSpecialActions(int actionNumber, AST target) {
		if(funcInfo.size() == 0) {
			throw new RuntimeException("No function info! Cannot modify looping");
		}
		if(scopes.size() == 0) {
			throw new RuntimeException("No scopes!");
		}

		if(actionNumber == 55) {
			funcInfo.set(funcInfo.size() - 1, new Pair<>(funcInfo.peek().getKey(), funcInfo.peek().getValue() + 1));
			return true;
		} else if(actionNumber == 56) {
			funcInfo.set(funcInfo.size() - 1, new Pair<>(funcInfo.peek().getKey(), funcInfo.peek().getValue() - 1));
			if(funcInfo.peek().getValue() < 0) {
				System.err.println("Cannot leave loop - not enough loops started. " + positionString(target));
				return false;
			} else {
				return true;
			}
		} else if(actionNumber == 57) {
			System.err.println("Identifier is not a routine or a variable! " + positionString(target));
			return false;
		} else if(actionNumber == 58) {
			// Does the subscript have the correct number of dimensions for the array.
			SubsExpn subs = (SubsExpn)target;
			Symbol sym = getScopeSymbol(subs.getVariable());
			if(sym == null) {
				System.err.println("Array does not exist! " + positionString(target));
				return false;
			} else if(sym.bounds == null) {
				System.err.println("Symbol is not an array! " + positionString(target));
				return false;
			} else if(sym.bounds.is2d != (subs.numSubscripts() == 2)) {
				System.err.println("Array subscript uses the wrong number of dimensions! " + positionString(target));
				return false;
			} else {
				return true;
			}
		} else if(actionNumber == 59) {
			// Is the expression assignable? Either through := or read.
			if(target instanceof SubsExpn) {
				// The assignment is valid.
				return true;
			} else if(target instanceof IdentExpn) {
				IdentExpn i = (IdentExpn) target;
				Symbol sym = getScopeSymbol(i.getIdent());
				if(sym == null) {
					System.err.println("Symbol does not exist! " + positionString(target));
					return false;
				} else if(sym.type != Symbol.SymbolType.Scalar) {
					System.err.println("Cannot assign to function! " + positionString(target));
					return false;
				} else {
					return true;
				}
			} else {
				System.err.println("Expression not assignable! " + positionString(target));
				return false;
			}
		}
		// Invalid action
		return false;
	}

	private boolean handleScopeActions(int actionNumber, AST target) {
		if(scopes.size() == 0) {
			throw new RuntimeException("No scopes! Cannot perform any scope actions!");
		}

		// If the action is 1, 5, 7, 9, pop the scope.
		if (actionNumber % 2 == 1) {
			// If the scope is a function or procedure, we need to remove the loop number and associated symbol.
			if(scopes.peek().getKey() == ScopeType.Function
				|| scopes.peek().getKey() == ScopeType.Procedure) {
				if(funcInfo.size() == 0) {
					throw new RuntimeException("No funcInfo to pop!");
				} else {
					funcInfo.pop();
				}
			}
			scopes.pop();
			return true;
		}

		if(actionNumber == 0) {
			symbols.allScopes.put(target, symbols.globalScope);
			return true;
		}

		SymbolTable.SymbolScope newScope = symbols.createNewScope(scopes.peek().getValue(), target instanceof RoutineDecl ? getScopeSymbol(((RoutineDecl)target).getName()) : null, target);
		if(actionNumber == 4) {
			scopes.push(new Pair<>(ScopeType.Function, newScope));
			// Action 13 will handle adding to funcInfo
			return true;
		} else if (actionNumber == 6) {
			scopes.push(new Pair<>(ScopeType.Normal, newScope));
			return true;
		} else if (actionNumber == 8) {
			scopes.push(new Pair<>(ScopeType.Procedure, newScope));
			// Action 13 will handle adding to funcInfo
			return true;
		} else {
			return true;
		}
	}

	private boolean handleDeclActions(int actionNumber, AST target) {
		if(scopes.size() == 0) {
			throw new RuntimeException("No scopes!");
		}

		SymbolTable.SymbolScope currentScope = scopes.peek().getValue();
		if(actionNumber == 15) {
			ScalarDecl decl = (ScalarDecl)target;
			try {
				currentScope.addSymbol(decl.getName(), new Symbol(Symbol.DTFromAST(decl.getType())));
				return true;
			} catch (RuntimeException ex) {
				System.err.println(ex.getMessage() + " " + positionString(target));
				return false;
			}
		} else if(actionNumber == 10) {
			ScalarDeclPart part = (ScalarDeclPart)target;
			Symbol s = new Symbol(null);
			try {
				currentScope.addSymbol(part.getName(), s);
				partsAwaitingType.add(s);
				return true;
			} catch(RuntimeException ex) {
				System.err.println(ex.getMessage() + " " + positionString(target));
				return false;
			}
		} else if(actionNumber == 11 || actionNumber == 12
			|| actionNumber == 17 || actionNumber == 18) {
			RoutineDecl decl = (RoutineDecl)target;
			List<Symbol.DataType> parameters = new ArrayList<>();
			for(ScalarDecl param : decl.getParameters()) {
				parameters.add(Symbol.DTFromAST(param.getType()));
			}
			lastRoutine = new Symbol(Symbol.DTFromAST(decl.getType()), parameters);
			decl.sym = lastRoutine;
			try {
				currentScope.addSymbol(decl.getName(), lastRoutine);
				return true;
			} catch(RuntimeException ex) {
				System.err.println(ex.getMessage() + " " + positionString(target));
				lastRoutine = null;
				return false;
			}
		} else if(actionNumber == 13) {
			funcInfo.push(new Pair<>(lastRoutine, 0));
			if(lastRoutine == null) {
				System.err.println("No last routine defined. Routine must have failed initialization. " + positionString(target));
				return false;
			}
			return true;
		} else if(actionNumber == 19) {
			ArrayDeclPart part = (ArrayDeclPart)target;
			Symbol.ArrayBounds bounds = new Symbol.ArrayBounds(part);
			Symbol s = new Symbol(null, bounds);
			try {
				currentScope.addSymbol(part.getName(), s);
				partsAwaitingType.add(s);
				return true;
			} catch (RuntimeException ex) {
				System.err.println(ex.getMessage() + " " + positionString(target));
				return false;
			}
		} else if(actionNumber == 46) {
			ArrayDeclPart part = (ArrayDeclPart)target;
			boolean result = part.getLowerBoundary1() <= part.getUpperBoundary1();
			result &= (!part.getTwoDimensional() || part.getLowerBoundary2() <= part.getUpperBoundary2());
			if(!result) {
				System.err.println("Boundaries of array are invalid! " + positionString(target));
				return false;
			}
			return true;
		} else if (actionNumber == 47) {
			MultiDeclarations decl = (MultiDeclarations)target;
			for(Symbol part : partsAwaitingType) {
				part.resultantType = Symbol.DTFromAST(decl.getType());
			}
			partsAwaitingType.clear();
			return true;
		} else {
			// Do nothing, we say this is ok.
			return true;
		}
	}

	private boolean handleExprTypeActions(int actionNumber, AST target) {
		if (actionNumber == 20) {
			Expn expn = (Expn)target;
			expns.put(expn, Symbol.DataType.Bool);
			return true;
		} else if (actionNumber == 21) {
			Expn expn = (Expn)target;
			expns.put(expn, Symbol.DataType.Int);
			return true;
		} else if (actionNumber == 24) {
			ConditionalExpn cond = (ConditionalExpn)target;
			Expn expnTrue = cond.getTrueValue();
			Expn expnFalse = cond.getFalseValue();
			if(!expns.containsKey(expnTrue) || !expns.containsKey(expnFalse)) {
				throw new RuntimeException("This should not be possible. We should have analyzed both child expressions before this.");
			}
			if(expns.get(expnTrue) != expns.get(expnFalse)) {
				System.err.println("Expressions of conditional do not have matching type. " + positionString(target));
				return false;
			} else {
				expns.put(cond, expns.get(expnTrue));
				return true;
			}
		} else if (actionNumber == 26) {
			IdentExpn expn = (IdentExpn)target;
			if(scopes.size() == 0) {
				throw new RuntimeException("No scopes!");
			}
			Symbol sym = getScopeSymbol(expn.getIdent());
			if(sym == null) {
				System.err.println("Variable not declared in scope! " + positionString(target));
				return false;
			} else if(sym.type != Symbol.SymbolType.Scalar) {
				System.err.println("Variable is not a scalar! " + positionString(target));
				return false;
			} else {
				expns.put(expn, sym.resultantType);
				return true;
			}
		} else if (actionNumber == 27) {
			SubsExpn expn = (SubsExpn) target;
			if(scopes.size() == 0) {
				throw new RuntimeException("No scopes!");
			}
			Symbol sym = getScopeSymbol(expn.getVariable());
			if(sym == null) {
				System.err.println("Variable not declared in scope! " + positionString(target));
				return false;
			} else if(sym.type != Symbol.SymbolType.Array) {
				System.err.println("Variable is not declared as an array! " + positionString(target));
				return false;
			} else {
				expns.put(expn, sym.resultantType);
				return true;
			}
		} else if (actionNumber == 28) {
			if(scopes.size() == 0) {
				throw new RuntimeException("No scopes!");
			}
			Symbol sym;
			Expn expn = (Expn) target;
			if(expn instanceof FunctionCallExpn) {
				FunctionCallExpn call = (FunctionCallExpn) expn;
				sym = getScopeSymbol(call.getIdent());
			} else {
				IdentExpn call = (IdentExpn) expn;
				sym = getScopeSymbol(call.getIdent());
			}

			if(sym == null) {
				System.err.println("Function not declared in scope! " + positionString(target));
				return false;
			} else if(sym.type != Symbol.SymbolType.Routine) {
				System.err.println("Function is not declared as a routine! " + positionString(target));
				return false;
			} else if(sym.resultantType == null) {
				System.err.println("Symbol is actually a procedure, not a function! " + positionString(target));
				return false;
			} else {
				expns.put(expn, sym.resultantType);
				return true;
			}
		}
		return true;
	}

	private boolean handleExprTypeCheckActions(int actionNumber, AST target) {
		if (actionNumber == 30) {
			Expn expn = (Expn) target;
			if (expns.containsKey(expn)) {
				if(expns.get(expn) != Symbol.DataType.Bool) {
					System.err.println("Type of expression is not boolean. " + positionString(target));
					return false;
				} else {
					return true;
				}
			} else {
				System.err.println("This expression isn't defined. " + positionString(target));
				return false;
			}
		} else if (actionNumber == 31) {
			Expn expn = (Expn) target;
			if (expns.containsKey(expn)) {
				if(expns.get(expn) != Symbol.DataType.Int) {
					System.err.println("Type of expression is not integer. " + positionString(target));
					return false;
				} else {
					return true;
				}
			} else {
				System.err.println("This expression isn't defined. " + positionString(target));
				return false;
			}
		} else if (actionNumber == 32) {
			Expn expnLeft = ((BinaryExpn)target).getLeft();
			Expn expnRight = ((BinaryExpn)target).getRight();
			if (expns.containsKey(expnLeft) && expns.containsKey(expnRight)) {
				if(expns.get(expnLeft) != expns.get(expnRight))  {
					System.err.println("Types of expressions do not match. " + positionString(target));
					return false;
				} else {
					return true;
				}
			} else {
				System.err.println("These expressions aren't defined. " + positionString(target));
				return false;
			}
		} else if (actionNumber == 33) {
			Expn expnTrue = ((ConditionalExpn)target).getTrueValue();
			Expn expnFalse = ((ConditionalExpn)target).getFalseValue();
			if (expns.containsKey(expnTrue) && expns.containsKey(expnFalse)) {
				if(expns.get(expnTrue) != expns.get(expnFalse))  {
					System.err.println("Types of expressions do not match. " + positionString(target));
					return false;
				} else {
					return true;
				}
			} else {
				System.err.println("These expressions aren't defined. " + positionString(target));
				return false;
			}
		} else if (actionNumber == 34) {
			Expn stmtLeft = ((AssignStmt)target).getLval();
			Expn stmtRight = ((AssignStmt)target).getRval();
			if (expns.containsKey(stmtLeft) && expns.containsKey(stmtRight)) {
				if(expns.get(stmtLeft) != expns.get(stmtRight))  {
					System.err.println("Types of assignment do not match. " + positionString(target));
					return false;
				} else {
					return true;
				}
			} else {
				System.err.println("These expressions aren't defined. " + positionString(target));
				return false;
			}
		} else if (actionNumber == 35) {
			Expn returnStmt = (Expn)target;
			if (!expns.containsKey(returnStmt)) {
				System.err.println("Expression is not defined. " + positionString(target));
				return false;
			} else if(funcInfo.size() == 0) {
				System.err.println("No function info!");
				return false;
			} else if(funcInfo.peek().getKey() == null) {
				System.err.println("Cannot return here! " + positionString(target));
				return false;
			} else if(funcInfo.peek().getKey().resultantType != expns.get(returnStmt)) {
				System.err.println("Expression does not have the return type of the function. " + positionString(target));
				return false;
			} else {
				return true;
			}
		} else if (actionNumber == 36) {
			Expn expn = (Expn)target;
			if(checkingCall == null) {
				System.err.println("Not checking any call arguments. " + positionString(target));
				return false;
			} else if(!expns.containsKey(expn)) {
				System.err.println("Expression has not been defined. " + positionString(target));
				return false;
			} else if(callArg >= checkingCall.parameters.size()) {
				// Do nothing, this will be reported later.
				return false;
			} else if(checkingCall.parameters.get(callArg) != expns.get(expn)) {
				System.err.println("Argument has the wrong type for function parameter. " + positionString(target));
				return false;
			} else {
				return true;
			}
		} else if (actionNumber == 37) {
			IdentExpn expn = (IdentExpn)target;
			if(scopes.size() == 0) {
				throw new RuntimeException("No scopes!");
			}
			Symbol sym = getScopeSymbol(expn.getIdent());
			if(sym == null) {
				System.err.println("Symbol not defined! " + positionString(target));
				return false;
			} else if(sym.type != Symbol.SymbolType.Scalar) {
				System.err.println("Symbol not declared as a scalar. " + positionString(target));
				return false;
			} else {
				return true;
			}
		} else if (actionNumber == 38) {
			SubsExpn expn = (SubsExpn)target;
			if(scopes.size() == 0) {
				throw new RuntimeException("No scopes!");
			}
			Symbol sym = getScopeSymbol(expn.getVariable());
			if(sym == null) {
				System.err.println("Symbol not defined! " + positionString(target));
				return false;
			} else if(sym.type != Symbol.SymbolType.Array) {
				System.err.println("Symbol not declared as an array. " + positionString(target));
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	private boolean handleStatementActions(int actionNumber, AST target) {
		if(funcInfo.size() == 0) {
			throw new RuntimeException("No function info stored!");
		}
		if(actionNumber == 50) {
			if(funcInfo.peek().getValue() <= 0) {
				System.err.println("Cannot exit from loops, as we are not directly in a loop! " + positionString(target));
				return false;
			} else {
				return true;
			}
		} else if(actionNumber == 51) {
			if(funcInfo.peek().getKey() == null || funcInfo.peek().getKey().resultantType == None) {
				System.err.println("This is not a function! Cannot return with result. " + positionString(target));
				return false;
			} else {
				return true;
			}
		} else if(actionNumber == 52) {
			if(funcInfo.peek().getKey() == null || funcInfo.peek().getKey().resultantType != None) {
				System.err.println("This is not a procedure! Cannot return with no result. " + positionString(target));
				return false;
			} else {
				return true;
			}
		} else if(actionNumber == 53) {
			ExitStmt exit = (ExitStmt)target;
			if(!(0 < exit.getLevel() && exit.getLevel() <= funcInfo.peek().getValue())) {
				System.err.println("Invalid number of loop levels to exit provided! " + positionString(target));
				return false;
			} else {
				return true;
			}
		} else {
			Scope s = (Scope) target;
			if(!s.hasReturn()) {
				System.err.println("Control flow of function can reach end of function. Needs return statement. " + positionString(target));
				return false;
			} else {
				return true;
			}
		}
	}

	private boolean handleCallActions(int actionNumber, AST target) {
		if(scopes.size() == 0) {
			throw new RuntimeException("No scopes to peek at!");
		}
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
			if(sym == null) {
				System.err.println("Function not found! " + positionString(target));
				return false;
			} else if(sym.type != Symbol.SymbolType.Routine || sym.resultantType == None) {
				System.err.println("Symbol is not a function! " + positionString(target));
				return false;
			} else {
				return true;
			}
		} else if(actionNumber == 41) {
			ProcedureCallStmt call = (ProcedureCallStmt)target;
			Symbol sym = getScopeSymbol(call.getName());
			if(sym == null) {
				System.err.println("Procedure not found! " + positionString(target));
				return false;
			} else if(sym.type != Symbol.SymbolType.Routine || sym.resultantType != None) {
				System.err.println("Symbol is not a procedure! " + positionString(target));
				return false;
			} else {
				return true;
			}
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
			if(sym == null) {
				System.err.println("Function/Procedure not found! " + positionString(target));
				return false;
			} else if(sym.parameters == null) {
				System.err.println("Symbol is not a function/procedure! " + positionString(target));
				return false;
			} else if(sym.parameters.size() != 0) {
				System.err.println("Function/procedure has parameters - cannot call without any parameters. " + positionString(target));
				return false;
			} else {
				return true;
			}
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
			if(sym == null) {
				System.err.println("Function/Procedure not found! " + positionString(target));
				return false;
			} else if(sym.parameters == null) {
				System.err.println("Symbol is not a function/procedure! " + positionString(target));
				return false;
			} else if(sym.parameters.size() != providedCount) {
				System.err.println("Function/procedure has a different number of arguments than what was provided. " + positionString(target));
				return false;
			} else {
				return true;
			}
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
			if(checkingCall == null) {
				System.err.println("Symbol not found for function/procedure to check. " + positionString(target));
				return false;
			} else {
				return true;
			}
		} else if(actionNumber == 45) {
			callArg++;
			return true;
		}
		System.err.println("Unknown action number: " + actionNumber + ". " + positionString(target));
		return false;
	}

	public Symbol getScopeSymbol(String name) {
		return scopes.peek().getValue().getSymbol(name);
	}

	public SymbolTable getSymbols() {
		return symbols;
	}
}
