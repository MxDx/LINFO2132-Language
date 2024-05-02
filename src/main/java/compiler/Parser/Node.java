package compiler.Parser;

import compiler.CodeGenerator.CodeGenerator;
import compiler.SemanticAnalysis.Type.IdentifierType;
import compiler.SemanticAnalysis.TypeVisitor;

public abstract class Node {
    public Parser parser;
    private final int line;
    private final int tokenNumber;
    private String type = null;
    public Node(Parser parser) {
        this.parser = parser;
        this.line = parser.currentToken.getLine();
        this.tokenNumber = parser.currentToken.getTokenNumber();
    }

    public int getLine() {
        return line;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }
    public String getNodeType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public IdentifierType accept(TypeVisitor visitor) throws Exception {
        return visitor.visit();
    }
    public void accept(CodeGenerator generator) {
        generator.generateCode(this);
    }

    public void accept(CodeGenerator codeGenerator, String identifier) {
        codeGenerator.generateCode(this, identifier);
    }
}
