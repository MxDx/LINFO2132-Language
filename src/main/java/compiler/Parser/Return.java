package compiler.Parser;

public class Return extends Node{
    Node expression;
    public Return(Parser parser) throws Exception {
        super(parser);
        parser.getNext();
    }
    public Node parse() throws Exception {
        expression = new Expression(parser).parse();
        parser.match(Parser.SEMICOLON);
        return this;
    }

    @Override
    public String toString() {
        return "\"RETURN_Statement\": {\n"
                + "\"expression\": "+ expression.toString() + "\n"
                + '}';
    }
}
