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
        Declaration declaration = new Declaration(parser).parse();
        returnType = declaration.type;
        name = new Identifier(declaration.identifier);
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
