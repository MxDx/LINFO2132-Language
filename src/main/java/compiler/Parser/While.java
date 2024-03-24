package compiler.Parser;

import compiler.Lexer.Symbol;

import java.util.ArrayList;

public class While extends Node{
    ArrayList<Symbol> EOF = new ArrayList<>(){{
        add(Parser.CLOSE_PARENTHESES);
    }};
    public Node expression;
    public Block block;
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
