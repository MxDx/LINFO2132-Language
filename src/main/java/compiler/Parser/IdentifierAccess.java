package compiler.Parser;

import java.util.ArrayList;

public class IdentifierAccess extends Node {
    String identifier;
    IdentifierAccess next;

    public IdentifierAccess(Parser parser) throws Exception {
        super(parser);
        if (!parser.currentToken.getType().equals("Identifier")) {
            throw new Exception("Invalid Identifier");
        }
        identifier = parser.currentToken.getValue();
        parser.getNext();
    }
    public IdentifierAccess(Parser parser, String identifier) {
        super(parser);
        this.identifier = identifier;
    }

    public IdentifierAccess parse() throws Exception {
        switch (parser.currentToken.getValue()) {
            case "=" -> new Assignment(parser).setIdentifier(this).parse();
            case "[" -> next = new ArrayAccess(parser,this).parse();
            case "." -> next = new StructAccess(parser,this).parse();
            case "(" -> next = new FunctionCall(parser,this).parse();
        };
        return this;
    }
    @Override
    public String toString() {
        return "IdentifierAccess: {\n"
                + "identifier: " + identifier + ", \n"
                + "next: {\n"
                + next.toString()
                + "\n}"
                + "\n}";
    }

    public static class ArrayAccess extends IdentifierAccess {
        Integer index;
        IdentifierAccess heritage;

        public ArrayAccess(Parser parser,IdentifierAccess BaseIdentifier) throws Exception {
            super(parser, BaseIdentifier.identifier);
            if (parser.currentToken.getType().equals("MyInteger")) {
                index = Integer.parseInt(parser.currentToken.getValue());
                parser.getNext();
            } else {
                throw new Exception("Invalid Array Index");
            }
            if (!parser.currentToken.getValue().equals("]")) {
                throw new Exception("Invalid Array Access");
            }
            this.heritage = BaseIdentifier;
            parser.getNext();
        }

        public IdentifierAccess parse() throws Exception {
            return super.parse();
        }

        @Override
        public String toString() {
            return "ArrayAccess: {\n"
                    + "index: " + index + ", \n"
                    + "heritage: {\n"
                    + heritage.toString()
                    + "\n}"
                    + "\n}";
        }
    }

    public static class StructAccess extends IdentifierAccess {
        String field;
        IdentifierAccess heritage;

        public StructAccess(Parser parser,IdentifierAccess BaseIdentifier) throws Exception {
            super(parser, BaseIdentifier.identifier);
            if (!parser.currentToken.getType().equals("Identifier")) {
                throw new Exception("Invalid Struct Field");
            }
            field = parser.currentToken.getValue();
            this.heritage = BaseIdentifier;
            parser.getNext();
        }

        public IdentifierAccess parse() throws Exception {
            return super.parse();
        }

        @Override
        public String toString() {
            return "StructAccess: {\n"
                    + "field: " + field + ", \n"
                    + "heritage: {\n"
                    + heritage.toString()
                    + "\n}"
                    + "\n}";
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
