package compiler.Parser;

public class If extends Node{
    Expression expression;
    Block block;
    public If(Parser parser) throws Exception {
        super(parser);
        parser.getNext();
    }
    public Node parse() throws Exception {
        parser.match(Parser.OPEN_PARENTHESES);
        expression = new Expression(parser).parse();
        parser.match(Parser.CLOSE_PARENTHESES);
        parser.match(Parser.OPEN_BRACES);
        block = new Block(parser).parse();
        parser.match(Parser.CLOSE_BRACES);
        return this;
    }

    @Override
    public String toString() {
        return "\"IF_Statement\": {\n"
                + "\"expression\": \""+ expression.toString() + "\",\n"
                + "\"block\": " + block.toString() + "\n"
                + '}';
    }
}
