package compiler.Parser;

import compiler.Lexer.Keyword;
import compiler.Lexer.Symbol;

import java.util.HashSet;
import java.util.Objects;

public class If extends Node{
    final static Symbol ELSE = new Keyword("else");
    final HashSet<Symbol> EOF = new HashSet<Symbol>(){{
        add(Parser.CLOSE_PARENTHESES);
    }};
    Node expression;
    Block block;
    Node elseStatement;
    public If(Parser parser) throws Exception {
        super(parser);
        parser.getNext();
    }
    public Node parse() throws Exception {
        parser.match(Parser.OPEN_PARENTHESES);
        expression = new Expression(parser).setEOF(EOF).parse();
        parser.match(Parser.CLOSE_PARENTHESES);
        block = new Block(parser).parse();
        if (Objects.equals(parser.currentToken, ELSE)){
            elseStatement = new Else(parser).parse();
        }
        return this;
    }

    @Override
    public String toString() {
        String str =  "\"IF_Statement\": {\n"
                + "\"expression\": {\n"+ expression.toString() + "\n},\n"
                + "\"block\": " + block.toString() + "\n";
        if (elseStatement != null){
            str += ",\n" + elseStatement.toString();
        }
        str += '}';
        return str;
    }

    private static class Else extends Node {
        Block block;

        public Else(Parser parser) throws Exception {
            super(parser);
            parser.getNext();
        }
        public Node parse() throws Exception {
            block = new Block(parser).parse();
            return this;
        }

        @Override
        public String toString() {
            return "\"ELSE_Statement\": {\n"
                    + "\"block\": " + block.toString() + "\n"
                    + '}';
        }
    }
}
