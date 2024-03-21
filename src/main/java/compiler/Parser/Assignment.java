package compiler.Parser;

public class Assignment extends Node {
    String identifier;
    Node expression;

    public Assignment(Parser parser) throws Exception {
        super(parser);
        parser.getNext();
    }

    public Assignment setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public Assignment parse() throws Exception {
        expression = new Expression(parser).parse();
        return this;
    }

    @Override
    public String toString() {
        return "\"Assignment\": {\n"
                + "\"identifier\": \""+ identifier + "\",\n"
                + "\"expression\": " + expression.toString() + "\n"
                + '}';
    }
}
