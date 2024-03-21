package compiler.Parser;

import compiler.Lexer.Special;
import compiler.Lexer.Symbol;

import java.util.ArrayList;

public class Expression extends Node {

    final static ArrayList<String> arithmeticOperations = new ArrayList<>() {{
        add("+");
        add("-");
        add("*");
        add("/");
    }};

    final static ArrayList<String> comparisonOperations = new ArrayList<>() {{
        add(">");
        add("<");
        add(">=");
        add("<=");
        add("==");
        add("!=");
    }};
    Node corps;
    Symbol EOF = new Special(";");

    public Expression(Parser parser) {
        super(parser);
    }

    public Expression parse() throws Exception {
        if (parser.currentToken.isValue()) {
            corps = new Value(parser).parse();
        } else {
            switch (parser.currentToken.getType()) {
                case "identifier":
                    corps = new IdentifierAccess(parser).parse();
                    break;
                case "(":
                    parser.getNext();
                    corps = new Expression(parser).parse();
                    parser.match(Parser.CLOSE_PARENTHESES);
                    break;
            }
        }
        if (parser.currentToken.equals(EOF)) {
            return this;
        }
        if (arithmeticOperations.contains(parser.currentToken.getValue())) {
            return new ArithmeticOperation(parser, corps).parse();
        } else if (comparisonOperations.contains(parser.currentToken.getValue())) {
            return new ComparisonOperation(parser, corps).parse();
        } else {
            parser.ParserException("Invalid expression");
        }

        return this;
    }

    public Expression setEOF(Symbol EOF) {
        this.EOF = EOF;
        return this;
    }

    @Override
    public String toString() {
        return corps.toString();
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

    public static class Operation extends Expression {
        String operation;
        Node left;
        Node right;

        public Operation(Parser parser, Node before) throws Exception {
            super(parser);
            operation = parser.currentToken.getValue();
            left = before;

        }

        public Expression parse() throws Exception {
            return this;
        }

        @Override
        public String toString() {
            return "{\nleft: " + left.toString() + ",\n"
                    + "operation: " + operation + ",\n"
                    + "right: " + right.toString() + "\n}";
        }
    }

    public static class ArithmeticOperation extends Operation {
        public ArithmeticOperation(Parser parser, Node before) throws Exception {
            super(parser, before);
            parser.getNext();
        }

        public Expression parse() throws Exception {
            right = new Expression(parser).parse();
            return this;
        }

        @Override
        public String toString() {
            String str = super.toString();
            return "ArithmeticOperation{\n" + str + "\n}";
        }
    }

    public static class ComparisonOperation extends Operation {
        public ComparisonOperation(Parser parser, Node before) throws Exception {
            super(parser, before);
            parser.getNext();
        }

        public Expression parse() throws Exception {
            right = new Expression(parser).parse();
            return this;
        }

        @Override
        public String toString() {
            String str = super.toString();
            return "ComparisonOperation{\n" + str + "\n}";
        }
    }
}
