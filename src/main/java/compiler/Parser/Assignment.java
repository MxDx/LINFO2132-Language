package compiler.Parser;

import compiler.Lexer.Special;
import compiler.Lexer.Symbol;

import java.util.HashSet;

public class Assignment extends Node {
    Node expression;
    HashSet<Symbol> EOF = new HashSet<Symbol>() {{
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

    public Assignment setEOF(HashSet<Symbol> EOF) {
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
