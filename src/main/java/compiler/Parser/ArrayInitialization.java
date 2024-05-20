package compiler.Parser;

import compiler.CodeGenerator.CodeGenerator;
import compiler.Lexer.Symbol;
import compiler.Lexer.VarType;
import compiler.SemanticAnalysis.Type.IdentifierType;
import compiler.SemanticAnalysis.TypeVisitor;

import java.util.ArrayList;

public class ArrayInitialization extends Node{
    public Node expression;
    public VarType type;
    public ArrayList<Symbol> EOF = new ArrayList<>() {{
        add(Parser.CLOSE_BRACKETS);
    }};

    public ArrayInitialization(Parser parser) throws Exception {
        super(parser);
        if (parser.isStruct(parser.currentToken.getValue())) {
            parser.currentToken = new VarType(parser.currentToken.getValue(), parser.currentToken.getLine(), parser.currentToken.getTokenNumber());
        }
        this.type = (VarType) parser.currentToken;
        parser.getNext();
    }

    public Node getIndex() {
        return expression;
    }

    public VarType getType() {
        return type;
    }

    public ArrayInitialization parse() throws Exception {
        parser.match(Parser.OPEN_BRACKETS);
        expression = new Expression(parser).setEOF(EOF).parse();
        parser.match(Parser.CLOSE_BRACKETS);
        return this;
    }
    @Override
    public String toString() {
        return "\"ArrayInitialization\": {\n"
                + "\"type\": \""+ type.toString() + "\",\n"
                + "\"expression\": {" + expression.toString() + "}\n"
                + '}';
    }

    @Override
    public IdentifierType accept(TypeVisitor visitor) throws Exception {
        return visitor.visit(this);
    }
    public int accept(CodeGenerator generator) {
        return generator.generateCode(this);
    }
}
