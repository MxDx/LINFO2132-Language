package compiler.Parser;

import compiler.Lexer.Special;
import compiler.Lexer.Symbol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class Expression extends Node {

    final static HashSet<String> arithmeticOperations = new HashSet<>() {{
        add("+");
        add("-");
        add("*");
        add("/");
    }};

    final static HashSet<String> comparisonOperations = new HashSet<>() {{
        add(">");
        add("<");
        add(">=");
        add("<=");
        add("==");
        add("!=");
    }};
    public Node corps;
    HashSet<Symbol> EOF = new HashSet<>() {{
        add(new Special(";"));
    }};

    public Expression(Parser parser) {
        super(parser);
    }

    public Node getCorps() throws Exception {
        if (parser.currentToken.isValue()) {
            return new Value(parser).parse();
        } else {
            if (Objects.equals(parser.currentToken.getType(), "Identifier")) {
                return new IdentifierAccess(parser).setEOF(EOF).parse();
            } else {
                parser.match(Parser.OPEN_PARENTHESES);
                Node result = new Expression(parser).setEOF(Parser.EOF_CLOSE_PARENTHESES).parse();
                if (!parser.currentToken.equals(Parser.CLOSE_PARENTHESES)) {
                    parser.ParserException("Invalid expression");
                }
                return result;
            }
        }
    }

    public Node parse() throws Exception {
        corps = getCorps();
        if (EOF.contains(parser.lookahead)) {
            parser.getNext();
            return this;
        }
        if (EOF.contains(parser.currentToken)) {
            return this;
        }

        if (arithmeticOperations.contains(parser.lookahead.getValue())) {
            Node result =  new ArithmeticOperation(parser, corps).setEOF(EOF).parse();
            if (EOF.contains(parser.currentToken)) {
                return result;
            }
            if (!comparisonOperations.contains(parser.lookahead.getValue())) {
                parser.ParserException("Invalid expression");
            }
            return new ComparisonOperation(parser, result).setEOF(EOF).parse();
        } else if (comparisonOperations.contains(parser.lookahead.getValue())) {
            return new ComparisonOperation(parser, corps).setEOF(EOF).parse();
        } else {
            parser.ParserException("Invalid expression");
        }

        return this;
    }

    public Expression setEOF(HashSet<Symbol> EOF) {
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
            return "\"value\": " + "\"" + value.getValue() + "\"";
        }
    }

    public static class Operation extends Expression {
        public String operation;
        public Node left;
        public Node right;

        public Operation(Parser parser, Node before) {
            super(parser);
            operation = parser.lookahead.getValue();
            left = before;

        }

        public Node parse() throws Exception {
            return this;
        }

        @Override
        public String toString() {
            String str = "\"left\": " + "{\n" + left.toString() + "\n},";
            str += "\"operation\": " + "\"" + operation + "\"" + ",\n";
            str += "\"right\": {\n" + right.toString() + "\n}\n";
            return str;
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
            if (EOF.contains(parser.currentToken) || comparisonOperations.contains(parser.lookahead.getValue())) {
                return left;
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
            return getCorps();
        }

        @Override
        public String toString() {
            String str = super.toString();
            return "\"ArithmeticOperation\": {\n" + str + "\n \n}";
        }
    }

    public static class ComparisonOperation extends Operation {
        public ComparisonOperation(Parser parser, Node before) throws Exception {
            super(parser, before);
            parser.getNext();
            parser.getNext();
        }

        public Node parse() throws Exception {
            right = new Expression(parser).setEOF(EOF).parse();
            return this;
        }

        @Override
        public String toString() {
            String str = super.toString();
            return "\"ComparisonOperation\": {\n" + str + "\n \n}";
        }
    }
}
