package compiler.Parser;

import compiler.Lexer.Keyword;
import compiler.Lexer.Symbol;

public class If extends Node{

    final static Symbol ELSE = new Keyword("else");
    Expression expression;
    Block block;

    Node elseStatement;


    public If(Parser parser) throws Exception {
        super(parser);
        parser.getNext();
    }
    public Node parse() throws Exception {
        parser.match(Parser.OPEN_PARENTHESES);
        expression = new Expression(parser).parse();
        parser.match(Parser.CLOSE_PARENTHESES);
        parser.match(Parser.OPEN_BRACES);
        block = new Block(parser).parse();
        parser.match(Parser.CLOSE_BRACES);

        if (parser.currentToken.equals(ELSE)){
            elseStatement = new ELse(parser).parse();
        }

        return this;
    }

    @Override
    public String toString() {
        String str =  "\"IF_Statement\": {\n"
                + "\"expression\": \""+ expression.toString() + "\",\n"
                + "\"block\": " + block.toString() + "\n";
        if (elseStatement != null){
            str += ",\n" + elseStatement.toString();
        }
        str += '}';
        return str;
    }

    private static class ELse extends Node {
        Block block;

        public ELse(Parser parser) throws Exception {
            super(parser);
            parser.getNext();
        }
        public Node parse() throws Exception {
            parser.match(Parser.OPEN_BRACES);
            block = new Block(parser).parse();
            parser.match(Parser.CLOSE_BRACES);
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
