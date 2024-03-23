package compiler.Parser;

public class Starting extends Node{
    public Starting(Parser parser) {
        super(parser);
    }
    public Starting parse() throws Exception {
        return new Statements(parser).parse();
    }
}
