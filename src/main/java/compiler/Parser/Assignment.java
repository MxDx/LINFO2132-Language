package compiler.Parser;

import compiler.Lexer.Special;
import compiler.Lexer.Symbol;

import java.util.ArrayList;

public class Assignment extends Node {
    public Node expression;
    ArrayList<Symbol> EOF = new ArrayList<>() {{
        add(new Special(";"));
    }};

    public Assignment(Parser parser) throws Exception {
        super(parser);
        parser.getNext();
        parser.match(Parser.EQUALS);
    }

    public Assignment parse() throws Exception {
        expression = new Expression(parser).setEOF(EOF).parse();
        return this;
    }

    public Assignment setEOF(ArrayList<Symbol> EOF) {
        this.EOF = EOF;
        return this;
    }

    @Override
    public String toString() {
        return "\"Assignment\": {\n"
                + "\"expression\": {" + expression.toString() + "}\n"
                + '}';
    }
}
