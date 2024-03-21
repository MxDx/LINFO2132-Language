package compiler.Parser;

public class Starting extends Node{
    public Starting(Parser parser) {
        super(parser);
    }
    public Statements parse() throws Exception {
        return new Statements(parser).parse();
    }
}
