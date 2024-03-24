package compiler.Parser;

import compiler.Lexer.Symbol;

import java.util.HashSet;

public class While extends Node{
    final HashSet<Symbol> EOF = new HashSet<Symbol>(){{
        add(Parser.CLOSE_PARENTHESES);
    }};
    Node expression;
    Block block;
    public While(Parser parser) throws Exception {
        super(parser);
        parser.getNext();
    }
    public Node parse() throws Exception {
        parser.match(Parser.OPEN_PARENTHESES);
        expression = new Expression(parser).setEOF(EOF).parse();
        parser.match(Parser.CLOSE_PARENTHESES);
        block = new Block(parser).parse();
        return this;
    }

    @Override
    public String toString() {
        return "\"WHILE_Statement\": {\n"
                + "\"expression\": \n{"+ expression.toString() + "\n},\n"
                + "\"block\": " + block.toString() + "\n"
                + '}';
    }
}
