package compiler.Parser;

import compiler.Lexer.VarType;

public class Parameter extends Node{
    VarType type;
    String identifier;
    public Parameter(Parser parser) {
        super(parser);
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
}
