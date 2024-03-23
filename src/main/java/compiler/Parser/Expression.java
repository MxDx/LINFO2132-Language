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
    public Node corps;
    Symbol EOF = new Special(";");

    public Expression(Parser parser) {
        super(parser);
    }

    public Node parse() throws Exception {
        if (parser.currentToken.isValue()) {
            corps = new Value(parser).parse();
        } else {
            switch (parser.currentToken.getType()) {
                case "Identifier":
                    corps = new IdentifierAccess(parser).parse();
                    break;
                case "(":
                    parser.getNext();
                    corps = new Expression(parser).parse();
                    parser.match(Parser.CLOSE_PARENTHESES);
                    break;
            }
        }
        if (parser.lookahead.equals(EOF)) {
            parser.getNext();
            return this;
        }
        if (arithmeticOperations.contains(parser.lookahead.getValue())) {
            return new ArithmeticOperation(parser, corps).parse();
        } else if (comparisonOperations.contains(parser.lookahead.getValue())) {
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
        public Symbol value;
        public Value(Parser parser) throws Exception {
            super(parser);
            if (!parser.currentToken.isValue()) {
                parser.ParserException("Invalid value");
            }
            value = parser.currentToken;
        }

        public Expression parse() throws Exception {
            return this;
        }

        @Override
        public String toString() {
            return "\"" + value.getValue() + "\"";
        }
    }

    public static class Operation extends Expression {
        public String operation;
        public Node left;
        public Node right;

        public Operation(Parser parser, Node before) throws Exception {
            super(parser);
            operation = parser.lookahead.getValue();
            left = before;

        }

        public Node parse() throws Exception {
            return this;
        }

        @Override
        public String toString() {
            return "{\n\"left\": " + left.toString() + ",\n"
                    + "\"operation\": " + "\"" + operation + "\"" + ",\n"
                    + "\"right\": " + right.toString() + "\n}";
        }
    }

    public static class ArithmeticOperation extends Operation {
        final static ArrayList<String> weak = new ArrayList<>() {{
            add("+");
            add("-");
        }};

        final static ArrayList<String> strong = new ArrayList<>() {{
            add("*");
            add("/");
        }};
        public ArithmeticOperation(Parser parser, Node before) throws Exception {
            super(parser, before);
        }

        public ArithmeticOperation(Parser parser, Node before, Node next, String operation) throws Exception {
            super(parser, before);
            this.operation = operation;
            right = next;
        }

        public Node parse() throws Exception {
            left = parseTerm(left);
            while (weak.contains(parser.lookahead.getValue())) {
                String operation = parser.lookahead.getValue();
                parser.getNext();
                parser.getNext();
                Node newRight = parseTerm(null);
                left = new ArithmeticOperation(parser, left, newRight, operation);
            }
            parser.getNext();
            return left;
        }

        private Node parseTerm(Node start) throws Exception {
            Node result = start;

            if (start == null) {
                result = parseIdentifier();
            }

            while (strong.contains(parser.lookahead.getValue())) {
                String operation = parser.lookahead.getValue();
                parser.getNext();
                parser.getNext();
                result = new ArithmeticOperation(parser, result, parseTerm(null), operation);
            }
            return result;
        }

        private Node parseIdentifier() throws Exception {
            Node result = null;

            if (parser.currentToken.isValue()) {
                result =  new Value(parser).parse();
            } else {
                switch (parser.currentToken.getType()) {
                    case "Identifier":
                        result = new IdentifierAccess(parser).parse();
                        break;
                    case "(":
                        parser.match(Parser.OPEN_PARENTHESES);
                        result = new Expression(parser).parse();
                        parser.match(Parser.CLOSE_PARENTHESES);
                    default:
                        parser.ParserException("Invalid term");
                }
            }
            return result;
        }

        @Override
        public String toString() {
            String str = super.toString();
            return "{\n\"ArithmeticOperation\": \n" + str + "\n \n}";
        }
    }

    public static class ComparisonOperation extends Operation {
        public ComparisonOperation(Parser parser, Node before) throws Exception {
            super(parser, before);
            parser.getNext();
        }

        public Node parse() throws Exception {
            right = new Expression(parser).parse();
            return this;
        }

        @Override
        public String toString() {
            String str = super.toString();
            return "{\n\"ComparisonOperation\": \n" + str + "\n \n}";
        }
    }
}
