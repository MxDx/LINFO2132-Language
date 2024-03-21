package compiler.Parser;

import compiler.Lexer.VarType;

public class Parameter extends Node{
    VarType type;
    String identifier;
    public Parameter(Parser parser) {
        super(parser);
    }
    public Parameter parse() throws Exception {
        if(parser.currentToken.getType().equals("Identifier")) {
            type = new VarType(parser.currentToken.getValue());
        }
        else {
            if (parser.currentToken.getType().equals("VarType")) {
                type = new VarType(parser.currentToken.getValue());
            } else {
                throw new Exception("Invalid Return Type");
            }
        }
        parser.getNext();
        if (!parser.currentToken.getType().equals("Identifier")) {
            throw new Exception("Invalid Parameter Name");
        }
        identifier = parser.currentToken.getValue();
        parser.getNext();
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
