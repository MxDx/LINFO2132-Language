package compiler.Parser;

public class While extends Node{
    Expression expression;
    Block block;
    public While(Parser parser) throws Exception {
        super(parser);
        parser.getNext();
    }
    public Node parse() throws Exception {
        parser.match(Parser.OPEN_PARENTHESES);
        expression = new Expression(parser).parse();
        parser.match(Parser.CLOSE_PARENTHESES);
        block = new Block(parser).parse();
        return this;
    }

    @Override
    public String toString() {
        return "\"WHILE_Statement\": {\n"
                + "\"expression\": \""+ expression.toString() + "\",\n"
                + "\"block\": " + block.toString() + "\n"
                + '}';
    }
}
