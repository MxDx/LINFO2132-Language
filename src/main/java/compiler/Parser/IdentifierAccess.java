package compiler.Parser;

import compiler.Lexer.Special;
import compiler.Lexer.Symbol;

import java.util.ArrayList;

public class IdentifierAccess extends Node {
    String identifier;
    IdentifierAccess next;
    Assignment assignment;
    Symbol EOF = new Special(";");

    public IdentifierAccess(Parser parser) throws Exception {
        super(parser);
        if (!parser.currentToken.getType().equals("Identifier")) {
            throw new Exception("Invalid Identifier");
        }
        identifier = parser.currentToken.getValue();
    }
    public IdentifierAccess(Parser parser, String identifier) {
        super(parser);
        this.identifier = identifier;
    }

    public IdentifierAccess setEOF(Symbol EOF) {
        this.EOF = EOF;
        return this;
    }

    public IdentifierAccess parse() throws Exception {
        switch (parser.lookahead.getValue()) {
            case "=" -> assignment =  new Assignment(parser).setEOF(EOF).parse();
            case "[" -> next = new ArrayAccess(parser,this).setEOF(EOF).parse();
            case "." -> next = new StructAccess(parser,this).setEOF(EOF).parse();
            case "(" -> next = new FunctionCall(parser,this).setEOF(EOF).parse();
        };
        return this;
    }
    @Override
    public String toString() {
        String str = "\"IdentifierAccess\": {\n"
                + "\"identifier\": " + "\"" + identifier + "\"";
        if (next != null) {
            str += ", \n\"next\": {\n" + next.toString() + "\n}";
        }
        if (assignment != null) {
            str += ", \n\"assignment\": {\n" + assignment.toString() + "\n}";
        }
        str += "\n}";
        return str;
    }

    public static class ArrayAccess extends IdentifierAccess {
        Integer index;

        public ArrayAccess(Parser parser,IdentifierAccess BaseIdentifier) throws Exception {
            super(parser, BaseIdentifier.identifier);
            parser.getNext();
            if (parser.lookahead.getType().equals("MyInteger")) {
                index = Integer.parseInt(parser.lookahead.getValue());
                parser.getNext();
            } else {
                throw new Exception("Invalid Array Index");
            }
            if (!parser.lookahead.getValue().equals("]")) {
                throw new Exception("Invalid Array Access");
            }
            parser.getNext();
        }

        public IdentifierAccess parse() throws Exception {
            return super.parse();
        }

        @Override
        public String toString() {
            String str = "\"ArrayAccess\": {\n"
                    + "\"index\": " + index;
            if (next != null) {
                str += "\n, \"next\": {\n" + next.toString() + "\n}";
            }
            if (assignment != null) {
                str += "\n, \"assignment\": {\n" + assignment.toString() + "\n}";
            }
            str += "\n}";
            return str;
        }
    }

    public static class StructAccess extends IdentifierAccess {
        String field;

        public StructAccess(Parser parser,IdentifierAccess BaseIdentifier) throws Exception {
            super(parser, BaseIdentifier.identifier);
            parser.getNext();
            if (!parser.lookahead.getType().equals("Identifier")) {
                throw new Exception("Invalid Struct Field");
            }
            field = parser.lookahead.getValue();
            parser.getNext();
        }

        public IdentifierAccess parse() throws Exception {
            return super.parse();
        }

        @Override
        public String toString() {
            String str = "\"StructAccess\": {\n"
                    + "\"field\": " + "\"" + field + "\"";
            if (next != null) {
                str += "\n, \"next\": {\n" + next.toString() + "\n}";
            }
            if (assignment != null) {
                str += "\n, \"assignment\": {\n" + assignment.toString() + "\n}";
            }
            str += "\n}";
            return str;
        }
    }

    public class FunctionCall extends IdentifierAccess {
        ArrayList<Node> arguments = new ArrayList<>();
        IdentifierAccess heritage;
        public FunctionCall(Parser parser,IdentifierAccess BaseIdentifier) throws Exception {
            super(parser, BaseIdentifier.identifier);
            this.heritage = BaseIdentifier;
        }
        public IdentifierAccess parse() throws Exception {

            while (!parser.currentToken.getValue().equals(")")) {
                arguments.add(new Expression(parser).parse());
                if (parser.currentToken.getValue().equals(",")) {
                    parser.getNext();
                }
            }
            parser.match(Parser.CLOSE_PARENTHESES);
            return this;
        }
    }
}
