package compiler488.codegen;

import java.util.*;

import compiler488.Pair;
import compiler488.ast.AST;
import compiler488.ast.decl.RoutineDecl;
import compiler488.ast.stmt.Program;
import compiler488.compiler.Main;
import compiler488.runtime.Machine;
import compiler488.runtime.MemoryAddressException;
import compiler488.symbol.Symbol;
import compiler488.symbol.SymbolTable;

/**
 * CodeGenerator.java
 *
 * <pre>
 *  Code Generation Conventions
 *
 *  To simplify the course project, this code generator is
 *  designed to compile directly to pseudo machine memory
 *  which is available as the private array memory[]
 *
 *  It is assumed that the code generator places instructions
 *  in memory in locations
 *
 *      memory[ 0 .. startMSP - 1 ]
 *
 *  The code generator may also place instructions and/or
 *  constants in high memory at locations (though this may
 *  not be necessary)
 *      memory[ startMLP .. Machine.MEMORY_SIZE - 1 ]
 *
 *  During program exection the memory area
 *      memory[ startMSP .. startMLP - 1 ]
 *  is used as a dynamic stack for storing activation records
 *  and temporaries used during expression evaluation.
 *  A hardware exception (stack overflow) occurs if the pointer
 *  for this stack reaches the memory limit register (mlp).
 *
 *  The code generator is responsible for setting the global
 *  variables:
 *      startPC         initial value for program counter
 *      startMSP        initial value for msp
 *      startMLP        initial value for mlp
 * </pre>
 *
 * @author <B> PUT YOUR NAMES HERE </B>
 */

public class CodeGen {
	/** initial value for memory stack pointer */
	private short startMSP;
	/** initial value for program counter */
	private short startPC;
	/** initial value for memory limit pointer */
	private short startMLP;

	/** flag for tracing code generation */
	private boolean traceCodeGen = Main.traceCodeGen;

	private Machine machine;
	private SymbolTable symbols;

	private Stack<SymbolTable.SymbolScope> scopeStack;
	private Stack<Stack<Pair<AST, SymbolTable.SymbolScope>>> loopStack;
	private HashMap<Integer, List<Integer>> awaitingLoops;

	private Queue<AST> routinesToGenerate;

	private HashMap<Symbol, Integer> routinePointers;
	private HashMap<Symbol, List<Integer>> awaitingRoutinePointer;
	private List<Integer> awaitingExitCode;

	private int memoryPosition = 0;

	/**
	 * Constructor to initialize code generation
	 */
	public CodeGen(Machine machine, SymbolTable symbols) {
		this.machine = machine;
		this.symbols = symbols;

		// YOUR CONSTRUCTOR GOES HERE.
	}

	// Utility procedures used for code generation GO HERE.

	/**
	 * Additional intialization for gode generation. Called once at the start of
	 * code generation. May be unnecesary if constructor does everything.
	 */

	/** Additional initialization for Code Generation (if required) */
	void Initialize() {
		/********************************************************/
		/* Initialization code for the code generator GOES HERE */
		/* This procedure is called once before codeGeneration */
		/*                                                      */
		/********************************************************/

		scopeStack = new Stack<>();
		loopStack = new Stack<>();

		routinesToGenerate = new LinkedList<>();

		routinePointers = new HashMap<>();
		awaitingRoutinePointer = new HashMap<>();

		awaitingLoops = new HashMap<>();

		memoryPosition = 0;
	}

	/**
	 * Perform any required cleanup at the end of code generation. Called once
	 * at the end of code generation.
	 *
	 * @throws MemoryAddressException
	 *             from Machine.writeMemory
	 */
	void Finalize() throws MemoryAddressException {
		/********************************************************/
		/* Finalization code for the code generator GOES HERE. */
		/*                                                      */
		/* This procedure is called once at the end of code */
		/* generation */
		/********************************************************/

		// REPLACE THIS CODE WITH YOUR OWN CODE
		// THIS CODE generates a single HALT instruction
		// as an example.
		machine.setPC((short) 0); /* where code to be executed begins */
		machine.setMSP((short) 1); /* where memory stack begins */
		machine.setMLP((short) (Machine.MEMORY_SIZE - 1));
		/* limit of stack */
		machine.writeMemory((short) 0, Machine.HALT);
	}

