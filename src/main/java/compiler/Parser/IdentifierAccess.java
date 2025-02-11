package compiler.Parser;

import compiler.CodeGenerator.CodeGenerator;
import compiler.Lexer.Special;
import compiler.Lexer.Symbol;
import compiler.SemanticAnalysis.Type.IdentifierType;
import compiler.SemanticAnalysis.TypeVisitor;

import java.util.ArrayList;
import org.objectweb.asm.Label;


public class IdentifierAccess extends Node {
    public String identifier;
    public IdentifierAccess next;
    public Assignment assignment;
    ArrayList<Symbol> EOF = new ArrayList<>() {{
        add(new Special(";"));
    }};

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

    public IdentifierAccess setEOF(ArrayList<Symbol> EOF) {
        this.EOF = EOF;
        return this;
    }

    public IdentifierAccess parse() throws Exception {
        switch (parser.lookahead.getValue()) {
            case "=" -> assignment =  new Assignment(parser).setEOF(EOF).parse();
            case "[" -> next = new ArrayAccess(parser,this).setEOF(EOF).parse();
            case "." -> next = new StructAccess(parser,this).setEOF(EOF).parse();
            case "(" -> next = new FunctionCall(parser, this).setEOF(Parser.EOF_CLOSE_PARENTHESES()).parse();
        }
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public IdentifierAccess getNext() {
        return next;
    }

    public Assignment getAssignment() {
        return assignment;
    }
    @Override
    public String toString() {
        String str = "\"IdentifierAccess\": {\n"
                + "\"identifier\": " + "\"" + identifier + "\"";
        if (next != null) {
            str += ", \n\"next\": {\n" + next + "\n}";
        }
        if (assignment != null) {
            str += ", \n\"assignment\": {\n" + assignment + "\n}";
        }
        str += "\n}";
        return str;
    }

    @Override
    public IdentifierType accept(TypeVisitor visitor) throws Exception {
        return visitor.visit(this);
    }

    public IdentifierType accept(TypeVisitor visitor, IdentifierType type) throws Exception {
        return null;
    }
    @Override
    public int accept(CodeGenerator generator) {
        return generator.generateCode(this);
    }
    public int accept(CodeGenerator generator, Label start, Label end) {
        return generator.generateCode(this, start, end);
    }

    public static class ArrayAccess extends IdentifierAccess {
        public Node index;

        public ArrayAccess(Parser parser,IdentifierAccess BaseIdentifier) throws Exception {
            super(parser, BaseIdentifier.identifier);
            parser.getNext();
            parser.match(Parser.OPEN_BRACKETS);
            EOF.add(Parser.CLOSE_BRACKETS);
            index = new Expression(parser).setEOF(EOF).parse();
            if (!parser.currentToken.getValue().equals("]")) {
                parser.ParserException("Invalid Array Access");
            }
        }

        public IdentifierAccess parse() throws Exception {
            return super.parse();
        }

        public Node getIndex() {
            return index;
        }

        @Override
        public String toString() {
            String str = "\"ArrayAccess\": {\n"
                    + "\"index\": {" + index + "}";
            if (next != null) {
                str += "\n, \"next\": {\n" + next + "\n}";
            }
            if (assignment != null) {
                str += "\n, \"assignment\": {\n" + assignment + "\n}";
            }
            str += "\n}";
            return str;
        }

        @Override
        public IdentifierType accept(TypeVisitor visitor, IdentifierType type) throws Exception {
            return visitor.visit(this, type);
        }
        public int accept(CodeGenerator generator) {
            return generator.generateCode(this);
        }
        public int accept(CodeGenerator generator, Label start, Label end) {
            return generator.generateCode(this, start, end);
        }
    }

    public static class StructAccess extends IdentifierAccess {
        public String field;

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

        public String getField() {
            return field;
        }

        @Override
        public String toString() {
            String str = "\"StructAccess\": {\n"
                    + "\"field\": " + "\"" + field + "\"";
            if (next != null) {
                str += "\n, \"next\": {\n" + next + "\n}";
            }
            if (assignment != null) {
                str += "\n, \"assignment\": {\n" + assignment + "\n}";
            }
            str += "\n}";
            return str;
        }

        @Override
        public IdentifierType accept(TypeVisitor visitor, IdentifierType type) throws Exception {
            return visitor.visit(this, type);
        }
        public int accept(CodeGenerator generator, Label start, Label end) {
            return generator.generateCode(this, start, end);
        }
        public int accept(CodeGenerator generator) {
            return generator.generateCode(this);
        }
    }

    public static class FunctionCall extends IdentifierAccess {
        public ArrayList<Node> arguments = new ArrayList<>();
        ArrayList<Symbol> EOF = new ArrayList<>(){{
            add(Parser.CLOSE_PARENTHESES);
            add(Parser.COMMA);
        }};
        IdentifierAccess heritage;
        public FunctionCall(Parser parser,IdentifierAccess BaseIdentifier) throws Exception {
            super(parser, BaseIdentifier.identifier);
            this.heritage = BaseIdentifier;
            parser.getNext();
            parser.match(Parser.OPEN_PARENTHESES);
        }

        public ArrayList<Node> getArguments() {
            return arguments;
        }

        public IdentifierAccess parse() throws Exception {

            while (!parser.currentToken.getValue().equals(")")) {
                Symbol oldSymbol = parser.currentToken;
                arguments.add(new Expression(parser).setEOF(EOF).parse());
                if (parser.currentToken.getValue().equals(",")) {
                    parser.getNext();
                } else if (oldSymbol.equals(parser.currentToken)) {
                    parser.getNext();
                    parser.getNext();
                }
            }
            parser.match(Parser.CLOSE_PARENTHESES);
            return this;
        }

        @Override
        public String toString() {
            StringBuilder str = new StringBuilder("""
                    "FunctionCall": {
                    "arguments": [
                    """);
            for (int i = 0; i < arguments.size(); i++) {
                str.append("{\n").append(arguments.get(i).toString()).append("\n}");
                if (i != arguments.size() - 1) {
                    str.append(",\n");
                }
            }
            str.append("\n]");
            if (next != null) {
                str.append(", \n\"next\": {\n").append(next).append("\n}");
            }
            if (assignment != null) {
                str.append(", \n\"assignment\": {\n").append(assignment).append("\n}");
            }
            str.append("\n}");
            return str.toString();
        }

        @Override
        public IdentifierType accept(TypeVisitor visitor, IdentifierType type) throws Exception {
            return visitor.visit(this, type);
        }
        @Override
        public int accept(CodeGenerator generator) {
            return generator.generateCode(this);
        }
        public int accept(CodeGenerator generator, Label start, Label end) {
            return generator.generateCode(this, start, end);
        }
    }
}
