package compiler.SemanticAnalysis.Type;

import compiler.Lexer.VarType;

public class UnaryType extends Type {

    public UnaryType(VarType type) {
        super(type);
    }

    public UnaryType(String type) {
        super(new VarType(type));
    }

    @Override
    public String toString() {
        return getType().toString();
    }
}
