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
	private Stack<Pair<AST, SymbolTable.SymbolScope>> loopStack;
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
	 */
	void Finalize() {
		/********************************************************/
		/* Finalization code for the code generator GOES HERE. */
		/*                                                      */
		/* This procedure is called once at the end of code */
		/* generation */
		/********************************************************/
		machine.setPC((short) 0); /* where code to be executed begins */
		machine.setMSP((short) memoryPosition); /* where memory stack begins */
		machine.setMLP((short) (Machine.MEMORY_SIZE - 1));
	}

	public boolean generate(Program AST) {
		Initialize();
		routinesToGenerate.add(AST);

		while(!routinesToGenerate.isEmpty()) {
			AST target = routinesToGenerate.remove();
			if(target instanceof RoutineDecl) {
				int routineAddress = getPosition();
				routinePointers.put(((RoutineDecl)target).sym, routineAddress);
				for(Integer addr : awaitingRoutinePointer.get(((RoutineDecl)target).sym)) {
					setInstruction(addr, routineAddress);
				}
				awaitingRoutinePointer.remove(((RoutineDecl)target).sym);
				awaitingExitCode = new LinkedList<>();
			}
			target.performCodeGeneration(this);
		}

		Finalize();
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
		if(scope.parent == null) {
			isMajorScope = true;
			loopStack = new Stack<>();
		} else {
			isMajorScope = scope.lexicalLevel - scope.parent.lexicalLevel == 1;
			if(isMajorScope) {
				loopStack = new Stack<>();
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
		if(scope.parent == null) {
			isMajorScope = true;
		} else {
			isMajorScope = scope.lexicalLevel - scope.parent.lexicalLevel == 1;
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
			if(space != 0) {
				addInstruction(Machine.PUSH);
				addInstruction(space);
				addInstruction(Machine.POPN);
			}
		}
	}

	public void enterLoop(AST loop) {
		// When entering a loop, we want to keep track of which scope to return to, which is the scope that contains the loop statement
		loopStack.push(new Pair<>(loop, scopeStack.peek()));
	}

	public void exitLoop(int address) {
		// When leaving a loop, we just leave the top most.
		loopStack.pop();
		if(awaitingLoops.containsKey(loopStack.size())) {
			for(Integer awaitAddr : awaitingLoops.get(loopStack.size())) {
				setInstruction(awaitAddr, address);
			}
			// We don't want to set those instructions again.
			awaitingLoops.remove(loopStack.size());
		}
	}

	public int getLoopExitSize(int count) {
		assert loopStack.size() <= count;
		// Get the scope we want to return to.
		SymbolTable.SymbolScope jumpToScope = loopStack.get(loopStack.size() - count).getValue();
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
		int targetLoop = loopStack.size() - depth;
		awaitingLoops.putIfAbsent(targetLoop, new LinkedList<>());
		awaitingLoops.get(targetLoop).add(address);
	}

	public void awaitExitCode(int address) {
		awaitingExitCode.add(address);
	}

	public void resolveExitCode(int address) {
		for(Integer await : awaitingExitCode) {
			setInstruction(await, address);
		}
		awaitingExitCode.clear();
	}
}
