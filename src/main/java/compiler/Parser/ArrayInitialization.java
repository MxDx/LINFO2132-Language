package compiler.Parser;

import compiler.Lexer.Symbol;
import compiler.Lexer.VarType;

import java.util.ArrayList;

public class ArrayInitialization extends Node{
    public Node expression;
    public VarType type;
    public ArrayList<Symbol> EOF = new ArrayList<>() {{
        add(Parser.CLOSE_BRACKETS);
    }};

    public ArrayInitialization(Parser parser) throws Exception {
        super(parser);
        this.type = (VarType) parser.currentToken;
        parser.getNext();
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
}
