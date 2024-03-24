package compiler.Parser;

public class Block extends Node{
    public Statements statements;
    public Block(Parser parser) {
        super(parser);
    }
    public Block parse() throws Exception {
        parser.match(Parser.OPEN_BRACES);
        statements = new Statements(parser).setEOF(Parser.CLOSE_BRACES).parse();
        parser.match(Parser.CLOSE_BRACES);
        return this;
    }

    @Override
    public String toString() {
        return statements.toString();
    }
}
