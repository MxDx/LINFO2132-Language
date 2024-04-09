package compiler.SemanticAnalysis;

import compiler.Lexer.VarType;

public abstract class Type {
    private VarType type;
    private int vectorDepth;
    public Type(VarType type) {
        this.type = type;
        this.vectorDepth = 0;
    }

    public VarType getType() {
        return type;
    }

    public int getVectorDepth() {
        return vectorDepth;
    }

    public void setVectorDepth(int vectorDepth) {
        this.vectorDepth = vectorDepth;
    }

    public boolean isVector() {
        return vectorDepth > 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UnaryType) {
            UnaryType unaryType = (UnaryType) obj;
            return type.equals(unaryType.getType());
        }
        return false;
    }

    @Override
    public String toString() {
        return type.toString();
    }

    @Override
    public Type clone() {
        return new UnaryType(type);
    }

}
