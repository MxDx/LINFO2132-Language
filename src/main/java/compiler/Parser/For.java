package compiler.Parser;

import compiler.CodeGenerator.CodeGenerator;
import compiler.SemanticAnalysis.Type.IdentifierType;
import compiler.SemanticAnalysis.TypeVisitor;

public class For extends Node{
    public Node firstAssignment;
    public Node expression;
    public Node secondAssignment;
    public Block block;
    public For(Parser parser) throws Exception {
        super(parser);
        parser.getNext();
    }
    public Node parse() throws Exception {
        parser.match(Parser.OPEN_PARENTHESES);
        if (!parser.currentToken.getType().equals("Identifier")){
            parser.ParserException("Invalid Identifier");
        }
        firstAssignment = new IdentifierAccess(parser).setEOF(Parser.EOF_COMMA()).parse();
        parser.match(Parser.COMMA);

        expression = new Expression(parser).setEOF(Parser.EOF_COMMA()).parse();
        parser.match(Parser.COMMA);

        if (!parser.currentToken.getType().equals("Identifier")){
            parser.ParserException("Invalid Identifier");
        }
        secondAssignment = new IdentifierAccess(parser).setEOF(Parser.EOF_CLOSE_PARENTHESES()).parse();
        parser.match(Parser.CLOSE_PARENTHESES);

        block = new Block(parser).parse();
        return this;
    }

    public Node getFirstAssignment() {
        return firstAssignment;
    }

    public Node getExpression() {
        return expression;
    }

    public Node getSecondAssignment() {
        return secondAssignment;
    }

    public Node getBlock() {
        return block;
    }

    @Override
    public String toString() {
        return "\"FOR_Statement\": {\n"
                + "\"firstAssignment\": {\n" + firstAssignment.toString() + "},\n"
                + "\"expression\": {\n"+ expression.toString() + "\n},\n"
                + "\"secondAssignment\": {\n" + secondAssignment.toString() + "},\n"
                + "\"block\": " + block.toString() + "\n"
                + '}';
    }

    @Override
    public IdentifierType accept(TypeVisitor visitor) throws Exception {
        return visitor.visit(this);
    }
    @Override
    public int accept(CodeGenerator generator) {
        return generator.generateCode(this);
    }
}
