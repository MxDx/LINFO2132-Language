package compiler.Parser;

import compiler.Lexer.Symbol;
import compiler.SemanticAnalysis.IdentifierType;
import compiler.SemanticAnalysis.Type;
import compiler.SemanticAnalysis.TypeVisitor;

import java.util.ArrayList;
import java.util.Objects;

public class Statements extends Starting {
    public ArrayList<Statement> statements;
    Symbol EOF;
    public Statements(Parser parser) {
        super(parser);
    }

    public Statements parse() throws Exception {
        statements = new ArrayList<>();
        while (parser.currentToken != null) {
            if (parser.currentToken.equals(EOF)) break;
            Statement statement = new Statement(parser);
            statements.add(statement.parse());
            while (Objects.equals(parser.currentToken, Parser.SEMICOLON)) {
                parser.getNext();
            }
        }
        return this;
    }

    public Statements setEOF(Symbol EOF) {
        this.EOF = EOF;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("{\n\"Statements\": [\n");
        if (statements.isEmpty()) {
            str.append("]");
            str.append("\n}");
            return str.toString();
        }
        for (int i = 0; i < statements.size()-1; i++) {
            str.append(statements.get(i).toString());
            str.append(",\n");
        }
        str.append(statements.get(statements.size()-1).toString());
        str.append("\n]\n}");
        return str.toString();
    }

    public static class Statement extends Node {
        public Node content;

        public Statement(Parser parser) {
            super(parser);
        }

        public Node getContent() {
            return content;
        }

        public Statement parse() throws Exception {
            switch (this.parser.currentToken.getType()) {
                case "Keyword":
                    switch (parser.currentToken.getValue()) {
                        case "if" -> content = new If(this.parser).parse();
                        case "while" -> content = new While(this.parser).parse();
                        case "return" -> content = new Return(this.parser).parse();
                        case "for" -> content = new For(this.parser).parse();
                        case "def" -> content = new Method(this.parser).parse();
                        case "struct" -> content = new Struct(this.parser).parse();
                        case "final" -> content = new Declaration(this.parser).parse();
                        default -> parser.ParserException("Invalid Statement Keyword");
                    }
                    return this;

                case "Identifier":
                    switch (parser.lookahead.getType()) {
                        case "Identifier" -> content = new Declaration(this.parser).parse();
                        case "Special" -> {
                            switch (parser.lookahead.getValue()) {
                                case "(", "[", "=", "." -> content = new IdentifierAccess(this.parser).parse();
                                default -> parser.ParserException("Invalid Statement Identifier");
                            }
                        }
                        default -> parser.ParserException("Invalid Statement Identifier");
                    }
                    parser.match(Parser.SEMICOLON);
                    return this;

                case "VarType":
                    content = new Declaration(this.parser).parse();
                    parser.match(Parser.SEMICOLON);
                    return this;
                default:
                    parser.ParserException("Invalid Statement");
            }
            return this;
        }
        @Override
        public String toString() {
            return "{\n"+
                    "\"content\": " + "{\n" + content.toString() +
                    "\n}" + "\n}";
        }

        @Override
        public IdentifierType accept(TypeVisitor visitor) throws Exception {
            return visitor.visit(this);
        }
    }
}
