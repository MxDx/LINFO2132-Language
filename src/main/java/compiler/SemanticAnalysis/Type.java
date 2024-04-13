package compiler.SemanticAnalysis;

import compiler.Lexer.VarType;

public abstract class Type {
    protected VarType type;
    public Type(VarType type) {
        this.type = type;
    }

    public VarType getType() {
        return type;
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
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public String toString() {
        return type.toString();
    }

}
