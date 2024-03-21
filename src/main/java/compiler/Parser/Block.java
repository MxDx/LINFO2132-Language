package compiler.Parser;

public class Block extends Node{
    Statements statements;
    public Block(Parser parser) {
        super(parser);
    }
    public Block parse() throws Exception {
        statements = new Statements(parser).setEOF(Parser.CLOSE_BRACES).parse();
        return this;
    }

    @Override
    public String toString() {
        return statements.toString();
    }
}
