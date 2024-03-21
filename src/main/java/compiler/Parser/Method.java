package compiler.Parser;


import compiler.Lexer.Identifier;
import compiler.Lexer.VarType;

import java.util.ArrayList;

public class Method extends Node{
    public Identifier name;
    public VarType returnType;
    ArrayList<Parameter> parameters;
    public Block block;

    public Method(Parser parser) throws Exception {
        super(parser);
        parser.getNext();
    }

    public Node parse() throws Exception {
        if(parser.currentToken.getType().equals("Identifier")) {
            returnType = new VarType(parser.currentToken.getValue());
        }
        else {
            if (parser.currentToken.getType().equals("VarType")) {
                returnType = new VarType(parser.currentToken.getValue());
            } else {
                throw new Exception("Invalid Return Type");
            }
        }
        parser.getNext();
        if (!parser.currentToken.getType().equals("Identifier")) {
            throw new Exception("Invalid Method Name");
        }
        name = new Identifier(parser.currentToken.getValue());
        parser.getNext();
        if (!parser.currentToken.getValue().equals("(")) {
            throw new Exception("Invalid Method Declaration");
        }
        parser.match(Parser.OPEN_PARENTHESES);
        parameters = new ArrayList<>();
        while (!parser.currentToken.getValue().equals(")")) {
            parameters.add(new Parameter(parser).parse());
            if (parser.currentToken.getValue().equals(",")) {
                parser.getNext();
            }
        }
        parser.match(Parser.CLOSE_PARENTHESES);
        block = new Block(parser).parse();
        return this;
    }
    @Override
    public String toString() {
        StringBuilder parametersString = new StringBuilder();
        for (Parameter parameter : parameters) {
            parametersString.append("{"+parameter.toString()+"}").append(",");
        }
        parametersString = new StringBuilder(parametersString.substring(0, parametersString.length() - 1));
        return "\"Method\": {\n"
                + "\"name\": \""+ name.toString() + "\",\n"
                + "\"returnType\": \""+ returnType.toString() + "\",\n"
                + "\"parameters\": ["+ parametersString + "],\n"
                + "\"block\": " + block.toString() + "\n"
                + '}';
    }
}
