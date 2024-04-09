package compiler.SemanticAnalysis;

import compiler.Lexer.VarType;

public class UnaryType extends Type {

    public UnaryType(VarType type) {
        super(type);
    }

    public UnaryType(String type) {
        super(new VarType(type));
    }
}
