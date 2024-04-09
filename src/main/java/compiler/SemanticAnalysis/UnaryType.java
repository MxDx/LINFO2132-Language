package compiler.SemanticAnalysis;

import compiler.Lexer.VarType;

public class UnaryType extends Type {
    private VarType type;

    public UnaryType(VarType type) {
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
    public String toString() {
        return type.toString();
    }
}
