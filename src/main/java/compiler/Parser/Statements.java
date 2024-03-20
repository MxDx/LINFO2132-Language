package compiler.Parser;

import org.junit.experimental.theories.internal.Assignments;

import java.util.ArrayList;
import java.util.Objects;

public class Statements extends Node {
    ArrayList<Node> statements;
    String EOF;
    public Statements(Parser parser) {
        super(parser);
    }

    public Node parse() throws Exception {
        statements = new ArrayList<>();
        while (parser.currentToken != null) {
            if (Objects.equals(parser.currentToken.getValue(), EOF)) break;
            Statement statement = new Statement(parser);
            statements.add(statement.parse());
            parser.getNext();
        }
        return this;
    }

    public static class Statement extends Node {
        Node content;

        public Statement(Parser parser) {
            super(parser);
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
                        default -> throw new Exception("Invalid Statement Keyword");
                    };
                    return this;

                case "Identifier":
                    switch (parser.lookahead.getType()) {
                        case "Identifier" -> content = new Declaration(this.parser).parse();
                        case "Special" -> {
                            switch (parser.lookahead.getValue()) {
                                case "(" -> content = new FunctionCall(this.parser).parse();
                                case "=" -> content = new Assignment(this.parser).parse();
                                default -> throw new Exception("Invalid Statement Identifier");
                            }
                        }
                        default -> throw new Exception("Invalid Statement Identifier");
                    };
                    return this;

                case "VarType":
                    content = new Declaration(this.parser).parse();
                    return this;
                default:
                    throw new Exception("Invalid Statement");
            }
        }
    }
}
