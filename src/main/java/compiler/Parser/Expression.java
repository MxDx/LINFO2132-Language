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
        add("%");
    }};

    final static HashSet<String> comparisonOperations = new HashSet<>() {{
        add(">");
        add("<");
        add(">=");
        add("<=");
        add("==");
        add("!=");
    }};
    final static HashSet<String> logicalOperations = new HashSet<>() {{
        add("&&");
        add("||");
    }};

    public Node corps;
    ArrayList<Symbol> EOF = new ArrayList<>() {{
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
            } else if (parser.currentToken.equals(Parser.OPEN_PARENTHESES)) {
                parser.match(Parser.OPEN_PARENTHESES);

                if (EOF.contains(Parser.CLOSE_PARENTHESES)) {
                    // Only useful to prevent the expression to stop early
                    EOF.add(Parser.CLOSE_PARENTHESES);
                }
                Node result = new Expression(parser).setEOF(EOF).parse();
                if (logicalOperations.contains(parser.currentToken.getValue())) {
                    return new LogicalOperation(parser, result).setEOF(EOF).parse();
                }
                if (!parser.currentToken.equals(Parser.CLOSE_PARENTHESES)) {
                    parser.ParserException("Invalid expression");
                }
                return result;
            } else if (Objects.equals(parser.currentToken.getType(), "VarType")) {
                return new ArrayInitialization(parser).parse();
            } else if (Objects.equals(parser.currentToken, new Special("-"))) {
                return new Negative(parser).setEOF(EOF).parse();
            } else if (Objects.equals(parser.currentToken, new Special("!"))) {
                return new Bang(parser).setEOF(EOF).parse();
            } else {
                parser.ParserException("Invalid expression");
            }
        }
        return null;
    }

    public Node parse() throws Exception {
        corps = getCorps();

        if (EOF.contains(parser.currentToken)) {
            EOF.remove(parser.currentToken);
            if (!EOF.contains(parser.currentToken)) {
                if (corps instanceof Value) {
                    return corps;
                }
                return this;
            }
            parser.getNext();
        }
        if (arithmeticOperations.contains(parser.lookahead.getValue()) || arithmeticOperations.contains(parser.currentToken.getValue())) {
            Node result =  new ArithmeticOperation(parser, corps).setEOF(EOF).parse();
            if (EOF.contains(parser.currentToken)) {
                return result;
            }
            if (comparisonOperations.contains(parser.lookahead.getValue())) {
                return new ComparisonOperation(parser, result).setEOF(EOF).parse();
            }
            return result;
        } else if (comparisonOperations.contains(parser.lookahead.getValue()) || comparisonOperations.contains(parser.currentToken.getValue())) {
            return new ComparisonOperation(parser, corps).setEOF(EOF).parse();
        }

        if (logicalOperations.contains(parser.lookahead.getValue())) {
            parser.getNext();
            return corps;
        } else if (EOF.contains(parser.lookahead)) {
            parser.getNext();
            EOF.remove(parser.currentToken);
            if (!EOF.contains(parser.currentToken)) {
                if (corps instanceof Value) {
                    return corps;
                }
                return this;
            }
            EOF.add(parser.currentToken);
        } else if (logicalOperations.contains(parser.currentToken.getValue())) {
            return corps;
        } else {
            parser.ParserException("Invalid expression");
        }

        return corps;
    }

    public Expression setEOF(ArrayList<Symbol> EOF) {
        this.EOF = EOF;
        return this;
    }

    @Override
    public String toString() {
        return corps.toString();
    }

    public static class Negative extends Expression {
        public Node expression;
        public Negative(Parser parser) throws Exception {
            super(parser);
            parser.match(new Special("-"));
        }

        public Node parse() throws Exception {
            ArrayList<Symbol> newEOF = new ArrayList<>(EOF);
            expression = new Expression(parser).setEOF(newEOF).parse();
            return this;
        }

        @Override
        public String toString() {
            return "\"Negative\": {\n" + "\"expression\": {" + expression.toString() + "}\n}";
        }
    }

    public static class Bang extends Expression {
        public Node expression;
        public Bang(Parser parser) throws Exception {
            super(parser);
            parser.match(new Special("!"));
        }

        public Node parse() throws Exception {
            ArrayList<Symbol> newEOF = new ArrayList<>(EOF);
            expression = new Expression(parser).setEOF(newEOF).parse();
            return this;
        }

        @Override
        public String toString() {
            return "\"Bang\": {\n" + "\"expression\": {" + expression.toString() + "}\n}";
        }
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

        public Operation(Parser parser, Node before, String operation) {
            super(parser);
            this.operation = operation;
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
            add("%");
        }};
        private boolean lastOperationStrong;
        public ArithmeticOperation(Parser parser, Node before) {
            super(parser, before);
            //if (Expression.arithmeticOperations.contains(parser.currentToken.getValue())) {
            //    operation = parser.currentToken.getValue();
            //}
        }

        public ArithmeticOperation(Parser parser, Node before, Node next, String operation) {
            super(parser, before);
            this.operation = operation;
            right = next;
        }

        public Node parse() throws Exception {
            left = parseTerm(left);
            if (EOF.contains(parser.currentToken)) {
                return left;
            }
            while (weak.contains(parser.lookahead.getValue()) || weak.contains(parser.currentToken.getValue())) {
                if (EOF.contains(parser.currentToken)) {
                    return left;
                }
                if (weak.contains(parser.lookahead.getValue())) {
                    parser.getNext();
                }
                String operation = parser.currentToken.getValue();
                parser.getNext();
                lastOperationStrong = strong.contains(operation);
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
                if (strong.contains(operation) && lastOperationStrong) {
                    return result;
                }
            }

            if (EOF.contains(parser.currentToken) || comparisonOperations.contains(parser.lookahead.getValue())) {
                return result;
            }


            while (strong.contains(parser.lookahead.getValue()) || strong.contains(parser.currentToken.getValue())) {
                if (EOF.contains(parser.currentToken)) {
                    return result;
                }
                if (strong.contains(parser.lookahead.getValue())) {
                    parser.getNext();
                }
                String operation = parser.currentToken.getValue();
                lastOperationStrong = strong.contains(operation);
                parser.getNext();
                result = new ArithmeticOperation(parser, result, parseTerm(null), operation).setEOF(EOF);
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
            if (comparisonOperations.contains(parser.currentToken.getValue())) {
                operation = parser.currentToken.getValue();
            } else {
                parser.getNext();
            }
            parser.getNext();
        }

        public Node parse() throws Exception {
            ArrayList<Symbol> newEOF = new ArrayList<>(EOF);
            Symbol AND = new Special("&&");
            Symbol OR = new Special("||");
            if (!EOF.contains(AND)) {
                newEOF.add(AND);
            }
            if (!EOF.contains(OR)) {
                newEOF.add(OR);
            }
            right = new Expression(parser).setEOF(newEOF).parse();
            if (Expression.logicalOperations.contains(parser.currentToken.getValue())) {
                return new LogicalOperation(parser, this).setEOF(EOF).parse();
            }
            return this;
        }

        @Override
        public String toString() {
            String str = super.toString();
            return "\"ComparisonOperation\": {\n" + str + "\n \n}";
        }
    }
    public static class LogicalOperation extends Operation {
        public LogicalOperation(Parser parser, Node before) throws Exception {
            super(parser, before, parser.currentToken.getValue());
            parser.getNext();
        }

        public Node parse() throws Exception {
            right = new Expression(parser).setEOF(EOF).parse();
            return this;
        }

        @Override
        public String toString() {
            String str = super.toString();
            return "\"LogicalOperation\": {\n" + str + "\n \n}";
        }
    }
}
