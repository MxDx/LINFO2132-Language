package compiler.SemanticAnalysis.Type;

import compiler.Lexer.VarType;

public class UnaryType extends Type {

    public UnaryType(VarType type) {
        super(type);
    }

    @Override
    public String toString() {
        return getType().toString();
    }
}
