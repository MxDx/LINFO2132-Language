package compiler.Parser;

import compiler.Lexer.Special;
import compiler.Lexer.Symbol;

public class Assignment extends Node {
    String identifier;
    Node expression;
    Symbol EOF = new Special(";");

    public Assignment(Parser parser) throws Exception {
        super(parser);
        parser.getNext();
        parser.match(Parser.EQUALS);
    }

    public Assignment setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public Assignment parse() throws Exception {
        expression = new Expression(parser).setEOF(EOF).parse();
        return this;
    }

    public Assignment setEOF(Symbol EOF) {
        this.EOF = EOF;
        return this;
    }

    @Override
    public String toString() {
        return "\"Assignment\": {\n"
                + "\"identifier\": \""+ identifier + "\",\n"
                + "\"expression\": " + expression.toString() + "\n"
                + '}';
    }
}
