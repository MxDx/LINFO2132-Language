package compiler.Parser;

import compiler.CodeGenerator.CodeGenerator;
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
        if (parser.currentToken.equals(Parser.SEMICOLON)) {
            parser.getNext();
            this.expression = null;
            return this;
        }
        expression = new Expression(parser).parse();
        parser.match(Parser.SEMICOLON);
        return this;
    }

    @Override
    public String toString() {
        String str =  "\"RETURN_Statement\":";
        if (expression == null) {
            str += "\"empty\"";
        } else {
            str += "{\n"
                    + "\"expression\": {\n"+ expression + "\n}\n"
                    + '}';
        }
        return str;
    }

    @Override
    public IdentifierType accept(TypeVisitor visitor) throws Exception {
        return visitor.visit(this);
    }
    @Override
    public void accept(CodeGenerator generator) {
        generator.generateCode(this);
    }
}
