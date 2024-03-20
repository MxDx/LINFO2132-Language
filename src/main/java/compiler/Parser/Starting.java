package compiler.Parser;
import compiler.Lexer.*;

public class Starting extends Node{
    public Starting(Parser parser) {
        super(parser);
    }
    public Node parse() throws Exception {
        return new Statements(parser).parse();
    }
}
