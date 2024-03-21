package compiler.Parser;

public class Assignment extends Node {
    Node identifier;
    Node expression;

    public Assignment(Parser parser) throws Exception {
        super(parser);
        parser.getNext();
    }

    public Assignment setIdentifier(IdentifierAccess identifier) {
        this.identifier = identifier;
        return this;
    }

    public Node parse() throws Exception {
        expression = new Expression(parser).parse();
        return this;
    }
}