	public boolean generate(Program AST) {
		Initialize();
		routinesToGenerate.add(AST);

		while(!routinesToGenerate.isEmpty()) {
			AST target = routinesToGenerate.remove();
			if(target instanceof RoutineDecl) {
				int routineAddress = getPosition();
				// TODO: Store this address in routinePointers and clear out the awaiting routine pointer stuff.
				awaitingExitCode = new LinkedList<>();
			}
			target.performCodeGeneration(this);
		}

		try {
			Finalize();
		} catch (MemoryAddressException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void addInstruction(int code) {
		try {
			machine.writeMemory((short) memoryPosition, (short)code);
			memoryPosition++;
		} catch (MemoryAddressException ex) {
			throw new RuntimeException(ex);
		}
	}

	public int getPosition() {
		return memoryPosition;
	}

	public void setInstruction(int address, int code) {
		try {
			machine.writeMemory((short) address, (short) code);
		} catch (MemoryAddressException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void enterScope(SymbolTable.SymbolScope scope) {
		scopeStack.push(scope);
		boolean isMajorScope;
		if(scope.lexicalLevel == 0) {
			isMajorScope = true;
			loopStack.push(new Stack<>());
		} else {
			isMajorScope = scope.lexicalLevel - scope.parent.lexicalLevel == 1;
			if(isMajorScope) {
				loopStack.push(new Stack<>());
			}
		}

		int space = scope.offset - (isMajorScope ? 0 : scope.parent.offset);
		// Parameters to a routine should not be allocated, since that will be done at routine call.
		if(scope.creatingSymbol != null) {
			space -= scope.creatingSymbol.parameters.size();
		}

		// No reason to do this stuff if there is no space.
		if (space > 0){
			addInstruction(Machine.PUSH);
			addInstruction(0);
			addInstruction(Machine.PUSH);
			addInstruction(space);
			addInstruction(Machine.DUPN);
		}
	}

	public void exitScope(SymbolTable.SymbolScope scope) {
		boolean isMajorScope;
		scopeStack.pop();
		if(scope.lexicalLevel == 0) {
			isMajorScope = true;
			loopStack.pop();
		} else {
			isMajorScope = scope.lexicalLevel - scope.parent.lexicalLevel == 1;
			if(isMajorScope) {
				loopStack.pop();
			}
		}

		// For major scopes, we remove all variables by using the current scope link
		// This is because it makes it easier to handle return statements and such
		if(isMajorScope) {
			addInstruction(Machine.PUSHMT);
			addInstruction(Machine.ADDR);
			addInstruction(scope.lexicalLevel);
			addInstruction(scope.creatingSymbol != null && scope.creatingSymbol.resultantType == null ? -1 : 0);
			addInstruction(Machine.SUB);
			addInstruction(Machine.POPN);
		} else {
			// For minor scopes, we can easily calculate the number of statements we have to leave in order to make it work.
			int space = scope.offset - scope.parent.offset;
			addInstruction(Machine.PUSH);
			addInstruction(space);
			addInstruction(Machine.POPN);
		}
	}

	public void enterLoop(AST loop) {
		// When entering a loop, we want to keep track of which scope to return to, which is the scope that contains the loop statement
		loopStack.peek().push(new Pair<>(loop, scopeStack.peek()));
	}

	public void exitLoop(int address) {
		// When leaving a loop, we just leave the top most.
		loopStack.peek().pop();
		if(awaitingLoops.containsKey(loopStack.peek().size())) {
			for(Integer awaitAddr : awaitingLoops.get(loopStack.peek().size())) {
				setInstruction(awaitAddr, address);
			}
			// We don't want to set those instructions again.
			awaitingLoops.remove(loopStack.peek().size());
		}
	}

	public int getLoopExitSize(int count) {
		assert loopStack.peek().size() <= count;
		// Get the scope we want to return to.
		SymbolTable.SymbolScope jumpToScope = loopStack.peek().get(loopStack.size() - count).getValue();
		// Get the current scope.
		SymbolTable.SymbolScope currentScope = scopeStack.peek();
		// Take the difference.
		return currentScope.offset - jumpToScope.offset;
	}

	public int getCurrentLexicalLevel() {
		return loopStack.size() - 1;
	}

	public SymbolTable getSymbols() {
		return symbols;
	}

	public Queue<AST> getRoutinesToGenerate() {
		return routinesToGenerate;
	}

	public SymbolTable.SymbolScope getCurrentScope() { return scopeStack.peek(); }

	public int getRoutineAddress(Symbol sym, int targetAddress) {
		if(routinePointers.containsKey(sym)) {
			return routinePointers.get(sym);
		}
		if(awaitingRoutinePointer.containsKey(sym)) {
			awaitingRoutinePointer.put(sym, new LinkedList<>());
		}
		awaitingRoutinePointer.get(sym).add(targetAddress);
		return -1;
	}

	public void awaitLoopAddress(int depth, int address) {
		int targetLoop = loopStack.peek().size() - depth;
		awaitingLoops.putIfAbsent(targetLoop, new LinkedList<>());
		awaitingLoops.get(targetLoop).add(address);
	}

	public void awaitExitCode(int address) {
		awaitingExitCode.add(address);
	}
}
