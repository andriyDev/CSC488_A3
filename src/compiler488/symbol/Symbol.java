package compiler488.symbol;

import compiler488.ast.AST;
import compiler488.ast.type.BooleanType;
import compiler488.ast.type.IntegerType;

import java.util.List;

public class Symbol {
    public enum DataType {
        Int, Bool, None
    }

    public static DataType DTFromAST(AST ast) {
        if(ast == null) {
            return DataType.None;
        }
        else if(ast instanceof BooleanType) {
            return DataType.Bool;
        } else if(ast instanceof IntegerType) {
            return DataType.Int;
        } else {
            return DataType.None;
        }
    }

    public enum SymbolType {
        Scalar, Array, Routine
    }

    public class ArrayBounds {
        int minX;
        int maxX;
        int minY;
        int maxY;
        boolean is2d;

        public ArrayBounds(int minX, int maxX) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = 0;
            this.maxY = 0;
            this.is2d = false;
        }

        public ArrayBounds(int minX, int maxX, int minY, int maxY) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
            this.is2d = true;
        }
    }

    public DataType resultantType;
    public SymbolType type;
    public ArrayBounds bounds;
    public List<DataType> parameters;

    public Symbol(DataType resultantType) {
        this.resultantType = resultantType;
        this.type = SymbolType.Scalar;

        this.bounds = null;
        this.parameters = null;
    }

    public Symbol(DataType resultantType, ArrayBounds bounds) {
        this.resultantType = resultantType;
        this.type = SymbolType.Array;
        this.bounds = bounds;

        this.parameters = null;
    }

    public Symbol(DataType resultantType, List<DataType> parameters) {
        this.resultantType = resultantType;
        this.type = SymbolType.Routine;
        this.parameters = parameters;

        this.bounds = null;
    }
}
