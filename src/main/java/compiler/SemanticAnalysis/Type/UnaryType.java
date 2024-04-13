package compiler.SemanticAnalysis.Type;

import compiler.Lexer.VarType;

public class UnaryType extends Type {

    public UnaryType(VarType type) {
        super(type);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("\"<");
        str.append(type.getValue());
        str.append(">\"");
        return str.toString();
    }
}
