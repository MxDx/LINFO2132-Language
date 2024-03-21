package compiler.Parser;

public class IdentifierAccess extends Node {
    String identifier;

    public IdentifierAccess(Parser parser) throws Exception {
        super(parser);
        identifier = parser.currentToken.getValue();
        parser.getNext();
    }

    public Node parse() throws Exception {
        return switch (parser.currentToken.getValue()) {
            case "=" -> new Assignment(parser).setIdentifier(this.identifier).parse();
            case "[" -> new ArrayAccess(parser).parse();
            case "." -> new StructAccess(parser).parse();
            default -> this;
        };
    }

    public static class ArrayAccess extends IdentifierAccess {
        Integer index;

        public ArrayAccess(Parser parser) throws Exception {
            super(parser);
            parser.getNext();
            if (parser.currentToken.getType().equals("MyInteger")) {
                index = Integer.parseInt(parser.currentToken.getValue());
                parser.getNext();
            } else {
                throw new Exception("Invalid Array Index");
            }
            if (!parser.currentToken.getValue().equals("]")) {
                throw new Exception("Invalid Array Access");
            }
            parser.getNext();
        }

        public Node parse() throws Exception {
            return super.parse();
        }
    }

    public static class StructAccess extends IdentifierAccess {
        String field;

        public StructAccess(Parser parser) throws Exception {
            super(parser);
            parser.getNext();
            if (!parser.currentToken.getType().equals("Identifier")) {
                throw new Exception("Invalid Struct Field");
            }
            field = parser.currentToken.getValue();
            parser.getNext();
        }

        public Node parse() throws Exception {
            return super.parse();
        }
    }

    public class FunctionCall extends IdentifierAccess {
        public FunctionCall(Parser parser) throws Exception {
            super(parser);
        }
        public Node parse() throws Exception {
            return null;
        }
    }
}
