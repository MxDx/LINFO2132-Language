package compiler.Parser;

import compiler.SemanticAnalysis.Type.IdentifierType;
import compiler.SemanticAnalysis.TypeVisitor;

public class Return extends Node{
    Node expression;
    public Return(Parser parser) throws Exception {
        super(parser);
        parser.getNext();
    }

    public Node getExpression() {
        return expression;
    }

    public Node parse() throws Exception {
        expression = new Expression(parser).parse();
        parser.match(Parser.SEMICOLON);
        return this;
    }

    @Override
    public String toString() {
        return "\"RETURN_Statement\": {\n"
                + "\"expression\": {\n"+ expression.toString() + "\n}\n"
                + '}';
    }

    @Override
    public IdentifierType accept(TypeVisitor visitor) throws Exception {
        return visitor.visit(this);
    }
}
