package compiler.Parser;

import compiler.Lexer.VarType;

public class Parameter extends Node {
    public VarType type;
    public String identifier;

    public Parameter(Parser parser) {
        super(parser);
    }

    public VarType getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Parameter parse() throws Exception {
        Declaration declaration = new Declaration(parser).parse();
        type = declaration.type;
        identifier = declaration.identifier;
        return this;
    }

    @Override
    public String toString() {
        return "\"Parameter\": {\n"
                + "\"type\": \""+ type.toString() + "\",\n"
                + "\"identifier\": \""+ identifier + "\"\n"
                + '}';
    }

    public int getVectorDepth() {
        return type.getVectorDepth();
    }
}
