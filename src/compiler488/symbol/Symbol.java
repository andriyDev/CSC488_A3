package compiler488.symbol;

import compiler488.ast.AST;
import compiler488.ast.decl.ArrayDeclPart;
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

    public static class ArrayBounds {
        public int minX;
        public int maxX;
        public int minY;
        public int maxY;
        public boolean is2d;

        public ArrayBounds(ArrayDeclPart decl) {
            this.minX = decl.getLowerBoundary1();
            this.maxX = decl.getUpperBoundary1();
            this.is2d = decl.getTwoDimensional();
            if(this.is2d) {
                this.minY = decl.getLowerBoundary2();
                this.maxY = decl.getUpperBoundary2();
            }
        }

        public int getSize() {
            if (this.is2d) {
                return (maxX - minX + 1) * (maxY - minY + 1);
            } else {
                return maxX - minX + 1;
            }
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

    public int getDataSize() {
        switch(this.type) {
            case Routine:
                return 0;
            case Scalar:
                return 1;
            default:
                return this.bounds.getSize();
        }
    }
}
