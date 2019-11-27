package compiler488.codegen;

import java.io.*;
import java.util.*;

import compiler488.ast.AST;
import compiler488.ast.decl.*;
import compiler488.ast.expn.*;
import compiler488.ast.stmt.*;
import compiler488.ast.type.BooleanType;
import compiler488.ast.type.IntegerType;

import compiler488.compiler.Main;
import compiler488.runtime.Machine;
import compiler488.runtime.MemoryAddressException;

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

	private List<Short> instructions;

	/**
	 * Constructor to initialize code generation
	 */
	public CodeGen(Machine machine) {
		this.machine = machine;

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
		instructions = new ArrayList<Short>();

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

	public void generate(Program AST) throws MemoryAddressException, Exception{

		Initialize();
		AST.performCodeGeneration(this);
		Finalize();
		
	}

	/**
	 * Procedure to implement code generation based on code generation action
	 * number
	 *
	 * @param actionNumber
	 *            code generation action to perform
	 */
	public void generateCode(int actionNumber, AST target) {
		if (traceCodeGen) {
			// output the standard trace stream
			Main.traceStream.println("CodeGen: C" + actionNumber);
		}

		/****************************************************************/
		/* Code to implement the code generation actions GOES HERE */
		/* This dummy code generator just prints the actionNumber */
		/* In Assignment 5, you'll implement something more interesting */
		/*                                                               */
		/* FEEL FREE TO ignore or replace this procedure */
		/****************************************************************/
		
		System.out.println("Codegen: C" + actionNumber);
	}


	public void generateCodeForExpn(int actionNumber, AST target) {
		if (actionNumber >= 40 || actionNumber <= 43) {
			// conditionalExpn
			// TODO
		}

		if (actionNumber == 78 || actionNumber == 79) {
			// BoolConstExpn
			instructions.add(Machine.PUSH);

			if (actionNumber == 78)
				instructions.add(Machine.MACHINE_FALSE);
			else 
				instructions.add(Machine.MACHINE_TRUE);
		}
		else if (actionNumber == 66 || actionNumber == 67) {
			// BoolExpn
			if (actionNumber == 67) {
				instructions.add(Machine.OR);
			}
			else {
				// add left and right value
				instructions.add(Machine.ADD);
				// compare with 2, which is true + true
				instructions.add(Machine.PUSH);
				instructions.add((short) 2);
				instructions.add(Machine.EQ);
			}
		}
		else if (actionNumber >= 61 && actionNumber <= 64) {
			// ArithExpn
			if (actionNumber == 61) 
				instructions.add(Machine.ADD);
			else if (actionNumber == 62) 
				instructions.add(Machine.SUB);
			else if (actionNumber == 63) 
				instructions.add(Machine.MUL);
			else if (actionNumber == 64)
				instructions.add(Machine.DIV);
		}
		else if (actionNumber >= 71 && actionNumber <= 74) {
			// CompareExpn
			if (actionNumber == 71) {
				// CompareExpn less than
				instructions.add(Machine.LT);
			} 
			else if (actionNumber == 72) {
				// CompareExpn less than or equal
				// which is not greater than
				instructions.add(Machine.SWAP);
				instructions.add(Machine.LT);
	
				instructions.add(Machine.PUSH);
				instructions.add(Machine.MACHINE_FALSE);
				instructions.add(Machine.EQ);
			}
			else if (actionNumber == 73) {
				// CompareExpn greater than
				// swap the value and check less than
				instructions.add(Machine.SWAP);
				instructions.add(Machine.LT);
			}
			else if (actionNumber == 74) {
				// CompareExpn greater than or equal
				// which is not less than
				instructions.add(Machine.LT);
				instructions.add(Machine.PUSH);
				instructions.add(Machine.MACHINE_FALSE);
				instructions.add(Machine.EQ);
			}
		}
		else if (actionNumber == 69 || actionNumber == 70) {
			// 69 -- equalsExpn - OP_EQUAL

			// 70 -- equalsExpn - OP_NOT_EQUAL
			// generate code for op_equal and add then not

			instructions.add(Machine.EQ);

			if (actionNumber == 70) {
				instructions.add(Machine.PUSH);
				instructions.add(Machine.MACHINE_FALSE);
				instructions.add(Machine.EQ);
			}
		}
		else if (actionNumber == 65) {
			// NotExpn
			instructions.add(Machine.PUSH);
			instructions.add(Machine.MACHINE_FALSE);
			instructions.add(Machine.EQ);
		}
		else if (actionNumber == 52) {	
			// textConstExpn
			String outputStr = ((TextConstExpn)target).getValue();
			for (char ch : outputStr.toCharArray() ) {
				instructions.add(Machine.PUSH);
				instructions.add((short) ch);
				instructions.add(Machine.PRINTC);
			}			
		}
		else if (actionNumber == 53) {
			// new line
			instructions.add(Machine.PUSH);
			instructions.add(((short)'\n'));
			instructions.add(Machine.PRINTC);
		}	
		else if (actionNumber == 80) {
			// IntConstExpn
			int intConst = ((IntConstExpn) target).getValue();
			instructions.add(Machine.PUSH);
			instructions.add((short) intConst);
		}
		else if (actionNumber == 60) {
			// UnaryMinusExpn
			instructions.add(Machine.NEG);
		}
	}

	// ADDITIONAL FUNCTIONS TO IMPLEMENT CODE GENERATION GO HERE
	
}
