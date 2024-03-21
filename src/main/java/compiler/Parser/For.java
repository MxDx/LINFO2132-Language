package compiler.Parser;

public class For extends Node{
    Node firstAssignment;
    Expression expression;
    Node secondAssignment;
    Block block;
    public For(Parser parser) throws Exception {
        super(parser);
        parser.getNext();
    }
    public Node parse() throws Exception {
        parser.match(Parser.OPEN_PARENTHESES);
        if (!parser.currentToken.getType().equals("Identifier")){
            parser.ParserException("Invalid Identifier");
        }
        firstAssignment = new IdentifierAccess(parser);
        parser.match(Parser.COMMA);

        expression = new Expression(parser).parse();
        parser.match(Parser.COMMA);

        if (!parser.currentToken.getType().equals("Identifier")){
            parser.ParserException("Invalid Identifier");
        }
        secondAssignment = new IdentifierAccess(parser);
        parser.match(Parser.CLOSE_PARENTHESES);

        block = new Block(parser).parse();
        return this;
    }

    @Override
    public String toString() {
        return "\"FOR_Statement\": {\n"
                + "\"firstAssignment\": {" + firstAssignment.toString() + "},\n"
                + "\"expression\": \""+ expression.toString() + "\",\n"
                + "\"secondAssignment\": {" + secondAssignment.toString() + "},\n"
                + "\"block\": " + block.toString() + "\n"
                + '}';
    }
}
