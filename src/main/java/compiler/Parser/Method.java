package compiler.Parser;


import compiler.CodeGenerator.CodeGenerator;
import compiler.Lexer.Identifier;
import compiler.Lexer.VarType;
import compiler.SemanticAnalysis.Type.IdentifierType;
import compiler.SemanticAnalysis.TypeVisitor;

import java.util.ArrayList;

public class Method extends Node {
    public Identifier name;
    public VarType returnType;
    public ArrayList<Parameter> parameters;
    public Block block;

    public Method(Parser parser) throws Exception {
        super(parser);
        parser.getNext();
    }

    public Identifier getName() {
        return name;
    }
    public VarType getReturnType() {
        return returnType;
    }
    public ArrayList<Parameter> getParameters() {
        return parameters;
    }
    public Block getBlock() {
        return block;
    }

    public Node parse() throws Exception {
        Declaration declaration = new Declaration(parser).parse();
        returnType = declaration.type;
        name = new Identifier(declaration.identifier, parser.currentToken.getLine(), parser.currentToken.TokenNumber);
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
        for (int i = 0; i < parameters.size(); i++) {
            parametersString.append("{\n");
            parametersString.append(parameters.get(i).toString());
            parametersString.append("\n}");
            if (i != parameters.size() - 1) {
                parametersString.append(", ");
            }
        }
        String str = "\"Method\": {\n"
                + "\"name\": \""+ name.toString() + "\",\n"
                + "\"returnType\": \""+ returnType.toString() + "\",\n";
        if (!parameters.isEmpty()) {
            str += "\"parameters\": [\n" + parametersString + "],\n";
        } else {
            str += "\"parameters\": [],\n";
        }
        str += "\"block\": " + block.toString() + "\n"
                + '}';

        return str;
    }

    @Override
    public IdentifierType accept(TypeVisitor visitor) throws Exception {
        return visitor.visit(this);
    }
    @Override
    public void accept(CodeGenerator generator) {
        generator.generateCode(this);
    }
}
