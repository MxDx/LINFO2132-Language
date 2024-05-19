package compiler.Parser;

import compiler.CodeGenerator.CodeGenerator;
import compiler.SemanticAnalysis.Type.IdentifierType;
import compiler.SemanticAnalysis.TypeVisitor;

import java.util.ArrayList;

public class Struct extends  Node {
    String identifier;
    ArrayList<Declaration> declarations = new ArrayList<>();

    public Struct(Parser parser) throws Exception {
        super(parser);
        parser.getNext();
    }

    public Struct parse() throws Exception {

        identifier = parser.currentToken.getValue();
        parser.getNext();
        parser.match(Parser.OPEN_BRACES);
        while (!parser.currentToken.equals(Parser.CLOSE_BRACES)) {
            declarations.add(new Declaration(parser).parse());
            parser.match(Parser.SEMICOLON);
        }
        parser.match(Parser.CLOSE_BRACES);
        return this;
    }

    public ArrayList<Declaration> getDeclarations() {
        return declarations;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("\"Struct_Statement\": {\n"
                + "\"identifier\": \"" + identifier + "\",\n"
                + "\"declarations\": [\n");
        for (int i = 0; i < declarations.size(); i++) {
            str.append("{\n").append(declarations.get(i).toString()).append("\n}");
            if (i != declarations.size() - 1) {
                str.append(",\n");
            }
        }
        str.append("\n]\n}");
        return str.toString();
    }

    @Override
    public IdentifierType accept(TypeVisitor visitor) throws Exception {
        return visitor.visit(this);
    }
    @Override
    public int accept(CodeGenerator generator) {
        return generator.generateCode(this);
    }
}
