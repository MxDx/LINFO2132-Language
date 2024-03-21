package compiler.Parser;

import compiler.Lexer.Symbol;

public class Expression extends Node {
    Node corps;
    Node expressionTail;

    public Expression(Parser parser) {
        super(parser);
    }

    public Expression parse() throws Exception {
        corps = new Value(parser).parse();
        return this;
    }

    @Override
    public String toString() {
        String str = corps.toString();
        if (expressionTail != null) {
            str += expressionTail.toString();
        }
        return str;
    }

    public static class Value extends Expression {
        Symbol value;
        public Value(Parser parser) throws Exception {
            super(parser);
            if (!parser.currentToken.isValue()) {
                parser.ParserException("Invalid value");
            }
            value = parser.currentToken;
            parser.getNext();
        }

        public Expression parse() throws Exception {
            return this;
        }

        @Override
        public String toString() {
            return value.getValue();
        }
    }
}
