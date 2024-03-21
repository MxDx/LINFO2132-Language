package compiler.Parser;

import compiler.Lexer.Symbol;

public class Expression extends Node {
    Node corps;
    Node expressionTail;

    public Expression(Parser parser) {
        super(parser);
    }

    public Node parse() throws Exception {
        corps = new Value(parser).parse();
        return this;
    }

    public class Value extends Expression {
        Symbol value;
        public Value(Parser parser) throws Exception {
            super(parser);
            if (!parser.currentToken.isValue()) {
                throw new Exception("Invalid value");
            }
            value = parser.currentToken;
            parser.getNext();
        }

        public Node parse() throws Exception {
            return this;
        }
    }
}
